/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.util.HashMap;
import java.util.Vector;

import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesConnectionException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLConnectorPreprocessor
	extends Connector
{

	public CellMLConnectorPreprocessor ()
	{
		super ();
	}

	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Connector#connect()
	 */
	@Override
	protected void connect () throws BivesConnectionException
	{
		//System.out.println ("get call");
		// connect components by name
		HashMap<String, CellMLComponent> componentMapper = new HashMap<String, CellMLComponent> ();
		Vector<DocumentNode> components = docA.getNodesByTag ("component");
		if (components != null)
			for (DocumentNode component : components)
			{
				//System.out.println ("found component A: " + component.getAttribute ("name"));
				CellMLComponent comp = new CellMLComponent (component, null, null);
				componentMapper.put (component.getAttribute ("name"), comp);
			}
		components = docB.getNodesByTag ("component");
		if (components != null)
			for (DocumentNode component : components)
			{
				//System.out.println ("found component B: " + component.getAttribute ("name"));

				CellMLComponent compA = componentMapper.get (component.getAttribute ("name"));
				if (compA != null)
				{
					//System.out.println ("connecting: " + component.getAttribute ("name"));
					compA.setTreeB (component);
					compA.connect (conMgmt);
				}
			}
		
	}
	
}
