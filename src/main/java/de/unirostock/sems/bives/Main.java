/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdom2.Document;
import org.jdom2.Element;
import org.json.simple.JSONObject;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
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

		HashMap<String, Executer.Option> options = exe.getOptions ();
		HashMap<String, Executer.Option> addOptions = exe.getAddOptions ();
		
		System.out.println ("ARGUMENTS:");
		System.out.println ("\t[option] FILE1 [FILE2]  compute the differences between 2 XML files");
		System.out.println ();
		System.out.println ("FILE1 and FILE2 define XML files or URLs to XML files on the internet");
		System.out.println ();
		System.out.println ("OPTIONS:");
		SortedSet<String> keys = new TreeSet<String>(options.keySet());
		int longest = 0;
		for (String key : keys)
		{
			if (key.length () > longest)
				longest = key.length ();
		}
		SortedSet<String> addKeys = new TreeSet<String>(addOptions.keySet());
		for (String key : addKeys)
		{
			if (key.length () > longest)
				longest = key.length ();
		}
		
		longest += 2;
		System.out.println ("\tCOMMON OPTIONS");
		System.out.println ("\t[none]  "+GeneralTools.repeat (" ", longest - "[none]".length ()) +"expect XML files and print patch");
		System.out.println ("\t--help"+GeneralTools.repeat (" ", longest - "help".length ()) +"print this help");
		System.out.println ("\t--debug"+GeneralTools.repeat (" ", longest - "debug".length ()) +"enable verbose mode");
		System.out.println ("\t--debugg"+GeneralTools.repeat (" ", longest - "debugg".length ()) +"enable even more verbose mode");
		System.out.println ("\t--out FILE"+GeneralTools.repeat (" ", longest - "out FILE".length ()) +"write output to FILE");

		System.out.println ();
		System.out.println ("\tMAPPING OPTIONS");
		
		for (String key : keys)
			System.out.println ("\t--"+key + GeneralTools.repeat (" ", longest - key.length ()) + options.get (key).description);
		System.out.println ();

		System.out.println ("\tENCODING OPTIONS");
		System.out.println ("\tby default we will just dump the result to the terminal. Thus, it's only usefull if you call for one single output.");
		System.out.println ("\t--json"+GeneralTools.repeat (" ", longest - "json".length ()) +"encode results in JSON");
		System.out.println ("\t--xml"+GeneralTools.repeat (" ", longest - "xml".length ()) +"encode results in XML");
		System.out.println ();

		System.out.println ("\tADDITIONAL OPTIONS for single files");
		for (String key : addKeys)
			System.out.println ("\t--"+key + GeneralTools.repeat (" ", longest - key.length ()) + addOptions.get (key).description);
		System.out.println ();

		if (exit)
			System.exit (2);
	}
	
	/**
	 * @param args 
	 */
	public static void main (String[] args)
	{
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
	
	private class HelpException extends Exception
	{

		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;}
	
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
  	int want = 0;
    HashMap<String, String> toReturn = new HashMap<String, String> ();
    // check for backwards compatibility (we moved from crn to rn)
    boolean chemicalReactionNetwork = false;
    File outFile = null;

    
    for (int i = 0; i < args.length; i++)
    {
    	if (args[i].substring (0, 2).equals ("--"))
    	{
	    	Executer.Option o = exe.get (args[i].substring (2));
	    	if (o != null)
	    	{
	    		want |= o.value;
	    		continue;
	    	}
    	}
    	
    	if (args[i].equals ("--debug"))
    	{
    		LOGGER.setLogToStdErr (true);
    		LOGGER.setLevel (LOGGER.INFO | LOGGER.WARN | LOGGER.ERROR);
    		continue;
    	}
    	if (args[i].equals ("--debugg"))
    	{
    		LOGGER.setLogToStdErr (true);
    		LOGGER.setLevel (LOGGER.DEBUG | LOGGER.INFO | LOGGER.WARN | LOGGER.ERROR);
    		continue;
    	}
    	if (args[i].equals ("--xml"))
    	{
    		output = 1;
    		continue;
    	}
    	if (args[i].equals ("--json"))
    	{
    		output = 2;
    		continue;
    	}
    	if (args[i].equals ("--out") && i < args.length - 1)
    	{
    		outFile = new File (args[i + 1]);
    		i++;
    		continue;
    	}
    	if (args[i].equals ("--help"))
    	{
    		throw new HelpException ();
    	}
    	
    	// START backwards compatibility
    	if (args[i].equals ("--crnGraphml"))
    	{
    		want |= Executer.WANT_REACTION_GRAPHML;
    		chemicalReactionNetwork = true;
    		continue;
    	}
    	if (args[i].equals ("--crnDot"))
    	{
    		want |= Executer.WANT_REACTION_DOT;
    		chemicalReactionNetwork = true;
    		continue;
    	}
    	if (args[i].equals ("--crnJson"))
    	{
    		want |= Executer.WANT_REACTION_JSON;
    		chemicalReactionNetwork = true;
    		continue;
    	}
    	if (args[i].equals ("--singleCrnGraphml"))
    	{
    		want |= Executer.WANT_SINGLE_REACTION_GRAPHML;
    		chemicalReactionNetwork = true;
    		continue;
    	}
    	if (args[i].equals ("--singleCrnDot"))
    	{
    		want |= Executer.WANT_SINGLE_REACTION_DOT;
    		chemicalReactionNetwork = true;
    		continue;
    	}
    	if (args[i].equals ("--singleCrnJson"))
    	{
    		want |= Executer.WANT_SINGLE_REACTION_JSON;
    		chemicalReactionNetwork = true;
    		continue;
    	}
    	// END backwards compatibility
    	
    	
    	if (file1 == null)
    		file1 = args[i];
    	else if (file2 == null)
    		file2 = args[i];
    	else
    		throw new ExecutionException ("do not understand " + args[i] + " (found files " + file1 + " and " + file2 + ")");
    		
    }
    

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
    	exe.executeSingle (file1, toReturn, want, errors);
    	// check for backwards compatibility
    	if (chemicalReactionNetwork)
    	{
    		if (toReturn.get (Executer.REQ_WANT_SINGLE_REACTIONS_GRAPHML) != null)
    			toReturn.put ("singleCrnGraphml", toReturn.get (Executer.REQ_WANT_SINGLE_REACTIONS_GRAPHML));
    		if (toReturn.get (Executer.REQ_WANT_SINGLE_REACTIONS_JSON) != null)
    			toReturn.put ("singleCrnJson", toReturn.get (Executer.REQ_WANT_SINGLE_REACTIONS_JSON));
    		if (toReturn.get (Executer.REQ_WANT_SINGLE_REACTIONS_DOT) != null)
    			toReturn.put ("singleCrnDot", toReturn.get (Executer.REQ_WANT_SINGLE_REACTIONS_DOT));
    	}
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
    	exe.executeCompare (file1, file2, toReturn, want, errors);
    	// check for backwards compatibility
    	if (chemicalReactionNetwork)
    	{
    		if (toReturn.get (Executer.REQ_WANT_REACTIONS_GRAPHML) != null)
    			toReturn.put ("crnGraphml", toReturn.get (Executer.REQ_WANT_REACTIONS_GRAPHML));
    		if (toReturn.get (Executer.REQ_WANT_REACTIONS_JSON) != null)
    			toReturn.put ("crnJson", toReturn.get (Executer.REQ_WANT_REACTIONS_JSON));
    		if (toReturn.get (Executer.REQ_WANT_REACTIONS_DOT) != null)
    			toReturn.put ("crnDot", toReturn.get (Executer.REQ_WANT_REACTIONS_DOT));
    	}
    }
    

		if (toReturn.size () < 1)
		{
			throw new ExecutionException ("invalid call. no output produced.");
		}
    
		PrintStream out = System.out;
		if (outFile != null)
		{
			out = new PrintStream (outFile);
		}
    if (output == 0)
    {
    	for (Exception e : errors)
    		System.err.println ("ERROR: " + e);
    	
    	for (String ret : toReturn.keySet ())
    		out.println (toReturn.get (ret));
    }
    else if (output == 1)
    {
    	//xml
    	Element root =  new Element ("bivesResult");
    	Document document = new Document (root);

    	for (String ret : toReturn.keySet ())
    	{
    		Element el = new Element (ret);
    		el.setText (toReturn.get (ret));
    		root.addContent (el);
    	}
    	
    	out.println (XmlTools.prettyPrintDocument (document));
    }
    else
    {
    	// json
    	JSONObject json = new JSONObject ();
    	for (String ret : toReturn.keySet ())
    		json.put (ret, toReturn.get (ret));
    	out.println (json);
    }
	}
	 
}
