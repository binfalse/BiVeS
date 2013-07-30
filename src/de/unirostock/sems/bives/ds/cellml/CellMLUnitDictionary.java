/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.HashMap;

import de.unirostock.sems.bives.exception.BivesConsistencyException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLUnitDictionary
{
	private CellMLModel model;
	private HashMap<String, CellMLUnit> commonUnits;
	private HashMap<String, CellMLUserUnit> modelUnits;
	private HashMap<CellMLComponent, HashMap<String, CellMLUserUnit>> componentUnits;
	
	public CellMLUnitDictionary (CellMLModel model)
	{
		this.model = model;
		commonUnits = new HashMap<String, CellMLUnit> ();
		modelUnits = new HashMap<String, CellMLUserUnit> ();
		componentUnits = new HashMap<CellMLComponent, HashMap<String, CellMLUserUnit>> ();
		
		init ();
	}
	
	public CellMLUnit getUnit (String name, CellMLComponent c)
	{
		HashMap<String, CellMLUserUnit> cu = componentUnits.get (c);
		if (cu != null)
		{
			CellMLUserUnit u = cu.get (name);
			if (u != null)
				return u;
		}

		CellMLUnit u = modelUnits.get (name);
		if (u != null)
			return u;

		u = commonUnits.get (name);
		if (u != null)
			return u;
		
		return null;
	}
	
	public HashMap<String, CellMLUserUnit> getComponetUnits (CellMLComponent component)
	{
		return componentUnits.get (component);
	}
	
	public HashMap<String, CellMLUserUnit> getModelUnits ()
	{
		return modelUnits;
	}
	
	public void addUnit (CellMLComponent c, CellMLUserUnit u) throws BivesConsistencyException
	{
		if (commonUnits.get (u.getName ()) != null)
			throw new BivesConsistencyException ("not allowed to overwrite unit: " + u.getName ());
		
		if (c == null)
		{
			if (modelUnits.get (u.getName ()) != null)
				throw new BivesConsistencyException ("unit name is not unique: " + u.getName ());
			modelUnits.put (u.getName (), u);
		}
		else
		{
			HashMap<String, CellMLUserUnit> cu = componentUnits.get (c);
			if (cu == null)
			{
				cu = new HashMap<String, CellMLUserUnit> ();
				componentUnits.put (c, cu);
			}
			if (cu.get (u.getName ()) != null)
				throw new BivesConsistencyException ("unit name is not unique: " + u.getName ());
			cu.put (u.getName (), u);
		}
	}
	
	private void init ()
	{
		String [] common = new String [] {"ampere", "farad", "katal", "lux", "pascal", "tesla", "becquerel", "gram", "kelvin", "meter", "radian", "volt", "candela", "gray", "kilogram", "metre", "second", "watt", "celsius", "henry", "liter", "mole", "siemens", "weber", "coulomb", "hertz", "litre", "newton", "sievert", "dimensionless", "joule", "lumen", "ohm", "steradian"};
		for (String c : common)
			commonUnits.put (c, CellMLUnit.createStandardUnit (c));
	}
	
	public void debug (String prefix)
	{
		System.out.println (prefix + "common units");
		for (CellMLUnit u : commonUnits.values ())
			u.debug (prefix + "  ");
		
		System.out.println (prefix + "model units");
		for (CellMLUnit u : modelUnits.values ())
			u.debug (prefix + "  ");
		
		for (CellMLComponent c : componentUnits.keySet ())
		{
			System.out.println (prefix + "component units: " + c.getName ());
			HashMap<String, CellMLUserUnit> m = componentUnits.get (c);
			for (CellMLUnit u : m.values ())
				u.debug (prefix + "  ");
		}
	}
}
