/**
 * 
 */
package de.unirostock.sems.bives.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;


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
	
	private static DocumentBuilder builder;
	private Vector<Exception> exceptions;
	private int type;
	
	
	public DocumentClassifier () throws ParserConfigurationException
	{
		builder = DocumentBuilderFactory.newInstance ().newDocumentBuilder ();
		builder.setErrorHandler (new ErrorHandler ()
		{
			
			@Override
			public void warning (SAXParseException e) throws SAXException
			{
				throw e;
			}
			
			
			@Override
			public void fatalError (SAXParseException e) throws SAXException
			{
				throw e;
			}
			
			
			@Override
			public void error (SAXParseException e) throws SAXException
			{
				throw e;
			}
		});
	}
	
	public  Vector<Exception> getExceptions ()
	{
		return exceptions;
	}
	
	public int classify (InputStream model, URI baseUri)
	{
		exceptions = new Vector<Exception> ();
		type = UNKNOWN;
		
		try
		{
			TreeDocument doc = new TreeDocument (builder.parse (model), new XyWeighter (), baseUri);
			type |= XML;
			
			// is sbml?
			isSBML (doc);
			
			// is cellml?
			isCellML (doc);
		}
		catch (SAXException | IOException e)
		{
			exceptions.add (e);
		}
		
		return type;
	}
	
	public int classify (String model)
	{
		exceptions = new Vector<Exception> ();
		return classify (new ByteArrayInputStream(model.getBytes ()), null);
	}
	
	public int classify (File model)
	{
		exceptions = new Vector<Exception> ();
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
		try
		{
			new SBMLDocument (doc);
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
			new CellMLDocument (doc);
			type |= CELLML;
		}
		catch (Exception e)
		{
			exceptions.add (e);
		}
	}
	
}
