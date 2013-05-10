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
public class SBMLEventAssignment
	extends SBMLSbase
{
	private SBMLMathML math;
	private SBMLSbase variable;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLEventAssignment (DocumentNode documentNode,
		SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		Vector<TreeNode> maths = documentNode.getChildrenWithTag ("math");
		if (maths.size () != 1)
			throw new BivesSBMLParseException ("event trigger has "+maths.size ()+" math elements. (expected exactly one element)");
		math = new SBMLMathML ((DocumentNode) maths.elementAt (0));
		
		variable = resolvVariable (documentNode.getAttribute ("variable"));
	}

	
	protected final SBMLSbase resolvVariable (String ref) throws BivesSBMLParseException
	{
		SBMLSbase var = sbmlModel.getCompartment (ref);
		if (var == null)
			var = sbmlModel.getSpecies (ref);
		if (var == null)
			var = sbmlModel.getParameter (ref);
		if (var == null)
			throw new BivesSBMLParseException ("variable "+ref+" of rule unmappable.");
		return var;
	}
	
	
	public SBMLSbase getVariable ()
	{
		return variable;
	}
	
	public SBMLMathML getMath ()
	{
		return math;
	}
	
}
