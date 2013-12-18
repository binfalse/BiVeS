/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;


/**
 * @author martin
 *
 */
public abstract class Single
{
	protected File a;
	protected TreeDocument treeA;

	public Single (File a) throws ParserConfigurationException, BivesDocumentParseException, FileNotFoundException, SAXException, IOException
	{
		this.a = a;

		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		treeA = new TreeDocument (builder.parse (new FileInputStream (a)), new XyWeighter (), a.toURI ());
	}

	public Single (String a) throws ParserConfigurationException, BivesDocumentParseException, FileNotFoundException, SAXException, IOException
	{
		this.a = null;

		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		treeA = new TreeDocument (builder.parse (new ByteArrayInputStream(a.getBytes ())), new XyWeighter (), null);
	}

	public Single (TreeDocument a)
	{
		treeA = a;
	}
	
	/**
	 * Returns the graph of the chemical reaction network providing an own graph translator.
	 *
	 * @param gt the graph translator
	 * @return the chemical reaction network or null if not available
	 * @throws Exception the exception
	 */
	public abstract Object getCRNGraph (GraphTranslator gt) throws Exception;
	
	/**
	 * Returns the component's hierarchy graph providing an own graph translator.
	 *
	 * @param gt the graph translator
	 * @return the hierarchy graph or null if not available
	 * @throws Exception the exception
	 */
	public abstract Object getHierarchyGraph (GraphTranslator gt) throws Exception;

	/**
	 * Returns the graph of the chemical reaction network encoded in GraphML.
	 *
	 * @return the chemical reaction network or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getCRNGraphML () throws ParserConfigurationException;

	/**
	 * Returns the component's hierarchy graph encoded in GraphML.
	 *
	 * @return the hierarchy graph or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getHierarchyGraphML () throws ParserConfigurationException;

	/**
	 * Returns the graph of the chemical reaction network encoded in DOT language.
	 *
	 * @return the chemical reaction network or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getCRNDotGraph ();

	/**
	 * Returns the component's hierarchy graph encoded in DOT language.
	 *
	 * @return the hierarchy graph or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getHierarchyDotGraph ();

	/**
	 * Returns the graph of the chemical reaction network encoded in JSON.
	 *
	 * @return the chemical reaction network or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getCRNJsonGraph ();

	/**
	 * Returns the component's hierarchy graph encoded in JSON.
	 *
	 * @return the hierarchy graph or null if not available
	 * @throws Exception the exception
	 */
	public abstract String getHierarchyJsonGraph ();
	
}
