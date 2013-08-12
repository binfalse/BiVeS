/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;

/**
 * @author Martin Scharm
 *
 */
public abstract class Diff
{
	public static final int PROD_DIFF = 0;
	public static final int PROD_GRAPH = 1;
	public static final int PROD_REPORT = 2;
	public static final int PROD_ALL = 3;
	
	protected File a,b;
	protected TreeDocument treeA, treeB;
	protected ClearConnectionManager connections;

	public Diff (File a, File b) throws ParserConfigurationException, BivesDocumentParseException, FileNotFoundException, SAXException, IOException
	{
		this.a = a;
		this.b = b;

		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		treeA = new TreeDocument (builder.parse (new FileInputStream (a)), new XyWeighter (), a.toURI ());
		treeB = new TreeDocument (builder.parse (new FileInputStream (b)), new XyWeighter (), b.toURI ());
	}

	public Diff (String a, String b) throws ParserConfigurationException, BivesDocumentParseException, FileNotFoundException, SAXException, IOException
	{
		this.a = null;
		this.b = null;

		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		treeA = new TreeDocument (builder.parse (new ByteArrayInputStream(a.getBytes ())), new XyWeighter (), null);
		treeB = new TreeDocument (builder.parse (new ByteArrayInputStream (b.getBytes ())), new XyWeighter (), null);
	}
	
	public abstract boolean mapTrees () throws Exception;
	
	/*public String getJSON () throws ParserConfigurationException
	{
		Map<String, Object> json=new LinkedHashMap<String, Object>();

		json.put("graphml", getGraphML ());
		json.put("htmlreport", getHTMLReport ());
		json.put("xmldiff", getDiff ());
		return JSONValue.toJSONString(json);
		
	}*/
	
	public String getDiff ()
	{
		PatchProducer producer = new PatchProducer ();
		producer.init (connections, treeA, treeB);
		return producer.produce ();
	}
	
	public abstract String getCRNGraphML () throws ParserConfigurationException;
	
	public abstract String getCRNDotGraph () throws ParserConfigurationException;
	
	public abstract String getHTMLReport ();
	
	public abstract String getMarkDownReport ();
	
	protected static class ArgsParser
	{
		private File fileA = null, fileB = null;
		private int result;
		private void error (String err)
		{
			if (err == null)
				System.out.println ("error parsing args");
			else
				System.out.println(err);
			
			System.out.println("valid ARGUMENTS:");
			System.out.println("\t--diff\trequest the diff encoded in xml. that's the default.");
			System.out.println("\t--graphml\trequest the visualization in graphml.");
			System.out.println("\t--report\trequest the html report.");
			System.out.println("\t--alljson\trequest all results, encoded in an json pobject.");
			
			System.exit(2);
		}
		public File getFileA ()
		{
			return fileA;
		}
		public File getFileB ()
		{
			return fileB;
		}
		public int getAction ()
		{
			return result;
		}
		public ArgsParser (String [] args)
		{
			
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].equals ("--alljson"))
					result = Diff.PROD_ALL;
				else if (args[i].equals("--diff"))
					result = Diff.PROD_DIFF;
				else if (args[i].equals ("--graphml"))
					result = Diff.PROD_GRAPH;
				else if (args[i].equals("--report"))
					result = Diff.PROD_REPORT;
				else if (fileA == null)
				{
					fileA = new File (args[i]);
				}
				else if (fileB == null)
					fileB = new File (args[i]);
				else
					error (null);
			}
			
			if (fileA == null)
				error ("no files provided");
			if (fileB == null)
				error ("need exactly two files!");

			testFile (fileA);
			testFile (fileB);
		}
		
		private void testFile (File f)
		{
			if (!f.isFile())
				error ("file " + f.getAbsolutePath() + " is not a file");
			if (!f.canRead())
				error ("not allowed to read " + f.getAbsolutePath());
		}
	}
	
}
