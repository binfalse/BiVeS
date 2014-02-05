package de.unirostock.sems.bives.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.xmlutils.ds.TreeDocument;


public class GeneralTest
{
	
	
	@Test
	public void noDiffIfEquals ()
	{
		try
		{
			File a = new File ("test/potato.xml");
			File b = new File ("test/potato.xml");
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
				.newDocumentBuilder ();
			
			TreeDocument treeA = new TreeDocument (builder.parse (new FileInputStream (a)), a.toURI ());
			TreeDocument treeB = new TreeDocument (builder.parse (new FileInputStream (b)), b.toURI ());
			
			XyDiffConnector con = new XyDiffConnector();
			con.init (treeA, treeB);
			con.findConnections ();
			ClearConnectionManager connections = con.getConnections();
			
			PatchProducer producer = new PatchProducer ();
			producer.init (connections, treeA, treeB);
			producer.produce ();
			Patch patch = producer.getPatch ();
			assertEquals ("same files must not result in a patch", 0, patch.getNumInsterts () + patch.getNumDeletes () + patch.getNumMoves () + patch.getNumUpdates ());
		}
		catch (Exception e)
		{
			fail ("excpetion while trying to compare xml files: " + e.getClass ().getName () + ": " + e.getMessage ());
		}
	}
	
	
	
	
	@Test
	public void diffIfNotEqual ()
	{
		try
		{
			File a = new File ("test/Teusink-2013Dec20.xml");
			File b = new File ("test/potato.xml");
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
				.newDocumentBuilder ();
			
			TreeDocument treeA = new TreeDocument (builder.parse (new FileInputStream (a)), a.toURI ());
			TreeDocument treeB = new TreeDocument (builder.parse (new FileInputStream (b)), b.toURI ());
			
			XyDiffConnector con = new XyDiffConnector();
			con.init (treeA, treeB);
			con.findConnections ();
			ClearConnectionManager connections = con.getConnections();
			
			PatchProducer producer = new PatchProducer ();
			producer.init (connections, treeA, treeB);
			producer.produce ();
			Patch patch = producer.getPatch ();
			assertTrue ("different files must result in a patch", 0 < patch.getNumInsterts () + patch.getNumDeletes () + patch.getNumMoves () + patch.getNumUpdates ());
		}
		catch (Exception e)
		{
			fail ("excpetion while trying to compare xml files: " + e.getClass ().getName () + ": " + e.getMessage ());
		}
	}
	
	
	
	
}
