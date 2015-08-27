package de.unirostock.sems.bives.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;

import de.unirostock.sems.bives.algorithm.SimpleConnectionManager;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class GeneralTest.
 *
 * @author Martin Scharm
 */
public class GeneralTest
{
	
	/**
	 * Simple test.
	 */
	@Test
	public void simpleTest ()
	{
		
	}
	
	/**
	 * No diff if equals.
	 */
	@Test
	public void noDiffIfEquals ()
	{
		try
		{
			File a = new File ("test/potato.xml");
			File b = new File ("test/potato.xml");
			
			TreeDocument treeA = new TreeDocument (XmlTools.readDocument (a), a.toURI ());
			TreeDocument treeB = new TreeDocument (XmlTools.readDocument (b), b.toURI ());
			
			XyDiffConnector con = new XyDiffConnector (treeA, treeB);
			con.findConnections ();
			SimpleConnectionManager connections = con.getConnections();
			
			PatchProducer producer = new PatchProducer ();
			producer.init (connections, treeA, treeB);
			String xml = producer.produce ();
			assertTrue ("XML patch should contain bives version", xml.contains ("BiVeS compiled with"));
			assertTrue ("XML patch should contain bives FrameWork version", xml.contains ("FrameWork"));
			assertTrue ("XML patch should contain bives Core version", xml.contains ("Core"));
			assertTrue ("XML patch should contain bives SBML version", xml.contains ("SBML"));
			assertTrue ("XML patch should contain bives CellML version", xml.contains ("CellML"));
			
			Patch patch = producer.getPatch ();
			assertEquals ("same files must not result in a patch", 0, patch.getNumInserts () + patch.getNumDeletes () + patch.getNumMoves () + patch.getNumUpdates ());
		}
		catch (Exception e)
		{
			fail ("excpetion while trying to compare xml files: " + e.getClass ().getName () + ": " + e.getMessage ());
		}
	}
	
	
	
	
	/**
	 * Diff if not equal.
	 */
	@Test
	public void diffIfNotEqual ()
	{
		try
		{
			File a = new File ("test/Teusink-2013Dec20.xml");
			File b = new File ("test/potato.xml");
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
				.newDocumentBuilder ();
			
			TreeDocument treeA = new TreeDocument (XmlTools.readDocument (a), a.toURI ());
			TreeDocument treeB = new TreeDocument (XmlTools.readDocument (b), b.toURI ());
			
			XyDiffConnector con = new XyDiffConnector (treeA, treeB);
			con.findConnections ();
			SimpleConnectionManager connections = con.getConnections();
			
			PatchProducer producer = new PatchProducer ();
			producer.init (connections, treeA, treeB);
			producer.produce ();
			Patch patch = producer.getPatch ();
			assertTrue ("different files must result in a patch", 0 < patch.getNumInserts () + patch.getNumDeletes () + patch.getNumMoves () + patch.getNumUpdates ());
		}
		catch (Exception e)
		{
			fail ("excpetion while trying to compare xml files: " + e.getClass ().getName () + ": " + e.getMessage ());
		}
	}
	
	
	
	
}
