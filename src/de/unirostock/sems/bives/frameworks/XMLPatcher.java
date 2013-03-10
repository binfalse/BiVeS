package de.unirostock.sems.bives.frameworks;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.SAXException;

import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.ds.xml.TreeDocument;


public class XMLPatcher
{

	public static void main (String[] args) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException
	{
    PropertyConfigurator.configure("src/log4j.prop");

		args = new String [] {"BSA-ptinst-2012-11-11", "BSA-sigbprlysis-2012-11-11"};
		args = new String [] {"Novak1997_CellCycle-R3.xml", "Novak1997_CellCycle-R37.xml"};
		//args = new String [] {"testA.xml", "testB.xml"};
		//args = new String [] {"BSA-ptinst-2012-11-11", "Novak1997_CellCycle-R37.xml"};

		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		TreeDocument td = new TreeDocument (builder.parse (new FileInputStream (args[0])), new XyWeighter ());
		TreeDocument td2 = new TreeDocument (builder.parse (new FileInputStream (args[1])), new XyWeighter ());
		

		Connector con = new XyDiffConnector ();
		con.init (td, td2);
		con.findConnections ();
		
		Producer patcher = new PatchProducer (con.getConnections (), td, td2);
		System.out.println (patcher.produce ());
		
	}
}
