/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

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
public class CellMLReport
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
	
	private Vector<DiffReporter>	insertedComponents;
	private Vector<DiffReporter>	deletedComponents;
	private Vector<DiffReporter>	modifiedComponents;
	
	
	public CellMLReport ()
	{
		header = "";
		insertedComponents = new Vector<DiffReporter> ();
		deletedComponents = new Vector<DiffReporter> ();
		modifiedComponents = new Vector<DiffReporter> ();
	}
	
	public String generateHTMLReport ()
	{
		String report = "<h2>Diff Report</h2><p>Please keep in mind that these modifications are choosen by what we think is important!</p><p>"+header+"</p>";
		

		// complex: reactions
		report = generateComplexElementHTMLReport (report, deletedComponents, insertedComponents, modifiedComponents, "Following Components were modified", "reaction");
		
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
				"<thead><th>Component</th><th>Modification</th></thead>";
			
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
	
	public void insertComponent (DiffReporter node)
	{
		insertedComponents.add (node);
	}
	
	
	public void deleteComponent (DiffReporter node)
	{
		deletedComponents.add (node);
	}
	
	
	public void modifyComponent (DiffReporter node)
	{
		modifiedComponents.add (node);
	}
}
