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
import de.unirostock.sems.bives.sbml.api.SBMLDiff;
import de.unirostock.sems.bives.sbml.api.SBMLSingle;
import de.unirostock.sems.bives.sbml.parser.SBMLDocument;
import de.unirostock.sems.bives.tools.DocumentClassifier;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * @author Martin Scharm
 *
 */
public class Executer
{
	
	/** Pattern to distinguish xml files from URLs */
	public static final Pattern	XML_PATTERN	= Pattern.compile ("^\\s*<.*",
																						Pattern.DOTALL);
	
	public static final int WANT_DIFF = 1;
	public static final int WANT_DOCUMENTTYPE = 2;
	public static final int WANT_META = 4;
	public static final int WANT_REPORT_MD = 8;
	public static final int WANT_REPORT_HTML = 16;
	public static final int WANT_REACTION_GRAPHML = 32;
	public static final int WANT_REACTION_DOT = 64;
	public static final int WANT_COMP_HIERARCHY_GRAPHML = 128;
	public static final int WANT_COMP_HIERARCHY_DOT = 256;
	public static final int WANT_REPORT_RST = 512;
	public static final int WANT_COMP_HIERARCHY_JSON = 1024;
	public static final int WANT_REACTION_JSON = 2048;
	public static final int WANT_SBML = 4096;
	public static final int WANT_CELLML = 8192;
	public static final int WANT_REGULAR = 16384;

	// single
	public static final int WANT_SINGLE_REACTION_GRAPHML = 32;
	public static final int WANT_SINGLE_REACTION_DOT = 64;
	public static final int WANT_SINGLE_COMP_HIERARCHY_GRAPHML = 128;
	public static final int WANT_SINGLE_COMP_HIERARCHY_DOT = 256;
	public static final int WANT_SINGLE_COMP_HIERARCHY_JSON = 1024;
	public static final int WANT_SINGLE_REACTION_JSON = 2048;
	public static final int WANT_SINGLE_FLATTEN = 32768;
	
	
	public static final String REQ_FILES = "files";
	public static final String REQ_WANT = "get";
	public static final String REQ_WANT_META = "meta";
	public static final String REQ_WANT_DOCUMENTTYPE = "documentType";
	public static final String REQ_WANT_DIFF = "xmlDiff";
	public static final String REQ_WANT_REPORT_MD = "reportMd";
	public static final String REQ_WANT_REPORT_RST = "reportRST";
	public static final String REQ_WANT_REPORT_HTML = "reportHtml";
	public static final String REQ_WANT_REACTIONS_GRAPHML = "reactionsGraphml";
	public static final String REQ_WANT_REACTIONS_DOT = "reactionsDot";
	public static final String REQ_WANT_REACTIONS_JSON = "reactionsJson";
	public static final String REQ_WANT_COMP_HIERARCHY_GRAPHML = "compHierarchyGraphml";
	public static final String REQ_WANT_COMP_HIERARCHY_DOT = "compHierarchyDot";
	public static final String REQ_WANT_COMP_HIERARCHY_JSON = "compHierarchyJson";
	
	public static final String REQ_WANT_SBML = "SBML";
	public static final String REQ_WANT_CELLML = "CellML";
	public static final String REQ_WANT_REGULAR = "regular";

	public static final String REQ_WANT_SINGLE_REACTIONS_GRAPHML = "singleReactionsGraphml";
	public static final String REQ_WANT_SINGLE_REACTIONS_DOT = "singleReactionsDot";
	public static final String REQ_WANT_SINGLE_REACTIONS_JSON = "singleReactionsJson";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML = "singleCompHierarchyGraphml";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_DOT = "singleCompHierarchyDot";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_JSON = "singleCompHierarchyJson";
	public static final String REQ_WANT_SINGLE_FLATTEN = "singleFlatten";
	

	private HashMap<String, Option> options;
	private HashMap<String, Option> addOptions;
	
	public HashMap<String, Option> getOptions ()
	{
		return options;
	}



	
	public HashMap<String, Option> getAddOptions ()
	{
		return addOptions;
	}




	public static class Option 
	{
		public String description;
		public int value;
		public Option (int value, String description)
		{
			this.description = description;
			this.value = value;
		}
	}
	
	public Executer ()
	{
		fillOptions ();
	}
	
	public Option get (String key)
	{
		Option o = options.get (key);
		if (o == null)
			o = addOptions.get (key);
		return o;
	}
	
	private void fillOptions ()
	{
		options = new HashMap<String, Option> ();
		options.put (REQ_WANT_DIFF, new Option (WANT_DIFF, "get the diff encoded in XML format"));
		options.put (REQ_WANT_REPORT_MD, new Option (WANT_REPORT_MD, "get the report of changes encoded in MarkDown"));
		options.put (REQ_WANT_REPORT_RST, new Option (WANT_REPORT_RST, "get the report of changes encoded in ReStructuredText"));
		options.put (REQ_WANT_REPORT_HTML, new Option (WANT_REPORT_HTML, "get the report of changes encoded in HTML"));
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

	
	public static String result (String s)
	{
		if (s == null)
			return "";
		return s;
	}
	
}
