/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.markup.Markup;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLParameter
	extends SBMLGenericIdNameObject
	implements DiffReporter, Markup
{
	private Double value; //optional
	private SBMLUnitDefinition units; //optional
	private boolean constant; //optional
	
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLParameter (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		

		if (documentNode.getAttribute ("value") != null)
		{
			try
			{
				value = Double.parseDouble (documentNode.getAttribute ("value"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("value of parameter "+id+" of unexpected format: " + documentNode.getAttribute ("value"));
			}
		}
		
		if (documentNode.getAttribute ("units") != null)
		{
			String tmp = documentNode.getAttribute ("units");
			units = sbmlModel.getUnitDefinition (tmp);
			if (units == null)
				throw new BivesSBMLParseException ("units attribute in parameter "+id+" not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("constant") != null)
		{
			try
			{
				constant = Boolean.parseBoolean (documentNode.getAttribute ("constant"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("constant of parameter "+id+" of unexpected format: " + documentNode.getAttribute ("constant"));
			}
		}
		else
			constant = true; // level <= 2
		
	}

	public double getValue ()
	{
		return value;
	}
	
	public boolean isConstant ()
	{
		return constant;
	}
	
	public String markup (MarkupDocument markupDocument)
	{
		return getNameAndId () + "=" + value + " " + units.getName () + (constant ? " [const]" : "");
	}

	@Override
	public MarkupElement reportMofification (ClearConnectionManager conMgmt, DiffReporter docA, DiffReporter docB, MarkupDocument markupDocument)
	{
		SBMLParameter a = (SBMLParameter) docA;
		SBMLParameter b = (SBMLParameter) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return null;
		
		String idA = a.getNameAndId (), idB = b.getNameAndId ();
		MarkupElement me = null;
		if (idA.equals (idB))
			me = new MarkupElement (idA);
		else
			me = new MarkupElement (markupDocument.delete (idA) + " "+markupDocument.rightArrow ()+" " + markupDocument.insert (idB));
		
		Tools.genAttributeHtmlStats (a.documentNode, b.documentNode, me, markupDocument);
		
		return me;
	}
	
	@Override
	public MarkupElement reportInsert (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement (markupDocument.insert (getNameAndId ()));
		me.addValue (markupDocument.insert ("inserted"));
		return me;
	}
	
	@Override
	public MarkupElement reportDelete (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement (markupDocument.delete (getNameAndId ()));
		me.addValue (markupDocument.delete ("deleted"));
		return me;
	}
}
