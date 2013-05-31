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
	
	public CellMLUnit (CellMLModel model, String name, DocumentNode node)
	{
		super (node, model);
		this.name = name;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public void setName (String name)
	{
		this.name = name;
	}
	
	public String toString ()
	{
		return name;
	}
	
	public void debug (String prefix)
	{
		System.out.println (prefix + "unit: " + name);
	}
}
