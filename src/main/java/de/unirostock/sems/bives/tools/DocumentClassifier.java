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
 * @author Martin Scharm
 *
 */
public class DocumentClassifier
{
	public static final int UNKNOWN = 0;
	public static final int XML = 1;
	public static final int SBML= 2;
	public static final int CELLML = 4;
	
	private List<Exception> exceptions;
	private int type;
	
	private SBMLDocument sbml;
	private CellMLDocument cellml;
	private TreeDocument xml;
	
	private void clear ()
	{
		sbml = null;
		cellml = null;
		xml = null;
	}
	
	public TreeDocument getXmlDocument ()
	{
		return xml;
	}
	
	public CellMLDocument getCellMlDocument ()
	{
		return cellml;
	}
	
	public SBMLDocument getSbmlDocument ()
	{
		return sbml;
	}
	
	public  List<Exception> getExceptions ()
	{
		return exceptions;
	}
	
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
	
	
	
	public int classify (String model)
	{
		exceptions = new ArrayList<Exception> ();
		return classify (new ByteArrayInputStream(model.getBytes ()), null);
	}
	
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
