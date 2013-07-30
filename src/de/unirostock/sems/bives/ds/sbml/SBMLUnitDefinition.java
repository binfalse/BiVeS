/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.markup.Markup;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLUnitDefinition
	extends SBMLGenericIdNameObject
	implements DiffReporter, Markup
{
	private boolean baseUnit; // is this a base unit?
	private Vector<SBMLUnit> listOfUnits;
	
	/**
	 * Instantiates a new SBML base unit.
	 * 
	 * @param name the name of the unit
	 * @throws BivesSBMLParseException 
	 */
	public SBMLUnitDefinition (String name, SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (null, sbmlModel);
		id = name;
		this.name = name;
		baseUnit = true;
	}
	
	/**
	 * @param documentNode
	 * @throws BivesSBMLParseException 
	 * @throws BivesConsistencyException 
	 */
	public SBMLUnitDefinition (DocumentNode documentNode, SBMLModel sbmlModel) throws BivesSBMLParseException, BivesConsistencyException
	{
		super (documentNode, sbmlModel);
		baseUnit = false;
		
		listOfUnits = new Vector<SBMLUnit> ();
		Vector<TreeNode> lounits = documentNode.getChildrenWithTag ("listOfUnits");
		for (int i = 0; i < lounits.size (); i++)
		{
			DocumentNode lounit = (DocumentNode) lounits.elementAt (i);
			
			Vector<TreeNode> unit = lounit.getChildrenWithTag ("unit");
			for (int j = 0; j < unit.size (); j++)
			{
				SBMLUnit u = new SBMLUnit ((DocumentNode) unit.elementAt (j), sbmlModel);
				listOfUnits.add (u);
			}
		}
		
		if (listOfUnits.size () < 1)
			throw new BivesSBMLParseException ("UnitDefinition "+id+" has "+listOfUnits.size ()+" units. (expected at least one unit)");
	}
	
	public boolean isBaseUnit ()
	{
		return baseUnit;
	}

	@Override
	public MarkupElement reportMofification (ClearConnectionManager conMgmt, DiffReporter docA, DiffReporter docB, MarkupDocument markupDocument)
	{
		SBMLUnitDefinition a = (SBMLUnitDefinition) docA;
		SBMLUnitDefinition b = (SBMLUnitDefinition) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return null;
		
		String idA = a.getNameAndId (), idB = b.getNameAndId ();
		MarkupElement me = null;
		if (idA.equals (idB))
			me = new MarkupElement (idA);
		else
			me = new MarkupElement (markupDocument.delete (idA) + " "+markupDocument.rightArrow ()+" " + markupDocument.insert (idB));
		
		// check whether unit definition has changed
		String oldDef = a.markup (markupDocument);
		String newDef = a.markup (markupDocument);
		if (oldDef.equals (newDef))
			me.addValue ("Defined by: " + oldDef);
		else
			me.addValue ("Definition changed from " + markupDocument.delete (oldDef) + " to " + markupDocument.insert (newDef));
		
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

	@Override
	public String markup (MarkupDocument markupDocument)
	{
		String ret = "";
		for (int i = 0; i < listOfUnits.size (); i++)
		{
			ret += listOfUnits.elementAt (i).markup (markupDocument);//.unitToHTMLString ();
			if (i+1 < listOfUnits.size ())
				ret += " "+markupDocument.multiply ()+" ";
		}
		return ret;
	}
}
