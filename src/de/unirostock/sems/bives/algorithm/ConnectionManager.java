/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class ConnectionManager
{
	private final static Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
	private Vector<Connection> connections;
	private HashMap<TreeNode, Vector<Connection>> conByTree1, conByTree2;
	private TreeDocument docA, docB;
	
	public ConnectionManager (TreeDocument docA, TreeDocument docB)
	{
		this.docA = docA;
		this.docB = docB;
		connections = new Vector<Connection> ();
		conByTree1 = new HashMap<TreeNode, Vector<Connection>> ();
		conByTree2 = new HashMap<TreeNode, Vector<Connection>> ();
	}
	
	/**
	 * Instantiates a new connection manager as a copy of toCopy.
	 *
	 * @param toCopy the connection manager to copy
	 */
	public ConnectionManager (ConnectionManager toCopy)
	{
		this.docA = toCopy.docA;
		this.docB = toCopy.docB;
		connections = new Vector<Connection> ();
		conByTree1 = new HashMap<TreeNode, Vector<Connection>> ();
		conByTree2 = new HashMap<TreeNode, Vector<Connection>> ();
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
	 * Adds a new connection unless connection exists. If connection exists, the weights are summed. Same result as calling `addConnection (c, true)`.
	 *
	 * @param c the c connection to add
	 * @return true, if c was added
	 */
	public boolean addConnection (Connection c)
	{
		return addConnection (c, true);
	}
	
	/**
	 * Adds a new connection. If checkExistence is true this method will first check if c.getTreeA () and c.getTreeB () are already connected by another connection.
	 *
	 * @param c the c
	 * @param checkExistence the check whether the connection already exists
	 * @return true, if connection was added
	 */
	private boolean addConnection (Connection c, boolean checkExistence)
	{
		// should we check for existing connection?
		if (checkExistence)
		{
			Connection c2 = getConnectionOfNodes (c.getTreeA (), c.getTreeB ());
			if (c2 != null)
			{
				c2.addWeight (c.getWeight ());
				return false;
			}
		}
		
		// adding for 1st tree
		Vector<Connection> vc = conByTree1.get (c.getTreeA ());
		if (vc == null)
		{
			vc = new Vector<Connection> ();
			conByTree1.put (c.getTreeA (), vc);
		}
		vc.add (c);
		// same for 2nd tree
		vc = conByTree2.get (c.getTreeB ());
		if (vc == null)
		{
			vc = new Vector<Connection> ();
			conByTree2.put (c.getTreeB (), vc);
		}
		vc.add (c);
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
	public ConnectionManager union (ConnectionManager cmgmt)
	{
		if (docA != cmgmt.docA || docB != cmgmt.docB)
		{
			LOGGER.error ("cannot join connection managers from different docs!");
			return null;
		}
		
		ConnectionManager union = new ConnectionManager (docA, docB);
		
		for (Connection c : connections)
			union.addConnection (new Connection (c), true);
		
		for (Connection c : cmgmt.connections)
			union.addConnection (new Connection (c), true);
		
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
	public ConnectionManager intersection (ConnectionManager cmgmt)
	{
		if (docA != cmgmt.docA || docB != cmgmt.docB)
		{
			LOGGER.error ("cannot intersect connection managers from different docs!");
			return null;
		}
		
		ConnectionManager intersection = new ConnectionManager (docA, docB);
		
		for (Connection c : connections)
		{
			if (cmgmt.getConnectionOfNodes (c.getTreeA (), c.getTreeB ()) != null)
				intersection.addConnection (c, true);
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
	public ConnectionManager setDiff (ConnectionManager cmgmt)
	{
		if (docA != cmgmt.docA || docB != cmgmt.docB)
		{
			LOGGER.error ("cannot calc the set diff of connection managers from different docs!");
			return null;
		}
		
		ConnectionManager intersection = new ConnectionManager (docA, docB);
		
		for (Connection c : connections)
		{
			if (cmgmt.getConnectionOfNodes (c.getTreeA (), c.getTreeB ()) == null)
				intersection.addConnection (c, true);
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
	public ConnectionManager symDiff (ConnectionManager cmgmt)
	{
		return setDiff (cmgmt).union (cmgmt.setDiff (this));
	}
	
	/**
	 * Drop all connections of a node. In general you'll find a correct connection for a node, so you'll drop all connections and afterwards add the good one.
	 *
	 * @param node the node to liberate
	 */
	public void dropConnections (TreeNode node)
	{
		// is this node from tree a?
		Vector<Connection> vc = conByTree1.get (node);
		if (vc != null)
		{
			// for all connections of this node
			for (Connection c : vc)
			{
				// remove the connection from vectore
				connections.remove (c);
				// and remove the connection from its mate
				conByTree2.get (c.getTreeB ()).remove (c);
			}
			// in the end clear all connections of this node
			vc.clear ();
		}
		
		// and the same if the node is from tree b...
		vc = conByTree2.get (node);
		if (vc != null)
		{
			for (Connection c : vc)
			{
				connections.remove (c);
				conByTree1.get (c.getTreeA ()).remove (c);
			}
			vc.clear ();
		}
	}
	
	public void dropConnection (Connection c)
	{
		connections.remove (c);

		Vector<Connection> vc = conByTree1.get (c.getTreeA ());
		if (vc != null)
			vc.remove (c);
		vc = conByTree2.get (c.getTreeB ());
		if (vc != null)
			vc.remove (c);
	}
	
	/**
	 * Gets the connections for node. Returns null if there is no connection for this node.
	 *
	 * @param node the node
	 * @return the connections for node
	 */
	public Vector<Connection> getConnectionsForNode (TreeNode node)
	{
		// might return null
		Vector<Connection> vc = conByTree1.get (node);
		if (vc != null)
		{
			if (vc.size () > 0)
				return vc;
			else
				return null;
		}
		vc = conByTree2.get (node);
		if (vc != null && vc.size () > 0)
			return vc;
		return null;
	}
	
	public Connection getConnectionOfNodes (TreeNode a, TreeNode b)
	{
		// might return null
		Vector<Connection> vc = conByTree1.get (a);
		if (vc != null)
		{
			for (Connection c : vc)
			{
				if (c.getTreeB () == b)
					return c;
			}
		}
		return null;
	}
	
	public String toString ()
	{
		StringBuilder sb = new StringBuilder ("connections: "+connections.size ()+"-"+conByTree1.size ()+"-"+conByTree2.size ()+"\n");
		
		for (Connection c : connections)
			sb.append (c + "\n");
		
		return sb.toString ();
	}
	
	public Vector<TreeNode> getUnmatched (TreeNode subtree, Vector<TreeNode> unmatched)
	{
		Vector<Connection> con = getConnectionsForNode (subtree);
		if (con == null || con.size () < 1)
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
			Vector<Connection> ccs = getConnectionsForNode (vec.elementAt (c));
			if (ccs != null && ccs.size () > 0)
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
