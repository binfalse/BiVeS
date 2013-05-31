/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLEventAssignment
	extends SBMLSBase
	implements SBMLDiffReporter
{
	private MathML math;
	private SBMLSBase variable;
	
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
		math = new MathML ((DocumentNode) maths.elementAt (0));
		
		variable = resolvVariable (documentNode.getAttribute ("variable"));
	}

	
	protected final SBMLSBase resolvVariable (String ref) throws BivesSBMLParseException
	{
		SBMLSBase var = sbmlModel.getCompartment (ref);
		if (var == null)
			var = sbmlModel.getSpecies (ref);
		if (var == null)
			var = sbmlModel.getParameter (ref);
		if (var == null)
			throw new BivesSBMLParseException ("variable "+ref+" of rule unmappable.");
		return var;
	}
	
	
	public SBMLSBase getVariable ()
	{
		return variable;
	}
	
	public MathML getMath ()
	{
		return math;
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLEventAssignment a = (SBMLEventAssignment) docA;
		SBMLEventAssignment b = (SBMLEventAssignment) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";
		
		String ret = "";
		String varA = SBMLModel.getSidName (a.variable);
		String varB = SBMLModel.getSidName (b.variable);
		if (varA.equals (varB))
			ret += "for: " + varA + " ";
		else
			ret += "was for: <span class='"+CLASS_DELETED+"'>" + varA + "</span> but now for: <span class='"+CLASS_INSERTED+"'>" + varB + "</span> ";

		ret += Tools.genMathHtmlStats (a.math.getMath (), b.math.getMath ());
		
		return ret;
	}

	@Override
	public String reportInsert ()
	{
		String ret = "<span class='"+CLASS_INSERTED+"'>" + SBMLModel.getSidName (variable) + " = ";
		try
		{
			ret += Tools.transformMathML (math.getMath ());
		}
		catch (TransformerException e)
		{
			LOGGER.error ("cannot parse math in event assignment", e);
			ret += "[math parsing err]";
		}
		return ret + "</span>";
	}

	@Override
	public String reportDelete ()
	{
		String ret = "<span class='"+CLASS_DELETED+"'>" + SBMLModel.getSidName (variable) + " = ";
		try
		{
			ret += Tools.transformMathML (math.getMath ());
		}
		catch (TransformerException e)
		{
			LOGGER.error ("cannot parse math in event assignment", e);
			ret += "[math parsing err]";
		}
		return ret + "</span>";
	}
	
}
