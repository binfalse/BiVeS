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
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Interpreter;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.sbmldeprecated.SBMLReport.ModConnection;
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


/**
 * @author Martin Scharm
 *
 */
public class SBMLDiffInterpreter
	extends Interpreter
{
	private SBMLDiffReport report;
	private SBMLDocument sbmlDocA, sbmlDocB;
	
	public SBMLDiffInterpreter (ClearConnectionManager conMgmt, SBMLDocument sbmlDocA,
		SBMLDocument sbmlDocB)
	{
		super (conMgmt, sbmlDocA.getTreeDocument (), sbmlDocB.getTreeDocument ());
			report = new SBMLDiffReport ();
			this.sbmlDocA = sbmlDocA;
			this.sbmlDocB = sbmlDocB;
	}
	
	// TODO!!!
	public void annotatePatch ()
	{
		// TODO!!!
	}
	
	public SBMLDiffReport getReport ()
	{
		return report;
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
			report.addHeader ("Both documents have same Level/Version: " + lvA);
		else
			report.addHeader ("Level/Version has changed: from <span class='" + SBMLDiffReporter.CLASS_DELETED+"'>" + lvA + "</span> to <span class='" + SBMLDiffReporter.CLASS_INSERTED+"'>" + lvB+"</span><br/>");
		
		
		LOGGER.info ("searching for rules in A");
		Vector<SBMLRule> rules = modelA.getRules ();
		for (SBMLRule rule : rules)
		{
			DocumentNode dn = rule.getDocumentNode ();
			LOGGER.info ("rule: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifyRules (rule.reportDelete ());
			else
			{
				report.modifyRules (rule.reportMofification (conMgmt, rule, (SBMLRule) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyRules (rule.reportInsert ());
		}
		

		LOGGER.info ("searching for compartments in A");
		HashMap<String, SBMLCompartment> compartments = modelA.getCompartments ();
		for (SBMLCompartment compartment : compartments.values ())
		{
			DocumentNode dn = compartment.getDocumentNode ();
			LOGGER.info ("compartment: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifyCompartments (compartment.reportDelete ());
			else
			{
				report.modifyCompartments (compartment.reportMofification (conMgmt, compartment, (SBMLCompartment) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyCompartments (compartment.reportInsert ());
		}
		

		LOGGER.info ("searching for compartmenttypes in A");
		HashMap<String, SBMLCompartmentType> compartmenttypes = modelA.getCompartmentTypes ();
		for (SBMLCompartmentType compartment : compartmenttypes.values ())
		{
			DocumentNode dn = compartment.getDocumentNode ();
			LOGGER.info ("compartmenttype: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifyCompartmentTypes (compartment.reportDelete ());
			else
			{
				report.modifyCompartmentTypes (compartment.reportMofification (conMgmt, compartment, (SBMLCompartmentType) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyCompartmentTypes (compartment.reportInsert ());
		}
		

		LOGGER.info ("searching for parameters in A");
		HashMap<String, SBMLParameter> parameters = modelA.getParameters();
		for (SBMLParameter parameter : parameters.values ())
		{
			DocumentNode dn = parameter.getDocumentNode ();
			LOGGER.info ("parameter: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifyParameter(parameter.reportDelete ());
			else
			{
				report.modifyParameter (parameter.reportMofification (conMgmt, parameter, (SBMLParameter) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyParameter (parameter.reportInsert ());
		}
		

		LOGGER.info ("searching for events in A");
		Vector<SBMLEvent> events = modelA.getEvents ();
		for (SBMLEvent event : events)
		{
			DocumentNode dn = event.getDocumentNode ();
			LOGGER.info ("event: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifyEvents (event.reportDelete ());
			else
			{
				report.modifyEvents (event.reportMofification (conMgmt, event, (SBMLEvent) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyEvents (event.reportInsert ());
		}
		

		LOGGER.info ("searching for species in A");
		HashMap<String, SBMLSpecies> species = modelA.getSpecies();
		for (SBMLSpecies spec : species.values ())
		{
			DocumentNode dn = spec.getDocumentNode ();
			LOGGER.info ("species: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifySpecies(spec.reportDelete ());
			else
			{
				report.modifySpecies (spec.reportMofification (conMgmt, spec, (SBMLSpecies) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifySpecies (spec.reportInsert ());
		}
		

		LOGGER.info ("searching for speciestypes in A");
		HashMap<String, SBMLSpeciesType> speciestypes = modelA.getSpeciesTypes();
		for (SBMLSpeciesType spec : speciestypes.values ())
		{
			DocumentNode dn = spec.getDocumentNode ();
			LOGGER.info ("speciestype: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifySpeciesTypes(spec.reportDelete ());
			else
			{
				report.modifySpeciesTypes (spec.reportMofification (conMgmt, spec, (SBMLSpeciesType) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifySpeciesTypes (spec.reportInsert ());
		}
		

		LOGGER.info ("searching for reactions in A");
		HashMap<String, SBMLReaction> reactions = modelA.getReactions();
		for (SBMLReaction reaction : reactions.values ())
		{
			DocumentNode dn = reaction.getDocumentNode ();
			LOGGER.info ("reaction: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifyReaction(reaction.reportDelete ());
			else
			{
				report.modifyReaction (reaction.reportMofification (conMgmt, reaction, (SBMLReaction) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyReaction(reaction.reportInsert ());
		}
		

		LOGGER.info ("searching for functions in A");
		HashMap<String, SBMLFunctionDefinition> functions = modelA.getFunctionDefinitions();
		for (SBMLFunctionDefinition function : functions.values ())
		{
			DocumentNode dn = function.getDocumentNode ();
			LOGGER.info ("function: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifyFunctions(function.reportDelete ());
			else
			{
				report.modifyFunctions (function.reportMofification (conMgmt, function, (SBMLFunctionDefinition) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyFunctions(function.reportInsert ());
		}
		

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
				report.modifyUnits(unit.reportDelete ());
			else
			{
				report.modifyUnits(unit.reportMofification (conMgmt, unit, (SBMLUnitDefinition) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyUnits(unit.reportInsert ());
		}
		

		
		LOGGER.info ("searching for initial assignments in A");
		Vector<SBMLInitialAssignment> initAss = modelA.getInitialAssignments ();
		for (SBMLInitialAssignment ia : initAss)
		{
			DocumentNode dn = ia.getDocumentNode ();
			LOGGER.info ("init. ass.: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifyInitialAssignments (ia.reportDelete ());
			else
			{
				report.modifyInitialAssignments (ia.reportMofification (conMgmt, ia, (SBMLInitialAssignment) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyInitialAssignments(ia.reportInsert ());
		}
		
		LOGGER.info ("searching for constraints in A");
		Vector<SBMLConstraint> constraints = modelA.getConstraints ();
		for (SBMLConstraint constraint : constraints)
		{
			DocumentNode dn = constraint.getDocumentNode ();
			LOGGER.info ("constraint: " + dn.getXPath ());

			Connection con = conMgmt.getConnectionForNode (dn);
			
			if (con == null)
				report.modifyContraints (constraint.reportDelete ());
			else
			{
				report.modifyContraints (constraint.reportMofification (conMgmt, constraint, (SBMLConstraint) modelB.getFromNode (con.getPartnerOf (dn))));
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
				report.modifyContraints (constraint.reportInsert ());
		}
		
		return ;
	}
}
