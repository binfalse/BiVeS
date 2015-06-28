/**
 * 
 */
package de.unirostock.sems.bives;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import de.unirostock.sems.bives.Main.ExecutionException;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.api.Single;
import de.unirostock.sems.bives.cellml.api.CellMLDiff;
import de.unirostock.sems.bives.cellml.api.CellMLSingle;
import de.unirostock.sems.bives.cellml.parser.CellMLDocument;
import de.unirostock.sems.bives.markup.Typesetting;
import de.unirostock.sems.bives.sbml.api.SBMLDiff;
import de.unirostock.sems.bives.sbml.api.SBMLSingle;
import de.unirostock.sems.bives.sbml.parser.SBMLDocument;
import de.unirostock.sems.bives.tools.DocumentClassifier;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * The Class Executer.
 *
 * @author Martin Scharm
 */
public class Executer
{
	
	/** Pattern to distinguish xml files from URLs. */
	public static final Pattern	XML_PATTERN	= Pattern.compile ("^\\s*<.*",
																						Pattern.DOTALL);
	
	/** The Constant WANT_DIFF. */
	public static final int WANT_DIFF = 1;
	
	/** The Constant WANT_DOCUMENTTYPE. */
	public static final int WANT_DOCUMENTTYPE = 2;
	
	/** The Constant WANT_META. */
	public static final int WANT_META = 4;
	
	/** The Constant WANT_REPORT_MD. */
	public static final int WANT_REPORT_MD = 8;
	
	/** The Constant WANT_REPORT_HTML. */
	public static final int WANT_REPORT_HTML = 16;
	
	/** The Constant WANT_REACTION_GRAPHML. */
	public static final int WANT_REACTION_GRAPHML = 32;
	
	/** The Constant WANT_REACTION_DOT. */
	public static final int WANT_REACTION_DOT = 64;
	
	/** The Constant WANT_COMP_HIERARCHY_GRAPHML. */
	public static final int WANT_COMP_HIERARCHY_GRAPHML = 128;
	
	/** The Constant WANT_COMP_HIERARCHY_DOT. */
	public static final int WANT_COMP_HIERARCHY_DOT = 256;
	
	/** The Constant WANT_REPORT_RST. */
	public static final int WANT_REPORT_RST = 512;
	
	/** The Constant WANT_COMP_HIERARCHY_JSON. */
	public static final int WANT_COMP_HIERARCHY_JSON = 1024;
	
	/** The Constant WANT_REACTION_JSON. */
	public static final int WANT_REACTION_JSON = 2048;
	
	/** The Constant WANT_SBML. */
	public static final int WANT_SBML = 4096;
	
	/** The Constant WANT_CELLML. */
	public static final int WANT_CELLML = 8192;
	
	/** The Constant WANT_REGULAR. */
	public static final int WANT_REGULAR = 16384;

	// single
	/** The Constant WANT_SINGLE_REACTION_GRAPHML. */
	public static final int WANT_SINGLE_REACTION_GRAPHML = 32;
	
	/** The Constant WANT_SINGLE_REACTION_DOT. */
	public static final int WANT_SINGLE_REACTION_DOT = 64;
	
	/** The Constant WANT_SINGLE_COMP_HIERARCHY_GRAPHML. */
	public static final int WANT_SINGLE_COMP_HIERARCHY_GRAPHML = 128;
	
	/** The Constant WANT_SINGLE_COMP_HIERARCHY_DOT. */
	public static final int WANT_SINGLE_COMP_HIERARCHY_DOT = 256;
	
	/** The Constant WANT_SINGLE_COMP_HIERARCHY_JSON. */
	public static final int WANT_SINGLE_COMP_HIERARCHY_JSON = 1024;
	
	/** The Constant WANT_SINGLE_REACTION_JSON. */
	public static final int WANT_SINGLE_REACTION_JSON = 2048;
	
	/** The Constant WANT_SINGLE_FLATTEN. */
	public static final int WANT_SINGLE_FLATTEN = 32768;
	
	/** The Constant WANT_REPORT_HTML_FP. */
	public static final int WANT_REPORT_HTML_FP = 65536;
	
	
	/** The Constant REQ_FILES. */
	public static final String REQ_FILES = "files";
	
	/** The Constant REQ_WANT. */
	public static final String REQ_WANT = "get";
	
	/** The Constant REQ_WANT_META. */
	public static final String REQ_WANT_META = "meta";
	
