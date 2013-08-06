/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLConnector;
import de.unirostock.sems.bives.algorithm.sbml.SBMLDiffInterpreter;
//import de.unirostock.sems.bives.algorithm.sbmldeprecated.SBMLDiffInterpreter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLGraphProducer;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesConnectionException;

//TODO: detect document type
//TODO: graph producer

/**
 * @author Martin Scharm
 *
 */
public class Main
{
	
	public static void usage (String msg)
	{
		System.out.println (msg);
		System.out.println ();

		System.out.println ("ARGUMENTS:");
		System.out.println ("\t[option] fileA.xml fileB.xml");
		System.out.println ();
		System.out.println ("OPTIONS:");
		System.out.println ("\t[none]\t\texpect XML file and print patch");
		System.out.println ("\t--sbml-patch\texpect SBML encoded model and print patch");
		System.out.println ("\t--sbml-graph\texpect SBML encoded model and print diff graph");
		System.out.println ("\t--cellml-patch\texpect CellML encoded model and print patch");
		System.out.println ();
		System.out.println ("[FILE1] and [FILE2] define XML files to compare");
		System.out.println ();
		
		System.exit (2);
	}
	
	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 * @throws BivesConnectionException 
	 */
	public static void main (String[] args) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException, BivesConnectionException
	{
		//LOGGER.setLogToStdErr (false);

		/*args = new String [] {"--sbml-graph", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		args = new String [] {"--sbml-graph", "test/TestModel_for_IB2013-version-one", "test/TestModel_for_IB2013-version-two"};
    
    String file1 = null, file2 = null;
    Connector con = new XyDiffConnector ();
    Producer producer = new PatchProducer ();
    
    if (args.length < 2)
    {
    	usage ("need at least 2 xml files as arguments.");
    }
    
    for (int i = 0; i < args.length; i++)
    {
    	if (args[i].equals ("--sbml-patch"))
    	{
    		//con = new SBMLConnector ();
    		producer = new PatchProducer ();
    	}
    	else if (args[i].equals ("--sbml-graph"))
    	{
    		//con = new SBMLConnector ();
    		//producer = new SBMLGraphProducer ();
    	}
    	else if (args[i].equals ("--cellml-patch"))
    	{
    		con = new CellMLConnector ();
    		producer = new PatchProducer ();
    	}
    	else if (file1 == null)
    		file1 = args[i];
    	else if (file2 == null)
    		file2 = args[i];
    	else
    	{
    		usage ("do not understand");
    	}
    }

		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		TreeDocument td = new TreeDocument (builder.parse (new FileInputStream (file1)), new XyWeighter ());
		TreeDocument td2 = new TreeDocument (builder.parse (new FileInputStream (file2)), new XyWeighter ());
		
		{
			SBMLDocument doc1 = new SBMLDocument (td);
			SBMLDocument doc2 = new SBMLDocument (td2);
			SBMLConnector con2 = new SBMLConnector (doc1, doc2);
			con2.init (td, td2);
			con2.findConnections ();
			//System.out.println (con2.getConnections ());
			
			td.getRoot ().resetModifications ();
			td.getRoot ().evaluate (con2.getConnections ());
			
			td2.getRoot ().resetModifications ();
			td2.getRoot ().evaluate (con2.getConnections ());*/
			//System.out.println ("a");
			/*System.out.println (con2.getConnections ());
			System.out.println (doc1.getModel ().getReaction ("v2").getDocumentNode ().getModification ());
			System.out.println (doc2.getModel ().getReaction ("v2").getDocumentNode ().getModification ());*/
			
			/*SBMLGraphProducer producer2 = new SBMLGraphProducer ();
			producer2.init (con2.getConnections (), doc1, doc2);*/
			//System.out.println (producer2.produce ());

			/*System.out.println (doc1.getModel ().getReaction ("v2").getDocumentNode ().getModification ());
			System.out.println (doc2.getModel ().getReaction ("v2").getDocumentNode ().getModification ());*/
			
			/*producer.init (con2.getConnections (), td, td2);
			//System.out.println (producer.produce ());
			
			SBMLDiffInterpreter inter = new SBMLDiffInterpreter (con2.getConnections (), doc1, doc2);
			inter.interprete ();
			//System.out.println (inter.getReport ().generateHTMLReport ());
			
			System.exit (1);
		}

		//Connector con = new SBMLConnector ();
		con.init (td, td2);
		con.findConnections ();
		
		/*System.out.println ("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		System.out.println (con.getConnections ());
		System.out.println ("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		td.getRoot ().resetModifications ();
		td.getRoot ().evaluate (con.getConnections ());
		
		td2.getRoot ().resetModifications ();
		td2.getRoot ().evaluate (con.getConnections ());

		System.out.println ("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
		System.out.println (con.getConnections ());
		System.out.println ("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");*/
		
		/*td.getRoot ().resetModifications ();
		td.getRoot ().evaluate (con.getConnections ());
		
		td2.getRoot ().resetModifications ();
		td2.getRoot ().evaluate (con.getConnections ());

		//Producer patcher = new PatchProducer (con.getConnections (), td, td2);
		producer.init (con.getConnections (), td, td2);
		System.out.println (producer.produce ());

		/*System.out.println ("CCCCCCCCCCCCCCCCCCCCCCCCCCCC");
		System.out.println (con.getConnections ());
		System.out.println ("CCCCCCCCCCCCCCCCCCCCCCCCCCCC");*/
		
	}
	
}
