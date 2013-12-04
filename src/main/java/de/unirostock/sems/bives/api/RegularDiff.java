/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.markup.Typesetting;


/**
 * @author Martin Scharm
 *
 */
public class RegularDiff
	extends Diff
{
	
	public RegularDiff (File a, File b)
		throws ParserConfigurationException,
			BivesDocumentParseException,
			FileNotFoundException,
			SAXException,
			IOException
	{
		super (a, b);
	}
	
	public RegularDiff (TreeDocument a, TreeDocument b)
		throws ParserConfigurationException,
			BivesDocumentParseException,
			FileNotFoundException,
			SAXException,
			IOException
	{
		super (a, b);
	}


	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#mapTrees()
	 */
	@Override
	public boolean mapTrees () throws BivesConnectionException
	{
		XyDiffConnector con = new XyDiffConnector();
		
		con.init (treeA, treeB);
		con.findConnections ();
		connections = con.getConnections();
		
		
		treeA.getRoot ().resetModifications ();
		treeA.getRoot ().evaluate (connections);
		
		treeB.getRoot ().resetModifications ();
		treeB.getRoot ().evaluate (connections);
		
		return true;
	}
	
	@Override
	public Object getCRNGraph (GraphTranslator gt) throws Exception
	{
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getGraphML()
	 */
	@Override
	public String getCRNGraphML () throws ParserConfigurationException
	{
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getDotGraph()
	 */
	@Override
	public String getCRNDotGraph ()
	{
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getHTMLReport()
	 */
	@Override
	public String getHTMLReport ()
	{
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getMarkDownReport()
	 */
	@Override
	public String getMarkDownReport ()
	{
		return null;
	}

	@Override
	public String getReStructuredTextReport ()
	{
		return null;
	}
	
	@Override
	public String getHierarchyGraph (GraphTranslator gt)
	{
		return null;
	}


	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getHierarchyGraphML()
	 */
	@Override
	public String getHierarchyGraphML ()
	{
		return null;
	}


	@Override
	public String getHierarchyDotGraph ()
	{
		return null;
	}

	@Override
	public String getCRNJsonGraph ()
	{
		return null;
	}

	@Override
	public String getHierarchyJsonGraph ()
	{
		return null;
	}

	@Override
	public String getReport (Typesetting ts)
	{
		return null;
	}
	
}
