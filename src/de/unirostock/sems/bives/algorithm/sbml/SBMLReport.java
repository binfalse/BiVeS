/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import de.unirostock.sems.bives.algorithm.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.tools.Tools;



/**
 * @author Martin Scharm
 * 
 */
public class SBMLReport
{
	public static class ModConnection
	{
		public DocumentNode alt, neu;
		public ModConnection (DocumentNode alt, DocumentNode neu)
		{
			this.alt = alt;
			this.neu = neu;
		}
	}
	
	private String header;
	
	private Vector<DocumentNode>	insertedSpecies;
	private Vector<DocumentNode>	deletedSpecies;
	private Vector<ModConnection>	modifiedSpecies;
	
	private Vector<DocumentNode>	insertedParameter;
	private Vector<DocumentNode>	deletedParameter;
	private Vector<ModConnection>	modifiedParameter;
	
	private Vector<DiffReporter>	insertedReactions;
	private Vector<DiffReporter>	deletedReactions;
	private Vector<DiffReporter>	modifiedReactions;
	
	private Vector<DocumentNode>	insertedCompartments;
	private Vector<DocumentNode>	deletedCompartments;
	private Vector<ModConnection>	modifiedCompartments;
	
	private Vector<DiffReporter>	insertedRules;
	private Vector<DiffReporter>	deletedRules;
	private Vector<DiffReporter>	modifiedRules;
	
	private Vector<DiffReporter>	insertedFunctions;
	private Vector<DiffReporter>	deletedFunctions;
	private Vector<DiffReporter>	modifiedFunctions;
	
	private Vector<DocumentNode>	insertedConstraints;
	private Vector<DocumentNode>	deletedConstraints;
	private Vector<ModConnection>	modifiedConstraints;
	
	private Vector<DiffReporter>	insertedEvents;
	private Vector<DiffReporter>	deletedEvents;
	private Vector<DiffReporter>	modifiedEvents;
	
	
	public SBMLReport ()
	{
		header = "";
		insertedSpecies = new Vector<DocumentNode> ();
		deletedSpecies = new Vector<DocumentNode> ();
		modifiedSpecies = new Vector<ModConnection> ();
		
		insertedParameter = new Vector<DocumentNode> ();
		deletedParameter = new Vector<DocumentNode> ();
		modifiedParameter = new Vector<ModConnection> ();
		
		insertedReactions = new Vector<DiffReporter> ();
		deletedReactions = new Vector<DiffReporter> ();
		modifiedReactions = new Vector<DiffReporter> ();
		
		insertedCompartments = new Vector<DocumentNode> ();
		deletedCompartments = new Vector<DocumentNode> ();
		modifiedCompartments = new Vector<ModConnection> ();
		
		insertedRules = new Vector<DiffReporter> ();
		deletedRules = new Vector<DiffReporter> ();
		modifiedRules = new Vector<DiffReporter> ();
		
		insertedConstraints = new Vector<DocumentNode> ();
		deletedConstraints = new Vector<DocumentNode> ();
		modifiedConstraints = new Vector<ModConnection> ();
		
		insertedFunctions = new Vector<DiffReporter> ();
		deletedFunctions = new Vector<DiffReporter> ();
		modifiedFunctions = new Vector<DiffReporter> ();
		
		insertedEvents = new Vector<DiffReporter> ();
		deletedEvents = new Vector<DiffReporter> ();
		modifiedEvents = new Vector<DiffReporter> ();
	}
	
	public String generateHTMLReport ()
	{
		String report = "<h2>Diff Report</h2><p>Please keep in mind that these modifications are choosen by what we think is important!</p><p>"+header+"</p>";
		
		// species
		report = generateSimpleElementHTMLReport (report, deletedSpecies, insertedSpecies, modifiedSpecies, "Following Species were modified", "species");
		// parameter
		report = generateSimpleElementHTMLReport (report, deletedParameter, insertedParameter, modifiedParameter, "Following Parameters were modified", "parameter");
		// compartments
		report = generateSimpleElementHTMLReport (report, deletedCompartments, insertedCompartments, modifiedCompartments, "Following Compartments were modified", "compartment");

		// complex: reactions
		report = generateComplexElementHTMLReport (report, deletedReactions, insertedReactions, modifiedReactions, "Following Reactions were modified", "reaction");
		// complex: rules
		report = generateComplexElementHTMLReport (report, deletedRules, insertedRules, modifiedRules, "Following Rules were modified", "rule");
		// complex: rules
		report = generateComplexElementHTMLReport (report, deletedFunctions, insertedFunctions, modifiedFunctions, "Following Functions were modified", "function");
		
		// complex: event no idea yet -> TODO
		
		return report;
	}
	
	public void addHeader (String header)
	{
		this.header += header;
	}
	
