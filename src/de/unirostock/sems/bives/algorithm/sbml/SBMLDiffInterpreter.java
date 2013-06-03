/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

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
import de.unirostock.sems.bives.algorithm.Interpreter;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.ds.sbml.SBMLCompartment;
import de.unirostock.sems.bives.ds.sbml.SBMLCompartmentType;
import de.unirostock.sems.bives.ds.sbml.SBMLConstraint;
import de.unirostock.sems.bives.ds.sbml.SBMLDiffReport;
import de.unirostock.sems.bives.ds.sbml.SBMLDiffReporter;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLEvent;
import de.unirostock.sems.bives.ds.sbml.SBMLFunctionDefinition;
import de.unirostock.sems.bives.ds.sbml.SBMLInitialAssignment;
import de.unirostock.sems.bives.ds.sbml.SBMLModel;
import de.unirostock.sems.bives.ds.sbml.SBMLParameter;
import de.unirostock.sems.bives.ds.sbml.SBMLReaction;
import de.unirostock.sems.bives.ds.sbml.SBMLRule;
import de.unirostock.sems.bives.ds.sbml.SBMLSpecies;
import de.unirostock.sems.bives.ds.sbml.SBMLSpeciesType;
import de.unirostock.sems.bives.ds.sbml.SBMLUnitDefinition;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.markup.MarkupSection;


/**
 * @author Martin Scharm
 *
 */
