/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import java.util.HashMap;
import java.util.Vector;


import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConnectionException;


/**
 * @author Martin Scharm
 *
 */
public class ClearConnectionManager
{
	private Vector<Connection> connections;
	private HashMap<TreeNode, Connection> conByTree1, conByTree2;
	private TreeDocument docA, docB;
	
	public ClearConnectionManager (TreeDocument docA, TreeDocument docB)
	{
		this.docA = docA;
		this.docB = docB;
		connections = new Vector<Connection> ();
		conByTree1 = new HashMap<TreeNode, Connection> ();
		conByTree2 = new HashMap<TreeNode, Connection> ();
	}
	
	/**
	 * Instantiates a new connection manager as a copy of toCopy.
	 *
	 * @param toCopy the connection manager to copy
	 * @throws BivesConnectionException 
	 */
	public ClearConnectionManager (ClearConnectionManager toCopy) throws BivesConnectionException
	{
		this.docA = toCopy.docA;
		this.docB = toCopy.docB;
		connections = new Vector<Connection> ();
		conByTree1 = new HashMap<TreeNode, Connection> ();
		conByTree2 = new HashMap<TreeNode, Connection> ();
		for (Connection c : toCopy.connections)
			addConnection (new Connection (c));
	}
	

	public void scaleWeightOfAllConnections (double value)
	{
		for (Connection c : connections)
			c.scaleWeight (value);
	}
	public void addWeightToAllConnections (double value)
	{
		for (Connection c : connections)
			c.addWeight (value);
	}
	public void setWeightOfAllConnections (double value)
	{
		for (Connection c : connections)
			c.setWeight (value);
	}
	
	
	/**
	 * Adds a new connection unless connection exists. If connection exists, the weights are summed. Throws exception if one of the nodes already connected.
	 *
	 * @param c the c connection to add
	 * @return true, if c was added
	 * @throws BivesConnectionException 
	 */
	public boolean addConnection (Connection c) throws BivesConnectionException
	{
		// checking for 1st tree
		Connection cc = conByTree1.get (c.getTreeA ());
		if (cc != null)
			throw new BivesConnectionException ("node " + c.getTreeA ().getXPath () + " already connected. cannot add another connection");
		// checking for 2nd tree
		cc = conByTree2.get (c.getTreeB ());
		if (cc != null)
			throw new BivesConnectionException ("node " + c.getTreeB ().getXPath () + " already connected. cannot add another connection");
		

		// adding for 1st tree
		conByTree1.put (c.getTreeA (), c);
		// same for 2nd tree
		conByTree2.put (c.getTreeB (), c);
		// add this connection to vector
		connections.add (c);
		
		return true;
	}
	
	/**
	 * Create a ConnectionManager that combines all connections from this ConnectionManager and another instance. Weights are taken from this instance.
	 * 
	 * E.g. given this is A and you pass B you'll get C = A u B
	 * 
	 * Use with caution, very expensive!
	 *
	 * @param cmgmt the ConnectionManager to join
	 * @return the united connection manager, containing all connections
	 */
	public ClearConnectionManager union (ClearConnectionManager cmgmt)
	{
		if (docA != cmgmt.docA || docB != cmgmt.docB)
		{
			LOGGER.error ("cannot join connection managers from different docs!");
			return null;
		}
		
		ClearConnectionManager union = new ClearConnectionManager (docA, docB);
		
		for (Connection c : connections)
		{
			try
			{
				union.addConnection (new Connection (c));
			}
			catch (BivesConnectionException e)
			{
				LOGGER.warn ("got an exception while joining connection managers", e);
			}
		}
		
		for (Connection c : cmgmt.connections)
			try
			{
				union.addConnection (new Connection (c));
			}
			catch (BivesConnectionException e)
			{
				LOGGER.info ("got an exception while joining connection managers, connection probably already included from first cmgr.", e);
			}
		
		return union;
	}
	
	/**
	 * Create a ConnectionManager that contains only connections from this ConnectionManager and_another instance. Weights are taken from this instance.
	 * 
	 * E.g. given this is A and you pass B you'll get C = A n B
	 * 
	 * Use with caution, very expensive!
	 *
	 * @param cmgmt the cmgmt
	 * @return the connection manager
	 */
	public ClearConnectionManager intersection (ClearConnectionManager cmgmt)
	{
		if (docA != cmgmt.docA || docB != cmgmt.docB)
		{
			LOGGER.error ("cannot intersect connection managers from different docs!");
			return null;
		}
		
		ClearConnectionManager intersection = new ClearConnectionManager (docA, docB);
		
		for (Connection c : connections)
		{
			
			if (cmgmt.getConnectionOfNodes (c.getTreeA (), c.getTreeB ()) != null)
				try
				{
					intersection.addConnection (c);
				}
				catch (BivesConnectionException e)
				{
					LOGGER.error ("got an exception while intersecting connection managers. this shouldn't happen!", e);
				}
		}
		
		return intersection;
	}
	
