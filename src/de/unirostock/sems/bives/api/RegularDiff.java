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
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;


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
	public String getCRNDotGraph () throws ParserConfigurationException
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
	
}
