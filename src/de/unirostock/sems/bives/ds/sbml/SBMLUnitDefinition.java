/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;


/**
 * @author Martin Scharm
 *
 */
public class SBMLUnitDefinition
	extends SBMLSbase
{
	private boolean baseUnit; // is this a base unit?
	private String id;
	private String name; //optional
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
		

		Vector<TreeNode> lounits = documentNode.getChildrenWithTag ("listOfUnits");
		for (int i = 0; i < lounits.size (); i++)
		{
			DocumentNode lounit = (DocumentNode) lounits.elementAt (i);
			
			Vector<TreeNode> unit = lounit.getChildrenWithTag ("functionDefinition");
			for (int j = 0; j < unit.size (); j++)
			{
				SBMLUnit u = new SBMLUnit ((DocumentNode) unit.elementAt (j), sbmlModel);
				listOfUnits.add (u);
			}
		}
		
		if (listOfUnits.size () < 1)
			throw new BivesSBMLParseException ("UnitDefinition "+id+" has "+listOfUnits.size ()+" units. (expected at least one unit)");
	}
	
	public String getID ()
	{
		return id;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public boolean isBaseUnit ()
	{
		return baseUnit;
	}
}
