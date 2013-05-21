/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesConnectionException;


/**
 * The Class Connector, intended to find node-correspondences between two trees.
 *
 * @author Martin Scharm
 */
public abstract class Connector
{
	
	/** The connection manager, holding node-correspondences. */
	protected ClearConnectionManager conMgmt;
	
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
	 * @throws BivesConnectionException 
	 */
	public void init (TreeDocument docA, TreeDocument docB) throws BivesConnectionException
	{
		this.docA = docA;
		this.docB = docB;
		conMgmt = new ClearConnectionManager (docA, docB);
	}
	
	/**
	 * Inherit to search for connections.
	 */
	protected abstract void connect () throws BivesConnectionException;
	
	
	public final void findConnections () throws BivesConnectionException
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
	public final ClearConnectionManager getConnections ()
	{
		return conMgmt;
	}
}
