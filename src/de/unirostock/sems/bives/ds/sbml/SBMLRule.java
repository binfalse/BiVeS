/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public abstract class SBMLRule
	extends SBMLSBase
	implements SBMLDiffReporter
{
	public static final int ASSIGNMENT_RULE = 1;
	public static final int ALGEBRAIC_RULE = 2;
	public static final int RATE_RULE = 3;
	protected MathML math;
	protected int type;
	
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
		math = new MathML ((DocumentNode) maths.elementAt (0));
	}
	
	public MathML getMath ()
	{
		return math;
	}
	
	protected final SBMLSBase resolvVariable (String ref) throws BivesSBMLParseException
	{
		SBMLSBase var = sbmlModel.getCompartment (ref);
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
	
	public int getRuleType ()
	{
		return type;
	}
}
