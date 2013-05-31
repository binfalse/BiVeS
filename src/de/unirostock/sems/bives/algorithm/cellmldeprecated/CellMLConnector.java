/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellmldeprecated;

import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesConnectionException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLConnector
	extends Connector
{

	public CellMLConnector ()
	{
		super ();
	}

	@Override
	public void init (TreeDocument docA, TreeDocument docB) throws BivesConnectionException
	{
		super.init (docA, docB);

		// preprocessor connects by name
		// xy propagates connections
		XyDiffConnector id = new XyDiffConnector (new CellMLConnectorPreprocessor ());
		id.init (docA, docB);
		id.findConnections ();

		conMgmt = id.getConnections ();
		
	}
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Connector#connect()
	 */
	@Override
	protected void connect ()
	{
		// remove senseless stuff
		
	}
	
}
