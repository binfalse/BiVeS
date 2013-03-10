/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.SAXException;

import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLConnector;
import de.unirostock.sems.bives.algorithm.sbml.SBMLDiffInterpreter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLGraphProducer;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;

//TODO: detect document type
//TODO: graph producer

/**
 * @author Martin Scharm
 *
 */
public class Main
{
	
	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 */
	public static void main (String[] args) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException
	{
    PropertyConfigurator.configure("log4j.prop");

		//args = new String [] {"test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
    
    if (args.length != 2)
    {
    	System.out.println ("need exactly 2 xml files as arguments.");
    	return;
    }
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		TreeDocument td = new TreeDocument (builder.parse (new FileInputStream (args[0])), new XyWeighter ());
		TreeDocument td2 = new TreeDocument (builder.parse (new FileInputStream (args[1])), new XyWeighter ());

		Connector con = new SBMLConnector ();
		con.init (td, td2);
		con.findConnections ();
		
		td.getRoot ().resetModifications ();
		td.getRoot ().evaluate (con.getConnections ());
		
		td2.getRoot ().resetModifications ();
		td2.getRoot ().evaluate (con.getConnections ());

		Producer patcher = new PatchProducer (con.getConnections (), td, td2);
		System.out.println (patcher.produce ());
		
		
	}
	
}
