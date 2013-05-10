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
public abstract class SBMLRule
	extends SBMLSbase
{
	private SBMLMathML math;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLRule (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		Vector<TreeNode> maths = documentNode.getChildrenWithTag ("math");
		if (maths.size () != 1)
			throw new BivesSBMLParseException ("initial assignment has "+maths.size ()+" math elements. (expected exactly one element)");
		math = new SBMLMathML ((DocumentNode) maths.elementAt (0));
	}
	
	public SBMLMathML getMath ()
	{
		return math;
	}
	
	protected final SBMLSbase resolvVariable (String ref) throws BivesSBMLParseException
	{
		SBMLSbase var = sbmlModel.getCompartment (ref);
		if (var == null)
			var = sbmlModel.getSpecies (ref);
		if (var == null)
			var = sbmlModel.getParameter (ref);
		if (var == null)
			var = sbmlModel.getSpeciesReference (ref);
		if (var == null)
			throw new BivesSBMLParseException ("variable "+ref+" of rule unmappable.");
		return var;
	}
}
