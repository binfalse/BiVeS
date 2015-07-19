package de.unirostock.sems.bives.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unirostock.sems.bives.Main;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class CommandLineTest.
 *
 * @author Martin Scharm
 */
public class CommandLineTest
{
	@BeforeClass
	public static void setUpEnv ()
	{
		Logger.getRootLogger ().setLevel (Level.OFF);
	}
	
	/**
	 * The Class CommandLineResults.
	 */
	public static class CommandLineResults
	{

		/** The sys out. */
		public ByteArrayOutputStream sysOut;
		/** The sys err. */
		public ByteArrayOutputStream sysErr;
		
		/**
		 * Instantiates a new command line results.
		 *
		 * @param sysOut the sys out
		 * @param sysErr the sys err
		 */
		public CommandLineResults (ByteArrayOutputStream sysOut, ByteArrayOutputStream sysErr)
		{
			this.sysOut = sysOut;
			this.sysErr = sysErr;
		}
	}
	
	/**
	 * Run BiVeS from a command line and capture the output.
	 *
	 * @param args the arguments for BiVeS 
	 * @return the command line results
	 */
	public static CommandLineResults runCommandLine (String [] args)
	{
		ByteArrayOutputStream sysErr = new ByteArrayOutputStream ();
		PrintStream err = new PrintStream(sysErr);
		ByteArrayOutputStream sysOut = new ByteArrayOutputStream ();
		PrintStream out = new PrintStream(sysOut);

		PrintStream orgErr = System.err;
		PrintStream orgOut = System.out;
		
		System.setErr (err);
		System.setOut (out);
		
		Main.exit = false;
		Main.main (args);
		
		System.setErr (orgErr);
		System.setOut (orgOut);
		
		return new CommandLineResults (sysOut, sysErr);
	}
	 
