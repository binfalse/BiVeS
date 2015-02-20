/**
 * 
 */
package de.unirostock.sems.bives.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.unirostock.sems.bives.cellml.parser.CellMLDocument;
import de.unirostock.sems.bives.sbml.parser.SBMLDocument;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class DocumentClassifier.
 *
 * @author Martin Scharm
 */
public class DocumentClassifier
{
	
	/** The Constant UNKNOWN. */
	public static final int UNKNOWN = 0;
	
	/** The Constant XML. */
	public static final int XML = 1;
	
	/** The Constant SBML. */
	public static final int SBML= 2;
	
	/** The Constant CELLML. */
	public static final int CELLML = 4;
	
	/** The exceptions. */
	private List<Exception> exceptions;
	
	/** The type. */
	private int type;
	
	/** The sbml. */
	private SBMLDocument sbml;
	
	/** The cellml. */
	private CellMLDocument cellml;
	
	/** The xml. */
	private TreeDocument xml;
	
	/**
	 * Clear.
	 */
	private void clear ()
	{
		sbml = null;
		cellml = null;
		xml = null;
	}
	
	/**
	 * Gets the xml document.
	 *
	 * @return the xml document
	 */
	public TreeDocument getXmlDocument ()
	{
		return xml;
	}
	
	/**
	 * Gets the cell ml document.
	 *
	 * @return the cell ml document
	 */
	public CellMLDocument getCellMlDocument ()
	{
		return cellml;
	}
	
	/**
	 * Gets the sbml document.
	 *
	 * @return the sbml document
	 */
	public SBMLDocument getSbmlDocument ()
	{
		return sbml;
	}
	
	/**
	 * Gets the exceptions.
	 *
	 * @return the exceptions
	 */
	public  List<Exception> getExceptions ()
	{
		return exceptions;
	}
	
	/**
	 * Classify.
	 *
	 * @param model the model
	 * @param baseUri the base uri
	 * @return the int
	 */
	public int classify (InputStream model, URI baseUri)
	{
		exceptions = new ArrayList<Exception> ();
		type = UNKNOWN;
		clear ();
		try
		{
			return classify (new TreeDocument (XmlTools.readDocument (model), baseUri));
		}
		catch (Exception e)
		{
			exceptions.add (e);
		}
		
		return type;
	}

	/**
	 * Classify.
	 *
	 * @param model the model
	 * @return the int
	 */
	public int classify (TreeDocument model)
	{
		type = UNKNOWN;
		clear ();
		
		xml = model;
		type |= XML;
		
		// is sbml?
		isSBML (model);
		
		// is cellml?
		isCellML (model);
		
		return type;
	}
	
	
	
	/**
	 * Classify.
	 *
	 * @param model the model
	 * @return the int
	 */
	public int classify (String model)
	{
		exceptions = new ArrayList<Exception> ();
		return classify (new ByteArrayInputStream(model.getBytes ()), null);
	}
	
	/**
	 * Classify.
	 *
	 * @param model the model
	 * @return the int
	 */
	public int classify (File model)
	{
		exceptions = new ArrayList<Exception> ();
		try
		{
			return classify (new FileInputStream (model), model.toURI ());
		}
		catch (FileNotFoundException e)
		{
			exceptions.add (e);
		}
		return UNKNOWN;
	}
	
	/**
	 * Checks if is sbml.
	 *
	 * @param doc the doc
	 */
	private void isSBML (TreeDocument doc)
	{
		exceptions = new ArrayList<Exception> ();
		try
		{
			sbml = new SBMLDocument (doc);
			type |= SBML;
		}
		catch (Exception e)
		{
			exceptions.add (e);
		}
	}
	
	/**
	 * Checks if is cell ml.
	 *
	 * @param doc the doc
	 */
	private void isCellML (TreeDocument doc)
	{
		try
		{
			cellml = new CellMLDocument (doc);
			type |= CELLML;
		}
		catch (Exception e)
		{
			exceptions.add (e);
		}
	}

	/**
	 * Human readable.
	 *
	 * @param type the type
	 * @return the string
	 */
	public static String humanReadable (int type)
	{
		String ret = "";
		if ((type & XML) != 0)
			ret += ("XML,");
		if ((type & CELLML) != 0)
			ret += ("CellML,");
		if ((type & SBML) != 0)
			ret += ("SBML,");
		if (ret.length () > 0)
		return ret.substring (0, ret.length () - 1);
		return "unknown type";
	}
	
}
