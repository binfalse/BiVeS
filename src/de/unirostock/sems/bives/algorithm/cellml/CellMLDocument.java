/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.CellMLReadException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLDocument
{
	
	private URI baseUri;
	private CellMLModel model;
	private TreeDocument doc;
	
	/*public CellMLDocument (File file, URI baseUri)
	{
		this.baseUri = baseUri;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	  DocumentBuilder db = dbf.newDocumentBuilder();
	  Document doc = db.parse(file);
	  
	  
	  readDocument (doc);
	}*/
	
	public CellMLDocument (TreeDocument doc, URI baseUri) throws CellMLReadException, BivesConsistencyException, BivesLogicalException, IOException, URISyntaxException
	{
		this.baseUri = baseUri;
	  this.doc = doc;
	  model = new CellMLModel (this, doc.getRoot ());
	}
	
	public URI getBaseUri ()
	{
		return baseUri;
	}
}
