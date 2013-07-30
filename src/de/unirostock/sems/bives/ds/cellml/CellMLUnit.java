/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import de.unirostock.sems.bives.ds.xml.DocumentNode;


/**
 * @author Martin Scharm
 *
 */
public class CellMLUnit
extends CellMLEntity
{
	private String name;
	private boolean standard_units;
	
	
	/**
	 * Instantiates a new CellML standard unit.
	 *
	 * @param model the model
	 * @param name the name
	 */
	public static CellMLUnit createStandardUnit (String name)
	{
		CellMLUnit u = new CellMLUnit (null, name, null);
		u.standard_units = true;
		return u;
	}
	
	protected CellMLUnit (CellMLModel model, String name, DocumentNode node)
	{
		super (node, model);
		this.name = name;
		this.standard_units = false;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public void setName (String name)
	{
		this.name = name;
		if (getDocumentNode () != null)
			getDocumentNode ().setAttribute ("name", name);
	}
	
	public String toString ()
	{
		return name;
	}
	
	public void debug (String prefix)
	{
		System.out.println (prefix + "unit: " + name);
	}
	
	public boolean isStandardUnits ()
	{
		return standard_units;
	}
}