	/** The Constant REQ_WANT_DOCUMENTTYPE. */
	public static final String REQ_WANT_DOCUMENTTYPE = "documentType";
	
	/** The Constant REQ_WANT_DIFF. */
	public static final String REQ_WANT_DIFF = "xmlDiff";
	
	/** The Constant REQ_WANT_REPORT_MD. */
	public static final String REQ_WANT_REPORT_MD = "reportMd";
	
	/** The Constant REQ_WANT_REPORT_RST. */
	public static final String REQ_WANT_REPORT_RST = "reportRST";
	
	/** The Constant REQ_WANT_REPORT_HTML. */
	public static final String REQ_WANT_REPORT_HTML = "reportHtml";
	
	/** The Constant REQ_WANT_REPORT_HTML_FP. */
	public static final String REQ_WANT_REPORT_HTML_FP = "reportHtmlFp";
	
	/** The Constant REQ_WANT_REACTIONS_GRAPHML. */
	public static final String REQ_WANT_REACTIONS_GRAPHML = "reactionsGraphml";
	
	/** The Constant REQ_WANT_REACTIONS_DOT. */
	public static final String REQ_WANT_REACTIONS_DOT = "reactionsDot";
	
	/** The Constant REQ_WANT_REACTIONS_JSON. */
	public static final String REQ_WANT_REACTIONS_JSON = "reactionsJson";
	
	/** The Constant REQ_WANT_COMP_HIERARCHY_GRAPHML. */
	public static final String REQ_WANT_COMP_HIERARCHY_GRAPHML = "compHierarchyGraphml";
	
	/** The Constant REQ_WANT_COMP_HIERARCHY_DOT. */
	public static final String REQ_WANT_COMP_HIERARCHY_DOT = "compHierarchyDot";
	
	/** The Constant REQ_WANT_COMP_HIERARCHY_JSON. */
	public static final String REQ_WANT_COMP_HIERARCHY_JSON = "compHierarchyJson";
	
	/** The Constant REQ_WANT_SBML. */
	public static final String REQ_WANT_SBML = "SBML";
	
	/** The Constant REQ_WANT_CELLML. */
	public static final String REQ_WANT_CELLML = "CellML";
	
	/** The Constant REQ_WANT_REGULAR. */
	public static final String REQ_WANT_REGULAR = "regular";

	/** The Constant REQ_WANT_SINGLE_REACTIONS_GRAPHML. */
	public static final String REQ_WANT_SINGLE_REACTIONS_GRAPHML = "singleReactionsGraphml";
	
	/** The Constant REQ_WANT_SINGLE_REACTIONS_DOT. */
	public static final String REQ_WANT_SINGLE_REACTIONS_DOT = "singleReactionsDot";
	
	/** The Constant REQ_WANT_SINGLE_REACTIONS_JSON. */
	public static final String REQ_WANT_SINGLE_REACTIONS_JSON = "singleReactionsJson";
	
	/** The Constant REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML. */
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML = "singleCompHierarchyGraphml";
	
	/** The Constant REQ_WANT_SINGLE_COMP_HIERARCHY_DOT. */
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_DOT = "singleCompHierarchyDot";
	
	/** The Constant REQ_WANT_SINGLE_COMP_HIERARCHY_JSON. */
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_JSON = "singleCompHierarchyJson";
	
	/** The Constant REQ_WANT_SINGLE_FLATTEN. */
	public static final String REQ_WANT_SINGLE_FLATTEN = "singleFlatten";
	

	/** The options. */
	private HashMap<String, Option> options;
	
	/** The add options. */
	private HashMap<String, Option> addOptions;
	
	/**
	 * Gets the options.
	 *
	 * @return the options
	 */
	public HashMap<String, Option> getOptions ()
	{
		return options;
	}



	
	/**
	 * Gets the adds the options.
	 *
	 * @return the adds the options
	 */
	public HashMap<String, Option> getAddOptions ()
	{
		return addOptions;
	}




	/**
	 * The Class Option.
	 */
	public static class Option 
	{
		
		/** The description. */
		public String description;
		
		/** The value. */
		public int value;
		
		/**
		 * Instantiates a new option.
		 *
		 * @param value the value
		 * @param description the description
		 */
		public Option (int value, String description)
		{
			this.description = description;
			this.value = value;
		}
	}
	
