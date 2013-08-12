/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Collection;
import java.util.HashMap;

import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class HierarchyNetwork
{
	public static final int UNMODIFIED = 0;
	public static final int INSERT = 1;
	public static final int DELETE = -1;
	public static final int MODIFIED = 2;
	
	
	private int componentID;
	private int variableID;
	private HashMap<TreeNode, HierarchyNetworkComponent> hnC;
	private HashMap<TreeNode, HierarchyNetworkVariable> hnV;
	private HashMap<HierarchyNetworkComponent, HierarchyNetworkComponent> uhnC;
	private HashMap<HierarchyNetworkVariable, HierarchyNetworkVariable> uhnV;
	
	public HierarchyNetwork ()
	{
		componentID = 0;
		variableID = 0;
		hnC = new HashMap<TreeNode, HierarchyNetworkComponent> ();
		hnV = new HashMap<TreeNode, HierarchyNetworkVariable> ();
		uhnC = new HashMap<HierarchyNetworkComponent, HierarchyNetworkComponent> ();
		uhnV = new HashMap<HierarchyNetworkVariable, HierarchyNetworkVariable> ();
	}
	
	public static String modToString (int modification)
	{
		switch (modification)
		{
			case INSERT:
				return "inserted";
			case DELETE:
				return "deleted";
			case MODIFIED:
				return "modified";
		}
		return "unmodified";
	}
	
	public Collection<HierarchyNetworkComponent> getComponents ()
	{
		return uhnC.values ();
	}
	
	public Collection<HierarchyNetworkVariable> getVariables ()
	{
		return uhnV.values ();
	}
	
	public int getNextComponentID ()
	{
		return ++componentID;
	}
	
	public int getNextVariableID ()
	{
		return ++variableID;
	}
	

	
	public void setComponent (TreeNode node, HierarchyNetworkComponent comp)
	{
		hnC.put (node, comp);
		uhnC.put (comp, comp);
	}
	
	public void setVariable (TreeNode node, HierarchyNetworkVariable var)
	{
		hnV.put (node, var);
		uhnV.put (var, var);
	}
	
	public HierarchyNetworkComponent getComponent (TreeNode node)
	{
		return hnC.get (node);
	}
	
	public HierarchyNetworkVariable getVariable (TreeNode node)
	{
		return hnV.get (node);
	}

	public void setSingleDocument ()
	{
		for (HierarchyNetworkComponent c : hnC.values ())
			c.setSingleDocument ();
		for (HierarchyNetworkVariable v : hnV.values ())
			v.setSingleDocument ();
	}
	
	
}
