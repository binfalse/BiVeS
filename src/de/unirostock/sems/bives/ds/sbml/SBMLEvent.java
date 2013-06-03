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
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
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
		
		// tigger optional w/ L3V2
		Vector<TreeNode> nodes = documentNode.getChildrenWithTag ("trigger");
		if (nodes.size () == 1)
			//throw new BivesSBMLParseException ("event has "+nodes.size ()+" trigger elements. (expected exactly one element)");
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
	public MarkupElement reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB, MarkupDocument markupDocument)
	{
		SBMLEvent a = (SBMLEvent) docA;
		SBMLEvent b = (SBMLEvent) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return null;
		
		String idA = a.getNameAndId (), idB = b.getNameAndId ();
		MarkupElement me = null;
		if (idA.equals (idB))
			me = new MarkupElement (idA);
		else
			me = new MarkupElement (markupDocument.delete (idA) + " "+markupDocument.rightArrow ()+" " + markupDocument.insert (idB));
		
		Tools.genAttributeHtmlStats (a.documentNode, b.documentNode, me, markupDocument);
		
		// trigger -> not optional!
		// changed in L3V2
		MarkupElement me2 = new MarkupElement ("Trigger");
		if (a.trigger != null && b.trigger != null)
		{
			a.trigger.reportMofification (conMgmt, a.trigger, b.trigger, me2, markupDocument);
			me.addSubElements (me2);
		}
		else if (a.trigger != null)
		{
			a.trigger.reportDelete (me2, markupDocument);
			me.addSubElements (me2);
		}
		else if (b.trigger != null)
		{
			b.trigger.reportInsert (me2, markupDocument);
			me.addSubElements (me2);
		}
		
		// priority
		me2 = new MarkupElement ("Priority");
		if (a.priority != null && b.priority != null)
		{
			a.priority.reportMofification (conMgmt, a.priority, b.priority, me2, markupDocument);
			me.addSubElements (me2);
		}
		else if (a.priority != null)
		{
			a.priority.reportDelete (me2, markupDocument);
			me.addSubElements (me2);
		}
		else if (b.priority != null)
		{
			b.priority.reportInsert (me2, markupDocument);
			me.addSubElements (me2);
		}
		
		// delay
		me2 = new MarkupElement ("Delay");
		if (a.delay != null && b.delay != null)
		{
			a.delay.reportMofification (conMgmt, a.delay, b.delay, me2, markupDocument);
			me.addSubElements (me2);
		}
		else if (a.priority != null)
		{
			a.delay.reportDelete (me2, markupDocument);
			me.addSubElements (me2);
		}
		else if (b.priority != null)
		{
			b.delay.reportInsert (me2, markupDocument);
			me.addSubElements (me2);
		}
		
		// assignments
		Vector<SBMLEventAssignment> assA = a.listOfEventAssignments;
		Vector<SBMLEventAssignment> assB = b.listOfEventAssignments;
		if (assA.size () > 0 || assB.size () > 0)
		{
			me2 = new MarkupElement ("Assignments");
			for (SBMLEventAssignment ass : assA)
			{
				if (conMgmt.getConnectionForNode (ass.documentNode) == null)
					ass.reportDelete (me2, markupDocument);
				else
				{
					Connection con = conMgmt.getConnectionForNode (ass.documentNode);
					SBMLEventAssignment partner = (SBMLEventAssignment) b.sbmlModel.getFromNode (con.getPartnerOf (ass.documentNode));
					ass.reportMofification (conMgmt, ass, partner, me2, markupDocument);
				}
			}
			for (SBMLEventAssignment ass : assB)
			{
				if (conMgmt.getConnectionForNode (ass.documentNode) == null)
					ass.reportInsert (me2, markupDocument);
			}
		}
		
		return me;
	}

	@Override
	public MarkupElement reportInsert (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement (markupDocument.insert (getNameAndId ()));
		me.addValue (markupDocument.insert ("inserted"));
		return me;
	}

	@Override
	public MarkupElement reportDelete (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement (markupDocument.delete (getNameAndId ()));
		me.addValue (markupDocument.delete ("deleted"));
		return me;
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