	/**
	 * Instantiates a new executer.
	 */
	public Executer ()
	{
		fillOptions ();
	}
	
	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the option
	 */
	public Option get (String key)
	{
		Option o = options.get (key);
		if (o == null)
			o = addOptions.get (key);
		return o;
	}
	
	/**
	 * Fill options.
	 */
	private void fillOptions ()
	{
		options = new HashMap<String, Option> ();
		options.put (REQ_WANT_DIFF, new Option (WANT_DIFF, "get the diff encoded in XML format"));
		options.put (REQ_WANT_REPORT_MD, new Option (WANT_REPORT_MD, "get the report of changes encoded in MarkDown"));
		options.put (REQ_WANT_REPORT_RST, new Option (WANT_REPORT_RST, "get the report of changes encoded in ReStructuredText"));
		options.put (REQ_WANT_REPORT_HTML, new Option (WANT_REPORT_HTML, "get the report of changes encoded in HTML"));
		options.put (REQ_WANT_REPORT_HTML_FP, new Option (WANT_REPORT_HTML_FP, "get the report of changes embedded in full HTML page (incl. HTML skeleton)"));
		options.put (REQ_WANT_REACTIONS_GRAPHML, new Option (WANT_REACTION_GRAPHML, "get the highlighted reaction network encoded in GraphML"));
		options.put (REQ_WANT_REACTIONS_DOT, new Option (WANT_REACTION_DOT, "get the highlighted reaction network encoded in DOT language"));
		options.put (REQ_WANT_REACTIONS_JSON, new Option (WANT_REACTION_JSON, "get the highlighted reaction network encoded in JSON"));
		options.put (REQ_WANT_COMP_HIERARCHY_GRAPHML, new Option (WANT_COMP_HIERARCHY_GRAPHML, "get the hierarchy of components in a CellML document encoded in GraphML"));
		options.put (REQ_WANT_COMP_HIERARCHY_DOT, new Option (WANT_COMP_HIERARCHY_DOT, "get the hierarchy of components in a CellML document encoded in DOT language"));
		options.put (REQ_WANT_COMP_HIERARCHY_JSON, new Option (WANT_COMP_HIERARCHY_JSON, "get the hierarchy of components in a CellML document encoded in JSON"));
		options.put (REQ_WANT_SBML, new Option (WANT_SBML, "force SBML comparison"));
		options.put (REQ_WANT_CELLML, new Option (WANT_CELLML, "force CellML comparison"));
		options.put (REQ_WANT_REGULAR, new Option (WANT_REGULAR, "force regular XML comparison"));
		
		addOptions = new HashMap<String, Option> ();
		addOptions.put (REQ_WANT_DOCUMENTTYPE, new Option (WANT_DOCUMENTTYPE, "get the documentType of an XML file"));
		addOptions.put (REQ_WANT_META, new Option (WANT_META, "get some meta information about an XML file"));
		addOptions.put (REQ_WANT_SINGLE_REACTIONS_JSON, new Option (WANT_SINGLE_REACTION_JSON, "get the reaction network of a single file encoded in JSON"));
		addOptions.put (REQ_WANT_SINGLE_REACTIONS_GRAPHML, new Option (WANT_SINGLE_REACTION_GRAPHML, "get the reaction network of a single file encoded in GraphML"));
		addOptions.put (REQ_WANT_SINGLE_REACTIONS_DOT, new Option (WANT_SINGLE_REACTION_DOT, "get the reaction network of a single file encoded in DOT language"));
		addOptions.put (REQ_WANT_SINGLE_COMP_HIERARCHY_JSON, new Option (WANT_SINGLE_COMP_HIERARCHY_JSON, "get the hierarchy of components in a single CellML document encoded in JSON"));
		addOptions.put (REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML, new Option (WANT_SINGLE_COMP_HIERARCHY_GRAPHML, "get the hierarchy of components in a single CellML document encoded in GraphML"));
		addOptions.put (REQ_WANT_SINGLE_COMP_HIERARCHY_DOT, new Option (WANT_SINGLE_COMP_HIERARCHY_DOT, "get the hierarchy of components in a single CellML document encoded in DOT language"));
		addOptions.put (REQ_WANT_SINGLE_FLATTEN, new Option (WANT_SINGLE_FLATTEN, "flatten the model"));
	}
	
