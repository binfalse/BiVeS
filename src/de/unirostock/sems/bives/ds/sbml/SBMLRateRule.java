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
public class SBMLRateRule
	extends SBMLRule
{
	private SBMLSBase variable;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLRateRule (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		type = SBMLRule.RATE_RULE;
		if (documentNode.getAttribute ("variable") == null)
			throw new BivesSBMLParseException ("rate rule doesn't define variable");
		variable = resolvVariable (documentNode.getAttribute ("variable"));
		if (variable == null)
			throw new BivesSBMLParseException ("cannot map varibale in rate rule: " + documentNode.getAttribute ("variable"));
	}
	
	public SBMLSBase getVariable ()
	{
		return variable;
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLRateRule a = (SBMLRateRule) docA;
		SBMLRateRule b = (SBMLRateRule) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";

		String idA = SBMLModel.getSidName (a.variable), idB = SBMLModel.getSidName (b.variable);
		String ret = "<tr><td>RateRule for ";
		if (idA.equals (idB))
			ret += idA;
		else
			ret += "<span class='"+CLASS_DELETED+"'>" + idA + "</span> &rarr; <span class='"+CLASS_DELETED+"'>" + idB + "</span> ";
		ret += "</td><td>";
		
		ret += Tools.genMathHtmlStats (a.math.getMath (), b.math.getMath ());
		
		ret += Tools.genAttributeHtmlStats (a.documentNode, b.documentNode);
		
		return ret + "</td></tr>";
	}
	
	@Override
	public String reportInsert ()
	{
		return "<tr><td><span class='"+CLASS_INSERTED+"'>RateRule for "+SBMLModel.getSidName (variable)+"</span></td><td><span class='"+CLASS_INSERTED+"'>inserted</span></td></tr>";
	}
	
	@Override
	public String reportDelete ()
	{
		return "<tr><td><span class='"+CLASS_DELETED+"'>RateRule for "+SBMLModel.getSidName (variable)+"</span></td><td><span class='"+CLASS_DELETED+"'>deleted</span></td></tr>";
	}
	
}
