/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.ds.DiffReporter;
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
public class SBMLConstraint
	extends SBMLSBase
	implements DiffReporter
{
	private MathML math;
	private SBMLXHTML message;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLConstraint (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);

		Vector<TreeNode> nodes = documentNode.getChildrenWithTag ("math");
		if (nodes.size () != 1)
			throw new BivesSBMLParseException ("constraint has "+nodes.size ()+" math elements. (expected exactly one element)");
		math = new MathML ((DocumentNode) nodes.elementAt (0));

		nodes = documentNode.getChildrenWithTag ("message");
		if (nodes.size () > 1)
			throw new BivesSBMLParseException ("constraint has "+nodes.size ()+" message elements. (expected not more than one element)");
		if (nodes.size () == 1)
		{
			message = new SBMLXHTML ();
			DocumentNode root = (DocumentNode) nodes.elementAt (0);
			Vector<TreeNode> kids = root.getChildren ();
			for (TreeNode n : kids)
				message.addXHTML (n);
		}
		
	}
	
	public MathML getMath ()
	{
		return math;
	}
	
	public SBMLXHTML getMessage ()
	{
		return message;
	}

	@Override
	public MarkupElement reportMofification (ClearConnectionManager conMgmt, DiffReporter docA, DiffReporter docB, MarkupDocument markupDocument)
	{
		SBMLConstraint a = (SBMLConstraint) docA;
		SBMLConstraint b = (SBMLConstraint) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return null;
		
		MarkupElement me = new MarkupElement ("-");
		
		Tools.genMathHtmlStats (a.math.getDocumentNode (), b.math.getDocumentNode (), me, markupDocument);
		
		Tools.genAttributeHtmlStats (a.documentNode, b.documentNode, me, markupDocument);
		
		if (a.message != null && b.message != null)
		{
			String msgA = a.message.toString ();
			String msgB = b.message.toString ();
			
			if (!msgA.equals (msgB))
				me.addValue ("message changed from: " + markupDocument.delete (msgA) + " to " + markupDocument.insert (msgB));
		}
		else if (a.message != null)
			me.addValue ("message deleted: " + markupDocument.delete (a.message.toString ()));
		else if (b.message != null)
			me.addValue ("message inserted: " + markupDocument.insert (b.message.toString ()));
		return me;
	}
	
	@Override
	public MarkupElement reportInsert (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement (markupDocument.insert ("-"));
		me.addValue (markupDocument.insert ("inserted"));
		return me;
	}
	
	@Override
	public MarkupElement reportDelete (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement (markupDocument.delete ("-"));
		me.addValue (markupDocument.delete ("deleted"));
		return me;
	}
	
}