	/**
	 * Execute single.
	 *
	 * @param document the document
	 * @param toReturn the to return
	 * @param want the want
	 * @param errors the errors
	 * @throws Exception the exception
	 */
	public void executeSingle (String document, HashMap<String, String> toReturn, int want, List<Exception> errors) throws Exception
	{
		TreeDocument td = null;
		if (XML_PATTERN.matcher (document).find ())
			td = new TreeDocument (XmlTools.readDocument (document), null);
		else
		{
			URL url = new URL (document);
			td = new TreeDocument (XmlTools.readDocument (url), url.toURI ());
		}
		
		DocumentClassifier classifier = null;
  	if ((Executer.WANT_META & want) > 0)
  	{
  		// meta
  		classifier = new DocumentClassifier ();
  		int type = classifier.classify (td);

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
			toReturn.put (Executer.REQ_WANT_META, ret);
  	}
  	
  	if ((Executer.WANT_DOCUMENTTYPE & want) > 0)
  	{
  		// doc type
  		classifier = new DocumentClassifier ();
  		int type = classifier.classify (td);
			
			toReturn.put (Executer.REQ_WANT_DOCUMENTTYPE, DocumentClassifier.humanReadable (type));
  	}
			
  	if ((Executer.WANT_SINGLE_FLATTEN|Executer.WANT_SINGLE_COMP_HIERARCHY_DOT|Executer.WANT_SINGLE_COMP_HIERARCHY_JSON|Executer.WANT_SINGLE_COMP_HIERARCHY_GRAPHML|Executer.WANT_SINGLE_REACTION_JSON|Executer.WANT_SINGLE_REACTION_GRAPHML|Executer.WANT_SINGLE_REACTION_DOT & want) > 0)
  	{
  		Single single = null;
  		
      // decide which kind of mapper to use
      if ((Executer.WANT_CELLML & want) > 0)
      	single = new CellMLSingle (td);
      else if ((Executer.WANT_SBML & want) > 0)
      	single = new SBMLSingle (td);
      else
      {
      	classifier = new DocumentClassifier ();
      	int type = classifier.classify (td);
      	if ((type & DocumentClassifier.SBML) != 0)
      	{
        	single = new SBMLSingle (td);
      	}
      	else if ((type & DocumentClassifier.CELLML) != 0)
      	{
        	single = new CellMLSingle (td);
      	}
      	else
      		throw new ExecutionException ("cannot process this file (type is: ["+DocumentClassifier.humanReadable (type) + "])");
      }
    	
  		if ((want & Executer.WANT_SINGLE_REACTION_JSON) > 0)
  		{
  			try
				{
  				toReturn.put (Executer.REQ_WANT_SINGLE_REACTIONS_JSON, result (single.getReactionsJsonGraph ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  		if ((want & Executer.WANT_SINGLE_REACTION_GRAPHML) > 0)
  		{
  			try
				{
  				toReturn.put (Executer.REQ_WANT_SINGLE_REACTIONS_GRAPHML, result (single.getReactionsGraphML ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  		if ((want & Executer.WANT_SINGLE_REACTION_DOT) > 0)
  		{
  			try
				{
  				toReturn.put (Executer.REQ_WANT_SINGLE_REACTIONS_DOT, result (single.getReactionsDotGraph ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  		if ((want & Executer.WANT_SINGLE_COMP_HIERARCHY_JSON) > 0)
  		{
  			try
				{
  				toReturn.put (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_JSON, result (single.getHierarchyJsonGraph ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  		if ((want & Executer.WANT_SINGLE_COMP_HIERARCHY_GRAPHML) > 0)
  		{
  			try
				{
  				toReturn.put (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML, result (single.getHierarchyGraphML ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  		if ((want & Executer.WANT_SINGLE_COMP_HIERARCHY_DOT) > 0)
  		{
  			try
				{
  				toReturn.put (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_DOT, result (single.getHierarchyDotGraph ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  		if ((want & Executer.WANT_SINGLE_FLATTEN) > 0)
  		{
  			try
				{
					toReturn.put (Executer.REQ_WANT_SINGLE_FLATTEN, result (single.flatten ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  	}
		
	}
	
	
	/**
	 * Execute compare.
	 *
	 * @param document1 the document1
	 * @param document2 the document2
	 * @param toReturn the to return
	 * @param want the want
	 * @param errors the errors
	 * @throws Exception the exception
	 */
	public void executeCompare (String document1, String document2, HashMap<String, String> toReturn, int want, List<Exception> errors) throws Exception
	{
		TreeDocument td1 = null, td2 = null;
		if (XML_PATTERN.matcher (document1).find ())
			td1 = new TreeDocument (XmlTools.readDocument (document1), null);
		else
		{
			URL url = new URL (document1);
			td1 = new TreeDocument (XmlTools.readDocument (url), url.toURI ());
		}
		if (XML_PATTERN.matcher (document2).find ())
			td2 = new TreeDocument (XmlTools.readDocument (document2), null);
		else
		{
			URL url = new URL (document2);
			td2 = new TreeDocument (XmlTools.readDocument (url), url.toURI ());
		}
		
  	// compare mode
		Diff diff = null;
  	DocumentClassifier classifier = null;
    
    if (want == 0)
    	want = Executer.WANT_DIFF;
    
    // decide which kind of mapper to use
    if ((Executer.WANT_CELLML & want) > 0)
    	diff = new CellMLDiff (td1, td2);
    else if ((Executer.WANT_SBML & want) > 0)
    	diff = new SBMLDiff (td1, td2);
    else if ((Executer.WANT_REGULAR & want) > 0)
    	diff = new RegularDiff (td1, td2);
    else
    {
    	classifier = new DocumentClassifier ();
    	int type1 = classifier.classify (td1);
    	int type2 = classifier.classify (td2);
    	int type = type1 & type2;
    	if ((type & DocumentClassifier.SBML) != 0)
    	{
    		diff = new SBMLDiff (td1, td2);
    	}
    	else if ((type & DocumentClassifier.CELLML) != 0)
    	{
    		diff = new CellMLDiff (td1, td2);
    	}
    	else if ((type & DocumentClassifier.XML) != 0)
    	{
    		diff = new RegularDiff (td1, td2);
    	}
    	else
    		throw new ExecutionException ("cannot compare these files (["+DocumentClassifier.humanReadable (type1) + "] ["+DocumentClassifier.humanReadable (type2)+"])");
    }

  	//System.out.println (want);
    
    // create mapping
    diff.mapTrees ();
    
    
    // compute results
		if ((want & Executer.WANT_DIFF) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_DIFF, result (diff.getDiff ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_REACTION_GRAPHML) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_REACTIONS_GRAPHML, result (diff.getReactionsGraphML ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_REACTION_DOT) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_REACTIONS_DOT, result (diff.getReactionsDotGraph ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_REACTION_JSON) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_REACTIONS_JSON, result (diff.getReactionsJsonGraph ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_COMP_HIERARCHY_DOT) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_COMP_HIERARCHY_DOT, result (diff.getHierarchyDotGraph ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_COMP_HIERARCHY_JSON) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_COMP_HIERARCHY_JSON, result (diff.getHierarchyJsonGraph ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_COMP_HIERARCHY_GRAPHML) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_COMP_HIERARCHY_GRAPHML, result (diff.getHierarchyGraphML ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_REPORT_HTML) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_REPORT_HTML, result (diff.getHTMLReport ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_REPORT_HTML_FP) > 0)
			try
			{
				String result = result (diff.getHTMLReport ());
				if (result != null)
					result = htmlPageStart () + result + htmlPageEnd ();
				toReturn.put (Executer.REQ_WANT_REPORT_HTML, result);
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_REPORT_MD) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_REPORT_MD, result (diff.getMarkDownReport ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if ((want & Executer.WANT_REPORT_RST) > 0)
			try
			{
				toReturn.put (Executer.REQ_WANT_REPORT_RST, result (diff.getReStructuredTextReport ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
	}

	
	/**
	 * Result.
	 *
	 * @param s the s
	 * @return the string
	 */
	public static String result (String s)
	{
		if (s == null)
			return "";
		return s;
	}
	
	
	public static String htmlPageStart ()
	{
		return "<!DOCTYPE html>"
			+ Typesetting.NL_TXT
			+ "<html><head><title>BiVeS differences</title>"
			+ "<style type=\"text/css\">"
			+ ".bives-insert"
			+ "{color:#01DF01;}"
			+ ".bives-delete"
			+ "{color:#FF4000;}"
			+ ".bives-attr"
			+ "{font-weight: bold;font-style: italic;}"
			+ ".bives-suppl"
			+ "{color:#A4A4A4;}"
			+ ".bives-update"
			+ "{color:#DFA601;}"
			+ ".bives-move"
			+ "{color:#014ADF;}"
			+ "</style>"
			+ "</head><body>";

	}
	
	public static String htmlPageEnd ()
	{
		return "</body></html>";
	}
	
}
