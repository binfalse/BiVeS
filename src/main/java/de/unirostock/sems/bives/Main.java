/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.File;
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
import de.unirostock.sems.bives.api.CellMLDiff;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.api.SBMLDiff;
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
		System.out.println ("\t[option] FILE1 FILE2");
		System.out.println ();
		System.out.println ("OPTIONS:");
		System.out.println ("\t[none]\t\texpect XML files and print patch");
		System.out.println ("\t--sbml-patch\texpect SBML encoded models and print patch");
		System.out.println ("\t--cellml-patch\texpect CellML encoded models and print patch");
		System.out.println ();
		System.out.println ("FILE1 and FILE2 define XML files to compare");
		System.out.println ();
		
		System.exit (2);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main (String[] args) throws Exception
	{
		/*LOGGER.setLogToStdErr (true);
		LOGGER.addLevel (LOGGER.DEBUG);
		LOGGER.addLevel (LOGGER.INFO);
		LOGGER.addLevel (LOGGER.WARN);
		LOGGER.addLevel (LOGGER.ERROR);*/
		
		//args = new String [] {"--sbml-graph", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--sbml-graph", "test/TestModel_for_IB2013-version-one", "test/TestModel_for_IB2013-version-two"};
		//args = new String [] {"--sbml-patch", "test/TestModel_for_IB2013-version-one", "test/TestModel_for_IB2013-version-two"};
		//args = new String [] {"/home/martin/unisonSyncPrivate/education/docs/papers/2013-paper-bives/models/modeldiff/extract_reaction_R3_2007-06-05.xml", "/home/martin/unisonSyncPrivate/education/docs/papers/2013-paper-bives/models/modeldiff/extract_reaction_R3_2013-11-03.xml"};
    
    File file1 = null, file2 = null;
    int type = 0;
    
    if (args.length < 2)
    {
    	usage ("need at least 2 xml files as arguments.");
    }
    
    for (int i = 0; i < args.length; i++)
    {
    	if (args[i].equals ("--sbml-patch"))
    	{
    		type = 1;
    	}
    	else if (args[i].equals ("--cellml-patch"))
    	{
    		type = 2;
    	}
    	else if (file1 == null)
    		file1 = new File (args[i]);
    	else if (file2 == null)
    		file2 = new File (args[i]);
    	else
    	{
    		usage ("do not understand");
    	}
    }
    
    if (file1 == null || file2 == null)
    	usage ("you need to prvide 2 files!");
    
    Diff differ = null;

    switch (type)
    {
    	case 1:
    		differ = new SBMLDiff (file1, file2);
    		break;
    	case 2:
    		differ = new CellMLDiff (file1, file2);
    		break;
    	default:
    		differ = new RegularDiff (file1, file2);
    }
    
    differ.mapTrees ();
    
    System.out.println (differ.getDiff ());
    
    
		
	}
	
}