	/**
	 * Test valid json.
	 */
	@Test
	public void testValidJson()
	{
		File file1 = TestResources.getValidSbmlFile ();
		
		if (!file1.exists ())
			fail ("file not found: " + file1.getAbsolutePath ());
		
		String [] args = new String [] {"--reportHtml", "--xmlDiff", "--json", file1.getAbsolutePath (), file1.getAbsolutePath ()};

		CommandLineResults clr = CommandLineTest.runCommandLine (args);

		ByteArrayOutputStream sysErr = clr.sysErr;
		ByteArrayOutputStream sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args), sysErr.toString().isEmpty());
		
		try
		{
			JSONObject json = (JSONObject) new JSONParser ().parse (sysOut.toString ());
			assertTrue ("expected to get some results", json.size () > 0);
			assertNotNull ("expected to get a report", json.get ("reportHtml"));
			assertNotNull ("expected to get a diff", json.get ("xmlDiff"));
		}
		catch (ParseException e)
		{
			fail ("wasn't ablt to read json: " + e.getMessage ());
		}
	}
	
	
	public static void testCommandLineOptions (File file1, File file2, String [] options, int add)
	{
		String [] args = new String [options.length + 3];
		System.arraycopy (options, 0, args, 3, options.length);
		args[0] = "--json";
		args[1] = file1.getAbsolutePath ();
		args[2] = file2.getAbsolutePath ();

		CommandLineResults clr = CommandLineTest.runCommandLine (args);

		ByteArrayOutputStream sysErr = clr.sysErr;
		ByteArrayOutputStream sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());

		try
		{
				//System.out.println (Arrays.toString (args) + " --- " + sysOut.toString ());
			JSONObject json = (JSONObject) new JSONParser ().parse (sysOut.toString ());
			//System.out.println (json);
			assertEquals ("expected to get some results", add + options.length, json.size ());
			for (String option : options){
				assertNotNull ("expected to get " + option, json.get (option.substring (2)));
			}
			
		}
		catch (ParseException e)
		{
			fail ("wasn't ablt to read json: " + e.getMessage ());
		}
		
		
		
		args[0] = "--xml";
		clr = CommandLineTest.runCommandLine (args);

		sysErr = clr.sysErr;
		sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());
		
		try
		{
			TreeDocument doc = new TreeDocument (XmlTools.readDocument (sysOut.toString ()), null);
			DocumentNode root = doc.getRoot ();
			assertEquals ("expected to get some results", add + options.length, root.getChildren ().size ());
			for (String option : options){
				assertEquals ("expected to get " + option, 1, root.getChildrenWithTag (option.substring (2)).size ());
				assertNotNull ("expected to get " + option, root.getChildrenWithTag (option.substring (2)).get (0));
			}
			
		}
		catch (Exception e)
		{
			fail ("wasn't ablt to read xml results: " + e.getMessage ());
		}
	}
	
	public static void testCommandLineOptions (File file1, String [] options, int add)
	{
		String [] args = new String [options.length + 2];
		System.arraycopy (options, 0, args, 2, options.length);
		args[0] = "--json";
		args[1] = file1.getAbsolutePath ();

		CommandLineResults clr = CommandLineTest.runCommandLine (args);

		ByteArrayOutputStream sysErr = clr.sysErr;
		ByteArrayOutputStream sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());

		try
		{
				//System.out.println (Arrays.toString (args) + " --- " + sysOut.toString ());
			JSONObject json = (JSONObject) new JSONParser ().parse (sysOut.toString ());
			//System.out.println (json);
			assertEquals ("expected to get some results", add + options.length, json.size ());
			for (String option : options){
				assertNotNull ("expected to get " + option, json.get (option.substring (2)));
			}
			
		}
		catch (ParseException e)
		{
			fail ("wasn't ablt to read json: " + e.getMessage ());
		}
		
		
		
		args[0] = "--xml";
		clr = CommandLineTest.runCommandLine (args);

		sysErr = clr.sysErr;
		sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());
		
		try
		{
			TreeDocument doc = new TreeDocument (XmlTools.readDocument (sysOut.toString ()), null);
			DocumentNode root = doc.getRoot ();
			assertEquals ("expected to get some results", add + options.length, root.getChildren ().size ());
			for (String option : options){
				assertEquals ("expected to get " + option, 1, root.getChildrenWithTag (option.substring (2)).size ());
				assertNotNull ("expected to get " + option, root.getChildrenWithTag (option.substring (2)).get (0));
			}
			
		}
		catch (Exception e)
		{
			fail ("wasn't ablt to read xml results: " + e.getMessage ());
		}
	}
	
	
	@Test
	public void testRemotes ()
	{
		String [] args = new String [] {"--SBML", "http://budhat.sems.uni-rostock.de/download?downloadModel=24", "http://budhat.sems.uni-rostock.de/download?downloadModel=25"};
		CommandLineResults clr = CommandLineTest.runCommandLine (args);

		ByteArrayOutputStream sysErr = clr.sysErr;
		ByteArrayOutputStream sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());
		assertFalse ("bives main doesn't sysout " + Arrays.toString (args) + ": " + sysOut.toString(), sysOut.toString().isEmpty());
	}
	
	@Test
	public void testSingleRemotes ()
	{
		String [] args = new String [] {"--SBML", "--singleReactionsGraphml", "http://budhat.sems.uni-rostock.de/download?downloadModel=24"};
		CommandLineResults clr = CommandLineTest.runCommandLine (args);

		ByteArrayOutputStream sysErr = clr.sysErr;
		ByteArrayOutputStream sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());
		assertFalse ("bives main doesn't sysout " + Arrays.toString (args) + ": " + sysOut.toString(), sysOut.toString().isEmpty());
	}
	
	@Test
	public void testSingle ()
	{
		File file1 = new File ("test/" + TestResources.validSbml[0]);
		testCommandLineOptions (file1, new String [] {"--singleReactionsGraphml"}, 0);
		testCommandLineOptions (file1, new String [] {"--singleCrnGraphml"}, 1);
		testCommandLineOptions (file1, new String [] {"--singleReactionsGraphml", "--singleCrnGraphml"}, 0);
		
		testCommandLineOptions (file1, new String [] {"--singleReactionsDot"}, 0);
		testCommandLineOptions (file1, new String [] {"--singleCrnDot"}, 1);
		testCommandLineOptions (file1, new String [] {"--singleReactionsDot", "--singleCrnDot"}, 0);
		
		testCommandLineOptions (file1, new String [] {"--singleReactionsJson"}, 0);
		testCommandLineOptions (file1, new String [] {"--singleCrnJson"}, 1);
		testCommandLineOptions (file1, new String [] {"--singleReactionsJson", "--singleCrnJson"}, 0);
		
		testCommandLineOptions (file1, new String [] {"--meta"}, 0);
		testCommandLineOptions (file1, new String [] {"--documentType"}, 0);
		testCommandLineOptions (file1, new String [] {"--documentType", "--meta"}, 0);
		testCommandLineOptions (file1, new String [] {"--documentType", "--meta", "--singleReactionsGraphml"}, 0);
		testCommandLineOptions (file1, new String [] {"--documentType", "--meta", "--singleReactionsGraphml", "--singleCrnJson"}, 1);
		
		file1 = new File ("test/" + TestResources.validCellML[0]);
		testCommandLineOptions (file1, new String [] {"--meta"}, 0);
		testCommandLineOptions (file1, new String [] {"--documentType"}, 0);
		testCommandLineOptions (file1, new String [] {"--documentType", "--meta"}, 0);
		testCommandLineOptions (file1, new String [] {"--singleCompHierarchyJson", "--singleCompHierarchyDot", "--singleCompHierarchyGraphml"}, 0);
		testCommandLineOptions (file1, new String [] {"--singleFlatten"}, 0);
	}
	
	@Test
	public void testSomeCommandLineOptions ()
	{
		File file1 = new File ("test/" + TestResources.validSbml[0]);
		File file2 = new File ("test/" + TestResources.validSbml[1]);
		
		if (!file1.exists ())
			fail ("file not found: " + file1.getAbsolutePath ());
		if (!file2.exists ())
			fail ("file not found: " + file2.getAbsolutePath ());
		
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--xmlDiff"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--crnGraphml"}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--reportMd"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--reportMd"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--reportHtmlFp"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--reportHtmlFp", "--reactionsGraphml"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportRST"}, 0);
		testCommandLineOptions (file1, file2, new String [] {}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtmlFp"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--crnDot"}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--crnDot", "--reactionsGraphml"}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--crnDot", "--reactionsDot"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--crnDot", "--crnGraphml"}, 2);
		testCommandLineOptions (file1, file2, new String [] {"--reactionsJson", "--reactionsDot"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reactionsJson", "--crnJson"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--crnJson"}, 1);
		

		file1 = new File ("test/" + TestResources.validCellML[0]);
		file2 = new File ("test/" + TestResources.validCellML[1]);
		
		if (!file1.exists ())
			fail ("file not found: " + file1.getAbsolutePath ());
		if (!file2.exists ())
			fail ("file not found: " + file2.getAbsolutePath ());
		
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--xmlDiff"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--crnGraphml"}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--reportMd"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--reportMd"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--reportHtmlFp"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--reportHtmlFp", "--reactionsGraphml"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportRST"}, 0);
		testCommandLineOptions (file1, file2, new String [] {}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtmlFp"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--crnDot"}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--crnDot", "--reactionsGraphml"}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--crnDot", "--reactionsDot"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--crnDot", "--crnGraphml"}, 2);
		testCommandLineOptions (file1, file2, new String [] {"--reactionsJson", "--reactionsDot"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reactionsJson", "--crnJson"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--crnJson"}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--compHierarchyDot"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--compHierarchyJson"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--compHierarchyGraphml"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--compHierarchyGraphml", "--compHierarchyJson", "--compHierarchyDot"}, 0);
		
		
		
	}
	
	@Test
	public void testTypeSwitches ()
	{

		File file1Sbml = new File ("test/" + TestResources.validSbml[0]);
		File file2Sbml = new File ("test/" + TestResources.validSbml[1]);
		File file1Cellml = new File ("test/" + TestResources.validCellML[0]);
		File file2Cellml = new File ("test/" + TestResources.validCellML[1]);
		
		
		
		
		String [] args = new String [] {"--SBML", file1Sbml.getAbsolutePath (), file2Sbml.getAbsolutePath ()};
		CommandLineResults clr = CommandLineTest.runCommandLine (args);

		ByteArrayOutputStream sysErr = clr.sysErr;
		ByteArrayOutputStream sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());
		assertFalse ("bives main doesn't sysout " + Arrays.toString (args) + ": " + sysOut.toString(), sysOut.toString().isEmpty());
		
		
		
		
		args = new String [] {"--SBML", file1Sbml.getAbsolutePath (), "--documentType", "--meta"};
		clr = CommandLineTest.runCommandLine (args);

		sysErr = clr.sysErr;
		sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());
		assertFalse ("bives main doesn't sysout " + Arrays.toString (args) + ": " + sysOut.toString(), sysOut.toString().isEmpty());
		
		
		
		
		args = new String [] {"--CellML", file1Cellml.getAbsolutePath (), "--documentType", "--meta"};
		clr = CommandLineTest.runCommandLine (args);

		sysErr = clr.sysErr;
		sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());
		assertFalse ("bives main doesn't sysout " + Arrays.toString (args) + ": " + sysOut.toString(), sysOut.toString().isEmpty());
		
		
		
		
		args = new String [] {"--CellML", file1Cellml.getAbsolutePath (), file2Cellml.getAbsolutePath ()};
		clr = CommandLineTest.runCommandLine (args);

		sysErr = clr.sysErr;
		sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());
		assertFalse ("bives main doesn't sysout " + Arrays.toString (args) + ": " + sysOut.toString(), sysOut.toString().isEmpty());
		
		
		
		
		args = new String [] {"--regular", file1Cellml.getAbsolutePath (), file1Sbml.getAbsolutePath ()};
		clr = CommandLineTest.runCommandLine (args);

		sysErr = clr.sysErr;
		sysOut = clr.sysOut;
		
		assertTrue ("bives main reports error for " + Arrays.toString (args) + ": " + sysErr.toString(), sysErr.toString().isEmpty());
		assertFalse ("bives main doesn't sysout " + Arrays.toString (args) + ": " + sysOut.toString(), sysOut.toString().isEmpty());

	}
	

	/**
	 * Command line should throw an error if one of the files is not XML.
	 */
	@Test
	public void failOnInvalidFile ()
	{
		File file1 = TestResources.getInvalidXmlFile();
		
		if (!file1.exists ())
			fail ("file not found: " + file1.getAbsolutePath ());
		
		String [] args = new String [] {"--SBML", "--json", file1.getAbsolutePath (), file1.getAbsolutePath ()};

		ByteArrayOutputStream sysErr = runCommandLine (args).sysErr;

		assertFalse ("invalid files don't produce error", sysErr.toString().isEmpty());
	}
	
	/**
	 * Command line should throw an error if one of the files doesn't exist.
	 */
	@Test
	public void failOnNoFile ()
	{
		String file1 = "/tmp/" + UUID.randomUUID ().toString ();
		File file2 = TestResources.getNonexistentFile ();
		
		String [] args = new String [] {file1, file2.getAbsolutePath ()};

		ByteArrayOutputStream sysErr = runCommandLine (args).sysErr;

		assertFalse ("invalid files don't produce error", sysErr.toString().isEmpty());
	}
	
}
