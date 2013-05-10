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
public class SBMLEventTrigger
	extends SBMLSbase
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
	
}
