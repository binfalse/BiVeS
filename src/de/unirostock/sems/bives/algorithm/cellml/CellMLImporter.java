/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.CellMLReadException;
import de.unirostock.sems.bives.tools.FileRetriever;


/**
 * @author Martin Scharm
 *
 */
public class CellMLImporter
extends CellMLEntity
{
	
	private String href;
	private DocumentNode node;
	
	public CellMLImporter (DocumentNode node, CellMLModel model) throws CellMLReadException
	{
		super (node, model);
		
		this.node = node;
		String href = node.getAttribute ("xlink:href");
		if (href == null)
			throw new CellMLReadException ("href attribute in import is empty");
		
	}
	
	
	
	public CellMLComponent getAdditionComponents ()
	{
		return null;
	}



	public void parse () throws IOException, URISyntaxException
	{
		LOGGER.info ("parsing import from " + href + " (base uri is: "+model.getBaseUri ()+")");
		File tmp = File.createTempFile ("cellmlimporter", "cellml");
		
		URI fileUri = FileRetriever.getFile (href, model.getBaseUri (), tmp);
		
		CellMLDocument toImport = new CellMLDocument (tmp, fileUri);
		CellMLUnitDictionary units = toImport.getUnits ();
		
		NodeList nodes = node.getChildNodes ();
		int numNodes = nodes.getLength ();
		for (int i = 0; i < numNodes; i++)
		{
			if (nodes.item (i).getNodeType () != Node.ELEMENT_NODE)
				continue;
			
			Element e = (Element) nodes.item (i);
			
			if (e.getTagName ().equals ("units"))
			{
				// import units
				CellMLUnit u = units.getUnit (e.getAttribute ("units_ref"), null);
				if (u instanceof CellMLUserUnit)
				{
					CellMLUserUnit uu = (CellMLUserUnit) u;
					String name = e.getAttribute ("name");
					if (name == null || name.length () < 1)
						throw new CellMLReadException ("unit import doesn't declare a valid name.");
					uu.setName (name);
					model.addUnit ((CellMLUserUnit) units.getUnit (e.getAttribute ("units_ref"), null));
				}
				else
				{
					throw new CellMLReadException ("unit import of base unit detected...");
				}
				
			}
			else if (e.getTagName ().equals ("component"))
			{
				// import component
				CellMLComponent c = toImport.getComponent (e.getAttribute ("component_ref"));
				String name = e.getAttribute ("name");
				if (name == null || name.length () < 1)
					throw new CellMLReadException ("component import doesn't declare a valid name.");
				c.setName (name);
				model.addComponent (c);
			}
		}
		
	}
}
