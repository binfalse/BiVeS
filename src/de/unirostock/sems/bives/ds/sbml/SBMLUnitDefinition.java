/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.HTMLMarkup;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLUnitDefinition
	extends SBMLGenericIdNameObject
	implements SBMLDiffReporter, HTMLMarkup
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
		super (DocumentNode.getDummyNode (), sbmlModel);
		id = name;
		this.name = name;
		baseUnit = true;
	}
	
	/**
	 * @param documentNode
	 * @throws BivesSBMLParseException 
	 */
	public SBMLUnitDefinition (DocumentNode documentNode, SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		baseUnit = false;
		
		id = documentNode.getAttribute ("id");
		if (id == null || id.length () < 1)
			throw new BivesSBMLParseException ("UnitDefinition "+id+" doesn't provide a valid id.");
		
		name = documentNode.getAttribute ("name");
		
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
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLUnitDefinition a = (SBMLUnitDefinition) docA;
		SBMLUnitDefinition b = (SBMLUnitDefinition) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";
		
		String idA = a.getNameAndId (), idB = b.getNameAndId ();
		String ret = "<tr><td>";
		if (idA.equals (idB))
			ret += idA;
		else
			ret += "<span class='"+CLASS_DELETED+"'>" + idA + "</span> &rarr; <span class='"+CLASS_INSERTED+"'>" + idB + "</span> ";
		ret += "</td><td>";
		
		// check whether unit definition has changed
		String oldDef = a.htmlMarkup ();
		String newDef = a.htmlMarkup ();
		if (oldDef.equals (newDef))
			ret += "Defined by: " + oldDef + "<br/>";
		else
			ret += "Definition changed from <span class='"+CLASS_DELETED+"'>" + oldDef + "</span> to <span class='"+CLASS_INSERTED+"'>" + newDef + "</span><br/>";
		
		ret += Tools.genAttributeHtmlStats (a.documentNode, b.documentNode);
		
		return ret + "</td></tr>";
	}

	@Override
	public String reportInsert ()
	{
		return "<tr><td><span class='"+CLASS_INSERTED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_INSERTED+"'>inserted</span></td></tr>";
	}

	@Override
	public String reportDelete ()
	{
		return "<tr><td><span class='"+CLASS_DELETED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_DELETED+"'>deleted</span></td></tr>";
	}

	@Override
	public String htmlMarkup ()
	{
		String ret = "";
		for (int i = 0; i < listOfUnits.size (); i++)
		{
			ret += listOfUnits.elementAt (i).unitToHTMLString ();
			if (i+1 < listOfUnits.size ())
				ret += " &middot; ";
		}
		return ret;
	}
}
