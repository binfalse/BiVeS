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
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.Tools;
import de.unirostock.sems.bives.tools.TreeTools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLEventAssignment
	extends SBMLSBase
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

	public void reportMofification (ClearConnectionManager conMgmt, SBMLEventAssignment a, SBMLEventAssignment b, MarkupElement me, MarkupDocument markupDocument)
	{
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return;
		
		String varA = SBMLModel.getSidName (a.variable);
		String varB = SBMLModel.getSidName (b.variable);
		if (varA.equals (varB))
			me.addValue ("for: " + varA);
		else
			me.addValue ("was for: " + markupDocument.delete (varA) + " but now for: " + markupDocument.insert (varB));

		Tools.genMathHtmlStats (a.math.getDocumentNode (), b.math.getDocumentNode (), me, markupDocument);
	}

	public void reportInsert (MarkupElement me, MarkupDocument markupDocument)
	{
		me.addValue (markupDocument.insert (SBMLModel.getSidName (variable) + " = " + flattenMath (math.getDocumentNode ())));
	}

	public void reportDelete (MarkupElement me, MarkupDocument markupDocument)
	{
		me.addValue (markupDocument.delete (SBMLModel.getSidName (variable) + " = " + flattenMath (math.getDocumentNode ())));
	}
	
	private String flattenMath (DocumentNode math)
	{
		try
		{
			return TreeTools.transformMathML (math);
		}
		catch (TransformerException e)
		{
			LOGGER.error ("cannot parse math in event assignment", e);
			return "[math parsing err]";
		}
		
	}
	
}
