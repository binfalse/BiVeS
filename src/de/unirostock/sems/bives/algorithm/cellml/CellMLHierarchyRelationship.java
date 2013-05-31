/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.util.Vector;

import de.unirostock.sems.bives.exception.BivesLogicalException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLHierarchyRelationship
{
	private CellMLHierarchyNode nodeA, nodeB;
	
	private Vector<String> relationship;
	
	public CellMLHierarchyRelationship ()
	{
		relationship = new Vector<String> ();
	}
	
	/**
	 * Copies the properties of the relationship. Will not copy the containing nodes.  
	 *
	 * @return the copy of this relationship
	 */
	public CellMLHierarchyRelationship copy ()
	{
		CellMLHierarchyRelationship copy = new CellMLHierarchyRelationship ();
		
		for (String rs : relationship)
			copy.relationship.add (rs);
		
		return copy;
	}
	
	public void setNodes (CellMLHierarchyNode nodeA, CellMLHierarchyNode nodeB)
	{
		this.nodeA = nodeA;
		this.nodeB = nodeB;
	}
	
	public void addRelationship (String relationship)
	{
		this.relationship.add (relationship);
	}
	
	public Vector<String> getRelationship ()
	{
		return relationship;
	}
	
	public CellMLHierarchyNode getMate (CellMLHierarchyNode node) throws BivesLogicalException
	{
		if (node == nodeA)
			return nodeB;
		if (node == nodeB)
			return nodeA;
		throw new BivesLogicalException ("wrong relationship request for node: " + node.getComponent ().getName () + " (query sent to relationship of "+nodeA.getComponent ().getName ()+","+nodeB.getComponent ().getName ()+")");
	}
}
