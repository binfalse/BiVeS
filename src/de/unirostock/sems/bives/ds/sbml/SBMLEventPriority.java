/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.MathML;
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
public class SBMLEventPriority
	extends SBMLSBase
{
	private MathML math;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLEventPriority (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		Vector<TreeNode> maths = documentNode.getChildrenWithTag ("math");
		if (maths.size () != 1)
			throw new BivesSBMLParseException ("event priority has "+maths.size ()+" math elements. (expected exactly one element)");
		math = new MathML ((DocumentNode) maths.elementAt (0));
	}
	
	public MathML getMath ()
	{
		return math;
	}

	public void reportMofification (ClearConnectionManager conMgmt, SBMLEventPriority a, SBMLEventPriority b, MarkupElement me, MarkupDocument markupDocument)
	{
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return;
		
		Tools.genAttributeHtmlStats (a.documentNode, b.documentNode, me, markupDocument);

		if (a.math != null && b.math != null)
			Tools.genMathHtmlStats (a.math.getMath (), b.math.getMath (), me, markupDocument);
		else if (a.math != null)
			Tools.genMathHtmlStats (a.math.getMath (), null, me, markupDocument);
		else if (b.math != null)
			Tools.genMathHtmlStats (null, b.math.getMath (), me, markupDocument);
	}

	public void reportInsert (MarkupElement me, MarkupDocument markupDocument)
	{
		Tools.genMathHtmlStats (null, math.getMath (), me, markupDocument);
	}

	public void reportDelete (MarkupElement me, MarkupDocument markupDocument)
	{
		Tools.genMathHtmlStats (math.getMath (), null, me, markupDocument);
	}
	
}
