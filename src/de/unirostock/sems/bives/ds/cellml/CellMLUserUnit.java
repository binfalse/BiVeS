/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.markup.Markup;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class CellMLUserUnit
	extends CellMLUnit
	implements DiffReporter, Markup
{
	// A modeller might want to define and use units for which no simple conversion to SI units exist. A good example of this is pH, which is dimensionless, but uses a log scale. Ideally, pH should not simply be defined as dimensionless because software might then attempt to map variables defined with units of pH to any other dimensionless variables.
	// CellML addresses this by allowing the model author to indicate that a units definition is a new type of base unit, the definition of which cannot be resolved into simpler subunits. This is done by defining a base_units attribute value of "yes" on the <units> element. This element must then be left empty. The base_units attribute is optional and has a default value of "no".
	private boolean base_units;
	
	private CellMLUnitDictionary dict;
	private CellMLComponent component;

	private Vector<BaseQuantity> baseQuantities;
	
	public class BaseQuantity
	implements Markup
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
		
		public BaseQuantity (DocumentNode node) throws BivesConsistencyException, BivesCellMLParseException
		{
			LOGGER.debug ("reading base quantity from: " + node.getXPath () + " -> " + node.getAttribute ("units"));
			
			this.unit = dict.getUnit (node.getAttribute ("units"), component);
			if (this.unit == null)
				throw new BivesConsistencyException ("no such base unit: " + node.getAttribute ("units"));
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
		
		/*public String toString ()
		{
			return "";
			String ret = multiplier == 1 ? "" : Tools.niceDouble (multiplier, 1) + markupDocument.multiply ();
			ret += prefix == 0 ? "" : "10^" + prefix + markupDocument.multiply ();
			ret += "[" + unit.toString () + "]";
			ret += exponent == 1 ? "" : "^" + Tools.niceDouble (exponent, 1);
			ret += offset == 0 ? "" : "+"+Tools.niceDouble (offset, 0);
			return "(" + ret + ")";
		}*/

		@Override
		public String markup (MarkupDocument markupDocument)
		{
			String ret = multiplier == 1 ? "" : Tools.prettyDouble (multiplier, 1) + markupDocument.multiply ();
			ret += prefix == 0 ? "" : "10^" + prefix + markupDocument.multiply ();
			ret += "[" + unit.toString () + "]";
			ret += exponent == 1 ? "" : "^" + Tools.prettyDouble (exponent, 1);
			ret += offset == 0 ? "" : "+"+Tools.prettyDouble (offset, 0);
			return "(" + ret + ")";
		}
	}
	
	public CellMLUserUnit (CellMLModel model, CellMLUnitDictionary dict, CellMLComponent component, DocumentNode node) throws BivesConsistencyException, BivesCellMLParseException
	{
		super (model, node.getAttribute ("name"), node);
		//System.out.println ("should be mapped: " + node.getXPath () + model);
		
		this.dict = dict;
		this.component = component;
		
		String base = node.getAttribute ("name");
		if (base != null && base.equals ("yes"))
		{
			base_units = true;
			return;
		}
		
		LOGGER.debug ("reading unit: " + getName ());
		
		baseQuantities = new Vector<BaseQuantity> ();
		
		Vector<TreeNode> kids = node.getChildrenWithTag ("unit");
		/*boolean nextRound = true;
		
		while (nextRound)
		{
			nextRound = false;
			for (int i = kids.size () - 1; i >= 0; i--)
			{
				try
				{
					baseQuantities.add (new BaseQuantity ((DocumentNode) kids.elementAt (i)));
				}
				catch (NumberFormatException ex)
				{
					throw new CellMLReadException ("unknown number format: " + ex.getMessage ());
				}
				catch (BivesConsistencyException ex)
				{
					continue;
				}
				kids.remove (i);
				nextRound = true;
			}
		}
		if (kids.size () != 0)
			throw new BivesConsistencyException ("inconsistencies for "+kids.size ()+" base quantities in units "+getName ());*/
		
		for (TreeNode kid : kids)
		{
			try
			{
				baseQuantities.add (new BaseQuantity ((DocumentNode) kid));
			}
			catch (NumberFormatException ex)
			{
				throw new BivesCellMLParseException ("unknown number format: " + ex.getMessage ());
			}
		}
	}
	
	public static final int scale (String s) throws BivesCellMLParseException
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
		
		throw new BivesCellMLParseException ("unknown prefix: " + s);
	}
	
	public String markup (MarkupDocument markupDocument)
	{
		if (base_units || baseQuantities == null)
			return "base units";
		
		String ret = "";
		for (int i = 0; i < baseQuantities.size (); i++)
		{
			ret += baseQuantities.elementAt (i).markup (markupDocument);//.toString ();
			if (i < baseQuantities.size () - 1)
				ret += " "+markupDocument.multiply ()+" ";
		}
		return ret;
	}
	
	public void debug (String prefix)
	{
		System.out.println (prefix + getName () + ": " + toString ());
	}

	public Vector<CellMLUserUnit> getDependencies (Vector<CellMLUserUnit> vector)
	{
		if (base_units || baseQuantities == null)
			return vector;
		
		for (BaseQuantity bq : baseQuantities)
		{
			if (!bq.unit.isStandardUnits ())
				vector.add ((CellMLUserUnit) bq.unit);
		}
		return vector;
	}

	@Override
	public MarkupElement reportMofification (ClearConnectionManager conMgmt,
		DiffReporter docA, DiffReporter docB, MarkupDocument markupDocument)
	{
		CellMLUserUnit a = (CellMLUserUnit) docA;
		CellMLUserUnit b = (CellMLUserUnit) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return null;
		
		String idA = a.getName (), idB = b.getName ();
		MarkupElement me = null;
		if (idA.equals (idB))
			me = new MarkupElement ("Units: " + idA);
		else
		{
			me = new MarkupElement ("Units: " + markupDocument.delete (idA) + " "+markupDocument.rightArrow ()+" " + markupDocument.insert (idB));
		}

		String oldDef = a.markup (markupDocument);
		String newDef = b.markup (markupDocument);
		if (oldDef.equals (newDef))
			me.addValue ("defined by: " + oldDef);
		else
		{
			me.addValue (markupDocument.delete ("old definition: " + oldDef));
			me.addValue (markupDocument.insert ("new definition: " + newDef));
		}
		
		Tools.genAttributeHtmlStats (a.getDocumentNode (), b.getDocumentNode (), me, markupDocument);
		
		return me;
	}

	@Override
	public MarkupElement reportInsert (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement ("Units: " + markupDocument.insert (getName ()));
		me.addValue (markupDocument.insert ("inserted: " + this.markup (markupDocument)));
		return me;
	}

	@Override
	public MarkupElement reportDelete (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement ("Units: " + markupDocument.delete (getName ()));
		me.addValue (markupDocument.delete ("deleted: " + this.markup (markupDocument)));
		return me;
	}
}
