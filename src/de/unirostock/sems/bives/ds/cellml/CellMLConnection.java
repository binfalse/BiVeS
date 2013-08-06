/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLConnection
{
	public static final boolean parseConnection (CellMLModel model, CellMLHierarchy hierarchy, DocumentNode connection, HashMap<String, CellMLComponent> limit) throws BivesCellMLParseException, BivesConsistencyException, BivesLogicalException
	{
		// A <connection> element must contain exactly one <map_components> element, which is used to reference the two componVector<E>nvolved in the connection.
		Vector<TreeNode> kids = connection.getChildrenWithTag ("map_components");
		if (kids.size () != 1)
			throw new BivesCellMLParseException ("connection does not have exactly one map_components.");
		DocumentNode child = (DocumentNode) kids.elementAt (0);
		String v1 = child.getAttribute ("component_1");
		String v2 = child.getAttribute ("component_2");
		
		if (limit != null)
			if (limit.get (v1) == null || limit.get (v2) == null)
				return false;
		
		if (v1 == null || v2 == null || v1.equals (v2))
			throw new BivesCellMLParseException ("map_components does not define two components.");
		
		CellMLComponent component_1 = model.getComponent (v1);
		CellMLComponent component_2 = model.getComponent (v2);
		
		if (component_1 == null)
			throw new BivesCellMLParseException ("in map_components: " + v1 + " is not a valid component.");
		if (component_2 == null)
			throw new BivesCellMLParseException ("in map_components: " + v2 + " is not a valid component.");
		
		int relation = hierarchy.getEncapsulationRelationship (component_1, component_2);
		
		// A <connection> element must also contain one or more <map_variables> elements, which are used to reference the variables being mapped between the two components in the connection.
		kids = connection.getChildrenWithTag ("map_variables");
		for (TreeNode kid : kids)
		{
			DocumentNode dkid = (DocumentNode) kid;
			// Each <map_variables> element must define variable_1 and variable_2 attributes, the values of which are equal to the names of variables defined in the components referenced by the component_1 and component_2 attributes on the <map_components> element, respectively. 
			v1 = dkid.getAttribute ("variable_1"); 
			v2 = dkid.getAttribute ("variable_2");
			
			if (v1 == null || v2 == null)
				throw new BivesCellMLParseException ("map_variables does not define two variables. (components: "+component_1.getName ()+","+component_2.getName ()+")");
			
			CellMLVariable variable_1 = component_1.getVariable (v1);
			CellMLVariable variable_2 = component_2.getVariable (v2);
			
			if (variable_1 == null)
				throw new BivesCellMLParseException ("in map_variables: " + v1 + " is not a valid variable. (component: "+component_1.getName ()+")");
			if (variable_2 == null)
				throw new BivesCellMLParseException ("in map_variables: " + v2 + " is not a valid variable. (component: "+component_2.getName ()+")");

			//System.out.println ("connecting variables (components: "+component_1.getName ()+","+component_2.getName ()+", variables: "+variable_1.getName ()+","+variable_2.getName ()+")");
			
			
			// The interface attributes of each pair of variables must be compatible — an "out" variable in one component's interface must map to an "in" variable in the other component's interface.
			
			switch (relation)
			{
				case CellMLHierarchy.RELATION_PARENT:
					// component_1 = parentOf (component_2)
					if (variable_1.getPrivateInterface () == CellMLVariable.INTERFACE_IN && variable_2.getPublicInterface () == CellMLVariable.INTERFACE_OUT && variable_1.getPrivateInterfaceConnections ().size () == 0)
					{
						// variable_1.private-in -> variable_2.public-out
						variable_1.addPrivateInterfaceConnection (variable_2);
						variable_2.addPublicInterfaceConnection (variable_1);
						continue;
					}
					if (variable_1.getPrivateInterface () == CellMLVariable.INTERFACE_OUT && variable_2.getPublicInterface () == CellMLVariable.INTERFACE_IN && variable_2.getPublicInterfaceConnections ().size () == 0)
					{
						// variable_1.private-out -> variable_2.public-in
						variable_1.addPrivateInterfaceConnection (variable_2);
						variable_2.addPublicInterfaceConnection (variable_1);
						continue;
					}
					throw new BivesLogicalException ("cannot connect variables due to logical restrictions. (par) (components: "+component_1.getName ()+","+component_2.getName ()+", variables: "+variable_1.getName ()+","+variable_2.getName ()+")");
				case CellMLHierarchy.RELATION_ENCAPSULATED:
					// component_1 = childOf (component_2)
					if (variable_2.getPrivateInterface () == CellMLVariable.INTERFACE_IN && variable_1.getPublicInterface () == CellMLVariable.INTERFACE_OUT && variable_2.getPrivateInterfaceConnections ().size () == 0)
					{
						// variable_2.private-in -> variable_1.public-out
						variable_2.addPrivateInterfaceConnection (variable_1);
						variable_1.addPublicInterfaceConnection (variable_2);
						continue;
					}
					if (variable_2.getPrivateInterface () == CellMLVariable.INTERFACE_OUT && variable_1.getPublicInterface () == CellMLVariable.INTERFACE_IN && variable_1.getPublicInterfaceConnections ().size () == 0)
					{
						// variable_2.private-out -> variable_1.public-in
						variable_2.addPrivateInterfaceConnection (variable_1);
						variable_1.addPublicInterfaceConnection (variable_2);
						continue;
					}
					throw new BivesLogicalException ("cannot connect variables due to logical restrictions. (enc) (components: "+component_1.getName ()+","+component_2.getName ()+", variables: "+variable_1.getName ()+","+variable_2.getName ()+")");
				case CellMLHierarchy.RELATION_SIBLING:
					// parentOf (component_1) = parentOf (component_2)
					if (variable_1.getPublicInterface () == CellMLVariable.INTERFACE_IN && variable_2.getPublicInterface () == CellMLVariable.INTERFACE_OUT && variable_1.getPublicInterfaceConnections ().size () == 0)
					{
						// variable_1.public-in -> variable_2.public-out
						variable_1.addPublicInterfaceConnection (variable_2);
						variable_2.addPublicInterfaceConnection (variable_1);
						continue;
					}
					if (variable_1.getPublicInterface () == CellMLVariable.INTERFACE_OUT && variable_2.getPublicInterface () == CellMLVariable.INTERFACE_IN && variable_2.getPublicInterfaceConnections ().size () == 0)
					{
						// variable_1.public-out -> variable_2.public-in
						variable_1.addPublicInterfaceConnection (variable_2);
						variable_2.addPublicInterfaceConnection (variable_1);
						continue;
					}
					throw new BivesLogicalException ("cannot connect variables due to logical restrictions. (sib) (components: "+component_1.getName ()+","+component_2.getName ()+", variables: "+variable_1.getName ()+","+variable_2.getName ()+")");
				default:
					throw new BivesLogicalException ("components are in hidden relationship and must not be connected. (components: "+component_1.getName ()+","+component_2.getName ()+")");
			}
		}
		
		return true;
	}
}
