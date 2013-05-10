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
public class SBMLFunctionDefinition
extends SBMLSbase
{
	private String id;
	private String name; //optional
	private SBMLMathML math;
	
	public SBMLFunctionDefinition (DocumentNode functionDefinition, SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (functionDefinition, sbmlModel);
		
		id = functionDefinition.getAttribute ("id");
		if (id == null || id.length () < 1)
			throw new BivesSBMLParseException ("FunctionDefinition "+id+" doesn't provide a valid id.");
		
		name = functionDefinition.getAttribute ("name");
		
		Vector<TreeNode> maths = functionDefinition.getChildrenWithTag ("math");
		if (maths.size () != 1)
			throw new BivesSBMLParseException ("FunctionDefinition "+id+" has "+maths.size ()+" math elements. (expected exactly one element)");
		math = new SBMLMathML ((DocumentNode) maths.elementAt (0));
	}
	
	public String getID ()
	{
		return id;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public SBMLMathML getMath ()
	{
		return math;
	}
}
