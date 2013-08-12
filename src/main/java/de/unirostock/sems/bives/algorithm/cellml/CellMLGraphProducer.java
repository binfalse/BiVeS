/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.GraphProducer;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.ds.SBOTerm;
import de.unirostock.sems.bives.ds.cellml.CellMLComponent;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.cellml.CellMLHierarchy;
import de.unirostock.sems.bives.ds.cellml.CellMLHierarchyNetwork;
import de.unirostock.sems.bives.ds.cellml.CellMLHierarchyNode;
import de.unirostock.sems.bives.ds.cellml.CellMLModel;
import de.unirostock.sems.bives.ds.cellml.CellMLReaction;
import de.unirostock.sems.bives.ds.cellml.CellMLReactionSubstance;
import de.unirostock.sems.bives.ds.cellml.CellMLVariable;
import de.unirostock.sems.bives.ds.graph.CRN;
import de.unirostock.sems.bives.ds.graph.CRNCompartment;
import de.unirostock.sems.bives.ds.graph.CRNReaction;
import de.unirostock.sems.bives.ds.graph.CRNSubstance;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.bives.ds.graph.HierarchyNetworkComponent;
import de.unirostock.sems.bives.ds.graph.HierarchyNetworkVariable;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLModel;
import de.unirostock.sems.bives.ds.sbml.SBMLReaction;
import de.unirostock.sems.bives.ds.sbml.SBMLSimpleSpeciesReference;
import de.unirostock.sems.bives.ds.sbml.SBMLSpecies;
import de.unirostock.sems.bives.ds.sbml.SBMLSpeciesReference;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class CellMLGraphProducer
extends GraphProducer
{
	private CellMLDocument cellmlDocA, cellmlDocB;
	private ClearConnectionManager conMgmt;
	private CRNCompartment wholeCompartment;
	
	public CellMLGraphProducer (ClearConnectionManager conMgmt, CellMLDocument cellmlDocA, CellMLDocument cellmlDocB)
	{
		super (false);
		this.cellmlDocA = cellmlDocA;
		this.cellmlDocB = cellmlDocB;
		this.conMgmt = conMgmt;
		wholeCompartment = new CRNCompartment (crn, "document", "document", null, null);
		wholeCompartment.setSingleDocument ();
	}
	
	public CellMLGraphProducer (CellMLDocument cellmlDoc)
	{
		super (true);
		this.cellmlDocA = cellmlDoc;
		wholeCompartment = new CRNCompartment (crn, "document", "document", null, null);
		wholeCompartment.setSingleDocument ();
	}

	@Override
	protected void produceCRN ()
	{
		processCrnA ();
		if (single)
			crn.setSingleDocument ();
		else
			processCrnB ();
	}

	@Override
	protected void produceHierachyGraph ()
	{
		processHnA ();
		if (single)
			hn.setSingleDocument ();
		else
			processHnB ();
	}
	
	protected void processHnA ()
	{
		CellMLHierarchyNetwork enc = cellmlDocA.getModel ().getHierarchy ().getHierarchyNetwork ("encapsulation", "");
		if (enc == null)
			return;
		
		// looks like wee need two traversals...
		HashMap<CellMLHierarchyNode, HierarchyNetworkComponent> componentMapper = new HashMap<CellMLHierarchyNode, HierarchyNetworkComponent> ();
		HashMap<CellMLVariable, HierarchyNetworkVariable> variableMapper = new HashMap<CellMLVariable, HierarchyNetworkVariable> ();
		
		Collection<CellMLHierarchyNode> nodes = enc.getNodes ();
		for (CellMLHierarchyNode node : nodes)
		{
			CellMLComponent comp = node.getComponent ();
			HierarchyNetworkComponent nc = new HierarchyNetworkComponent (hn, comp.getName (), null, comp.getDocumentNode (), null);
			
			HashMap<String, CellMLVariable> vars = comp.getVariables ();
			for (CellMLVariable var : vars.values ())
			{
				HierarchyNetworkVariable hnVar = new HierarchyNetworkVariable (hn, var.getName (), null, var.getDocumentNode (), null, nc, null);
				nc.addVaribaleA (hnVar);
				hn.setVariable (var.getDocumentNode (), hnVar);
				variableMapper.put (var, hnVar);
			}
			hn.setComponent (comp.getDocumentNode (), nc);
			componentMapper.put (node, nc);
		}
		

		for (CellMLHierarchyNode node : nodes)
		{
			CellMLComponent comp = node.getComponent ();
			HierarchyNetworkComponent nc = componentMapper.get (node);
			CellMLHierarchyNode parent = node.getParent ();
			if (parent != null)
			{
				HierarchyNetworkComponent pc = componentMapper.get (parent);
				nc.setParentA (pc);
			}
			Vector<CellMLHierarchyNode> kids = node.getChildren ();
			for (CellMLHierarchyNode kid : kids)
			{
				nc.addChildA (componentMapper.get (kid));
			}
			HashMap<String, CellMLVariable> vars = comp.getVariables ();
			for (CellMLVariable var : vars.values ())
			{
				HierarchyNetworkVariable hnv = variableMapper.get (var);
				Vector<CellMLVariable> cons = var.getPrivateInterfaceConnections ();
				for (CellMLVariable con : cons)
					hnv.addConnectionA (variableMapper.get (con));
				/*
				 * just do it one direction
				cons = var.getPublicInterfaceConnections ();
				for (CellMLVariable con : cons)
					hnv.addConnectionA (variableMapper.get (con));
					*/
			}
		}
	}
	
	protected void processHnB ()
	{
		CellMLHierarchyNetwork enc = cellmlDocB.getModel ().getHierarchy ().getHierarchyNetwork ("encapsulation", "");
		if (enc == null)
			return;
		
		// looks like wee need two traversals...
		HashMap<CellMLHierarchyNode, HierarchyNetworkComponent> componentMapper = new HashMap<CellMLHierarchyNode, HierarchyNetworkComponent> ();
		HashMap<CellMLVariable, HierarchyNetworkVariable> variableMapper = new HashMap<CellMLVariable, HierarchyNetworkVariable> ();
		
		Collection<CellMLHierarchyNode> nodes = enc.getNodes ();
		for (CellMLHierarchyNode node : nodes)
		{
			CellMLComponent comp = node.getComponent ();
			DocumentNode compNode = comp.getDocumentNode ();
			// connected?

			Connection con = conMgmt.getConnectionForNode (compNode);
			HierarchyNetworkComponent nc = null;
			if (con == null)
			{
				// no equivalent in doc a
				nc = new HierarchyNetworkComponent (hn, null, comp.getName (), null, compNode);
			}
			else
			{
				nc = hn.getComponent (con.getPartnerOf (compNode));
				nc.setDocB (compNode);
				nc.setLabelB (comp.getName ());
			}
			hn.setComponent (compNode, nc);
			componentMapper.put (node, nc);
			

			HashMap<String, CellMLVariable> vars = comp.getVariables ();
			for (CellMLVariable var : vars.values ())
			{
				DocumentNode varNode = var.getDocumentNode ();
				HierarchyNetworkVariable hnVar = null;
				
				
				// var already defined?
				Connection c = conMgmt.getConnectionForNode (varNode);
				if (c == null || hn.getVariable (c.getPartnerOf (varNode)) == null)
				{
					// no equivalent in doc a
					hnVar = new HierarchyNetworkVariable (hn, null, var.getName (), null, varNode, null, nc);
				}
				else
				{
					hnVar = hn.getVariable (c.getPartnerOf (varNode));
					hnVar.setDocB (varNode);
					hnVar.setLabelA (var.getName ());
					hnVar.setComponentB (nc);
				}
				
				nc.addVaribaleB (hnVar);
				hn.setVariable (varNode, hnVar);
				variableMapper.put (var, hnVar);
			}
		}
		
		
		// TODO: to go
		cccc
		
		for (CellMLHierarchyNode node : nodes)
		{
			CellMLComponent comp = node.getComponent ();
			HierarchyNetworkComponent nc = componentMapper.get (node);
			CellMLHierarchyNode parent = node.getParent ();
			if (parent != null)
			{
				HierarchyNetworkComponent pc = componentMapper.get (parent);
				nc.setParentA (pc);
			}
			Vector<CellMLHierarchyNode> kids = node.getChildren ();
			for (CellMLHierarchyNode kid : kids)
			{
				nc.addChildA (componentMapper.get (kid));
			}
			HashMap<String, CellMLVariable> vars = comp.getVariables ();
			for (CellMLVariable var : vars.values ())
			{
				HierarchyNetworkVariable hnv = variableMapper.get (var);
				Vector<CellMLVariable> cons = var.getPrivateInterfaceConnections ();
				for (CellMLVariable con : cons)
					hnv.addConnectionA (variableMapper.get (con));
				cons = var.getPublicInterfaceConnections ();
				for (CellMLVariable con : cons)
					hnv.addConnectionA (variableMapper.get (con));
			}
		}
		
	}
	
	
	protected void processCrnA ()
	{
		LOGGER.info ("init compartment");
		CellMLModel modelA = cellmlDocA.getModel ();
		//LOGGER.info ("setup compartment in A: " + wholeCompartment + " - " + modelA);
		wholeCompartment.setDocA (modelA.getDocumentNode ());
		//LOGGER.info ("setting compartment");
		crn.setCompartment (modelA.getDocumentNode (), wholeCompartment);

		
		LOGGER.info ("looping through components in A");
		HashMap<String, CellMLComponent> components = modelA.getComponents ();
		for (CellMLComponent component : components.values ())
		{
			Vector<CellMLReaction> reactions = component.getReactions ();
			for (CellMLReaction reaction : reactions)
			{
				CRNReaction crnreaction = new CRNReaction (crn, reaction.getComponent ().getName (), null, reaction.getDocumentNode (), null, reaction.isReversible ());
				crn.setReaction (reaction.getDocumentNode (), crnreaction);
				crnreaction.setCompartmentA (wholeCompartment);
				Vector<CellMLReactionSubstance> substances = reaction.getSubstances ();
				for (CellMLReactionSubstance substance : substances)
				{
					boolean addSubstance = false;
					CellMLVariable var = substance.getVariable ();
					CellMLVariable rootvar = var.getRootVariable ();
					Vector<CellMLReactionSubstance.Role> roles = substance.getRoles ();
					CRNSubstance subst = crn.getSubstance (rootvar.getDocumentNode ());
					// substance undefined?
					if (subst == null)
					{
						subst = new CRNSubstance (crn, rootvar.getName (), null, rootvar.getDocumentNode (), null, wholeCompartment, null);
						addSubstance = true;
					}
					// set up of reaction
					for (CellMLReactionSubstance.Role role : roles)
					{
						switch (role.role)
						{
							case CellMLReactionSubstance.ROLE_REACTANT:
								crnreaction.addInputA (subst, null);
								break;
							case CellMLReactionSubstance.ROLE_PRODUCT:
								crnreaction.addOutputA (subst, null);
								break;
							case CellMLReactionSubstance.ROLE_MODIFIER:
								crnreaction.addModA (subst, null);
								break;
							case CellMLReactionSubstance.ROLE_ACTIVATOR:
							case CellMLReactionSubstance.ROLE_CATALYST:
								crnreaction.addModA (subst, SBOTerm.createStimulator ());
								break;
							case CellMLReactionSubstance.ROLE_INHIBITOR:
								crnreaction.addModA (subst, SBOTerm.createInhibitor ());
								break;
							case CellMLReactionSubstance.ROLE_RATE:
								continue;
						}
						if (addSubstance)
						{
							crn.setSubstance (rootvar.getDocumentNode (), subst);
							addSubstance = false;
						}
					}
				}
			}
		}
	}
	
	protected void processCrnB ()
	{
		CellMLModel modelB = cellmlDocB.getModel ();
		wholeCompartment.setDocB (modelB.getDocumentNode ());
		crn.setCompartment (modelB.getDocumentNode (), wholeCompartment);

		LOGGER.info ("looping through components in B");
		HashMap<String, CellMLComponent> components = modelB.getComponents ();
		for (CellMLComponent component : components.values ())
		{
			Vector<CellMLReaction> reactions = component.getReactions ();
			for (CellMLReaction reaction : reactions)
			{
				DocumentNode rNode = reaction.getDocumentNode ();
				Connection con = conMgmt.getConnectionForNode (rNode);
				CRNReaction crnreaction = null;
				if (con == null)
				{
					// no equivalent in doc a
					crnreaction = new CRNReaction (crn, null, reaction.getComponent ().getName (), null, reaction.getDocumentNode (), reaction.isReversible ());
					crn.setReaction (rNode, crnreaction);
				}
				else
				{
					crnreaction = crn.getReaction (con.getPartnerOf (rNode));
					crn.setReaction (rNode, crnreaction);
					crnreaction.setDocB (rNode);
				}
				crnreaction.setCompartmentB (wholeCompartment);
				Vector<CellMLReactionSubstance> substances = reaction.getSubstances ();
				for (CellMLReactionSubstance substance : substances)
				{
					CellMLVariable var = substance.getVariable ();
					CellMLVariable rootvar = var.getRootVariable ();
					Vector<CellMLReactionSubstance.Role> roles = substance.getRoles ();

					DocumentNode varDoc = var.getDocumentNode ();
					DocumentNode varRootDoc = rootvar.getDocumentNode ();

					CRNSubstance subst = null;
					
					// species already defined?
					Connection c = conMgmt.getConnectionForNode (varRootDoc);
					if (c == null || crn.getSubstance (c.getPartnerOf (varRootDoc)) == null)
					{
						// no equivalent in doc a
						subst = new CRNSubstance (crn, null, rootvar.getName (), null, rootvar.getDocumentNode (), null, wholeCompartment);
						//crn.setSubstance (varRootDoc, subst);
					}
					else
					{
						//System.out.println (varRootDoc);
						//System.out.println (c.getPartnerOf (varRootDoc));
						subst = crn.getSubstance (c.getPartnerOf (varRootDoc));
						//System.out.println (subst);
						subst.setDocB (varRootDoc);
						subst.setLabelB (rootvar.getName ());
						subst.setCompartmentB (wholeCompartment);
						//crn.setSubstance (varRootDoc, subst);
					}
					
					// set up of reaction
					for (CellMLReactionSubstance.Role role : roles)
					{
						switch (role.role)
						{
							case CellMLReactionSubstance.ROLE_REACTANT:
								crnreaction.addInputB (subst, null);
								break;
							case CellMLReactionSubstance.ROLE_PRODUCT:
								crnreaction.addOutputB (subst, null);
								break;
							case CellMLReactionSubstance.ROLE_MODIFIER:
								crnreaction.addModB (subst, null);
								break;
							case CellMLReactionSubstance.ROLE_ACTIVATOR:
							case CellMLReactionSubstance.ROLE_CATALYST:
								crnreaction.addModB (subst, SBOTerm.createStimulator ());
								break;
							case CellMLReactionSubstance.ROLE_INHIBITOR:
								crnreaction.addModB (subst, SBOTerm.createInhibitor ());
								break;
							case CellMLReactionSubstance.ROLE_RATE:
								continue;
						}
						crn.setSubstance (rootvar.getDocumentNode (), subst);
					}
				}
			}
		}
	}
	

}
