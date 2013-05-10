/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;


/**
 * @author Martin Scharm
 *
 */
public class SBMLEvent
	extends SBMLSbase
{
	private String id; //optional
	private String name; //optional
	private boolean useValuesFromTriggerTime;
	private SBMLEventTrigger trigger;
	private SBMLEventPriority priority;
	private SBMLEventDelay delay;
	private Vector<SBMLEventAssignment> listOfEventAssignments;
	
	
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
	
}
