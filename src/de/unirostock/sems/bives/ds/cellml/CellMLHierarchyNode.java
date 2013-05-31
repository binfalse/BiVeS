/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.Vector;


/**
 * @author Martin Scharm
 *
 */
public class CellMLHierarchyNode
{
	private CellMLComponent component;

	private CellMLHierarchyNode parent;
	private Vector<CellMLHierarchyRelationship> children;
	
	public CellMLHierarchyNode (CellMLComponent component)
	{
		this.component = component;
		children = new Vector<CellMLHierarchyRelationship> ();
	}
	
	public CellMLHierarchyNode (CellMLComponent component, CellMLHierarchyNode parent)
	{
		this.component = component;
		this.parent = parent;
		children = new Vector<CellMLHierarchyRelationship> ();
	}
	
	public CellMLComponent getComponent ()
	{
		return component;
	}
	
	public void setParent (CellMLHierarchyNode parent)
	{
		this.parent = parent;
	}
	
	public CellMLHierarchyNode getParent ()
	{
		return parent;
	}
	
	public void addChild (CellMLHierarchyRelationship child)
	{
		this.children.add (child);
	}
	
}
