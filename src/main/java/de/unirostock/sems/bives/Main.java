/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.json.simple.JSONObject;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.xmlutils.tools.XmlTools;

//TODO: detect document type
//TODO: graph producer

/**
 * @author Martin Scharm
 *
 */
public class Main
{
	
	/** Should we exit?. */
	public static boolean exit = true;
	
	
	
	/**
	 * Usage.
	 *
	 * @param msg the error/info msg
	 */
	public void usage (String msg)
	{
		if (msg != null && msg.length () > 0)
		{
			System.err.println (msg);
			System.out.println ();
		}
		

		HelpFormatter formatter = new HelpFormatter ();
		formatter.setOptionComparator (new Comparator<Option> ()
		{
			
			private static final String	OPTS_ORDER	= "hcrio";
			
			
			public int compare (Option o1, Option o2)
			{
				return OPTS_ORDER.indexOf (o1.getLongOpt ())
					- OPTS_ORDER.indexOf (o2.getLongOpt ());
			}
		});
		formatter.printHelp ("java -jar bives.jar", exe.getOptions (), true);

		if (exit)
			System.exit (2);
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main (String[] args)
	{
		Logger.getRootLogger ().setLevel (Level.OFF);
		LOGGER.setLogToStdErr (false);
		LOGGER.setLogToStdOut (false);
		LOGGER.setLevel (LOGGER.ERROR);
		
		Main m = new Main ();
		try
		{
			m.run (args); 
		}
		catch (HelpException e)
		{
			m.usage (null);
		}
		catch (Exception e)
		{
			m.usage (e.getClass ().getSimpleName () + ": " + e.getMessage ());
			//System.exit (2);
		}
	}
	
	/**
	 * The Class HelpException.
	 */
	public static class HelpException extends Exception
	{	private static final long	serialVersionUID	= 1L;}
	
	/**
	 * The Class ExecutionException.
	 */
	public static class ExecutionException extends Exception
	{
		
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;

		/**
		 * Instantiates a new execution exception.
		 *
		 * @param msg the msg
		 */
		public ExecutionException (String msg)
		{
			super (msg);
		}
	}
	
	Executer exe;
	
	/**
	 * Instantiates a new main.
	 */
	public Main ()
	{
		exe = new Executer ();
	}
	
	
	/**
	 * Parses the command line.
	 *
	 * @param args given arguments
	 * @param exe the executer
	 * @return the command line
	 * @throws HelpException the help exception
	 * @throws ExecutionException the execution exception
	 */
	public static CommandLine parseCommandLine (String[] args, Executer exe) throws HelpException, ExecutionException
	{
    Options options = exe.getOptions ();
		CommandLineParser parser = new DefaultParser ();

    CommandLine line = null;
		try
		{
			line = parser.parse (options, args);
			if (line.hasOption ("help"))
				throw new HelpException ();
		}
		catch (ParseException e)
		{
			throw new ExecutionException ("Parsing of command line options failed.  Reason: " + e.getMessage ());
		}
		
		return line;
	}
	
	/**
	 * Run.
	 *
	 * @param args the args
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public void run (String[] args) throws Exception
	{
    String file1 = null, file2 = null;
    int output = 0;
    JSONObject toReturn = new JSONObject ();
    File outFile = null;

    CommandLine line = parseCommandLine (args, exe);
  	
  	if (line.hasOption (Executer.REQ_DEBUG))
  	{
  		LOGGER.setLogToStdErr (true);
  		LOGGER.setLevel (LOGGER.INFO | LOGGER.WARN | LOGGER.ERROR);
  	}
  	if (line.hasOption (Executer.REQ_DEBUGG))
  	{
  		LOGGER.setLogToStdErr (true);
  		LOGGER.setLevel (LOGGER.DEBUG | LOGGER.INFO | LOGGER.WARN | LOGGER.ERROR);
  		LOGGER.setLogStackTrace (true);
  	}
  	
  	if (line.hasOption (Executer.REQ_JSON))
  		output = 2;
  	if (line.hasOption (Executer.REQ_XML))
  		output = 1;
  	
		if (line.hasOption (Executer.REQ_OUT))
			outFile = new File (line.getOptionValue (Executer.REQ_OUT));
		
		
    List<String> remainingArgs = line.getArgList ();
    if (remainingArgs.size () > 3)
    	throw new ExecutionException ("Parsing of command line options failed. Do not know what to do with these arguments: " + remainingArgs);
    
    if (remainingArgs.size () < 1)
    	throw new ExecutionException ("Parsing of command line options failed. need at least one file");
    
    file1 = remainingArgs.get (0);
    if (remainingArgs.size () == 2)
    	file2 = remainingArgs.get (1);

    if (file1 == null)
    	throw new ExecutionException ("no file provided");
    
    if (!file1.matches ("^https?://.*"))
    {
    	File tmp = new File (file1);
	    if (!tmp.exists ())
	    	throw new ExecutionException ("cannot find " + tmp.getAbsolutePath ());
	    if (!tmp.canRead ())
	    	throw new ExecutionException ("cannot read " + tmp.getAbsolutePath ());
	    file1 = tmp.toURI ().toURL ().toString ();//GeneralTools.fileToString (tmp);
    }
    
    List<Exception> errors = new ArrayList<Exception> ();
    if (file2 == null)
    {
    	// single mode
    	exe.executeSingle (file1, toReturn, line, errors);
    }
    else
    {
    	// compare two files
      if (!file2.matches ("^https?://.*"))
      {
      	File tmp = new File (file2);
  	    if (!tmp.exists ())
  	    	throw new ExecutionException ("cannot find " + tmp.getAbsolutePath ());
  	    if (!tmp.canRead ())
  	    	throw new ExecutionException ("cannot read " + tmp.getAbsolutePath ());
  	    file2 =  tmp.toURI ().toURL ().toString ();//GeneralTools.fileToString (tmp);
      }
    	exe.executeCompare (file1, file2, toReturn, line, errors);
    }

		if (toReturn.size () < 1)
			throw new ExecutionException ("invalid call. no output produced.");
    
		PrintStream out = System.out;
		if (outFile != null)
			out = new PrintStream (new FileOutputStream (outFile), true, "UTF-8");
		
    if (output == 0)
    {
    	for (Exception e : errors)
    		System.err.println ("ERROR: " + e);
    	
    	for (Object ret : toReturn.keySet ())
    		out.println (toReturn.get (ret));
    }
    else if (output == 1)
    {
    	//xml
    	Element root =  new Element ("bivesResult");
    	Document document = new Document (root);

    	for (Object ret : toReturn.keySet ())
    	{
    		Element el = new Element (ret.toString ());
    		el.setText (toReturn.get (ret).toString ());
    		root.addContent (el);
    	}
    	
    	out.println (XmlTools.prettyPrintDocument (document));
    }
    else
    {
    	// json
    	/*JSONObject json = new JSONObject ();
    	for (Object ret : toReturn.keySet ())
    		json.put (ret, toReturn.get (ret));*/
    	out.println (toReturn);
    }
    
    if (outFile != null)
    	out.close ();
	}
	 
}
