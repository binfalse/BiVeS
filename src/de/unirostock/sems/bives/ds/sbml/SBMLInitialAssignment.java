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
public class SBMLInitialAssignment
	extends SBMLSBase
	implements SBMLDiffReporter
{
	private SBMLSBase symbol;
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
	
	public SBMLSBase getSymbol ()
	{
		return symbol;
	}
	
	public SBMLMathML getMath ()
	{
		return math;
	}
	
	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLInitialAssignment a = (SBMLInitialAssignment) docA;
		SBMLInitialAssignment b = (SBMLInitialAssignment) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";
		
		String idA = SBMLModel.getSidName (a.symbol), idB = SBMLModel.getSidName (b.symbol);
		
		String ret = "<tr><td>";
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
		return "<tr><td><span class='"+CLASS_INSERTED+"'>" + SBMLModel.getSidName (symbol) + "</span></td><td><span class='"+CLASS_INSERTED+"'>inserted</span></td></tr>";
	}
	
	@Override
	public String reportDelete ()
	{
		return "<tr><td><span class='"+CLASS_DELETED+"'>" + SBMLModel.getSidName (symbol) + "</span></td><td><span class='"+CLASS_DELETED+"'>deleted</span></td></tr>";
	}
}
