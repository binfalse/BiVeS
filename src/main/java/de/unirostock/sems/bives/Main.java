/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.File;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.GraphProducer;
import de.unirostock.sems.bives.algorithm.cellml.CellMLGraphProducer;
import de.unirostock.sems.bives.algorithm.sbml.SBMLGraphProducer;
import de.unirostock.sems.bives.api.CellMLDiff;
import de.unirostock.sems.bives.api.CellMLSingle;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.api.SBMLDiff;
import de.unirostock.sems.bives.api.SBMLSingle;
import de.unirostock.sems.bives.api.Single;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.tools.DocumentClassifier;
import de.unirostock.sems.bives.tools.Tools;
import de.unirostock.sems.bives.tools.XmlTools;
//import de.unirostock.sems.bives.algorithm.sbmldeprecated.SBMLDiffInterpreter;

//TODO: detect document type
//TODO: graph producer

/**
 * @author Martin Scharm
 *
 */
public class Main
{
	public static boolean exit = true;
	
	/*public static final int WANT_DIFF = 1;
	public static final int WANT_DOCUMENTTYPE = 2;
	public static final int WANT_META = 4;
	public static final int WANT_REPORT_MD = 8;
	public static final int WANT_REPORT_HTML = 16;
	public static final int WANT_CRN_GRAPHML = 32;
	public static final int WANT_CRN_DOT = 64;
	public static final int WANT_COMP_HIERARCHY_GRAPHML = 128;
	public static final int WANT_COMP_HIERARCHY_DOT = 256;
	public static final int WANT_REPORT_RST = 512;
	public static final int WANT_COMP_HIERARCHY_JSON = 1024;
	public static final int WANT_CRN_JSON = 2048;
	public static final int WANT_SBML = 4096;
	public static final int WANT_CELLML = 8192;
	public static final int WANT_REGULAR = 16384;

	// single
	public static final int WANT_SINGLE_CRN_GRAPHML = 32;
	public static final int WANT_SINGLE_CRN_DOT = 64;
	public static final int WANT_SINGLE_COMP_HIERARCHY_GRAPHML = 128;
	public static final int WANT_SINGLE_COMP_HIERARCHY_DOT = 256;
	public static final int WANT_SINGLE_COMP_HIERARCHY_JSON = 1024;
	public static final int WANT_SINGLE_CRN_JSON = 2048;
	
	
	public static final String REQ_FILES = "files";
	public static final String REQ_WANT = "get";
	public static final String REQ_WANT_META = "meta";
	public static final String REQ_WANT_DOCUMENTTYPE = "documentType";
	public static final String REQ_WANT_DIFF = "xmlDiff";
	public static final String REQ_WANT_REPORT_MD = "reportMd";
	public static final String REQ_WANT_REPORT_RST = "reportRST";
	public static final String REQ_WANT_REPORT_HTML = "reportHtml";
	public static final String REQ_WANT_CRN_GRAPHML = "crnGraphml";
	public static final String REQ_WANT_CRN_DOT = "crnDot";
	public static final String REQ_WANT_CRN_JSON = "crnJson";
	public static final String REQ_WANT_COMP_HIERARCHY_GRAPHML = "compHierarchyGraphml";
	public static final String REQ_WANT_COMP_HIERARCHY_DOT = "compHierarchyDot";
	public static final String REQ_WANT_COMP_HIERARCHY_JSON = "compHierarchyJson";

	public static final String REQ_WANT_SINGLE_CRN_GRAPHML = "singleCrnGraphml";
	public static final String REQ_WANT_SINGLE_CRN_DOT = "singleCrnDot";
	public static final String REQ_WANT_SINGLE_CRN_JSON = "singleCrnJson";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML = "singleCompHierarchyGraphml";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_DOT = "singleCompHierarchyDot";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_JSON = "singleCompHierarchyJson";
	
	private class Option 
	{
		public String description;
		public int value;
		public Option (int value, String description)
		{
			this.description = description;
			this.value = value;
		}
	}

	private HashMap<String, Option> options;
	private HashMap<String, Option> addOptions;
	
	private void fillOptions ()
	{
		options = new HashMap<String, Option> ();
		//options.put ("--meta", new Option (WANT_META, "get meta information about documents"));
		//options.put ("--documentType", new Option (WANT_DOCUMENTTYPE, ""));
		options.put ("--xmlDiff", new Option (WANT_DIFF, "get the diff encoded in XML format"));
		options.put ("--reportMd", new Option (WANT_REPORT_MD, "get the report of changes encoded in MarkDown"));
		options.put ("--reportRST", new Option (WANT_REPORT_RST, "get the report of changes encoded in ReStructuredText"));
		options.put ("--reportHtml", new Option (WANT_REPORT_HTML, "get the report of changes encoded in HTML"));
		options.put ("--crnGraphml", new Option (WANT_CRN_GRAPHML, "get the highlighted chemical reaction network encoded in GraphML"));
		options.put ("--crnDot", new Option (WANT_CRN_DOT, "get the highlighted chemical reaction network encoded in DOT language"));
		options.put ("--crnJson", new Option (WANT_CRN_JSON, "get the highlighted chemical reaction network encoded in JSON"));
		options.put ("--compHierarchyGraphml", new Option (WANT_COMP_HIERARCHY_GRAPHML, "get the hierarchy of components in a CellML document encoded in GraphML"));
		options.put ("--compHierarchyDot", new Option (WANT_COMP_HIERARCHY_DOT, "get the hierarchy of components in a CellML document encoded in DOT language"));
		options.put ("--compHierarchyJson", new Option (WANT_COMP_HIERARCHY_JSON, "get the hierarchy of components in a CellML document encoded in JSON"));
		options.put ("--SBML", new Option (WANT_SBML, "force SBML comparison"));
		options.put ("--CellML", new Option (WANT_CELLML, "force CellML comparison"));
		options.put ("--regular", new Option (WANT_REGULAR, "force regular XML comparison"));
		
		addOptions = new HashMap<String, Option> ();
		addOptions.put ("--documentType", new Option (WANT_DOCUMENTTYPE, "get the documentType of an XML file"));
		addOptions.put ("--meta", new Option (WANT_META, "get some meta information about an XML file"));
		addOptions.put ("--singleCrnJson", new Option (WANT_SINGLE_CRN_JSON, "get the chemical reaction network of a single file encoded in JSON"));
		addOptions.put ("--singleCrnGraphml", new Option (WANT_SINGLE_CRN_GRAPHML, "get the chemical reaction network of a single file encoded in GraphML"));
		addOptions.put ("--singleCrnDot", new Option (WANT_SINGLE_CRN_DOT, "get the chemical reaction network of a single file encoded in DOT language"));
		addOptions.put ("--singleCompHierarchyJson", new Option (WANT_SINGLE_COMP_HIERARCHY_JSON, "get the hierarchy of components in a single CellML document encoded in JSON"));
		addOptions.put ("--singleCompHierarchyGraphml", new Option (WANT_SINGLE_COMP_HIERARCHY_GRAPHML, "get the hierarchy of components in a single CellML document encoded in GraphML"));
		addOptions.put ("--singleCompHierarchyDot", new Option (WANT_SINGLE_COMP_HIERARCHY_DOT, "get the hierarchy of components in a single CellML document encoded in DOT language"));
	}*/
	
	
	
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
		System.out.println ("FILE1 and FILE2 define XML files to compare");
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
		System.out.println ("\t[none]"+Tools.repeat (" ", longest - "[none]".length ()) +"expect XML files and print patch");
		System.out.println ("\t--help"+Tools.repeat (" ", longest - "--help".length ()) +"print this help");
		System.out.println ("\t--debug"+Tools.repeat (" ", longest - "--debug".length ()) +"enable verbose mode");
		System.out.println ("\t--debugg"+Tools.repeat (" ", longest - "--debugg".length ()) +"enable even more verbose mode");

