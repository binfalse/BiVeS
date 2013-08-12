/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLHierarchy
{
	public static final int RELATION_HIDDEN = 0;
	public static final int RELATION_SIBLING = 1;
	public static final int RELATION_PARENT = 2;
	public static final int RELATION_ENCAPSULATED = 3;
	
	private HashMap <String, CellMLHierarchyNetwork> networks;
	
	private CellMLModel model;
	
	public CellMLHierarchy (CellMLModel model)
	{
		this.model = model;
		networks = new HashMap <String, CellMLHierarchyNetwork> ();
	}
	
	public CellMLHierarchyNetwork getHierarchyNetwork (String relationship, String name)
	{
		return networks.get (relationship + ":" + name);
	}
	
	public void parseGroup (DocumentNode node) throws BivesCellMLParseException, BivesLogicalException
	{
		//CellMLHierarchyRelationship relationship = new CellMLHierarchyRelationship ();
		Vector<CellMLHierarchyNetwork> curNetworks = new Vector<CellMLHierarchyNetwork> ();
		
		Vector<TreeNode> kids = node.getChildrenWithTag ("relationship_ref");
		for (TreeNode kid : kids)
		{
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;
			
			DocumentNode dkid = (DocumentNode) kid;
			String rs = dkid.getAttribute ("relationship");
			if (rs == null)
			{
				LOGGER.warn ("skipping relationship_ref definition: no valid relation ship defined.");
				continue;
			}
			
			String name = dkid.getAttribute ("name");
			if (name == null)
				name = "";
			
			if (rs.equals ("encapsulation") && name.length () > 0)
				throw new BivesLogicalException ("A name attribute must not be defined on a <relationship_ref> element with a relationship attribute value of \"encapsulation\"!");
			
			//relationship.addRelationship (rs);

			CellMLHierarchyNetwork cur = networks.get (rs + ":" + name);
			if (cur == null)
			{
				cur = new CellMLHierarchyNetwork (rs, name);
				networks.put (rs + ":" + name, cur);
			}
			curNetworks.add (cur);
		}
		
		
		if (curNetworks.size () < 0)
		{
			LOGGER.warn ("skipping group definition: no recognizable relationships defined.");
			return;
		}
		
		Stack<CellMLComponent> parents = new Stack<CellMLComponent> ();
		recursiveParseGroup (node, parents, curNetworks);
	}
	
	private void recursiveParseGroup (DocumentNode cur, Stack<CellMLComponent> parents, Vector<CellMLHierarchyNetwork> curNetworks) throws BivesCellMLParseException, BivesLogicalException
	{
		Vector<TreeNode> kids = cur.getChildrenWithTag ("component_ref");
		
		if (kids.size () == 0 && parents.size () == 0)
			throw new BivesCellMLParseException ("group doesn't contain component_refs");
		
		for (TreeNode kid : kids)
		{
			//Node node = nodes.item (i);
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;
			DocumentNode next = (DocumentNode) kid;
			
			String componentName = (next).getAttribute ("component");
			if (componentName == null)
				throw new BivesCellMLParseException ("no component defined in component_ref of grouping.");
			
			CellMLComponent child = model.getComponent (componentName);
			if (child == null)
			{
				throw new BivesLogicalException ("cannot find component with name: " + componentName + ")");
			}
			
			if (parents.size () > 0)
			{
				// when we are encapsulated -> extend the network
				CellMLComponent parent = parents.peek ();
				
				for (CellMLHierarchyNetwork network : curNetworks)
					network.connectHierarchically (parent, child);
			}
			
			parents.add (child);
			recursiveParseGroup (next, parents, curNetworks);
		}
		
		if (parents.size () > 0)
			parents.pop ();
	}

	public int getEncapsulationRelationship (CellMLComponent component_1,
		CellMLComponent component_2) throws BivesLogicalException
	{
		CellMLHierarchyNetwork network = networks.get ("encapsulation:");
		if (network == null)
			return RELATION_SIBLING;
		
		CellMLHierarchyNode node_1 = network.get (component_1);
		CellMLHierarchyNode node_2 = network.get (component_2);

		if (node_1 == null)
		{
			if (node_2 == null || node_2.getParent () == null)
				return RELATION_SIBLING;
			return RELATION_HIDDEN;
		}
		if (node_2 == null)
		{
			if (node_1.getParent () == null)
				return RELATION_SIBLING;
			return RELATION_HIDDEN;
		}
		
		// TODO: or following better?
		/*if (node_1 == null || node_2 == null)
		{
			throw new BivesLogicalException ("cannot find nodes for components. (component: " + component_1.getName () + "," + component_2.getName () + ")");
		}*/
		
		if (node_1.getParent () == node_2.getParent ())
			return RELATION_SIBLING;
		if (node_1 == node_2.getParent ())
			return RELATION_PARENT;
		if (node_1.getParent () == node_2)
			return RELATION_ENCAPSULATED;
		
		return RELATION_HIDDEN;
	}
}
