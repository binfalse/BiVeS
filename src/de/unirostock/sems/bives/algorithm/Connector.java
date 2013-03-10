/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.xml.TreeDocument;


/**
 * The Class Connector, intended to find node-correspondences between two trees.
 *
 * @author Martin Scharm
 */
public abstract class Connector
{
	
	/** The connection manager, holding node-correspondences. */
	protected ConnectionManager conMgmt;
	
	/** The documents a and b. */
	protected TreeDocument docA, docB;
	
	/**
	 * Instantiates a new connector.
	 *
	 * @param docA the document A
	 * @param docB the document B
	 */
	public Connector()
	{
	}
	
	/**
	 * Instantiates a new connector with biased connections.
	 *
	 * @param docA the document A
	 * @param docB the document B
	 */
	/*public Connector(ConnectionManager conMgmt)
	{
		this.conMgmt = conMgmt;
	}*/
	
	/**
	 * Inits the connector.
	 *
	 * @param docA the document A
	 * @param docB the document B
	 */
	public void init (TreeDocument docA, TreeDocument docB)
	{
		this.docA = docA;
		this.docB = docB;
		conMgmt = new ConnectionManager (docA, docB);
	}
	
	/**
	 * Inherit to search for connections.
	 */
	protected abstract void connect ();
	
	
	public final void findConnections ()
	{
		connect ();

		docA.getRoot ().resetModifications ();
		docA.getRoot ().evaluate (conMgmt);
		
		docB.getRoot ().resetModifications ();
		docB.getRoot ().evaluate (conMgmt);
	}
	
	/**
	 * Gets the connections.
	 *
	 * @return the connections
	 */
	public final ConnectionManager getConnections ()
	{
		return conMgmt;
	}
}
