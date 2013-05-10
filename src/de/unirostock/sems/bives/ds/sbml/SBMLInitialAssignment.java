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
public class SBMLInitialAssignment
	extends SBMLSbase
{
	private SBMLSbase symbol;
	private SBMLMathML math;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLInitialAssignment (DocumentNode documentNode,
		SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		String tmp = documentNode.getAttribute ("symbol");
		symbol = sbmlModel.getCompartment (tmp);
		if (symbol == null)
			symbol = sbmlModel.getSpecies (tmp);
		if (symbol == null)
			symbol = sbmlModel.getParameter (tmp);
		if (symbol == null)
			symbol = sbmlModel.getSpeciesReference (tmp);
		if (symbol == null)
			throw new BivesSBMLParseException ("symbol "+tmp+" of initial assignment unmappable.");
		

		Vector<TreeNode> maths = documentNode.getChildrenWithTag ("math");
		if (maths.size () != 1)
			throw new BivesSBMLParseException ("initial assignment has "+maths.size ()+" math elements. (expected exactly one element)");
		math = new SBMLMathML ((DocumentNode) maths.elementAt (0));
	}
	
	public SBMLSbase getSymbol ()
	{
		return symbol;
	}
	
	public SBMLMathML getMath ()
	{
		return math;
	}
}
