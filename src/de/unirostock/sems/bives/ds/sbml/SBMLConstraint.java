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
public class SBMLConstraint
	extends SBMLSbase
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
	
}
