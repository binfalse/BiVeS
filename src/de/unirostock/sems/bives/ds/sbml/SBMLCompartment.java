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
public class SBMLCompartment
	extends SBMLGenericIdNameObject
	implements SBMLDiffReporter
{
	private double spatialDimensions; //optional
	private double size; //optional
	private SBMLUnitDefinition units; //optional
	private boolean constant;
	private SBMLCompartmentType compartmentType;

	public SBMLCompartment (DocumentNode documentNode, SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		id = documentNode.getAttribute ("id");
		if (id == null || id.length () < 1)
			throw new BivesSBMLParseException ("Compartment "+id+" doesn't provide a valid id.");
		
		name = documentNode.getAttribute ("name");

		String tmp = documentNode.getAttribute ("compartmentType");
		if (tmp != null)
		{
			compartmentType = sbmlModel.getCompartmentType (tmp);
			if (compartmentType == null)
				throw new BivesSBMLParseException ("no valid compartmentType for species "+id+" defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("spatialDimensions") != null)
		{
			try
			{
				spatialDimensions = Double.parseDouble (documentNode.getAttribute ("spatialDimensions"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("spatialDimensions in compartment "+id+" of unexpected format: " + documentNode.getAttribute ("spatialDimensions"));
			}
		}
		
		if (documentNode.getAttribute ("size") != null)
		{
			try
			{
				size = Double.parseDouble (documentNode.getAttribute ("size"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("size in compartment "+id+" of unexpected format: " + documentNode.getAttribute ("size"));
			}
		}
		
		if (documentNode.getAttribute ("units") != null)
		{
			String unitStr = documentNode.getAttribute ("units");
			units = sbmlModel.getUnitDefinition (unitStr);
			
			if (units == null)
				throw new BivesSBMLParseException ("Unit attribute in compartment "+id+" not defined: " + unitStr);
		}
		
		if (documentNode.getAttribute ("constant") != null)
		{
			try
			{
				constant = Boolean.parseBoolean (documentNode.getAttribute ("constant"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("constant attr in compartment "+id+" of unexpected format: " + documentNode.getAttribute ("constant"));
			}
		}
		else
			constant = true; // level <= 2
	}

	@Override
	public MarkupElement reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB, MarkupDocument markupDocument)
	{
		SBMLCompartment a = (SBMLCompartment) docA;
		SBMLCompartment b = (SBMLCompartment) docB;
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
