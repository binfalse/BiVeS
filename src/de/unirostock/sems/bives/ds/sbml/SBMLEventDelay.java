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
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLEventDelay
	extends SBMLSBase
	implements SBMLDiffReporter
{
	private MathML math;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLEventDelay (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		Vector<TreeNode> maths = documentNode.getChildrenWithTag ("math");
		if (maths.size () != 1)
			throw new BivesSBMLParseException ("event trigger has "+maths.size ()+" math elements. (expected exactly one element)");
		math = new MathML ((DocumentNode) maths.elementAt (0));
	}
	
	public MathML getMath ()
	{
		return math;
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLEventDelay a = (SBMLEventDelay) docA;
		SBMLEventDelay b = (SBMLEventDelay) docB;
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
