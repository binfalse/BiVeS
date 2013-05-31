/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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
	
	private CellMLModel model;
	private TreeDocument doc;
	
	public CellMLDocument (TreeDocument doc) throws CellMLReadException, BivesConsistencyException, BivesLogicalException, IOException, URISyntaxException, ParserConfigurationException, SAXException
	{
	  this.doc = doc;
	  if (!doc.getRoot ().getTagName ().equals ("model"))
	  	throw new CellMLReadException ("cellml document does not define a model");
	  model = new CellMLModel (this, doc.getRoot ());
	}
	
	public CellMLModel getModel ()
	{
		return model;
	}
	
	public URI getBaseUri ()
	{
		return doc.getBaseUri ();
	}
	public void debug (String prefix)
	{
		System.out.println (prefix + "cellml: " + doc.getBaseUri ());
		model.debug (prefix + "  ");
	}
}
