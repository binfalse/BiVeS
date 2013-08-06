/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;


/**
 * @author Martin Scharm
 *
 */
public class SBMLDiffReport
{
	private String header;
	
	private Vector<String>	modifiedSpecies;
	private Vector<String>	modifiedSpeciesTypes;
	private Vector<String>	modifiedParameter;
	private Vector<String>	modifiedReactions;
	private Vector<String>	modifiedCompartments;
	private Vector<String>	modifiedCompartmentTypes;
	private Vector<String>	modifiedRules;
	private Vector<String>	modifiedFunctions;
	private Vector<String>	modifiedConstraints;
	private Vector<String>	modifiedEvents;
	private Vector<String>	modifiedInitialAssignments;
	private Vector<String>	modifiedUnits;
	
	public SBMLDiffReport ()
	{
		header = "";
		modifiedSpecies = new Vector<String> ();
		modifiedSpeciesTypes = new Vector<String> ();
		modifiedParameter = new Vector<String> ();
		modifiedReactions = new Vector<String> ();
		modifiedCompartments = new Vector<String> ();
		modifiedCompartmentTypes = new Vector<String> ();
		modifiedRules = new Vector<String> ();
		modifiedFunctions = new Vector<String> ();
		modifiedConstraints = new Vector<String> ();
		modifiedEvents = new Vector<String> ();
		modifiedInitialAssignments = new Vector<String> ();
		modifiedUnits = new Vector<String> ();
	}
	
	public String generateHTMLReport ()
	{
		String report = "<h2>Diff Report</h2><p>Please keep in mind that these modifications are choosen by what we think is important!</p><p>"+header+"</p>";
		
		report += generateTable (modifiedSpecies, "Following Species were modified", "species");
		report += generateTable (modifiedSpeciesTypes, "Following SpeciesTypes were modified", "speciestypes");
		report += generateTable (modifiedParameter, "Following Parameter were modified", "parameter");
		report += generateTable (modifiedReactions, "Following Reactions were modified", "reaction");
		report += generateTable (modifiedCompartments, "Following Compartments were modified", "compartment");
		report += generateTable (modifiedCompartmentTypes, "Following CompartmentTypes were modified", "compartmenttype");
		report += generateTable (modifiedUnits, "Following Units were modified", "unit");
		report += generateTable (modifiedRules, "Following Rules were modified", "rule");
		report += generateTable (modifiedFunctions, "Following Functions were modified", "function");
		report += generateTable (modifiedConstraints, "Following Constraints were modified", "constraint");
		report += generateTable (modifiedEvents, "Following Events were modified", "event");
		report += generateTable (modifiedInitialAssignments, "Following InitialAssignments were modified", "initialssignment");
		
		return report;
	}
	
	private String generateTable (Vector<String> mods, String headline, String cssclass)
	{
		if (mods.size () < 1)
			return "";
		
		String rep = "";

		for (String mod: mods)
			rep += mod;
		
		if (rep.length () < 1)
			return "";
		
		return "<h3 class='"+cssclass+"'>"+headline+"</h3><table class='"+cssclass+"'>" +
			"<thead><th>ID (name)</th><th>Modification</th></thead>" + rep + "</table>";
	}
	
	public void addHeader (String header)
	{
		this.header += header;
	}
	
	public void modifySpecies (String rep)
	{
		modifiedSpecies.add (rep);
	}
	
	public void modifySpeciesTypes (String rep)
	{
		modifiedSpeciesTypes.add (rep);
	}
	
	public void modifyParameter (String rep)
	{
		modifiedParameter.add (rep);
	}
	
	public void modifyReaction (String rep)
	{
		modifiedReactions.add (rep);
	}
	
	public void modifyCompartments (String rep)
	{
		modifiedCompartments.add (rep);
	}
	
	public void modifyCompartmentTypes (String rep)
	{
		modifiedCompartmentTypes.add (rep);
	}
	
	public void modifyRules (String rep)
	{
		modifiedRules.add (rep);
	}
	
	public void modifyFunctions (String rep)
	{
		modifiedFunctions.add (rep);
	}
	
	public void modifyContraints (String rep)
	{
		modifiedConstraints.add (rep);
	}
	
	public void modifyEvents (String rep)
	{
		modifiedEvents.add (rep);
	}
	
	public void modifyInitialAssignments (String rep)
	{
		modifiedInitialAssignments.add (rep);
	}
	
	public void modifyUnits (String rep)
	{
		modifiedUnits.add (rep);
	}
}
