/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
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
		href = node.getAttribute ("xlink:href");
		if (href == null)
			throw new CellMLReadException ("href attribute in import is empty");
		
	}



	public void parse () throws IOException, URISyntaxException, ParserConfigurationException, SAXException, CellMLReadException, BivesConsistencyException, BivesLogicalException
	{
		URI baseUri = model.getDocument ().getBaseUri ();
		LOGGER.info ("parsing import from " + href + " (base uri is: "+baseUri+")");
		File tmp = File.createTempFile ("cellmlimporter", "cellml");
		
		URI fileUri = FileRetriever.getFile (href, baseUri, tmp);
	  
	  TreeDocument tdoc = new TreeDocument (DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tmp), null, fileUri);
		
		CellMLDocument toImport = new CellMLDocument (tdoc);
		CellMLModel modelToImport = toImport.getModel ();
		
		CellMLUnitDictionary units = modelToImport.getUnits ();
		Vector<TreeNode> kids = node.getChildrenWithTag ("units");
		for (TreeNode kid : kids)
		{
			DocumentNode ukid = (DocumentNode) kid;
			String ref = ukid.getAttribute ("units_ref");
			String name = ukid.getAttribute ("name");
			
			if (ref == null || name == null || ref.length () < 1 || name.length () < 1)
				throw new CellMLReadException ("unit import should define a name _and_ a units_ref! (name: "+name+", units_ref: "+ref+")");
			

			CellMLUnit u = units.getUnit (ref, null);
			if (u instanceof CellMLUserUnit)
			{
				CellMLUserUnit uu = (CellMLUserUnit) u;
				uu.setName (name);
				model.addUnit (uu);
				LOGGER.info ("imported unit " + name + " from " + ref + "@" + href);
			}
			else
			{
				throw new BivesConsistencyException ("unit import of base unit detected...");
			}
		}
		

		kids = node.getChildrenWithTag ("component");
		for (TreeNode kid : kids)
		{
			DocumentNode ckid = (DocumentNode) kid;
			String ref = ckid.getAttribute ("component_ref");
			String name = ckid.getAttribute ("name");
			
			if (ref == null || name == null || ref.length () < 1 || name.length () < 1)
				throw new CellMLReadException ("component import should define a name _and_ a component_ref! (name: "+name+", component_ref: "+ref+")");
			
			CellMLComponent c = modelToImport.getComponent (ref);
			c.setName (name);
			model.addComponent (c);
			LOGGER.info ("imported component " + name + " from " + ref + "@" + href);
		}
		
	}
}