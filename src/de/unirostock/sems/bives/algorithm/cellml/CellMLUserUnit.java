/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.CellMLReadException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLUserUnit
	extends CellMLUnit
{
	// A modeller might want to define and use units for which no simple conversion to SI units exist. A good example of this is pH, which is dimensionless, but uses a log scale. Ideally, pH should not simply be defined as dimensionless because software might then attempt to map variables defined with units of pH to any other dimensionless variables.
	// CellML addresses this by allowing the model author to indicate that a units definition is a new type of base unit, the definition of which cannot be resolved into simpler subunits. This is done by defining a base_units attribute value of "yes" on the <units> element. This element must then be left empty. The base_units attribute is optional and has a default value of "no".
	public boolean base_units;
	
	private CellMLUnitDictionary dict;
	private CellMLComponent component;

	public Vector<BaseQuantity> baseQuantities;
	
	public class BaseQuantity
	{
		public CellMLUnit unit;
		public double multiplier;
		public double offset;
		public int prefix; // power of ten
		public double exponent;
		
		public BaseQuantity (CellMLUnit unit)
		{
			this.unit = unit;
			multiplier = 1;
			offset = 0;
			prefix = 0;
			exponent = 1;
		}
		
		public BaseQuantity (DocumentNode node) throws BivesConsistencyException, CellMLReadException
		{
			this.unit = dict.getUnit (node.getAttribute ("units"), component);
			multiplier = 1;
			offset = 0;
			prefix = 0;
			exponent = 1;
			if (node.getAttribute ("multiplier") != null)
			{
				multiplier = Double.parseDouble (node.getAttribute ("multiplier"));
			}
			if (node.getAttribute ("prefix") != null)
			{
				prefix = scale (node.getAttribute ("prefix"));
			}
			if (node.getAttribute ("offset") != null)
			{
				offset = Double.parseDouble (node.getAttribute ("offset"));
			}
			if (node.getAttribute ("exponent") != null)
			{
				exponent = Double.parseDouble (node.getAttribute ("exponent"));
			}
		}
	}
	
	public CellMLUserUnit (CellMLModel model, CellMLUnitDictionary dict, CellMLComponent component, DocumentNode node) throws BivesConsistencyException, CellMLReadException
	{
		super (model, node.getAttribute ("name"), node);
		
		this.dict = dict;
		this.component = component;
		
		String base = node.getAttribute ("name");
		if (base != null && base.equals ("yes"))
		{
			base_units = true;
			return;
		}
		
		baseQuantities = new Vector<BaseQuantity> ();
		
		Vector<TreeNode> kids = node.getChildrenWithTag ("unit");
		for (TreeNode kid : kids)
		{
			try
			{
				baseQuantities.add (new BaseQuantity ((DocumentNode) kid));
			}
			catch (NumberFormatException ex)
			{
				throw new CellMLReadException ("unknown number format: " + ex.getMessage ());
			}
		}
	}
	
	public static final int scale (String s) throws CellMLReadException
	{
		if (s.equals ("yotta"))
			return 24;
		if (s.equals ("zetta"))
			return 21;
		if (s.equals ("exa"))
			return 18;
		if (s.equals ("peta"))
			return 15;
		if (s.equals ("tera"))
			return 12;
		if (s.equals ("giga"))
			return 9;
		if (s.equals ("mega"))
			return 6;
		if (s.equals ("kilo"))
			return 3;
		if (s.equals ("hecto"))
			return 2;
		if (s.equals ("deka"))
			return 1;
		if (s.equals ("deci"))
			return -1;
		if (s.equals ("centi"))
			return -2;
		if (s.equals ("milli"))
			return -3;
		if (s.equals ("micro"))
			return -6;
		if (s.equals ("nano"))
			return -9;
		if (s.equals ("pico"))
			return -12;
		if (s.equals ("femto"))
			return -15;
		if (s.equals ("atto"))
			return -18;
		if (s.equals ("zepto"))
			return -21;
		if (s.equals ("yocto"))
			return -24;
		
		throw new CellMLReadException ("unknown prefix: " + s);
	}
}
