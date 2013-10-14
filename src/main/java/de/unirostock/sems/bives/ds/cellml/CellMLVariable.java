/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.Vector;

import org.w3c.dom.Element;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class CellMLVariable
extends CellMLEntity
implements DiffReporter
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
	
	public CellMLVariable (CellMLModel model, CellMLComponent component, DocumentNode node) throws BivesCellMLParseException, BivesConsistencyException, BivesLogicalException
	{
		super (node, model);
		this.component = component;
		name = node.getAttribute ("name");
		if (name == null || name.length () < 1)
			throw new BivesCellMLParseException ("variable doesn't have a name. (component: "+component.getName ()+")");
		unit = component.getUnit (node.getAttribute ("units"));
		if (unit == null)
			throw new BivesCellMLParseException ("variable "+name+" doesn't have a valid unit. (component: "+component.getName ()+", searching for: "+node.getAttribute ("units")+")");
		
		public_interface = parseInterface (node.getAttribute ("public_interface"));
		private_interface = parseInterface (node.getAttribute ("private_interface"));
		
		if (public_interface == private_interface && public_interface == INTERFACE_IN)
			throw new BivesLogicalException ("variable " + name + " defines public and private interface to be 'in'. (component: "+component.getName ()+")");
		
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
				try
				{
					v_initial_value = component.getVariable (attr);
				}
				catch (BivesConsistencyException e)
				{
					throw new BivesCellMLParseException ("cannot understand an initial concentration of '" + attr + "' in variable " + name + " (component: "+component.getName ()+")");
				}
			}
		}
		
	}
	
	public CellMLComponent getComponent ()
	{
		return component;
	}
	
	public int getPublicInterface ()
	{
		return public_interface;
	}
	
	public void addPublicInterfaceConnection (CellMLVariable var) throws BivesLogicalException
	{
		if (public_interface == INTERFACE_IN && public_interface_connection.size () > 0)
			throw new BivesLogicalException ("variable " + name + " defines public interface to be 'in' but wants to add more than one connection. (component: "+component.getName ()+")");
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
	
	public void addPrivateInterfaceConnection (CellMLVariable var) throws BivesLogicalException
	{
		if (private_interface == INTERFACE_IN && private_interface_connection.size () > 0)
			throw new BivesLogicalException ("variable " + name + " defines private interface to be 'in' but wants to add more than one connection. (component: "+component.getName ()+")");
		private_interface_connection.add (var);
	}
	
	public Vector<CellMLVariable> getPrivateInterfaceConnections ()
	{
		return private_interface_connection;
	}
	
	public CellMLVariable getRootVariable ()
	{
		if (private_interface == INTERFACE_IN && private_interface_connection.size () == 1)
			return private_interface_connection.elementAt (0).getRootVariable ();
		if (public_interface == INTERFACE_IN && public_interface_connection.size () == 1)
			return public_interface_connection.elementAt (0).getRootVariable ();
		return this;
	}
	
	public void unconnect ()
	{
		public_interface_connection = new Vector<CellMLVariable> (); 
		private_interface_connection = new Vector<CellMLVariable> (); 
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

	public void getDependencies (Vector<CellMLUserUnit> vector)
	{
		if (!unit.isStandardUnits ())
		{
			CellMLUserUnit u = (CellMLUserUnit) unit;
			vector.add (u);
			u.getDependencies (vector);
		}
	}

	@Override
	public MarkupElement reportMofification (ClearConnectionManager conMgmt,
		DiffReporter docA, DiffReporter docB, MarkupDocument markupDocument)
	{
		CellMLVariable a = (CellMLVariable) docA;
		CellMLVariable b = (CellMLVariable) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return null;
		
		String idA = a.name, idB = b.name;
		MarkupElement me = null;
		if (idA.equals (idB))
			me = new MarkupElement ("Variable: " + idA);
		else
		{
			me = new MarkupElement ("Variable: " + markupDocument.delete (idA) + " "+markupDocument.rightArrow ()+" " + markupDocument.insert (idB));
		}
		
		Tools.genAttributeHtmlStats (a.getDocumentNode (), b.getDocumentNode (), me, markupDocument);
		
		return me;
		
	}

	@Override
	public MarkupElement reportInsert (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement ("Variable: " + markupDocument.insert (name));
		me.addValue (markupDocument.insert ("inserted"));
		return me;
	}

	@Override
	public MarkupElement reportDelete (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement ("Variable: " + markupDocument.delete (name));
		me.addValue (markupDocument.delete ("deleted"));
		return me;
	}
}
