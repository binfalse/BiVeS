/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
import de.unirostock.sems.bives.ds.cellml.CellMLModel;
import de.unirostock.sems.bives.ds.cellml.CellMLReaction;
import de.unirostock.sems.bives.ds.cellml.CellMLReactionSubstance;
import de.unirostock.sems.bives.ds.cellml.CellMLVariable;
import de.unirostock.sems.bives.ds.graph.CRN;
import de.unirostock.sems.bives.ds.graph.CRNReaction;
import de.unirostock.sems.bives.ds.graph.CRNSubstance;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
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
	/*private Element graphRoot;
	private Document graphDocument;
	private HashMap<String, String> entityMapper;
	private int maxSpecies;
	private int maxReaction;
	private static final String INSERT = "1";
	private static final String DELETE = "-1";
	private static final String MODIFIED = "2";*/
	private CellMLDocument cellmlDocA, cellmlDocB;
	private ClearConnectionManager conMgmt;
	
	public CellMLGraphProducer (ClearConnectionManager conMgmt, CellMLDocument cellmlDocA, CellMLDocument cellmlDocB)
	{
		//super.init (conMgmt, sbmlDocA.getTreeDocument (), sbmlDocA.getTreeDocument ());
		this.cellmlDocA = cellmlDocA;
		this.cellmlDocB = cellmlDocB;
		this.conMgmt = conMgmt;
		processA ();
		processB ();
	}
	
	public CellMLGraphProducer (CellMLDocument cellmlDoc)
	{
		//super.init (conMgmt, sbmlDocA.getTreeDocument (), sbmlDocA.getTreeDocument ());
		this.cellmlDocA = cellmlDoc;
		processA ();
		crn.setSingleDocument ();
	}
	
	/*public Object translate (GraphTranslator trans)
	{
		return trans.translate (crn);
	}*/
	
	
	public void processA ()
	{
		CellMLModel modelA = cellmlDocA.getModel ();

		LOGGER.info ("looping through components in A");
		HashMap<String, CellMLComponent> components = modelA.getComponents ();
		for (CellMLComponent component : components.values ())
		{
			Vector<CellMLReaction> reactions = component.getReactions ();
			for (CellMLReaction reaction : reactions)
			{
				CRNReaction crnreaction = new CRNReaction (crn, reaction.getComponent ().getName (), null, reaction.getDocumentNode (), null, reaction.isReversible ());
				crn.setReaction (reaction.getDocumentNode (), crnreaction);
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
						subst = new CRNSubstance (crn, rootvar.getName (), null, rootvar.getDocumentNode (), null);
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
	
	public void processB ()
	{
		CellMLModel modelB = cellmlDocB.getModel ();

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
						subst = new CRNSubstance (crn, null, rootvar.getName (), null, rootvar.getDocumentNode ());
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
		/*CellMLModel modelB = cellmlDocB.getModel ();
		LOGGER.info ("searching for species in B");
		HashMap<String, SBMLSpecies> species = modelB.getSpecies ();
		for (SBMLSpecies s : species.values ())
		{
			DocumentNode sDoc = s.getDocumentNode ();
			Connection c = conMgmt.getConnectionForNode (sDoc);
			if (c == null)
			{
				// no equivalent in doc a
				crn.setSubstance (sDoc, new CRNSubstance (crn, null, s.getNameOrId (), null, sDoc));
			}
			else
			{
				CRNSubstance subst = crn.getSubstance (c.getPartnerOf (sDoc));
				subst.setDocB (sDoc);
				subst.setLabelB (s.getNameOrId ());
				crn.setSubstance (sDoc, subst);
			}
		}
		
		LOGGER.info ("searching for reactions in B");
		HashMap<String, SBMLReaction> reactions = modelB.getReactions ();
		for (SBMLReaction r : reactions.values ())
		{
			DocumentNode rNode = r.getDocumentNode ();
			Connection c = conMgmt.getConnectionForNode (rNode);
			CRNReaction reaction = null;
			if (c == null)
			{
				// no equivalent in doc a
				reaction = new CRNReaction (crn, null, r.getNameOrId (), null, r.getDocumentNode ());
				crn.setReaction (rNode, reaction);
			}
			else
			{
				reaction = crn.getReaction (c.getPartnerOf (rNode));
				crn.setReaction (rNode, reaction);
			}
				
			Vector<SBMLSpeciesReference> sRefs = r.getReactants ();
			for (SBMLSpeciesReference sRef : sRefs)
			{
				reaction.addInputB (crn.getSubstance (sRef.getSpecies ().getDocumentNode ()));
			}
			
			sRefs = r.getProducts ();
			for (SBMLSpeciesReference sRef : sRefs)
			{
				reaction.addOutputB (crn.getSubstance (sRef.getSpecies ().getDocumentNode ()));
			}
			
			Vector<SBMLSimpleSpeciesReference> ssRefs = r.getModifiers ();
			for (SBMLSimpleSpeciesReference sRef : ssRefs)
			{
				SBMLSpecies spec = sRef.getSpecies ();
				reaction.addModB (crn.getSubstance (spec.getDocumentNode ()), spec.getSBOTerm ());
			}
		}*/
	}
	

	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Producer#produce()
	 */
	/*@Override
	public String produce ()
	{
		SBMLModel modelA = sbmlDocA.getModel ();
		SBMLModel modelB = sbmlDocB.getModel ();
		
		LOGGER.info ("searching for species in A");
		HashMap<String, SBMLSpecies> species = modelA.getSpecies ();
		for (SBMLSpecies s : species.values ())
		{
			DocumentNode sNode = s.getDocumentNode ();
			LOGGER.info ("species: " + sNode.getXPath ());
			String id = "s" + ++maxSpecies;
			
			if (sNode.hasModification (TreeNode.UNMAPPED))
			{
				// delete
				entityMapper.put ("sd" + s.getID (), id);
				createGraphMLNode (graphRoot,
				id, "species",
				s.getID (),
				DELETE, null, null);
			}
			else
			{
				// both have this species in common
				entityMapper.put ("sc" + s.getID (), id);
				createGraphMLNode (graphRoot,
				id, "species",
				s.getID (),
				sNode.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED) ? MODIFIED : null, null, null);
			}
		}
		
		LOGGER.info ("searching for species in B");
		species = modelB.getSpecies ();
		for (SBMLSpecies s : species.values ())
		{
			DocumentNode sNode = s.getDocumentNode ();
			LOGGER.info ("species: " + sNode.getXPath ());
			
			if (sNode.hasModification (TreeNode.UNMAPPED))
			{
				String id = "s" + ++maxSpecies;
				// insert
				entityMapper.put ("si" + s.getID (), id);
				createGraphMLNode (graphRoot,
				id, "species",
				s.getID (),
				INSERT, null, null);
			}
		}
		
		// write reactions
		LOGGER.info ("searching for reactions in A");
		HashMap<String, SBMLReaction> reactions = modelA.getReactions ();
		for (SBMLReaction r : reactions.values ())
		{
			DocumentNode rNode = r.getDocumentNode ();
			LOGGER.debug ("reaction: " + rNode.getXPath ());
			LOGGER.debug ("reaction marker: " + rNode.getModification () + " mod/submod: " + rNode.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED));
			
			String reactionID = r.getID ();
			String id = "r" + ++maxReaction;
			
			//System.out.println (r.getNameAndId () + " = " + rNode.getXPath () + " -> " + rNode.getModification ());
			
			if (rNode.hasModification (TreeNode.UNMAPPED))
			{
				// delete
				entityMapper.put ("rd" + reactionID, id);
				createGraphMLNode (graphRoot,
					id, "reaction",
				r.getID (),
				DELETE, r.isReversible (), r.isFast ());
			}
			else
			{
				entityMapper.put ("rc" + reactionID, id);
				createGraphMLNode (graphRoot,
					id, "reaction",
				r.getID (),
				rNode.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED) ? MODIFIED : null, r.isReversible (), r.isFast ());
			}
			
			Vector<SBMLSpeciesReference> sRefs = r.getReactants ();
			for (SBMLSpeciesReference sRef : sRefs)
			{
				//System.out.println ("sd" + sRef.getSpecies ().getID ());
				String spec = entityMapper.get ("sd" + sRef.getSpecies ());
				if (spec == null)
					spec = entityMapper.get ("sc" + sRef.getSpecies ().getID ());

				createEdge (graphRoot, spec,
					id, sRef.getDocumentNode ().hasModification (TreeNode.UNMAPPED) ? DELETE : null, "none");
			}
			
			sRefs = r.getProducts ();
			for (SBMLSpeciesReference sRef : sRefs)
			{
				String spec = entityMapper.get ("sd" + sRef.getSpecies ().getID ());
				if (spec == null)
					spec = entityMapper.get ("sc" + sRef.getSpecies ().getID ());

				createEdge (graphRoot, id, spec,
					sRef.getDocumentNode ().hasModification (TreeNode.UNMAPPED) ? DELETE : null, "none");
			}
			
			Vector<SBMLSimpleSpeciesReference> ssRefs = r.getModifiers ();
			for (SBMLSimpleSpeciesReference sRef : ssRefs)
			{
				String spec = entityMapper.get ("sd" + sRef.getSpecies ().getID ());
				if (spec == null)
					spec = entityMapper.get ("sc" + sRef.getSpecies ().getID ());

				createEdge (graphRoot, spec,
					id, sRef.getDocumentNode ().hasModification (TreeNode.UNMAPPED) ? DELETE : null, sRef.getSBOTerm ().resolvModifier ());
			}
		}
		

		LOGGER.info ("searching for reactions in B");
		reactions = modelB.getReactions ();
		for (SBMLReaction r : reactions.values ())
		{
			DocumentNode rNode = r.getDocumentNode ();
			LOGGER.debug ("reaction: " + rNode.getXPath ());
			LOGGER.debug ("reaction marker: " + rNode.getModification () + " mod/submod: " + rNode.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED));
			
			String reactionID = r.getID ();
			String id;
			
			if (rNode.hasModification (TreeNode.UNMAPPED))
			{
				// insert
				id = "r" + ++maxReaction;
				entityMapper.put ("ri" + reactionID, id);
				createGraphMLNode (graphRoot,
					id, "reaction",
				r.getID (),
				INSERT, r.isReversible (), r.isFast ());
			}
			else
			{
				id = entityMapper.get ("rc" + reactionID);
			}

			Vector<SBMLSpeciesReference> sRefs = r.getReactants ();
			for (SBMLSpeciesReference sRef : sRefs)
			{
				if (!sRef.getDocumentNode ().hasModification (TreeNode.UNMAPPED))
					continue;
				String spec = entityMapper.get ("si" + sRef.getSpecies ().getID ());
				if (spec == null)
					spec = entityMapper.get ("sc" + sRef.getSpecies ().getID ());

				createEdge (graphRoot, spec,
					id, sRef.getDocumentNode ().hasModification (TreeNode.UNMAPPED) ? INSERT : null, "none");
			}

			sRefs = r.getProducts ();
			for (SBMLSpeciesReference sRef : sRefs)
			{
				if (!sRef.getDocumentNode ().hasModification (TreeNode.UNMAPPED))
					continue;
				String spec = entityMapper.get ("si" + sRef.getSpecies ().getID ());
				if (spec == null)
					spec = entityMapper.get ("sc" + sRef.getSpecies ().getID ());

				createEdge (graphRoot, id, spec,
					sRef.getDocumentNode ().hasModification (TreeNode.UNMAPPED) ? INSERT : null, "none");
			}
			
			Vector<SBMLSimpleSpeciesReference> ssRefs = r.getModifiers ();
			for (SBMLSimpleSpeciesReference sRef : ssRefs)
			{
				if (!sRef.getDocumentNode ().hasModification (TreeNode.UNMAPPED))
					continue;
				String spec = entityMapper.get ("si" + sRef.getSpecies ().getID ());
				if (spec == null)
					spec = entityMapper.get ("sc" + sRef.getSpecies ().getID ());

				createEdge (graphRoot, spec,
					id, sRef.getDocumentNode ().hasModification (TreeNode.UNMAPPED) ? INSERT : null, sRef.getSBOTerm ().resolvModifier ());
			}
		}
		
		
		try
		{
			return printDocument (new OutputStream()
			{
			  private StringBuilder string = new StringBuilder();
			  
			  @Override
			  public void write(int b) throws IOException {
			      this.string.append((char) b );
			  }

			  //Netbeans IDE automatically overrides this toString()
			  public String toString(){
			      return this.string.toString();
			  }
}).toString ();
		}
		catch (IOException | TransformerException e)
		{

			LOGGER.error ("error printing graphml", e);
		}
		return null;
	}
	
	public OutputStream printDocument(OutputStream out) throws IOException, TransformerException
	{
		LOGGER.info ("delivering sbml graphml");
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    transformer.transform(new DOMSource(graphDocument), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    return out;
  }

	/**
	 * Creates the preamble of the graph. Creates some nodes in the document defining the graph and its properties. Creates also the <graph> node to insert the graph itself. This graph-node will be returned afterwards.
	 * 
	 * @param doc
	 *          the document
	 * @return the graph node (root for the graph)
	 */
	/*private Element addGraphMLPreamble (Document doc)
	{
		Element graphML = doc.createElement("graphml");
		//graphML.setNamespace (Namespace.getNamespace ("http://graphml.graphdrawing.org/xmlns"));
		doc.appendChild (graphML);
		
		// key zur beschreibung des nodenamens
		Element keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "name");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "name");
		keyEl.setAttribute ("attr.type", "string");
		graphML.appendChild (keyEl);
		
		// key zur beschreibung des nodeset
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "ns");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "node set");
		keyEl.setAttribute ("attr.type", "string");
		Element defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("species"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung des reversible-attributs
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "rev");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "reversible");
		keyEl.setAttribute ("attr.type", "boolean");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("false"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung des fast-attributs
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "fast");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "fast");
		keyEl.setAttribute ("attr.type", "boolean");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("false"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung der struktur
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "vers");
		keyEl.setAttribute ("for", "all");
		keyEl.setAttribute ("attr.name", "surce");
		keyEl.setAttribute ("attr.type", "int");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("0"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung der modifier von edges
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "mod");
		keyEl.setAttribute ("for", "edge");
		keyEl.setAttribute ("attr.name", "modifier");
		keyEl.setAttribute ("attr.type", "string");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("none"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung der initialAmounts von species
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "init");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "initial amount");
		keyEl.setAttribute ("attr.type", "double");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("0"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// <graph>
		keyEl = doc.createElement("graph");
		keyEl.setAttribute ("id", "G");
		keyEl.setAttribute ("edgedefault", "directed");
		graphML.appendChild (keyEl);
		
		return keyEl;
	}
	
	/**
	 * Inserts a new node to the graph. This node will automatically appended to the {@code parent}s children. The arguments {@code reversible} and {@code fast} are intended for reaction-nodes. Non-reation nodes might leave them null.
	 * 
	 * @param graphDocument
	 *          the relevant document
	 * @param parent
	 *          the parent node
	 * @param id
	 *          the ID of the node
	 * @param ns
	 *          the node set
	 * @param name
	 *          the name of the node
	 * @param src
	 *          the source document (reference doc or diff), should be -1, 0 or 1
	 * @param reversible
	 *          reaction only: is this reaction reversible? (should be null for non-reactions)
	 * @param fast
	 *          reaction only: the fast attribute (should be null for non-reactions)
	 */
	/*private void createGraphMLNode (Element parent, String id,
		String ns, String name, String modification, Boolean reversible, Boolean fast)
	{
		LOGGER.debug ("create gml node: " + id + " mod: " + modification);
		Element element = graphDocument.createElement ("node");
		
		element.setAttribute ("id", id);
		
		Element nsElement = graphDocument.createElement ("data");
		nsElement.setAttribute ("key", "ns");
		nsElement.appendChild (graphDocument.createTextNode (ns));
		element.appendChild (nsElement);
		
		if (modification != null)
		{
			Element srcElement = graphDocument.createElement ("data");
			srcElement.setAttribute ("key", "vers");
			srcElement.appendChild (graphDocument.createTextNode (modification));
			element.appendChild (srcElement);
		}
		
		if (reversible != null)
		{
			//name += " (reversible)";
			Element revElement = graphDocument.createElement ("data");
			revElement.setAttribute ("key", "rev");
			revElement.appendChild (graphDocument.createTextNode (reversible ? "true" : "false"));
			element.appendChild (revElement);
		}
		
		if (fast != null)
		{
			//name += " (fast)";
			Element fastElement = graphDocument.createElement ("data");
			fastElement.setAttribute ("key", "fast");
			fastElement.appendChild (graphDocument.createTextNode (fast ? "true" : "false"));
			element.appendChild (fastElement);
		}
		
		Element nameElement = graphDocument.createElement ("data");
		nameElement.setAttribute ("key", "name");
		nameElement.appendChild (graphDocument.createTextNode (name));
		element.appendChild (nameElement);
		
		
		parent.appendChild (element);
	}
	
	
	/**
	 * Inserts a new edge to the graph. This edge will automatically appended to the {@code parent}s children.
	 * 
	 * @param graphDocument
	 *          the doc
	 * @param parent
	 *          the parent
	 * @param source
	 *          the source
	 * @param target
	 *          the target
	 * @param src
	 *          the src
	 * @param mod
	 *          the mod
	 */
	/*private void createEdge (Element parent, String source,
		String target, String modification, String mod)
	{
		LOGGER.debug ("create gml edge: " + source + " -> " + target + " mod: " + modification);
		Element element = graphDocument.createElement ("edge");
		
		element.setAttribute ("source", source);
		element.setAttribute ("target", target);
		
		Element nsElement = graphDocument.createElement ("data");
		nsElement.setAttribute ("key", "mod");
		nsElement.appendChild (graphDocument.createTextNode (mod));
		element.appendChild (nsElement);

		if (modification != null)
		{
			Element srcElement = graphDocument.createElement ("data");
			srcElement.setAttribute ("key", "vers");
			srcElement.appendChild (graphDocument.createTextNode (modification));
			element.appendChild (srcElement);
		}
		
		parent.appendChild (element);
	}*/
}