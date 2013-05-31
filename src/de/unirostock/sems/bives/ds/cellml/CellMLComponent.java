/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Element;

import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.CellMLReadException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLComponent
extends CellMLEntity
{
	
	// Each <component> must have a name attribute, the value of which is a unique identifier for the component amongst all other components within the current model.
	private String name;
	
	// A modeller can define a set of units to use within the component
	private CellMLUnitDictionary units;
	
	// A component may contain any number of <variable> elements, which define variables that may be mathematically related in the equation blocks contained in the component.
	private HashMap<String, CellMLVariable> variables;
	
	// A component may contain <reaction> elements, which are used to provide chemical and biochemical context for the equations describing a reaction. It is recommended that only one <reaction> element appear in any <component> element.
	private Vector<CellMLReaction> reactions;
	
	// A component may contain a set of mathematical relationships between the variables declared in this component.
	private Vector<MathML> math;
	
	public CellMLComponent (CellMLModel model, DocumentNode node) throws BivesConsistencyException, CellMLReadException, BivesLogicalException
	{
		super (node, model);
		
		units = model.getUnits ();
		math = new Vector<MathML> ();
		variables = new HashMap<String, CellMLVariable> ();
		reactions = new Vector<CellMLReaction> ();

		name = node.getAttribute ("name");
		if (name == null || name.length () < 1)
			throw new CellMLReadException ("component doesn't have a name.");
		
		Vector<TreeNode> kids = node.getChildrenWithTag ("units");
		for (TreeNode kid : kids)
		{
			units.addUnit (this, new CellMLUserUnit (model, units, this, (DocumentNode) kid));
		}
		
		kids = node.getChildrenWithTag ("variable");
		boolean nextRound = true;
		while (nextRound && kids.size () > 0)
		{
			nextRound = false;
			for (int i = kids.size () - 1; i >= 0; i--)
			{
				TreeNode kid = kids.elementAt (i);
				if (kid.getType () != TreeNode.DOC_NODE)
					continue;
				try
				{
					CellMLVariable var = new CellMLVariable (model, this, (DocumentNode) kid);
					variables.put (var.getName (), var);
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
			throw new BivesConsistencyException ("inconsistencies for "+kids.size ()+" variables in component "+name+", e.q. "+kids.elementAt (0).getXPath ());
		
		kids = node.getChildrenWithTag ("reaction");
		for (TreeNode kid : kids)
		{
			reactions.add (new CellMLReaction (model, this, (DocumentNode) kid));
		}
		
		kids = node.getChildrenWithTag ("math");
		for (TreeNode kid : kids)
		{
			math.add (new MathML ((DocumentNode) kid));
		}
	}
	
	public CellMLVariable getVariable (String name) throws BivesConsistencyException
	{
		CellMLVariable var = variables.get (name);
		if (var == null)
			throw new BivesConsistencyException ("unknown variable: " + name + " in component " + this.name);
		return var;
	}
	
	public CellMLUnit getUnit (String name) throws BivesConsistencyException
	{
		return units.getUnit (name, this);
	}
	
	public String getName ()
	{
		return name;
	}
	
	public void setName (String name)
	{
		this.name = name;
	}
	
	public void debug (String prefix)
	{
		System.out.println (prefix + "comp: " + name);
		for (CellMLVariable v : variables.values ())
			v.debug (prefix + "  ");
	}
}
