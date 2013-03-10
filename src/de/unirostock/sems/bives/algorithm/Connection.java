/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class Connection
{
	private TreeNode a, b;
	private double weight;
	
	/**
	 * Instantiates a new connection, implicitly setting the weight to 1.
	 *
	 * @param a the node of tree 1
	 * @param b the node of tree 2
	 */
	public Connection (TreeNode a, TreeNode b)
	{
		this.a = a;
		this.b = b;
		weight = 1;
	}
	
	
	/**
	 * Instantiates a new connection as a copy of toCopy.
	 *
	 * @param toCopy the connection to copy
	 */
	public Connection (Connection toCopy)
	{
		this.a = toCopy.a;
		this.b = toCopy.b;
		weight = toCopy.weight;
	}
	
	/**
	 * Instantiates a new connection, explicitly defining a weight.
	 *
	 * @param a the node of tree 1
	 * @param b the node of tree 2
	 * @param weight the weight of this connection
	 */
	public Connection (TreeNode a, TreeNode b, double weight)
	{
		this.a = a;
		this.b = b;
		this.weight = weight;
	}
	
	/**
	 * Gets the corresponding node in tree a.
	 *
	 * @return the node in tree a
	 */
	public TreeNode getTreeA ()
	{
		return a;
	}
	
	/**
	 * Gets the corresponding node in tree b.
	 *
	 * @return the node in tree b
	 */
	public TreeNode getTreeB ()
	{
		return b;
	}
	
	public void setWeight (double u)
	{
		weight = u;
	}
	
	public void addWeight (double u)
	{
		weight += u;
	}
	
	public void scaleWeight (double u)
	{
		weight *= u;
	}
	
	public double getWeight ()
	{
		return weight;
	}
	
	public String toString ()
	{
		return "[" + a.getXPath () + " => " + b.getXPath () + " (" + weight + ")]";
	}
	
	public TreeNode getPartnerOf (TreeNode t)
	{
		if (a == t)
			return b;
		if (b == t)
			return a;
		return null;
	}
}
