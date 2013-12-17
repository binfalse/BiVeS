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
import de.unirostock.sems.bives.api.CellMLDiff;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.api.SBMLDiff;
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
	public static final int WANT_DIFF = 1;
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
	
	private void fillOptions ()
	{
		options = new HashMap<String, Option> ();
		//options.put ("--meta", new Option (WANT_META, "get meta information about documents"));
		//options.put ("--documentType", new Option (WANT_DOCUMENTTYPE, ""));
		options.put ("--xmlDiff", new Option (WANT_DIFF, ""));
		options.put ("--reportMd", new Option (WANT_REPORT_MD, ""));
		options.put ("--reportRST", new Option (WANT_REPORT_RST, ""));
		options.put ("--reportHtml", new Option (WANT_REPORT_HTML, ""));
		options.put ("--crnGraphml", new Option (WANT_CRN_GRAPHML, ""));
		options.put ("--crnDot", new Option (WANT_CRN_DOT, ""));
		options.put ("--crnJson", new Option (WANT_CRN_JSON, ""));
		options.put ("--compHierarchyGraphml", new Option (WANT_COMP_HIERARCHY_GRAPHML, ""));
		options.put ("--compHierarchyDot", new Option (WANT_COMP_HIERARCHY_DOT, ""));
		options.put ("--compHierarchyJson", new Option (WANT_COMP_HIERARCHY_JSON, ""));
		options.put ("--SBML", new Option (WANT_SBML, "force SBML comparison"));
		options.put ("--CellML", new Option (WANT_CELLML, "force CellML comparison"));
		options.put ("--regular", new Option (WANT_REGULAR, "force regular XML comparison"));
	}
	
	
	
	public void usage (String msg)
	{
		System.out.println (msg);
		System.out.println ();

		System.out.println ("ARGUMENTS:");
		System.out.println ("\t[option] FILE1 FILE2  compute the differences between 2 XML files");
		System.out.println ("\t--documentType FILE1  get the documentType of an XML file");
		System.out.println ("\t--meta FILE1          get some meta information about an XML file");
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
		
		longest += 2;
		System.out.println ("\tCOMMON OPTIONS");
		System.out.println ("\t[none]"+Tools.repeat (" ", longest - "[none]".length ()) +"expect XML files and print patch");
		System.out.println ("\t--debug"+Tools.repeat (" ", longest - "--debug".length ()) +"enable verbose mode");
		System.out.println ("\t--debugg"+Tools.repeat (" ", longest - "--debugg".length ()) +"enable even more verbose mode");

		System.out.println ();
		System.out.println ("\tMAPPING OPTIONS");
		
		for (String key : keys)
			System.out.println ("\t"+key + Tools.repeat (" ", longest - key.length ()) + options.get (key).description);
		System.out.println ();

		System.out.println ("\tENCODING OPTIONS");
		System.out.println ("\tby default we will just dump the result to the terminal. Thus, it's only usefull if you call for one single output.");
		System.out.println ("\t--json"+Tools.repeat (" ", longest - "--json".length ()) +"encode results in JSON");
		System.out.println ("\t--xml"+Tools.repeat (" ", longest - "--xml".length ()) +"encode results in XML");
		System.exit (2);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main (String[] args) throws Exception
	{
		LOGGER.setLogToStdErr (false);
		LOGGER.setLogToStdOut (false);
		LOGGER.setLevel (LOGGER.ERROR);
		
		//args = new String [] {"test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportHtml", "--xml", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportRST", "--crnGraphml", "--json", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportRST", "--crnGraphml", "--json", "--CellML", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--reportRST", "--crnGraphml", "--json", "--regular", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--debugg", "--reportRST", "--crnGraphml", "--json", "--SBML", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--debugg", "--reportRST", "--crnGraphml", "--json", "--regular", "test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		//args = new String [] {"--meta", "test/BSA-ptinst-2012-11-11"};
		//args = new String [] {"--documentType", "test/BSA-ptinst-2012-11-11"};
		//args = new String [] {"--debugg", "--reportHtml", "test/potato (3).xml", "test/potato (3).xml"};
		
		new Main ().run (args); 
	}
	
	private Main ()
	{
		fillOptions ();
	}
	
	@SuppressWarnings("unchecked")
	private void run (String[] args) throws Exception
	{
		
		Diff diff = null;
    File file1 = null, file2 = null;
    int output = 0;
  	int want = 0;
  	DocumentClassifier classifier = null;
    HashMap<String, String> toReturn = new HashMap<String, String> ();
    
    
    if (args.length < 2)
    {
    	usage ("need at least 2 xml files as arguments.");
    }

    
    for (int i = 0; i < args.length; i++)
    {
    	Option o = options.get (args[i]);
    	if (want >= 0 && o != null)
    	{
    		want |= o.value;
    		continue;
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
    	if (args[i].equals ("--meta"))
    	{
    		want = -1;
    		continue;
    	}
    	if (args[i].equals ("--documentType"))
    	{
    		want = -2;
    		continue;
    	}
    	if (file1 == null)
    		file1 = new File (args[i]);
    	else if (file2 == null)
    		file2 = new File (args[i]);
    	else
    		usage ("do not understand " + args[i] + " (found files " + file1 + " and " + file2 + ")");
    		
    }
    
    if (want < 0 && file1 != null)
    {
    	if (want == -1)
    	{
    		// meta
    		classifier = new DocumentClassifier ();
    		int type = classifier.classify (file1);

  			String ret = "";
  			
    		if ((type & DocumentClassifier.SBML) > 0)
    		{
    			SBMLDocument doc = classifier.getSbmlDocument ();
    			ret += "sbmlVersion:" + doc.getVersion () + ";sbmlLevel:" + doc.getLevel () + ";modelId:" + doc.getModel ().getID () + ";modelName:" + doc.getModel ().getName () + ";";
    		}
    		if ((type & DocumentClassifier.CELLML) > 0)
    		{
    			CellMLDocument doc = classifier.getCellMlDocument ();
    			ret += "containsImports:" + doc.containsImports () + ";modelName:" + doc.getModel ().getName () + ";";
    		}
				if ((type & DocumentClassifier.XML) > 0)
				{
					TreeDocument doc = classifier.getXmlDocument ();
					ret += "nodestats:" + doc.getNodeStats () + ";";
				}
				toReturn.put (REQ_WANT_META, ret);
    	}
    	else if (want == -2)
    	{
    		// doc type
    		classifier = new DocumentClassifier ();
    		int type = classifier.classify (file1);

  			String ret = "";
    		
    		if ((type & DocumentClassifier.XML) != 0)
					ret += ("XML,");
				if ((type & DocumentClassifier.CELLML) != 0)
					ret += ("CellML,");
				if ((type & DocumentClassifier.SBML) != 0)
					ret += ("SBML,");
				
				toReturn.put (REQ_WANT_DOCUMENTTYPE, ret);
    	}
    }
    else
    {
	    if (file1 == null || file2 == null)
	    	usage ("you need to prvide 2 files!");
	    if (!file1.exists ())
	    	usage ("cannot find " + file1.getAbsolutePath ());
	    if (!file1.canRead ())
	    	usage ("cannot read " + file1.getAbsolutePath ());
	    if (!file2.exists ())
	    	usage ("cannot find " + file2.getAbsolutePath ());
	    if (!file2.canRead ())
	    	usage ("cannot read " + file2.getAbsolutePath ());
	    
	    if (want == 0)
	    	want = WANT_DIFF;
	    
	    // decide which kind of mapper to use
	    if ((WANT_CELLML & want) > 0)
	    	diff = new CellMLDiff (file1, file2);
	    else if ((WANT_SBML & want) > 0)
	    	diff = new SBMLDiff (file1, file2);
	    else if ((WANT_REGULAR & want) > 0)
	    	diff = new RegularDiff (file1, file2);
	    else
	    {
	    	classifier = new DocumentClassifier ();
	    	int type = classifier.classify (file1);
	    	type = type & classifier.classify (file2);
	    	if ((type & DocumentClassifier.SBML) != 0)
	    	{
	    		diff = new SBMLDiff (file1, file2);
	    	}
	    	else if ((type & DocumentClassifier.CELLML) != 0)
	    	{
	    		diff = new SBMLDiff (file1, file2);
	    	}
	    	else if ((type & DocumentClassifier.XML) != 0)
	    	{
	    		diff = new SBMLDiff (file1, file2);
	    	}
	    	else
	    		usage ("cannot compare these files");
	    }
	    
	    if (diff == null)
	  		usage ("cannot compare these files");
	
	  	//System.out.println (want);
	    
	    // create mapping
	    diff.mapTrees ();
	    
	    
	    // compute results
			if ((want & WANT_DIFF) > 0)
				toReturn.put (REQ_WANT_DIFF, diff.getDiff ());
			
			if ((want & WANT_CRN_GRAPHML) > 0)
				toReturn.put (REQ_WANT_CRN_GRAPHML, diff.getCRNGraphML ());
			
			if ((want & WANT_CRN_DOT) > 0)
				toReturn.put (REQ_WANT_CRN_DOT, diff.getCRNDotGraph ());
			
			if ((want & WANT_CRN_JSON) > 0)
				toReturn.put (REQ_WANT_CRN_JSON, diff.getCRNJsonGraph ());
			
			if ((want & WANT_COMP_HIERARCHY_DOT) > 0)
				toReturn.put (REQ_WANT_COMP_HIERARCHY_DOT, diff.getHierarchyDotGraph ());
			
			if ((want & WANT_COMP_HIERARCHY_JSON) > 0)
				toReturn.put (REQ_WANT_COMP_HIERARCHY_JSON, diff.getHierarchyJsonGraph ());
			
			if ((want & WANT_COMP_HIERARCHY_GRAPHML) > 0)
				toReturn.put (REQ_WANT_COMP_HIERARCHY_GRAPHML, diff.getHierarchyGraphML ());
			
			if ((want & WANT_REPORT_HTML) > 0)
				toReturn.put (REQ_WANT_REPORT_HTML, diff.getHTMLReport ());
			
			if ((want & WANT_REPORT_MD) > 0)
				toReturn.put (REQ_WANT_REPORT_MD, diff.getMarkDownReport ());
			
			if ((want & WANT_REPORT_RST) > 0)
				toReturn.put (REQ_WANT_REPORT_RST, diff.getReStructuredTextReport ());
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
