/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.util.Vector;

import org.w3c.dom.Element;

import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.CellMLReadException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLReactionSubstance
{
	public static final int ROLE_REACTANT = 1;
	public static final int ROLE_PRODUCT = 2;
	public static final int ROLE_CATALYST = 3;
	public static final int ROLE_INHIBITOR = 4;
	public static final int ROLE_ACTIVATOR= 5;
	public static final int ROLE_RATE= 6;
	public static final int ROLE_MODIFIER= 7;
	public static final int DIRECTION_FORWARD = 0;
	public static final int DIRECTION_REVERSE = 1;
	public static final int DIRECTION_BOTH = 2;
	
	public class Role
	{
		// The role attribute must have a value of "reactant", "product", "catalyst", "activator", "inhibitor", "modifier", or "rate"
		public int role;
		// The optional direction attribute may be used on <role> elements in reversible reactions. If defined, it must have a value of "forward", "reverse", or "both". Its value indicates the direction of the reaction for which the role is relevant. It has a default value of "forward".
		public int direction;
		// The optional delta_variable attribute indicates which variable is used to store the change in concentration of the species represented by the variable referenced by the current <variable_ref> element
		public CellMLVariable delta_variable;
		// The optional stoichiometry attribute stores the stoichiometry of the current variable relative to the other reaction participants.
		public double stoichiometry;
		// The <role> elements may also contain <math> elements in the MathML namespace, which define equations using MathML
		public Vector<MathML> math;
		
		public Role (DocumentNode node) throws CellMLReadException, BivesConsistencyException
		{
			direction = DIRECTION_FORWARD;
			delta_variable = null;
			stoichiometry = -1;
			math = new Vector<MathML> ();
			
			role = resolveRole (node.getAttribute ("role"));
			
			if (node.getAttribute ("direction") != null)
				direction = resolveDirection (node.getAttribute ("direction"));
			
			if (node.getAttribute ("delta_variable") != null)
				delta_variable = component.getVariable (node.getAttribute ("delta_variable"));
			
			if (node.getAttribute ("stoichiometry") != null)
				try
				{
					stoichiometry = Double.parseDouble (node.getAttribute ("stoichiometry"));
				}
				catch (NumberFormatException ex)
				{
					throw new CellMLReadException ("no proper stoichiometry: " + node.getAttribute ("stoichiometry"));
				}
			
			Vector<TreeNode> kids = node.getChildrenWithTag ("math");
			for (TreeNode kid : kids)
				math.add (new MathML ((DocumentNode) kid));
		}
	}
	
	public CellMLVariable variable;
	public Vector<Role> role;
	public CellMLComponent component;
	
	public CellMLReactionSubstance (CellMLComponent component, DocumentNode node) throws BivesConsistencyException, CellMLReadException
	{
		this.component = component;
		variable = component.getVariable (node.getAttribute ("variable"));
		
		Vector<TreeNode> kids = node.getChildrenWithTag ("role");
		for (TreeNode kid : kids)
			role.add (new Role ((DocumentNode) kid));
	}

	public static final String resolveDirection (int direction) throws CellMLReadException
	{
		if (direction == DIRECTION_FORWARD)
			return "forward";
		if (direction == DIRECTION_REVERSE)
			return "reverse";
		if (direction == DIRECTION_BOTH)
			return "both";
		throw  new CellMLReadException ("unknown direction: " + direction);
	}
	public static final int resolveDirection (String direction) throws CellMLReadException
	{
		if (direction.equals ("forward"))
			return DIRECTION_FORWARD;
		if (direction.equals ("reverse"))
			return DIRECTION_REVERSE;
		if (direction.equals ("both"))
			return DIRECTION_BOTH;
		throw  new CellMLReadException ("unknown direction: " + direction);
	}

	public static final String resolveRole (int role) throws CellMLReadException
	{
		if (role == ROLE_REACTANT)
			return "reactant";
		if (role == ROLE_PRODUCT)
			return "product";
		if (role == ROLE_CATALYST)
			return "catalyst";
		if (role == ROLE_ACTIVATOR)
			return "activator";
		if (role == ROLE_INHIBITOR)
			return "inhibitor";
		if (role == ROLE_MODIFIER)
			return "modifier";
		if (role == ROLE_RATE)
			return "rate";
		throw  new CellMLReadException ("unknown role: " + role);
	}
	public static final int resolveRole (String role) throws CellMLReadException
	{
		if (role.equals ("reactant"))
			return ROLE_REACTANT;
		if (role.equals ("product"))
			return ROLE_PRODUCT;
		if (role.equals ("catalyst"))
			return ROLE_CATALYST;
		if (role.equals ("activator"))
			return ROLE_ACTIVATOR;
		if (role.equals ("inhibitor"))
			return ROLE_INHIBITOR;
		if (role.equals ("modifier"))
			return ROLE_MODIFIER;
		if (role.equals ("rate"))
			return ROLE_RATE;
		throw  new CellMLReadException ("unknown role: " + role);
	}
}
