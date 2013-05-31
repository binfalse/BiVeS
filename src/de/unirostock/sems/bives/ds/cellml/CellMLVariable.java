/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.Vector;

import org.w3c.dom.Element;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.CellMLReadException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLVariable
extends CellMLEntity
{
	public static final int INTERFACE_NONE = 0;
	public static final int INTERFACE_IN = -1;
	public static final int INTERFACE_OUT = 1;
	
	private CellMLComponent component;
	
	// Variables must define a name attribute, the value of which must be unique across all variables in the current component
	private String name;
	
	// All variables must also define a units attribute
	private CellMLUnit unit;
	
	// This attribute provides a convenient means for specifying the value of a scalar real variable when all independent variables in the model have a value of 0.0. Independent variables are those whose values do not depend on others.
	private double d_initial_value;
	private CellMLVariable v_initial_value;
	
	//This attribute specifies the interface exposed to components in the parent and sibling sets (see below). The public interface must have a value of "in", "out", or "none". The absence of a public_interface attribute implies a default value of "none".
	private int public_interface;
	private Vector<CellMLVariable> public_interface_connection;
	// This attribute specifies the interface exposed to components in the encapsulated set (see below). The private interface must have a value of "in", "out", or "none". The absence of a private_interface attribute implies a default value of "none".
	private int private_interface;
	private Vector<CellMLVariable> private_interface_connection;
	
	public CellMLVariable (CellMLModel model, CellMLComponent component, DocumentNode node) throws CellMLReadException, BivesConsistencyException, BivesLogicalException
	{
		super (node, model);
		this.component = component;
		name = node.getAttribute ("name");
		if (name == null || name.length () < 1)
			throw new CellMLReadException ("variable doesn't have a name. (component: "+component.getName ()+")");
		unit = component.getUnit (node.getAttribute ("units"));
		if (unit == null)
			throw new CellMLReadException ("variable doesn't have a unit. (component: "+component.getName ()+")");
		
		public_interface = parseInterface (node.getAttribute ("public_interface"));
		private_interface = parseInterface (node.getAttribute ("private_interface"));
		
		private_interface_connection = new Vector<CellMLVariable> ();
		public_interface_connection = new Vector<CellMLVariable> ();
		
		// An initial_value attribute must not be defined on a <variable> element with a public_interface or private_interface attribute with a value of "in". [ These variables receive their value from variables belonging to another component. ]
		
		String attr = node.getAttribute ("initial_value");
		if (attr != null)
		{
			if (public_interface == INTERFACE_IN || private_interface == INTERFACE_IN)
				throw new BivesLogicalException ("initial_value attribute must not be defined on a <variable> element with a public_interface or private_interface attribute with a value of 'in' (variable: "+name+", component: "+component.getName ()+")");
			try
			{
				d_initial_value = Double.parseDouble (attr);
			}
			catch (NumberFormatException ex)
			{
				// TODO: may be a variable name
				// If present, the value of the initial_value attribute may be a real number or the value of the name attribute of a <variable> element declared in the current component.
				// throw new CellMLReadException ("Unsupported number format: " + attr + " in variable " + name + " of component " + component.getName ());
				v_initial_value = component.getVariable (attr);
			}
		}
		
	}
	
	public int getPublicInterface ()
	{
		return public_interface;
	}
	
	public void addPublicInterfaceConnection (CellMLVariable var)
	{
		public_interface_connection.add (var);
	}
	
	public Vector<CellMLVariable> getPublicInterfaceConnections ()
	{
		return public_interface_connection;
	}
	
	public int getPrivateInterface ()
	{
		return private_interface;
	}
	
	public void addPrivateInterfaceConnection (CellMLVariable var)
	{
		private_interface_connection.add (var);
	}
	
	public Vector<CellMLVariable> getPrivateInterfaceConnections ()
	{
		return private_interface_connection;
	}
	
	private String parseInterface (int attr)
	{
		if (attr == INTERFACE_IN)
			return "in";
		if (attr == INTERFACE_OUT)
			return "out";
		return "none";
	}

	private int parseInterface (String attr)
	{
		if (attr == null)
			return INTERFACE_NONE;
		if (attr.equals ("in"))
			return INTERFACE_IN;
		if (attr.equals ("out"))
			return INTERFACE_OUT;
		return INTERFACE_NONE;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public void debug (String prefix)
	{
		System.out.println (prefix + "var: " + name);
	}
}
