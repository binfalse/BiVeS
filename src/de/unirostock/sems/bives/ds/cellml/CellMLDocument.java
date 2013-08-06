/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesFlattenException;
import de.unirostock.sems.bives.exception.BivesImportException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.tools.Tools;
import de.unirostock.sems.bives.tools.TreeTools;
import de.unirostock.sems.bives.tools.XmlTools;


/**
 * @author Martin Scharm
 *
 */
public class CellMLDocument
{
	
	private CellMLModel model;
	private TreeDocument doc;
	
	public CellMLDocument (TreeDocument doc) throws BivesCellMLParseException, BivesConsistencyException, BivesLogicalException, IOException, URISyntaxException, ParserConfigurationException, SAXException, BivesImportException
	{
	  this.doc = doc;
	  if (!doc.getRoot ().getTagName ().equals ("model"))
	  	throw new BivesCellMLParseException ("cellml document does not define a model");
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
	public boolean containsImports ()
	{
		return model.containsImports ();
	}
	
	public void flatten () throws BivesFlattenException, BivesConsistencyException
	{
		model.flatten ();
	}
	
	public void write (File dest) throws IOException, TransformerException
	{
		String s = XmlTools.prettyPrintDocument (TreeTools.getDoc (doc));
		BufferedWriter bw = new BufferedWriter (new FileWriter (dest));
		bw.write (s);
		bw.close ();
	}

	public TreeDocument getTreeDocument ()
	{
		return doc;
	}
}
