/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLEvent
	extends SBMLSBase
	implements SBMLDiffReporter
{
	private String id; //optional
	private String name; //optional
	private boolean useValuesFromTriggerTime;
	private SBMLEventTrigger trigger;
	private SBMLEventPriority priority; //optional
	private SBMLEventDelay delay; //optional
	private Vector<SBMLEventAssignment> listOfEventAssignments; //optional
	
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLEvent (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		id = documentNode.getAttribute ("id");
		name = documentNode.getAttribute ("name");

		if (documentNode.getAttribute ("useValuesFromTriggerTime") != null)
		{
			try
			{
				useValuesFromTriggerTime = Boolean.parseBoolean (documentNode.getAttribute ("useValuesFromTriggerTime"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("useValuesFromTriggerTime of event "+id+" of unexpected format: " + documentNode.getAttribute ("useValuesFromTriggerTime"));
			}
		}
		else
			useValuesFromTriggerTime = true; // level <= 2
		

		Vector<TreeNode> nodes = documentNode.getChildrenWithTag ("trigger");
		if (nodes.size () != 1)
			throw new BivesSBMLParseException ("event has "+nodes.size ()+" trigger elements. (expected exactly one element)");
		trigger = new SBMLEventTrigger ((DocumentNode) nodes.elementAt (0), sbmlModel);
		
		nodes = documentNode.getChildrenWithTag ("delay");
		if (nodes.size () > 1)
			throw new BivesSBMLParseException ("event has "+nodes.size ()+" delay elements. (expected not more than one element)");
		if (nodes.size () == 1)
			delay = new SBMLEventDelay ((DocumentNode) nodes.elementAt (0), sbmlModel);
		
		nodes = documentNode.getChildrenWithTag ("priority");
		if (nodes.size () > 1)
			throw new BivesSBMLParseException ("event has "+nodes.size ()+" priority elements. (expected not more than one element)");
		if (nodes.size () == 1)
			priority = new SBMLEventPriority ((DocumentNode) nodes.elementAt (0), sbmlModel);
		
		listOfEventAssignments = new Vector<SBMLEventAssignment> ();
		nodes = documentNode.getChildrenWithTag ("listOfEventAssignments");
		if (nodes.size () < 1)
			throw new BivesSBMLParseException ("event has "+nodes.size ()+" event assignment list elements. (expected at least one element)");
		for (int i = 0; i < nodes.size (); i++)
		{
			Vector<TreeNode> ass = ((DocumentNode) nodes.elementAt (i)).getChildrenWithTag ("eventAssignment");
			if (ass.size () < 1)
				throw new BivesSBMLParseException ("event assignment list has "+ass.size ()+" event assignment elements. (expected at least one element)");
			for (int j = 0; j < ass.size (); j++)
			{
				SBMLEventAssignment ea = new SBMLEventAssignment ((DocumentNode) ass.elementAt (j), sbmlModel);
				listOfEventAssignments.add (ea);
			}
		}
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLEvent a = (SBMLEvent) docA;
		SBMLEvent b = (SBMLEvent) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";
		
		String idA = a.getNameAndId (), idB = b.getNameAndId ();
		String ret = "<tr><td>";
		if (idA.equals (idB))
			ret += idA;
		else
			ret += "<span class='"+CLASS_DELETED+"'>" + idA + "</span> &rarr; <span class='"+CLASS_INSERTED +"'>" + idB + "</span> ";
		ret += "</td><td>";
		
		ret += Tools.genAttributeHtmlStats (a.documentNode, b.documentNode);
		
		// trigger -> not optional!
		ret += "<strong>Trigger:</strong><br/>" + a.trigger.reportMofification (conMgmt, a.trigger, b.trigger);
		
		// priority
		if (a.priority != null && b.priority != null)
			ret += "<strong>Priority:</strong><br/>" + a.priority.reportMofification (conMgmt, a.priority, b.priority);
		else if (a.priority != null)
			ret += "<strong>Priority:</strong><br/>" + a.priority.reportDelete ();
		else if (b.priority != null)
			ret += "<strong>Priority:</strong><br/>" + b.priority.reportInsert ();
		
		// delay
		if (a.delay != null && b.delay != null)
			ret += "<strong>Delay:</strong><br/>" + a.delay.reportMofification (conMgmt, a.delay, b.delay);
		else if (a.priority != null)
			ret += "<strong>Delay:</strong><br/>" + a.delay.reportDelete ();
		else if (b.priority != null)
			ret += "<strong>Delay:</strong><br/>" + b.delay.reportInsert ();
		
		// assignments
		Vector<SBMLEventAssignment> assA = a.listOfEventAssignments;
		Vector<SBMLEventAssignment> assB = b.listOfEventAssignments;
		if (assA.size () > 0 || assB.size () > 0)
			ret += "<strong>Assignments:</strong><br/>";
		for (SBMLEventAssignment ass : assA)
		{
			if (conMgmt.getConnectionForNode (ass.documentNode) == null)
				ret += ass.reportDelete () + "<br/>";
			else
			{
				Connection con = conMgmt.getConnectionForNode (ass.documentNode);
				SBMLEventAssignment partner = (SBMLEventAssignment) b.sbmlModel.getFromNode (con.getPartnerOf (ass.documentNode));
				ret += ass.reportMofification (conMgmt, ass, partner) + "<br/>";
			}
		}
		for (SBMLEventAssignment ass : assB)
		{
			if (conMgmt.getConnectionForNode (ass.documentNode) == null)
				ret += ass.reportInsert () + "<br/>";
		}
		
		return ret + "</td></tr>";
	}

	@Override
	public String reportInsert ()
	{
		return "<tr><td><span class='"+CLASS_INSERTED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_INSERTED+"'>inserted</span></td></tr>";
	}

	@Override
	public String reportDelete ()
	{
		return "<tr><td><span class='"+CLASS_DELETED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_DELETED+"'>deleted</span></td></tr>";
	}
	
	private String getNameAndId ()
	{
		if (name != null && id != null)
			return id + " (" + name + ")";
		if (name != null)
			return name;
		if (id != null)
			return id;
		return "-";
	}
}
