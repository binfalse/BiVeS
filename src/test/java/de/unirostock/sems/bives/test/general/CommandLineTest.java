package de.unirostock.sems.bives.test.general;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.Main;


/**
 * The Class CommandLineTest.
 *
 * @author Martin Scharm
 */
public class CommandLineTest
{
	
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
	}
	
	@Test
	public void testSomeCommandLineOptions ()
	{
		File file1 = new File ("test/" + TestResources.validSbml[0]);
		File file2 = new File ("test/" + TestResources.validSbml[1]);
		
		if (!file1.exists ())
			fail ("file not found: " + file1.getAbsolutePath ());
		
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--xmlDiff"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--crnGraphml"}, 1);
		testCommandLineOptions (file1, file2, new String [] {"--reportMd"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--reportMd"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--reportHtmlFp"}, 0);
		testCommandLineOptions (file1, file2, new String [] {"--reportHtml", "--reportHtmlFp", "--reactionsGraphml"}, 0);
		LOGGER.closeLogger ();
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
