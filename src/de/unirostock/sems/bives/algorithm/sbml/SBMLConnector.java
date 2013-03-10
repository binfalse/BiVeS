/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import java.util.Vector;

import org.apache.log4j.Logger;

import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class SBMLConnector
	extends Connector
{
	private final static Logger LOGGER = Logger.getLogger(SBMLConnector.class.getName());
	private Connector preprocessor;

	public SBMLConnector ()
	{
		super ();
	}
	
	public SBMLConnector (Connector preprocessor)
	{
		super ();
		this.preprocessor = preprocessor;
	}
	

	@Override
	public void init (TreeDocument docA, TreeDocument docB)
	{
		// TODO: maybe preporcessing -> instead of id's use annotations/ontologies etc
		// use id's
		// use variables for rules
		super.init (docA, docB);

		// preprocessor connects by id and stuff
		// xy propagates connections
		XyDiffConnector id = new XyDiffConnector (new SBMLConnectorPreprocessor ());
		id.init (docA, docB);
		id.findConnections ();

		conMgmt = id.getConnections ();
		
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Connector#findConnections()
	 */
	@Override
	protected void connect ()
	{
		
		// post processing
		
		// following nodes cannot have a connection with changes...
		Vector<DocumentNode> lists = docA.getNodesByTag ("listOfModifiers");
		lists.addAll (docA.getNodesByTag ("listOfProducts"));
		lists.addAll (docA.getNodesByTag ("listOfReactants"));
		lists.addAll (docA.getNodesByTag ("listOfEventAssignments"));
		lists.addAll (docA.getNodesByTag ("modifierSpeciesReference"));
		lists.addAll (docA.getNodesByTag ("speciesReference"));
		lists.addAll (docA.getNodesByTag ("trigger"));
		lists.addAll (docA.getNodesByTag ("eventAssignment"));
		lists.addAll (docA.getNodesByTag ("delay"));
		for (DocumentNode tn : lists)
		{
			Vector<Connection> cons = conMgmt.getConnectionsForNode (tn);
			if (cons == null)
				continue;
			
			boolean unconnect = false;
			for (Connection c : cons)
			{
				TreeNode partner = c.getTreeB ();
				if (tn.networkDiffers (partner, conMgmt, c))
				{
					unconnect = true;
					break;
				}
			}
			if (unconnect)
			{
				//System.out.println ("dropping connections of " + tn.getXPath ());
				conMgmt.dropConnections (tn);
			}
			/*if (tn)
			{
				System.out.println ("dropping connections of " + tn.getXPath ());
				conMgmt.dropConnections (tn);
			}*/
		}
		
		// different kind of modifiers?
		for (TreeNode tn : docA.getNodesByTag ("modifierSpeciesReference"))
		{
			Vector<Connection> cons = conMgmt.getConnectionsForNode (tn);
			if (cons == null)
				continue;
			
			for (Connection c : cons)
			{
				DocumentNode a = (DocumentNode) c.getTreeA ();
				DocumentNode b = (DocumentNode) c.getTreeB ();
				if (!ChemicalReactionNetwork.resolvModSBO (a.getAttribute ("sboTerm")).equals (ChemicalReactionNetwork.resolvModSBO (a.getAttribute ("sboTerm"))))
					conMgmt.dropConnection (c);
			}
			
		}
		
	}
	
}
