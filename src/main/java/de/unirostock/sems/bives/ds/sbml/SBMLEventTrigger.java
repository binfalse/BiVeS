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
public class SBMLEventTrigger
	extends SBMLSBase
{
	private MathML math;
	private Boolean initialValue;
	private Boolean persistent;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLEventTrigger (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		Vector<TreeNode> maths = documentNode.getChildrenWithTag ("math");
		if (maths.size () != 1)
			throw new BivesSBMLParseException ("event trigger has "+maths.size ()+" math elements. (expected exactly one element)");
		math = new MathML ((DocumentNode) maths.elementAt (0));
		

		if (documentNode.getAttribute ("initialValue") != null)
		{
			try
			{
				initialValue = Boolean.parseBoolean (documentNode.getAttribute ("initialValue"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("initialValue of event trigger of unexpected format: " + documentNode.getAttribute ("initialValue"));
			}
		}
		else
			initialValue = null; // level <= 2
		
		if (documentNode.getAttribute ("persistent") != null)
		{
			try
			{
				persistent = Boolean.parseBoolean (documentNode.getAttribute ("persistent"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("persistent of event trigger of unexpected format: " + documentNode.getAttribute ("persistent"));
			}
		}
		else
			persistent = null; // level <= 2
	}
	
	public MathML getMath ()
	{
		return math;
	}

	public void reportMofification (ClearConnectionManager conMgmt, SBMLEventTrigger a, SBMLEventTrigger b, MarkupElement me, MarkupDocument markupDocument)
	{
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return;
		
		Tools.genAttributeHtmlStats (a.documentNode, b.documentNode, me, markupDocument);

		if (a.math != null && b.math != null)
			Tools.genMathHtmlStats (a.math.getDocumentNode (), b.math.getDocumentNode (), me, markupDocument);
		else if (a.math != null)
			Tools.genMathHtmlStats (a.math.getDocumentNode (), null, me, markupDocument);
		else if (b.math != null)
			Tools.genMathHtmlStats (null, b.math.getDocumentNode (), me, markupDocument);
		
	}

	public void reportInsert (MarkupElement me, MarkupDocument markupDocument)
	{
		Tools.genMathHtmlStats (null, math.getDocumentNode (), me, markupDocument);
	}

	public void reportDelete (MarkupElement me, MarkupDocument markupDocument)
	{
		Tools.genMathHtmlStats (math.getDocumentNode (), null, me, markupDocument);
	}
	
}