	/**
	 * Create a ConnectionManager that contains only connections from this ConnectionManager which are not included in another instance. Weights are taken from this instance.
	 * 
	 * E.g. given this is A and you pass B you'll get C = A \ B
	 * 
	 * Use with caution, very expensive!
	 *
	 * @param cmgmt the cmgmt
	 * @return the connection manager
	 */
	public ClearConnectionManager setDiff (ClearConnectionManager cmgmt)
	{
		if (docA != cmgmt.docA || docB != cmgmt.docB)
		{
			LOGGER.error ("cannot calc the set diff of connection managers from different docs!");
			return null;
		}
		
		ClearConnectionManager intersection = new ClearConnectionManager (docA, docB);
		
		for (Connection c : connections)
		{
			if (cmgmt.getConnectionOfNodes (c.getTreeA (), c.getTreeB ()) == null)
				try
				{
					intersection.addConnection (c);
				}
				catch (BivesConnectionException e)
				{
					LOGGER.error ("got an exception while ste-diffing connection managers. this shouldn't happen!", e);
				}
		}
		
		return intersection;
	}
	
	/**
	 * Create a ConnectionManager that contains only connections from this ConnectionManager and another instance and are not included in both managers. Weights are taken from this instance.
	 * 
	 * E.g. given this is A and you pass B you'll get C = (A u B) \ (A n B) = (A \ B) u (B \ A)
	 * 
	 * Use with caution, very VERY expensive!
	 *
	 * @param cmgmt the cmgmt
	 * @return the connection manager
	 */
	public ClearConnectionManager symDiff (ClearConnectionManager cmgmt)
	{
		return setDiff (cmgmt).union (cmgmt.setDiff (this));
	}
	
	/**
	 * Drop all connections of a node. In general you'll find a correct connection for a node, so you'll drop all connections and afterwards add the good one.
	 *
	 * @param node the node to liberate
	 */
	public void dropConnection (TreeNode node)
	{

		Connection c = conByTree1.get (node);
		if (c == null)
		{
			c = conByTree2.get (node);
			if (c != null)
				conByTree2.remove (node);
		}
		else
			conByTree1.remove (node);
		
		if (c != null)
			connections.remove (c);
	}
	
	public void dropConnection (Connection c)
	{
		if (connections.remove (c))
		{
			conByTree1.remove (c.getTreeA ());
			conByTree2.remove (c.getTreeB ());
		}
	}
	
	/**
	 * Gets the connections for node. Returns null if there is no connection for this node.
	 *
	 * @param node the node
	 * @return the connections for node
	 */
	public Connection getConnectionForNode (TreeNode node)
	{
		Connection c = conByTree1.get (node);
		if (c != null)
			return c;
		
		c = conByTree2.get (node);
		return c;
	}
	
	/**
	 * Gets the connection of two certain nodes. Returns null if nodes are not connected.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the connection of nodes
	 */
	public Connection getConnectionOfNodes (TreeNode a, TreeNode b)
	{
		Connection c = conByTree1.get (a);
		if (c != null && c.getTreeB () == b)
			return c;
		
		return null;
	}
	
	public String toString ()
	{
		StringBuilder sb = new StringBuilder ("connections: "+connections.size ()+"-"+conByTree1.size ()+"-"+conByTree2.size ()+"\n");
		
		for (Connection c : connections)
			sb.append (c + "\n");
		
		return sb.toString ();
	}
	
	/**
	 * Gets the unmatched nodes in a subtree.
	 *
	 * @param subtree the subtree
	 * @param unmatched the vector in which the unmatched nodes are collected
	 * @return the the vector in which the unmatched nodes are collected
	 */
	public Vector<TreeNode> getUnmatched (TreeNode subtree, Vector<TreeNode> unmatched)
	{
		Connection c = getConnectionForNode (subtree);
		if (c == null)
			unmatched.add (subtree);

		if (subtree.getType () == TreeNode.DOC_NODE)
		{
			DocumentNode dn = (DocumentNode) subtree;
			for (TreeNode kid : dn.getChildren ())
				getUnmatched (kid, unmatched);
		}
		return unmatched;
	}
	
	/**
	 * Delete nodes of a vector which have connections.
	 *
	 * @param vec the vector of nodes to modify
	 */
	public void deleteMatchedNodes (Vector<TreeNode> vec)
	{
		for (int c = vec.size () - 1; c >= 0; c--)
		{
			Connection ccs = getConnectionForNode (vec.elementAt (c));
			if (ccs != null)
				vec.remove (c);
		}
	}
	
	/**
	 * Check if parents connected. Parents are connected if there exists a connection between the parents of both nodes or both nodes don't have a parent (~> parents of two roots are connected!)
	 *
	 * @param c the c
	 * @return true, if connected
	 */
	public boolean parentsConnected (Connection c)
	{
		TreeNode pA = c.getTreeA ().getParent (), pB = c.getTreeB ().getParent ();
		if (pA == null && pB == null)
			// both roots
			return true;
		if (pA == null || pB == null)
			// one root one not-root
			return false;
		
		return getConnectionOfNodes (pA, pB) != null;
		
	}
}
