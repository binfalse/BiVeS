/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.CellMLReadException;


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
	
	
	private Vector<CellMLHierarchyNode> unencapsulated;
	private CellMLModel model;
	private HashMap<CellMLComponent, CellMLHierarchyNode> componentMapper;
	
	public CellMLHierarchy (CellMLModel model)
	{
		this.model = model;
		unencapsulated = new Vector<CellMLHierarchyNode> ();
		componentMapper = new HashMap<CellMLComponent, CellMLHierarchyNode> ();
	}
	
	public void parseGroup (DocumentNode node) throws CellMLReadException, BivesLogicalException
	{
		CellMLHierarchyRelationship relationship = new CellMLHierarchyRelationship ();
		
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
			relationship.addRelationship (rs);
		}
		
		
		if (relationship.getRelationship ().size () < 0)
		{
			LOGGER.warn ("skipping group definition: no valid relation ship defined.");
			return;
		}
		
		Stack<CellMLHierarchyNode> parents = new Stack<CellMLHierarchyNode> ();
		recursiveParseGroup (node, parents, relationship);
	}
	
	private void recursiveParseGroup (DocumentNode cur, Stack<CellMLHierarchyNode> parents, CellMLHierarchyRelationship relationship) throws CellMLReadException, BivesLogicalException
	{
		Vector<TreeNode> kids = cur.getChildrenWithTag ("component_ref");
		
		if (kids.size () == 0 && parents.size () == 0)
			throw new CellMLReadException ("group doesn't contain component_refs");
		
		for (TreeNode kid : kids)
		{
			//Node node = nodes.item (i);
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;
			DocumentNode next = (DocumentNode) kid;
			
			String componentName = (next).getAttribute ("component");
			if (componentName == null)
				throw new CellMLReadException ("no component defined in component_ref of grouping.");
			
			CellMLComponent component = model.getComponent (componentName);
			if (component == null)
			{
				throw new BivesLogicalException ("cannot find component with name: " + componentName + ")");
			}
			
			CellMLHierarchyNode child = componentMapper.get (component);
			if (child == null)
			{
				throw new BivesLogicalException ("cannot find node for component. (component: " + component.getName () + ")");
			}
			
			if (parents.size () > 0)
			{
				if (child.getParent () != null)
				{
					throw new BivesLogicalException ("encapsulation failed: child wants to have to parents? (component: " + component.getName () + ")");
				}
				CellMLHierarchyNode parent = parents.peek ();
				child.setParent (parent);
				CellMLHierarchyRelationship rs = relationship.copy ();
				rs.setNodes (parent, child);
				parent.addChild (rs);
				unencapsulated.remove (child);
			}
			
			parents.add (child);
			recursiveParseGroup (next, parents, relationship);
		}
		
		if (parents.size () > 0)
			parents.pop ();
	}
	
	public void addUnencapsulatedComponent (CellMLComponent component) throws BivesLogicalException
	{
		CellMLHierarchyNode node = new CellMLHierarchyNode (component);
		
		if (componentMapper.get (component) != null)
			throw new BivesLogicalException ("component already in hierarchy. (component: " + component.getName () + ")");
		componentMapper.put (component, node);
		unencapsulated.add (node);
	}

	public int getRelationship (CellMLComponent component_1,
		CellMLComponent component_2) throws BivesLogicalException
	{
		CellMLHierarchyNode node_1 = componentMapper.get (component_1);
		CellMLHierarchyNode node_2 = componentMapper.get (component_2);

		if (node_1 == null || node_2 == null)
		{
			throw new BivesLogicalException ("cannot find nodes for components. (component: " + component_1.getName () + "," + component_2.getName () + ")");
		}
		
		if (node_1.getParent () == node_2.getParent ())
			return RELATION_SIBLING;
		if (node_1 == node_2.getParent ())
			return RELATION_PARENT;
		if (node_1.getParent () == node_2)
			return RELATION_ENCAPSULATED;
		
		return RELATION_HIDDEN;
	}
	
	/*public void encapsulate (CellMLComponent parent, CellMLComponent child) throws CellMLLogicalException
	{
		if (parent == null || child == null)
		{
			throw new CellMLLogicalException ("cannot encapsulate nullpointers.");
		}
		
		if (parent == child)
			throw new CellMLLogicalException ("component cannot be parent and child at the same time. (component: " + parent.getName () + ")");
		
		CellMLHierarchyNode parentNode = componentMapper.get (parent);
		CellMLHierarchyNode childNode = componentMapper.get (child);
		
		if (parentNode == null || childNode == null)
		{
			throw new CellMLLogicalException ("cannot find nodes for components. (component: " + parent.getName () + "," + child.getName () + ")");
		}
		
		if (childNode.getParent () != null)
		{
			throw new CellMLLogicalException ("encapsulation failed: child wants to have to parents? (component: " + child.getName () + ")");
		}
		
		parentNode.addChild (childNode);
		childNode.setParent (parentNode);
		unencapsulated.remove (childNode);
	}*/
}