		System.out.println ();
		System.out.println ("\tMAPPING OPTIONS");
		
		for (String key : keys)
			System.out.println ("\t--"+key + Tools.repeat (" ", longest - key.length ()) + options.get (key).description);
		System.out.println ();

		System.out.println ("\tENCODING OPTIONS");
		System.out.println ("\tby default we will just dump the result to the terminal. Thus, it's only usefull if you call for one single output.");
		System.out.println ("\t--json"+Tools.repeat (" ", longest - "--json".length ()) +"encode results in JSON");
		System.out.println ("\t--xml"+Tools.repeat (" ", longest - "--xml".length ()) +"encode results in XML");
		System.out.println ();

		System.out.println ("\tADDITIONAL OPTIONS for single files");
		for (String key : addKeys)
			System.out.println ("\t--"+key + Tools.repeat (" ", longest - key.length ()) + addOptions.get (key).description);
		System.out.println ();

		if (exit)
			System.exit (2);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main (String[] args)
	{
		LOGGER.setLogToStdErr (false);
		LOGGER.setLogToStdOut (false);
		LOGGER.setLevel (LOGGER.ERROR);
		
		//args = new String [] {"test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportHtml", "--xml", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportHtml", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportHtml", "test/BSA-ptinst-2012-11-11", "test/BSA-ptinst-2012-11-11"};
		//args = new String [] {"--reportRST", "--crnGraphml", "--json", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportRST", "--crnGraphml", "--json", "--CellML", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportRST", "--crnGraphml", "--json", "--regular", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--debugg", "--reportRST", "--crnGraphml", "--json", "--SBML", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--debugg", "--reportRST", "--crnGraphml", "--json", "--regular", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--meta", "test/BSA-ptinst-2012-11-11"};
		//args = new String [] {"--documentType", "test/BSA-ptinst-2012-11-11"};
		//args = new String [] {"--documentType", "test/BSA-ptinst-2012-11-11", "test/BSA-ptinst-2012-11-11"};
		//args = new String [] {"--debugg", "--reportHtml", "test/potato (3).xml", "test/potato (3).xml"};
		//args = new String [] {"--help"};
		//args = new String [] {"--singleCompHierarchyJson", "test/bhalla_iyengar_1999_j_v1.cellml"};
		//args = new String [] {"--reportHtml", "--SBML", "test/teusink-1.dat", "test/teusink-1.dat"};
		
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
	{}
	
	public static class ExecutionException extends Exception
	{
		public ExecutionException (String msg)
		{
			super (msg);
		}
	}
	
	Executer exe;
	
	private Main ()
	{
		exe = new Executer ();
	}
	
	@SuppressWarnings("unchecked")
	private void run (String[] args) throws Exception
	{
		
    File file1 = null, file2 = null;
    int output = 0;
  	int want = 0;
    HashMap<String, String> toReturn = new HashMap<String, String> ();
    

    
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
    	if (args[i].equals ("--help"))
    	{
    		throw new HelpException ();
    	}
    	if (file1 == null)
    		file1 = new File (args[i]);
    	else if (file2 == null)
    		file2 = new File (args[i]);
    	else
    		throw new ExecutionException ("do not understand " + args[i] + " (found files " + file1 + " and " + file2 + ")");
    		
    }
    

    if (file1 == null)
    	throw new ExecutionException ("no file provided");
    if (!file1.exists ())
    	throw new ExecutionException ("cannot find " + file1.getAbsolutePath ());
    if (!file1.canRead ())
    	throw new ExecutionException ("cannot read " + file1.getAbsolutePath ());
    
    
    if (file2 == null)
    {
    	// single mode
    	exe.executeSingle (file1, toReturn, want);
    }
    else
    {
    	// compare two files
    	exe.executeCompare (file1, file2, toReturn, want);
    }
    

		if (toReturn.size () < 1)
		{
			throw new ExecutionException ("invalid call. no output produced.");
		}
    
    if (output == 0)
    {
    	for (String ret : toReturn.keySet ())
    		System.out.println (toReturn.get (ret));
    }
    else if (output == 1)
    {
    	//xml
    	DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
    	Document document = builder.newDocument();
    	Element root =  (Element) document.createElement("bivesResult"); 
    	document.appendChild(root);

    	for (String ret : toReturn.keySet ())
    	{
    		Element el = (Element) document.createElement(ret);
    		el.appendChild (document.createTextNode(toReturn.get (ret)));
    		root.appendChild(el);
    	}
    	
    	System.out.println (XmlTools.prettyPrintDocument (document));
    }
    else
    {
    	// json
    	JSONObject json = new JSONObject ();
    	for (String ret : toReturn.keySet ())
    		json.put (ret, toReturn.get (ret));
    	System.out.println (json);
    }
	}
	
}
