/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLAssignmentRule
	extends SBMLRule
{
	private SBMLSBase variable;
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLAssignmentRule (DocumentNode documentNode,
		SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		type = SBMLRule.ASSIGNMENT_RULE;
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
	public MarkupElement reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB, MarkupDocument markupDocument)
	{
		SBMLAssignmentRule a = (SBMLAssignmentRule) docA;
		SBMLAssignmentRule b = (SBMLAssignmentRule) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return null;

		String idA = SBMLModel.getSidName (a.variable), idB = SBMLModel.getSidName (b.variable);
		MarkupElement me = null;
		if (idA.equals (idB))
			me = new MarkupElement ("AssignmentRule for " + idA);
		else
			me = new MarkupElement ("AssignmentRule for " + markupDocument.delete (idA)+ " &rarr; " + markupDocument.insert (idB));
		
		Tools.genAttributeHtmlStats (a.documentNode, b.documentNode, me, markupDocument);
		Tools.genMathHtmlStats (a.math.getMath (), b.math.getMath (), me, markupDocument);
		
		return me;
	}
	
	@Override
	public MarkupElement reportInsert (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement (markupDocument.insert ("AssignmentRule for "+SBMLModel.getSidName (variable)));
		me.addValue (markupDocument.insert ("inserted"));
		return me;
	}
	
	@Override
	public MarkupElement reportDelete (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement (markupDocument.delete ("AssignmentRule for "+SBMLModel.getSidName (variable)));
		me.addValue (markupDocument.delete ("deleted"));
		return me;
	}
}