public class SBMLDiffInterpreter
	extends Interpreter
{
	//private SBMLDiffReport report;
	private MarkupDocument markupDocument;
	private SBMLDocument sbmlDocA, sbmlDocB;
	
	public SBMLDiffInterpreter (ClearConnectionManager conMgmt, SBMLDocument sbmlDocA,
		SBMLDocument sbmlDocB)
	{
		super (conMgmt, sbmlDocA.getTreeDocument (), sbmlDocB.getTreeDocument ());
			//report = new SBMLDiffReport ();
		markupDocument = new MarkupDocument ("SBML Differences");
			this.sbmlDocA = sbmlDocA;
			this.sbmlDocB = sbmlDocB;
	}
	
	// TODO!!!
	public void annotatePatch ()
	{
		// TODO!!!
	}
	
	public MarkupDocument getReport ()
	{
		return markupDocument;
	}
	

	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Producer#produce()
	 */
	@Override
	public void interprete ()
	{
		// id's are quite critical!

		SBMLModel modelA = sbmlDocA.getModel ();
		SBMLModel modelB = sbmlDocB.getModel ();

		String lvA = "L" + sbmlDocA.getLevel () + "V" + sbmlDocA.getVersion ();
		String lvB = "L" + sbmlDocB.getLevel () + "V" + sbmlDocB.getVersion ();
		if (lvA.equals (lvB))
			markupDocument.addHeader ("Both documents have same Level/Version: " + markupDocument.highlight (lvA));
		else
			markupDocument.addHeader ("Level/Version has changed: from " + markupDocument.delete (lvA) + " to " + markupDocument.delete (lvB));
		
		checkUnits (modelA, modelB);
		checkParameters (modelA, modelB);
		checkCompartments (modelA, modelB);
		checkCompartmentTypes (modelA, modelB);
		checkSpecies (modelA, modelB);
		checkSpeciesTypes (modelA, modelB);
		checkReactions (modelA, modelB);
		checkRules (modelA, modelB);
		checkConstraints (modelA, modelB);
		checkInitialAssignments (modelA, modelB);
		checkFunctions (modelA, modelB);
		checkEvents (modelA, modelB);
	}
	
	private void checkRules (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Rules");
		LOGGER.info ("searching for rules in A");
		Vector<SBMLRule> rules = modelA.getRules ();
		for (SBMLRule rule : rules)
		{
			DocumentNode dn = rule.getDocumentNode ();
			LOGGER.info ("rule: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (rule.reportDelete (markupDocument));
			else
			{
				msec.addValue (rule.reportMofification (conMgmt, rule, (SBMLRule) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for rules in B");
		rules = modelB.getRules ();
		for (SBMLRule rule : rules)
		{
			DocumentNode dn = rule.getDocumentNode ();
			LOGGER.info ("rule: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (rule.reportInsert(markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkCompartments (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Compartments");
		LOGGER.info ("searching for compartments in A");
		HashMap<String, SBMLCompartment> compartments = modelA.getCompartments ();
		for (SBMLCompartment compartment : compartments.values ())
		{
			DocumentNode dn = compartment.getDocumentNode ();
			LOGGER.info ("compartment: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (compartment.reportDelete(markupDocument));
			else
			{
				msec.addValue (compartment.reportMofification (conMgmt, compartment, (SBMLCompartment) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for compartments in B");
		compartments = modelB.getCompartments ();
		for (SBMLCompartment compartment : compartments.values ())
		{
			DocumentNode dn = compartment.getDocumentNode ();
			LOGGER.info ("compartment: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (compartment.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkCompartmentTypes (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Compartment Types");
		LOGGER.info ("searching for compartmenttypes in A");
		HashMap<String, SBMLCompartmentType> compartmenttypes = modelA.getCompartmentTypes ();
		for (SBMLCompartmentType compartment : compartmenttypes.values ())
		{
			DocumentNode dn = compartment.getDocumentNode ();
			LOGGER.info ("compartmenttype: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (compartment.reportDelete (markupDocument));
			else
			{
				msec.addValue (compartment.reportMofification (conMgmt, compartment, (SBMLCompartmentType) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for compartmenttypes in B");
		compartmenttypes = modelB.getCompartmentTypes ();
		for (SBMLCompartmentType compartment : compartmenttypes.values ())
		{
			DocumentNode dn = compartment.getDocumentNode ();
			LOGGER.info ("compartmenttype: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (compartment.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkParameters (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Parameters");
		LOGGER.info ("searching for parameters in A");
		HashMap<String, SBMLParameter> parameters = modelA.getParameters();
		for (SBMLParameter parameter : parameters.values ())
		{
			DocumentNode dn = parameter.getDocumentNode ();
			LOGGER.info ("parameter: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (parameter.reportDelete (markupDocument));
			else
			{
				msec.addValue (parameter.reportMofification (conMgmt, parameter, (SBMLParameter) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for parameters in B");
		parameters = modelB.getParameters ();
		for (SBMLParameter parameter : parameters.values ())
		{
			DocumentNode dn = parameter.getDocumentNode ();
			LOGGER.info ("parameter: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (parameter.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkEvents (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Events");
		LOGGER.info ("searching for events in A");
		Vector<SBMLEvent> events = modelA.getEvents ();
		for (SBMLEvent event : events)
		{
			DocumentNode dn = event.getDocumentNode ();
			LOGGER.info ("event: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (event.reportDelete (markupDocument));
			else
			{
				msec.addValue (event.reportMofification (conMgmt, event, (SBMLEvent) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for events in B");
		events = modelB.getEvents ();
		for (SBMLEvent event : events)
		{
			DocumentNode dn = event.getDocumentNode ();
			LOGGER.info ("event: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (event.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkSpecies (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Species");
		LOGGER.info ("searching for species in A");
		HashMap<String, SBMLSpecies> species = modelA.getSpecies();
		for (SBMLSpecies spec : species.values ())
		{
			DocumentNode dn = spec.getDocumentNode ();
			LOGGER.info ("species: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (spec.reportDelete (markupDocument));
			else
			{
				msec.addValue (spec.reportMofification (conMgmt, spec, (SBMLSpecies) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for species in B");
		species = modelB.getSpecies ();
		for (SBMLSpecies spec : species.values ())
		{
			DocumentNode dn = spec.getDocumentNode ();
			LOGGER.info ("species: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (spec.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkSpeciesTypes (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Species Types");
		LOGGER.info ("searching for speciestypes in A");
		HashMap<String, SBMLSpeciesType> speciestypes = modelA.getSpeciesTypes();
		for (SBMLSpeciesType spec : speciestypes.values ())
		{
			DocumentNode dn = spec.getDocumentNode ();
			LOGGER.info ("speciestype: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (spec.reportDelete (markupDocument));
			else
			{
				msec.addValue (spec.reportMofification (conMgmt, spec, (SBMLSpeciesType) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for speciestypes in B");
		speciestypes = modelB.getSpeciesTypes ();
		for (SBMLSpeciesType spec : speciestypes.values ())
		{
			DocumentNode dn = spec.getDocumentNode ();
			LOGGER.info ("speciestype: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (spec.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkReactions (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Reactions");
		LOGGER.info ("searching for reactions in A");
		HashMap<String, SBMLReaction> reactions = modelA.getReactions();
		for (SBMLReaction reaction : reactions.values ())
		{
			DocumentNode dn = reaction.getDocumentNode ();
			LOGGER.info ("reaction: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (reaction.reportDelete (markupDocument));
			else
			{
				msec.addValue (reaction.reportMofification (conMgmt, reaction, (SBMLReaction) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for reactions in B");
		reactions = modelB.getReactions();
		for (SBMLReaction reaction : reactions.values ())
		{
			DocumentNode dn = reaction.getDocumentNode ();
			LOGGER.info ("reaction: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (reaction.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkFunctions (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Functions");
		LOGGER.info ("searching for functions in A");
		HashMap<String, SBMLFunctionDefinition> functions = modelA.getFunctionDefinitions();
		for (SBMLFunctionDefinition function : functions.values ())
		{
			DocumentNode dn = function.getDocumentNode ();
			LOGGER.info ("function: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (function.reportDelete (markupDocument));
			else
			{
				msec.addValue (function.reportMofification (conMgmt, function, (SBMLFunctionDefinition) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for functions in B");
		functions = modelB.getFunctionDefinitions();
		for (SBMLFunctionDefinition function : functions.values ())
		{
			DocumentNode dn = function.getDocumentNode ();
			LOGGER.info ("function: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (function.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkUnits (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Units");
		LOGGER.info ("searching for units in A");
		HashMap<String, SBMLUnitDefinition> units = modelA.getUnitDefinitions();
		for (SBMLUnitDefinition unit : units.values ())
		{
			if (unit.isBaseUnit ())
				continue;
			DocumentNode dn = unit.getDocumentNode ();
			LOGGER.info ("unit: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (unit.reportDelete (markupDocument));
			else
			{
				msec.addValue (unit.reportMofification (conMgmt, unit, (SBMLUnitDefinition) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for units in B");
		units = modelB.getUnitDefinitions();
		for (SBMLUnitDefinition unit : units.values ())
		{
			if (unit.isBaseUnit ())
				continue;
			DocumentNode dn = unit.getDocumentNode ();
			LOGGER.info ("unit: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (unit.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkInitialAssignments (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Initial Assignments");
		LOGGER.info ("searching for initial assignments in A");
		Vector<SBMLInitialAssignment> initAss = modelA.getInitialAssignments ();
		for (SBMLInitialAssignment ia : initAss)
		{
			DocumentNode dn = ia.getDocumentNode ();
			LOGGER.info ("init. ass.: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (ia.reportDelete (markupDocument));
			else
			{
				msec.addValue (ia.reportMofification (conMgmt, ia, (SBMLInitialAssignment) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for initial assignments in B");
		initAss = modelB.getInitialAssignments ();
		for (SBMLInitialAssignment ia : initAss)
		{
			DocumentNode dn = ia.getDocumentNode ();
			LOGGER.info ("init. ass.: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (ia.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
	
	private void checkConstraints (SBMLModel modelA, SBMLModel modelB)
	{
		MarkupSection msec = new MarkupSection ("Constraints");
		LOGGER.info ("searching for constraints in A");
		Vector<SBMLConstraint> constraints = modelA.getConstraints ();
		for (SBMLConstraint constraint : constraints)
		{
			DocumentNode dn = constraint.getDocumentNode ();
			LOGGER.info ("constraint: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (constraint.reportDelete (markupDocument));
			else
			{
				msec.addValue (constraint.reportMofification (conMgmt, constraint, (SBMLConstraint) modelB.getFromNode (con.getPartnerOf (dn)), markupDocument));
			}
		}
		LOGGER.info ("searching for constraints in B");
		constraints = modelB.getConstraints ();
		for (SBMLConstraint constraint : constraints)
		{
			DocumentNode dn = constraint.getDocumentNode ();
			LOGGER.info ("constraint: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				msec.addValue (constraint.reportInsert (markupDocument));
		}
		if (msec.getValues ().size () > 0)
			markupDocument.addSection (msec);
	}
}
