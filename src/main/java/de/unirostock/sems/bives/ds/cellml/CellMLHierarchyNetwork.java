/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.Collection;
import java.util.HashMap;

import de.unirostock.sems.bives.exception.BivesLogicalException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLHierarchyNetwork
{
	private String realtionship;
	private String name;
	private HashMap<CellMLComponent, CellMLHierarchyNode> componentMapper;
	
	public CellMLHierarchyNetwork (String realtionship, String name)
	{
		this.realtionship = realtionship;
		this.name = name;
		componentMapper = new HashMap<CellMLComponent, CellMLHierarchyNode> ();
	}
	
	public Collection<CellMLHierarchyNode> getNodes ()
	{
		return componentMapper.values ();
	}
	
	public void connectHierarchically (CellMLComponent parent, CellMLComponent kid) throws BivesLogicalException
	{
		CellMLHierarchyNode pNode = componentMapper.get (parent);
		CellMLHierarchyNode kNode = componentMapper.get (kid);

		if (pNode == null)
		{
			pNode = new CellMLHierarchyNode (parent);
			componentMapper.put (parent, pNode);
		}
		if (kNode == null)
		{
			kNode = new CellMLHierarchyNode (kid);
			componentMapper.put (kid, kNode);
		}
		
		if (kNode.getParent () != null)
			throw new BivesLogicalException ("encapsulation failed: child wants to have two parents? (component: " + kid.getName () + ", parents: "+parent.getName ()+","+kNode.getParent ().getComponent ().getName ()+")");
		
		pNode.addChild (kNode);
		kNode.setParent (pNode);
	}
	
	public CellMLHierarchyNode get (CellMLComponent node)
	{
		return componentMapper.get (node);
	}
	
	public String getRelationship ()
	{
		return realtionship;
	}
	
	public String getName ()
	{
		return name;
	}
	
}
