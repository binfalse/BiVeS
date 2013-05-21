/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLEventTrigger
	extends SBMLSBase
	implements SBMLDiffReporter
{
	private SBMLMathML math;
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
		math = new SBMLMathML ((DocumentNode) maths.elementAt (0));
		

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
	
	public SBMLMathML getMath ()
	{
		return math;
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLEventTrigger a = (SBMLEventTrigger) docA;
		SBMLEventTrigger b = (SBMLEventTrigger) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";
		
		String ret =  Tools.genAttributeHtmlStats (a.documentNode, b.documentNode);

		if (a.math != null && b.math != null)
			ret += Tools.genMathHtmlStats (a.math.getMath (), b.math.getMath ());
		else if (a.math != null)
			ret += Tools.genMathHtmlStats (a.math.getMath (), null);
		else if (b.math != null)
			ret += Tools.genMathHtmlStats (null, b.math.getMath ());
		
		return ret;
	}

	@Override
	public String reportInsert ()
	{
		return Tools.genMathHtmlStats (null, math.getMath ());
	}

	@Override
	public String reportDelete ()
	{
		return Tools.genMathHtmlStats (math.getMath (), null);
	}
	
}
