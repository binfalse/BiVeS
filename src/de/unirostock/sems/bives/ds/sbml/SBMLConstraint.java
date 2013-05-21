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
public class SBMLConstraint
	extends SBMLSBase
	implements SBMLDiffReporter
{
	private SBMLMathML math;
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
		math = new SBMLMathML ((DocumentNode) nodes.elementAt (0));

		nodes = documentNode.getChildrenWithTag ("message");
		if (nodes.size () > 1)
			throw new BivesSBMLParseException ("constraint has "+nodes.size ()+" message elements. (expected not more than one element)");
		if (nodes.size () == 1)
			message = new SBMLXHTML ((DocumentNode) nodes.elementAt (0));
		
	}
	
	public SBMLMathML getMath ()
	{
		return math;
	}
	
	public SBMLXHTML getMessage ()
	{
		return message;
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLConstraint a = (SBMLConstraint) docA;
		SBMLConstraint b = (SBMLConstraint) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";
		
		String ret = "<tr><td>-</td><td>";
		
		ret += Tools.genMathHtmlStats (a.math.getMath (), b.math.getMath ());
		
		ret += Tools.genAttributeHtmlStats (a.documentNode, b.documentNode);
		
		ret += "[change in message not implemented yet.]";
		
		return ret + "</td></tr>";
	}
	
	@Override
	public String reportInsert ()
	{
		return "<tr><td><span class='"+CLASS_INSERTED+"'>-</span></td><td><span class='"+CLASS_INSERTED+"'>inserted</span></td></tr>";
	}
	
	@Override
	public String reportDelete ()
	{
		return "<tr><td><span class='"+CLASS_DELETED+"'>-</span></td><td><span class='"+CLASS_DELETED+"'>deleted</span></td></tr>";
	}
	
}
