package de.unirostock.sems.bives.test.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;

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
