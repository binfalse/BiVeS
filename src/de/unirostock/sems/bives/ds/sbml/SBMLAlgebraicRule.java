/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLAlgebraicRule
	extends SBMLRule
{
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLAlgebraicRule (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		type = SBMLRule.ALGEBRAIC_RULE;
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLAlgebraicRule a = (SBMLAlgebraicRule) docA;
		SBMLAlgebraicRule b = (SBMLAlgebraicRule) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";

		String ret = "<tr><td>AlgebraicRule</td><td>";
		
		ret += Tools.genMathHtmlStats (a.math.getMath (), b.math.getMath ());
		
		ret += Tools.genAttributeHtmlStats (a.documentNode, b.documentNode);
		
		return ret + "</td></tr>";
	}
	
	@Override
	public String reportInsert ()
	{
		return "<tr><td><span class='"+CLASS_INSERTED+"'>AlgebraicRule</span></td><td><span class='"+CLASS_INSERTED+"'>inserted</span></td></tr>";
	}
	
	@Override
	public String reportDelete ()
	{
		return "<tr><td><span class='"+CLASS_DELETED+"'>AlgebraicRule</span></td><td><span class='"+CLASS_DELETED+"'>deleted</span></td></tr>";
	}
	
}
