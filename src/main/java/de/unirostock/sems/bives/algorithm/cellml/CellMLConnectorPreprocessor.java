/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.util.HashMap;
import java.util.Vector;


import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.general.IdConnector;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.ds.cellml.CellMLComponent;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.cellml.CellMLModel;
import de.unirostock.sems.bives.ds.cellml.CellMLUnitDictionary;
import de.unirostock.sems.bives.ds.cellml.CellMLUserUnit;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConnectionException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLConnectorPreprocessor
	extends Connector
{
	private Connector preprocessor;
	private CellMLDocument cellmlDocA, cellmlDocB;

	public CellMLConnectorPreprocessor (CellMLDocument cellmlDocA, CellMLDocument cellmlDocB)
	{
		super ();
		this.cellmlDocA = cellmlDocA;
		this.cellmlDocB = cellmlDocB;
	}
	
	public CellMLConnectorPreprocessor (Connector preprocessor)
	{
		super ();
		this.preprocessor = preprocessor;
	}
	
	@Override
	public void init (TreeDocument docA, TreeDocument docB) throws BivesConnectionException
	{
		super.init (cellmlDocA.getTreeDocument (), cellmlDocB.getTreeDocument ());
		
		// not yet initialized?
		if (preprocessor == null)
		{
			// then we'll use by default an id-connector...
			IdConnector id = new IdConnector ();
			id.init (docA, docB);
			id.findConnections ();
	
			conMgmt = id.getConnections ();
		}
		else
		{
			preprocessor.init (docA, docB);
			preprocessor.findConnections ();
	
			conMgmt = preprocessor.getConnections ();
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Connector#findConnections()
	 */
	@Override
	protected void connect () throws BivesConnectionException
	{
		CellMLModel modelA = cellmlDocA.getModel ();
		CellMLModel modelB = cellmlDocB.getModel ();
		
		// units
		CellMLUnitDictionary unitsA = modelA.getUnits ();
		CellMLUnitDictionary unitsB = modelB.getUnits ();
		HashMap<String, CellMLUserUnit> modelUnitsA = unitsA.getModelUnits ();
		HashMap<String, CellMLUserUnit> modelUnitsB = unitsB.getModelUnits ();
		for (String id : modelUnitsA.keySet ())
		{
			CellMLUserUnit cB = modelUnitsB.get (id);
			if (cB == null)
				continue;
			CellMLUserUnit cA = modelUnitsA.get (id);
			
			// both still unconnected? -> connect
			if (conMgmt.getConnectionForNode (cA.getDocumentNode ()) == null && conMgmt.getConnectionForNode (cB.getDocumentNode ()) == null)
				conMgmt.addConnection (new Connection (cA.getDocumentNode (), cB.getDocumentNode ()));
			else continue;
			
		}
		
		// components
		HashMap<String, CellMLComponent> componentsA = modelA.getComponents ();
		HashMap<String, CellMLComponent> componentsB = modelB.getComponents ();
		for (String id : componentsA.keySet ())
		{
			CellMLComponent cB = componentsB.get (id);
			if (cB == null)
				continue;
			CellMLComponent cA = componentsA.get (id);
			
			// both still unconnected? -> connect
			if (conMgmt.getConnectionForNode (cA.getDocumentNode ()) == null && conMgmt.getConnectionForNode (cB.getDocumentNode ()) == null)
				conMgmt.addConnection (new Connection (cA.getDocumentNode (), cB.getDocumentNode ()));
			else continue;
			
			
		}
		
	}
	
}
