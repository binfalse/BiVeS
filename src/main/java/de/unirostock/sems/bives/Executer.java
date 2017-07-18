/**
 * 
 */
package de.unirostock.sems.bives;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.json.simple.JSONObject;

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
	
	public static final String REQ_WANT_MATCHING_IDS = "ids-must-match";
	public static final String REQ_WANT_NEGLECT_NAMES = "neglect-names";
	public static final String REQ_WANT_STRICT_NAMES = "strict-names";
	
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
	public static final String REQ_WANT_REACTIONS_GRAPHML2 = "crnGraphml";
	
	/** The Constant REQ_WANT_REACTIONS_DOT. */
	public static final String REQ_WANT_REACTIONS_DOT = "reactionsDot";
	public static final String REQ_WANT_REACTIONS_DOT2 = "crnDot";
	
	/** The Constant REQ_WANT_REACTIONS_SBGN_JSON. */
	public static final String REQ_WANT_REACTIONS_SBGN_JSON = "reactionsSbgnJson";
		
	/** The Constant REQ_WANT_REACTIONS_JSON. */
	public static final String REQ_WANT_REACTIONS_JSON = "reactionsJson";
	public static final String REQ_WANT_REACTIONS_JSON2 = "crnJson";
	
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
	public static final String REQ_WANT_SINGLE_REACTIONS_GRAPHML2 = "singleCrnGraphml";
	
	/** The Constant REQ_WANT_SINGLE_REACTIONS_DOT. */
	public static final String REQ_WANT_SINGLE_REACTIONS_DOT = "singleReactionsDot";
	public static final String REQ_WANT_SINGLE_REACTIONS_DOT2 = "singleCrnDot";
	
	/** The Constant REQ_WANT_SINGLE_REACTIONS_JSON. */
	public static final String REQ_WANT_SINGLE_REACTIONS_JSON = "singleReactionsJson";
	public static final String REQ_WANT_SINGLE_REACTIONS_JSON2 = "singleCrnJson";
	
	/** The Constant REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML. */
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML = "singleCompHierarchyGraphml";
	
	/** The Constant REQ_WANT_SINGLE_COMP_HIERARCHY_DOT. */
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_DOT = "singleCompHierarchyDot";
	
	/** The Constant REQ_WANT_SINGLE_COMP_HIERARCHY_JSON. */
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_JSON = "singleCompHierarchyJson";
	
	/** The Constant REQ_WANT_SINGLE_FLATTEN. */
	public static final String REQ_WANT_SINGLE_FLATTEN = "singleFlatten";

	public static final String REQ_DEBUG = "debug";
	public static final String REQ_DEBUGG = "debugg";
	public static final String REQ_XML = "xml";
	public static final String REQ_INC_ANNO = "inclAnnotations";
	public static final String REQ_SEP_ANNO = "separateAnnotations";
	public static final String REQ_JSON = "json";
	public static final String REQ_CA = "combinearchive";
	public static final String REQ_OUT = "out";
	public static final String REQ_HELP = "help";

	/** The options. */
	private List<String> comparisonOptions;
	
	/** The add options. */
	private List<String> singleOptions;
	
	private List<String> commonOptions;


	private Options options;

	
	/**
	 * Get available (command-line) options.
	 *
	 * @return the options
	 */
	public Options getOptions ()
	{
		return options;
	}
	
	/**
	 * Instantiates a new executer.
	 */
	public Executer ()
	{
		fillOptions ();
	}

	/**
	 * Fill options.
	 */
	private void fillOptions ()
	{
		options = new Options ();

		options.addOption (Option.builder ("v").longOpt (REQ_DEBUG).desc ("enable verbose mode").build ());
		options.addOption (Option.builder ().longOpt (REQ_DEBUGG).desc ("enable even more verbose mode").build ());
		options.addOptionGroup (new OptionGroup ()
			.addOption (Option.builder ("x").longOpt (REQ_XML).desc ("encode results in XML").build ())
			.addOption (Option.builder ("j").longOpt (REQ_JSON).desc ("encode results in JSON").build ()));
		options.addOption (Option.builder ("o").longOpt (REQ_OUT).hasArg ().desc ("write output to a file").build ());
		options.addOption (Option.builder ("h").longOpt (REQ_HELP).desc ("help").build ());
		options.addOption (Option.builder ().longOpt (REQ_INC_ANNO).desc ("include annotations in the patch").build ());
		options.addOption (Option.builder ().longOpt (REQ_SEP_ANNO).desc ("get annotations separated from the patch").build ());
		
		
		options.addOption (Option.builder ().longOpt (REQ_WANT_DIFF).desc ("get the diff encoded in XML format").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REPORT_MD).desc ("get the report of changes encoded in MarkDown").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REPORT_RST).desc ("get the report of changes encoded in ReStructuredText").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REPORT_HTML).desc ("get the report of changes encoded in HTML").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REPORT_HTML_FP).desc ("get the report of changes embedded in full HTML page (incl. HTML skeleton)").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REACTIONS_GRAPHML).desc ("get the highlighted reaction network encoded in GraphML").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REACTIONS_DOT).desc ("get the highlighted reaction network encoded in DOT language").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REACTIONS_JSON).desc ("get the highlighted reaction network encoded in JSON").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REACTIONS_SBGN_JSON).desc ("get the highlighted reaction network encoded in an SBGN-JSON format").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REACTIONS_GRAPHML2).desc ("get the highlighted reaction network encoded in GraphML (deprecated version)").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REACTIONS_DOT2).desc ("get the highlighted reaction network encoded in DOT language (deprecated version)").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REACTIONS_JSON2).desc ("get the highlighted reaction network encoded in JSON (deprecated version)").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_COMP_HIERARCHY_GRAPHML).desc ("get the hierarchy of components in a CellML document encoded in GraphML").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_COMP_HIERARCHY_DOT).desc ("get the hierarchy of components in a CellML document encoded in DOT language").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_COMP_HIERARCHY_JSON).desc ("get the hierarchy of components in a CellML document encoded in JSON").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SBML).desc ("force SBML comparison").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_CELLML).desc ("force CellML comparison").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_REGULAR).desc ("force regular XML comparison").build ());

		options.addOption (Option.builder ().longOpt (REQ_WANT_MATCHING_IDS).desc ("ids of mapped nodes MUST match").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_NEGLECT_NAMES).desc ("treat names as usual attributes").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_STRICT_NAMES).desc ("compare names more strictly").build ());
		
		
		options.addOption (Option.builder ().longOpt (REQ_WANT_DOCUMENTTYPE).desc ("get the documentType of an XML file").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_META).desc ("get some meta information about an XML file").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_REACTIONS_JSON).desc ("get the reaction network of a single file encoded in JSON").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_REACTIONS_GRAPHML).desc ("get the reaction network of a single file encoded in GraphML").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_REACTIONS_DOT).desc ("get the reaction network of a single file encoded in DOT language").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_REACTIONS_JSON2).desc ("get the reaction network of a single file encoded in JSON (deprecated version)").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_REACTIONS_GRAPHML2).desc ("get the reaction network of a single file encoded in GraphML (deprecated version)").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_REACTIONS_DOT2).desc ("get the reaction network of a single file encoded in DOT language (deprecated version)").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_COMP_HIERARCHY_JSON).desc ("get the hierarchy of components in a single CellML document encoded in JSON").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML).desc ("get the hierarchy of components in a single CellML document encoded in GraphML").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_COMP_HIERARCHY_DOT).desc ("get the hierarchy of components in a single CellML document encoded in DOT language").build ());
		options.addOption (Option.builder ().longOpt (REQ_WANT_SINGLE_FLATTEN).desc ("flatten the model").build ());
	}
	
	/**
	 * Execute single.
	 *
	 * @param document the document
	 * @param toReturn the to return
	 * @param line the line
	 * @param errors the errors
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public void executeSingle (String document, HashMap<String, String> toReturn, CommandLine line, List<Exception> errors) throws Exception
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
  	if (line.hasOption (REQ_WANT_META))
  	{
  		// meta
  		classifier = new DocumentClassifier ();
  		int type = classifier.classify (td);
    	JSONObject json = new JSONObject ();

			//String ret = "";
			
  		if ((type & DocumentClassifier.SBML) > 0)
  		{
  			SBMLDocument doc = classifier.getSbmlDocument ();
  			//ret += "sbmlVersion:" + doc.getVersion () + ";sbmlLevel:" + doc.getLevel () + ";modelId:" + doc.getModel ().getID () + ";modelName:" + doc.getModel ().getName () + ";";
  			json.put ("sbmlVersion", doc.getVersion ());
  			json.put ("sbmlLevel", doc.getLevel ());
  			json.put ("modelId", doc.getModel ().getID ());
  			json.put ("modelName", doc.getModel ().getName ());
  		}
  		if ((type & DocumentClassifier.CELLML) > 0)
  		{
  			CellMLDocument doc = classifier.getCellMlDocument ();
  			//ret += "containsImports:" + doc.containsImports () + ";modelName:" + doc.getModel ().getName () + ";";
  			json.put ("containsImports", doc.containsImports ());
  			json.put ("modelName", doc.getModel ().getName ());
  		}
			if ((type & DocumentClassifier.XML) > 0)
			{
				TreeDocument doc = classifier.getXmlDocument ();
				//ret += "nodestats:" + doc.getNodeStats () + ";";
  			json.put ("nodestats", doc.getNodeStats ());
			}
			toReturn.put (Executer.REQ_WANT_META, json.toJSONString ());
  	}
  	
  	if (line.hasOption (REQ_WANT_DOCUMENTTYPE))
  	{
  		// doc type
  		classifier = new DocumentClassifier ();
  		int type = classifier.classify (td);
			
			toReturn.put (Executer.REQ_WANT_DOCUMENTTYPE, DocumentClassifier.humanReadable (type));
  	}
			
  	if (
  		line.hasOption (REQ_WANT_SINGLE_FLATTEN) ||
  		line.hasOption (REQ_WANT_SINGLE_COMP_HIERARCHY_DOT) ||
  		line.hasOption (REQ_WANT_SINGLE_COMP_HIERARCHY_JSON) ||
  		line.hasOption (REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML) ||
  		line.hasOption (REQ_WANT_SINGLE_REACTIONS_JSON) ||
  		line.hasOption (REQ_WANT_SINGLE_REACTIONS_GRAPHML) ||
  		line.hasOption (REQ_WANT_SINGLE_REACTIONS_DOT) ||
  		line.hasOption (REQ_WANT_SINGLE_REACTIONS_JSON2) ||
  		line.hasOption (REQ_WANT_SINGLE_REACTIONS_GRAPHML2) ||
  		line.hasOption (REQ_WANT_SINGLE_REACTIONS_DOT2)
  		)
  	{
  		Single single = null;
  		
      // decide which kind of mapper to use
      if (line.hasOption (REQ_WANT_CELLML))
      	single = new CellMLSingle (td);
      else if (line.hasOption (REQ_WANT_SBML))
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
    	
  		if (line.hasOption (REQ_WANT_SINGLE_REACTIONS_JSON) || line.hasOption (REQ_WANT_SINGLE_REACTIONS_JSON2))
  		{
  			try
				{
  				toReturn.put (Executer.REQ_WANT_SINGLE_REACTIONS_JSON, result (single.getReactionsJsonGraph ()));
  				if (line.hasOption (REQ_WANT_SINGLE_REACTIONS_JSON2))
    				toReturn.put (Executer.REQ_WANT_SINGLE_REACTIONS_JSON2, result (single.getReactionsJsonGraph ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  		if (line.hasOption (REQ_WANT_SINGLE_REACTIONS_GRAPHML) || line.hasOption (REQ_WANT_SINGLE_REACTIONS_GRAPHML2))
  		{
  			try
				{
  				toReturn.put (Executer.REQ_WANT_SINGLE_REACTIONS_GRAPHML, result (single.getReactionsGraphML ()));
  				if (line.hasOption (REQ_WANT_SINGLE_REACTIONS_GRAPHML2))
    				toReturn.put (Executer.REQ_WANT_SINGLE_REACTIONS_GRAPHML2, result (single.getReactionsGraphML ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  		if (line.hasOption (REQ_WANT_SINGLE_REACTIONS_DOT) || line.hasOption (REQ_WANT_SINGLE_REACTIONS_DOT2))
  		{
  			try
				{
  				toReturn.put (Executer.REQ_WANT_SINGLE_REACTIONS_DOT, result (single.getReactionsDotGraph ()));
  				if (line.hasOption (REQ_WANT_SINGLE_REACTIONS_DOT2))
  					toReturn.put (Executer.REQ_WANT_SINGLE_REACTIONS_DOT2, result (single.getReactionsDotGraph ()));
				}
				catch (Exception e)
				{
					errors.add (e);
				}
  		}
  		if (line.hasOption (REQ_WANT_SINGLE_COMP_HIERARCHY_JSON))
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
  		if (line.hasOption (REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML))
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
  		if (line.hasOption (REQ_WANT_SINGLE_COMP_HIERARCHY_DOT))
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
  		if (line.hasOption (REQ_WANT_SINGLE_FLATTEN))
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
	 * @param line the line
	 * @param errors the errors
	 * @throws Exception the exception
	 */
	public void executeCompare (String document1, String document2, HashMap<String, String> toReturn, CommandLine line, List<Exception> errors) throws Exception
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
    
    // decide which kind of mapper to use
    if (line.hasOption (REQ_WANT_CELLML))
    	diff = new CellMLDiff (td1, td2);
    else if (line.hasOption (REQ_WANT_SBML))
    	diff = new SBMLDiff (td1, td2);
    else if (line.hasOption (REQ_WANT_REGULAR))
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
    diff.mapTrees (
    	!line.hasOption (REQ_WANT_MATCHING_IDS),
    	!line.hasOption (REQ_WANT_NEGLECT_NAMES),
    	line.hasOption (REQ_WANT_STRICT_NAMES)
    	);
    
    
    // compute results
    boolean hasOption = false;
		if (line.hasOption (REQ_WANT_REACTIONS_GRAPHML) || line.hasOption (REQ_WANT_REACTIONS_GRAPHML2))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_REACTIONS_GRAPHML, result (diff.getReactionsGraphML ()));
				if (line.hasOption (REQ_WANT_REACTIONS_GRAPHML2))
					toReturn.put (Executer.REQ_WANT_REACTIONS_GRAPHML2, result (diff.getReactionsGraphML ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if (line.hasOption (REQ_WANT_REACTIONS_DOT) || line.hasOption (REQ_WANT_REACTIONS_DOT2))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_REACTIONS_DOT, result (diff.getReactionsDotGraph ()));
				if (line.hasOption (REQ_WANT_REACTIONS_DOT2))
					toReturn.put (Executer.REQ_WANT_REACTIONS_DOT2, result (diff.getReactionsDotGraph ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if (line.hasOption (REQ_WANT_REACTIONS_JSON) || line.hasOption (REQ_WANT_REACTIONS_JSON2))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_REACTIONS_JSON, result (diff.getReactionsJsonGraph ()));
				if (line.hasOption (REQ_WANT_REACTIONS_JSON2))
					toReturn.put (Executer.REQ_WANT_REACTIONS_JSON2, result (diff.getReactionsJsonGraph ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if (line.hasOption (REQ_WANT_REACTIONS_SBGN_JSON))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_REACTIONS_SBGN_JSON, result (diff.getReactionsSbgnJsonGraph ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if (line.hasOption (REQ_WANT_COMP_HIERARCHY_DOT))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_COMP_HIERARCHY_DOT, result (diff.getHierarchyDotGraph ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if (line.hasOption (REQ_WANT_COMP_HIERARCHY_JSON))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_COMP_HIERARCHY_JSON, result (diff.getHierarchyJsonGraph ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if (line.hasOption (REQ_WANT_COMP_HIERARCHY_GRAPHML))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_COMP_HIERARCHY_GRAPHML, result (diff.getHierarchyGraphML ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if (line.hasOption (REQ_WANT_REPORT_HTML) || line.hasOption (REQ_WANT_REPORT_HTML_FP))
			try
			{
				hasOption = true;
				String result = result (diff.getHTMLReport ());
				if (line.hasOption (REQ_WANT_REPORT_HTML))
					toReturn.put (Executer.REQ_WANT_REPORT_HTML, result);
				if (line.hasOption (REQ_WANT_REPORT_HTML_FP) && result != null)
				{
					toReturn.put (Executer.REQ_WANT_REPORT_HTML_FP, htmlPageStart () + result + htmlPageEnd ());
				}
					
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if (line.hasOption (REQ_WANT_REPORT_MD))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_REPORT_MD, result (diff.getMarkDownReport ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		
		if (line.hasOption (REQ_WANT_REPORT_RST))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_REPORT_RST, result (diff.getReStructuredTextReport ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		if (line.hasOption (REQ_SEP_ANNO))
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_SEP_ANNO, result (diff.getPatch ().getAnnotationDocumentXml ()));
			}
			catch (Exception e)
			{
				errors.add (e);
			}
		if (line.hasOption (REQ_WANT_DIFF) || !hasOption)
			try
			{
				hasOption = true;
				toReturn.put (Executer.REQ_WANT_DIFF, result (diff.getDiff (line.hasOption (REQ_INC_ANNO))));
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
			+ ".bives-suppl"
			+ "{font-size:.8em;}"
			+ "</style>"
			+ "</head><body>";

	}
	
	public static String htmlPageEnd ()
	{
		return "</body></html>";
	}
	
}