	private String generateComplexElementHTMLReport (String rep, Vector<DiffReporter> del, Vector<DiffReporter> ins, Vector<DiffReporter> mods, String headline, String cssclass)
	{
		if (del.size () > 0 || ins.size () > 0 || mods.size () > 0)
		{
			rep += "<h3 class='"+cssclass+"'>"+headline+"</h3><table class='"+cssclass+"'>" +
				"<thead><th>ID (name)</th><th>Modification</th></thead>";
			
			for (DiffReporter deleted : del)
			{
				rep += deleted.reportHTML (cssclass);
			}
			for (DiffReporter modified : mods)
			{
				rep += modified.reportHTML (cssclass);
			}
			for (DiffReporter inserted : ins)
			{
				rep += inserted.reportHTML (cssclass);
			}
			rep += "</table>";
		}

		return rep;
	}
	
	private String generateSimpleElementHTMLReport (String rep, Vector<DocumentNode> del, Vector<DocumentNode> ins, Vector<ModConnection> mods, String headline, String cssclass)
	{
		if (del.size () > 0 || ins.size () > 0 || mods.size () > 0)
		{
			rep += "<h3 class='"+cssclass+"'>"+headline+"</h3><table class='"+cssclass+"'>" +
				"<thead><th>ID (name)</th><th>Modification</th></thead>";
			
			for (DocumentNode deleted : del)
			{
				rep += "<tr><td class='"+cssclass+"'>" + deleted.getId () + 
					((deleted.getAttribute ("name") != null) ? " ("+deleted.getAttribute ("name")+")" : "") +
					"</td><td class='"+cssclass+"'><span class='deleted'>deleted</span>" +
						"</td></tr>";
			}
			for (ModConnection modified : mods)
			{
				rep += "<tr><td class='"+cssclass+"'>" + modified.alt.getId () +
			((modified.alt.getAttribute ("name") != null) ? " ("+modified.alt.getAttribute ("name")+")" : "") +
			"</td><td class='"+cssclass+"'>";
				
				rep += Tools.genAttributeHtmlStats (modified.alt, modified.neu);
						rep += "</td></tr>";
			}
			for (DocumentNode inserted : ins)
			{
				rep += "<tr><td class='"+cssclass+"'>" + inserted.getId () +
			((inserted.getAttribute ("name") != null) ? " ("+inserted.getAttribute ("name")+")" : "") +
			"</td><td class='"+cssclass+"'><span class='inserted'>inserted</span>" +
						"</td></tr>";
			}
			rep += "</table>";
		}
		
		return rep;
	}
	
	public void insertSpecies (DocumentNode node)
	{
		insertedSpecies.add (node);
	}
	
	
	public void deleteSpecies (DocumentNode node)
	{
		deletedSpecies.add (node);
	}
	
	
	public void modifySpecies (ModConnection node)
	{
		modifiedSpecies.add (node);
	}
	
	
	public void insertParameter (DocumentNode node)
	{
		insertedParameter.add (node);
	}
	
	
	public void deleteParameter (DocumentNode node)
	{
		deletedParameter.add (node);
	}
	
	
	public void modifyParameter (ModConnection node)
	{
		modifiedParameter.add (node);
	}
	
	
	public void insertReaction (CRNReaction node)
	{
		insertedReactions.add (node);
	}
	
	
	public void deleteReaction (CRNReaction node)
	{
		deletedReactions.add (node);
	}
	
	
	public void modifyReaction (CRNReaction node)
	{
		modifiedReactions.add (node);
	}
	
	
	public void insertCompartments (DocumentNode node)
	{
		insertedCompartments.add (node);
	}
	
	
	public void deleteCompartments (DocumentNode node)
	{
		deletedCompartments.add (node);
	}
	
	
	public void modifyCompartments (ModConnection node)
	{
		modifiedCompartments.add (node);
	}
	
	
	public void insertFunction (SBMLRule node)
	{
		insertedFunctions.add (node);
	}
	
	
	public void deleteFunction (SBMLRule node)
	{
		deletedFunctions.add (node);
	}
	
	
	public void modifyFunction (SBMLRule node)
	{
		modifiedFunctions.add (node);
	}
	
	
	public void insertRule (SBMLRule node)
	{
		insertedRules.add (node);
	}
	
	
	public void deleteRule (SBMLRule node)
	{
		deletedRules.add (node);
	}
	
	
	public void modifyRule (SBMLRule node)
	{
		modifiedRules.add (node);
	}
	
	
	public void insertConstraints (DocumentNode node)
	{
		insertedConstraints.add (node);
	}
	
	
	public void deleteConstraints (DocumentNode node)
	{
		deletedConstraints.add (node);
	}
	
	
	public void modifyConstraints (ModConnection node)
	{
		modifiedConstraints.add (node);
	}
	
	
	public void insertEvent (DiffReporter node)
	{
		insertedEvents.add (node);
	}
	
	
	public void deleteEvent (DiffReporter node)
	{
		deletedEvents.add (node);
	}
	
	
	public void modifyEvent (DiffReporter node)
	{
		modifiedEvents.add (node);
	}
}
