/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.Main.ExecutionException;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.api.Single;
import de.unirostock.sems.bives.cellml.api.CellMLDiff;
import de.unirostock.sems.bives.cellml.api.CellMLSingle;
import de.unirostock.sems.bives.cellml.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.cellml.parser.CellMLDocument;
import de.unirostock.sems.bives.exception.BivesImportException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.BivesDocumentConsistencyException;
import de.unirostock.sems.bives.sbml.api.SBMLDiff;
import de.unirostock.sems.bives.sbml.api.SBMLSingle;
import de.unirostock.sems.bives.sbml.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.sbml.parser.SBMLDocument;
import de.unirostock.sems.bives.tools.DocumentClassifier;
import de.unirostock.sems.xmltools.ds.TreeDocument;
import de.unirostock.sems.xmltools.exception.XmlDocumentParseException;


/**
 * @author martin
 *
 */
public class Executer
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
	
	public static final String REQ_WANT_SBML = "SBML";
	public static final String REQ_WANT_CELLML = "CellML";
	public static final String REQ_WANT_REGULAR = "regular";

	public static final String REQ_WANT_SINGLE_CRN_GRAPHML = "singleCrnGraphml";
	public static final String REQ_WANT_SINGLE_CRN_DOT = "singleCrnDot";
	public static final String REQ_WANT_SINGLE_CRN_JSON = "singleCrnJson";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML = "singleCompHierarchyGraphml";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_DOT = "singleCompHierarchyDot";
	public static final String REQ_WANT_SINGLE_COMP_HIERARCHY_JSON = "singleCompHierarchyJson";
	

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
		options.put (REQ_WANT_CRN_GRAPHML, new Option (WANT_CRN_GRAPHML, "get the highlighted chemical reaction network encoded in GraphML"));
		options.put (REQ_WANT_CRN_DOT, new Option (WANT_CRN_DOT, "get the highlighted chemical reaction network encoded in DOT language"));
		options.put (REQ_WANT_CRN_JSON, new Option (WANT_CRN_JSON, "get the highlighted chemical reaction network encoded in JSON"));
		options.put (REQ_WANT_COMP_HIERARCHY_GRAPHML, new Option (WANT_COMP_HIERARCHY_GRAPHML, "get the hierarchy of components in a CellML document encoded in GraphML"));
		options.put (REQ_WANT_COMP_HIERARCHY_DOT, new Option (WANT_COMP_HIERARCHY_DOT, "get the hierarchy of components in a CellML document encoded in DOT language"));
		options.put (REQ_WANT_COMP_HIERARCHY_JSON, new Option (WANT_COMP_HIERARCHY_JSON, "get the hierarchy of components in a CellML document encoded in JSON"));
		options.put (REQ_WANT_SBML, new Option (WANT_SBML, "force SBML comparison"));
		options.put (REQ_WANT_CELLML, new Option (WANT_CELLML, "force CellML comparison"));
		options.put (REQ_WANT_REGULAR, new Option (WANT_REGULAR, "force regular XML comparison"));
		
		addOptions = new HashMap<String, Option> ();
		addOptions.put (REQ_WANT_DOCUMENTTYPE, new Option (WANT_DOCUMENTTYPE, "get the documentType of an XML file"));
		addOptions.put (REQ_WANT_META, new Option (WANT_META, "get some meta information about an XML file"));
		addOptions.put (REQ_WANT_SINGLE_CRN_JSON, new Option (WANT_SINGLE_CRN_JSON, "get the chemical reaction network of a single file encoded in JSON"));
		addOptions.put (REQ_WANT_SINGLE_CRN_GRAPHML, new Option (WANT_SINGLE_CRN_GRAPHML, "get the chemical reaction network of a single file encoded in GraphML"));
		addOptions.put (REQ_WANT_SINGLE_CRN_DOT, new Option (WANT_SINGLE_CRN_DOT, "get the chemical reaction network of a single file encoded in DOT language"));
		addOptions.put (REQ_WANT_SINGLE_COMP_HIERARCHY_JSON, new Option (WANT_SINGLE_COMP_HIERARCHY_JSON, "get the hierarchy of components in a single CellML document encoded in JSON"));
		addOptions.put (REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML, new Option (WANT_SINGLE_COMP_HIERARCHY_GRAPHML, "get the hierarchy of components in a single CellML document encoded in GraphML"));
		addOptions.put (REQ_WANT_SINGLE_COMP_HIERARCHY_DOT, new Option (WANT_SINGLE_COMP_HIERARCHY_DOT, "get the hierarchy of components in a single CellML document encoded in DOT language"));
	}
	
	public void executeSingle (File file1, HashMap<String, String> toReturn, int want) throws ExecutionException, ParserConfigurationException, FileNotFoundException, SAXException, IOException, BivesCellMLParseException, BivesLogicalException, URISyntaxException, BivesImportException, XmlDocumentParseException, BivesDocumentConsistencyException, BivesSBMLParseException
	{
  	DocumentClassifier classifier = null;

  	if ((Executer.WANT_META & want) > 0)
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
			toReturn.put (Executer.REQ_WANT_META, ret);
  	}
  	if ((Executer.WANT_DOCUMENTTYPE & want) > 0)
  	{
  		// doc type
  		classifier = new DocumentClassifier ();
  		int type = classifier.classify (file1);
			
			toReturn.put (Executer.REQ_WANT_DOCUMENTTYPE, DocumentClassifier.humanReadable (type));
  	}
  	
  	if ((Executer.WANT_SINGLE_COMP_HIERARCHY_DOT|Executer.WANT_SINGLE_COMP_HIERARCHY_JSON|Executer.WANT_SINGLE_COMP_HIERARCHY_GRAPHML|Executer.WANT_SINGLE_CRN_JSON|Executer.WANT_SINGLE_CRN_GRAPHML|Executer.WANT_SINGLE_CRN_DOT & want) > 0)
  	{
  		Single single = null;
    	classifier = new DocumentClassifier ();
    	int type = classifier.classify (file1);
    	
    	if ((type & DocumentClassifier.SBML) != 0)
    	{
    		single = new SBMLSingle (file1);
    	}
    	else if ((type & DocumentClassifier.CELLML) != 0)
    	{
    		single = new CellMLSingle (file1);
    	}
    	if (single == null)
    		throw new ExecutionException ("cannot produce the requested output for the provided file.");
  		if ((want & Executer.WANT_SINGLE_CRN_JSON) > 0)
  			toReturn.put (Executer.REQ_WANT_SINGLE_CRN_JSON, result (single.getCRNJsonGraph ()));
  		if ((want & Executer.WANT_SINGLE_CRN_GRAPHML) > 0)
  			toReturn.put (Executer.REQ_WANT_SINGLE_CRN_GRAPHML, result (single.getCRNGraphML ()));
  		if ((want & Executer.WANT_SINGLE_CRN_DOT) > 0)
  			toReturn.put (Executer.REQ_WANT_SINGLE_CRN_DOT, result (single.getCRNDotGraph ()));
  		if ((want & Executer.WANT_SINGLE_COMP_HIERARCHY_JSON) > 0)
  			toReturn.put (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_JSON, result (single.getHierarchyJsonGraph ()));
  		if ((want & Executer.WANT_SINGLE_COMP_HIERARCHY_GRAPHML) > 0)
  			toReturn.put (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_GRAPHML, result (single.getHierarchyGraphML ()));
  		if ((want & Executer.WANT_SINGLE_COMP_HIERARCHY_DOT) > 0)
  			toReturn.put (Executer.REQ_WANT_SINGLE_COMP_HIERARCHY_DOT, result (single.getHierarchyDotGraph ()));
  	}
	}
	
	
	public void executeCompare (File file1, File file2, HashMap<String, String> toReturn, int want) throws Exception
	{
  	// compare mode
		Diff diff = null;
  	DocumentClassifier classifier = null;
		
    if (!file2.exists ())
    	throw new ExecutionException ("cannot find " + file2.getAbsolutePath ());
    if (!file2.canRead ())
    	throw new ExecutionException ("cannot read " + file2.getAbsolutePath ());
    
    if (want == 0)
    	want = Executer.WANT_DIFF;
    
    // decide which kind of mapper to use
    if ((Executer.WANT_CELLML & want) > 0)
    	diff = new CellMLDiff (file1, file2);
    else if ((Executer.WANT_SBML & want) > 0)
    	diff = new SBMLDiff (file1, file2);
    else if ((Executer.WANT_REGULAR & want) > 0)
    	diff = new RegularDiff (file1, file2);
    else
    {
    	classifier = new DocumentClassifier ();
    	int type1 = classifier.classify (file1);
    	int type2 = classifier.classify (file2);
    	int type = type1 & type2;
    	if ((type & DocumentClassifier.SBML) != 0)
    	{
    		diff = new SBMLDiff (file1, file2);
    	}
    	else if ((type & DocumentClassifier.CELLML) != 0)
    	{
    		diff = new CellMLDiff (file1, file2);
    	}
    	else if ((type & DocumentClassifier.XML) != 0)
    	{
    		diff = new RegularDiff (file1, file2);
    	}
    	else
    		throw new ExecutionException ("cannot compare these files (["+DocumentClassifier.humanReadable (type1) + "] ["+DocumentClassifier.humanReadable (type2)+"])");
    }

  	//System.out.println (want);
    
    // create mapping
    diff.mapTrees ();
    
    
    // compute results
		if ((want & Executer.WANT_DIFF) > 0)
			toReturn.put (Executer.REQ_WANT_DIFF, result (diff.getDiff ()));
		
		if ((want & Executer.WANT_CRN_GRAPHML) > 0)
			toReturn.put (Executer.REQ_WANT_CRN_GRAPHML, result (diff.getCRNGraphML ()));
		
		if ((want & Executer.WANT_CRN_DOT) > 0)
			toReturn.put (Executer.REQ_WANT_CRN_DOT, result (diff.getCRNDotGraph ()));
		
		if ((want & Executer.WANT_CRN_JSON) > 0)
			toReturn.put (Executer.REQ_WANT_CRN_JSON, result (diff.getCRNJsonGraph ()));
		
		if ((want & Executer.WANT_COMP_HIERARCHY_DOT) > 0)
			toReturn.put (Executer.REQ_WANT_COMP_HIERARCHY_DOT, result (diff.getHierarchyDotGraph ()));
		
		if ((want & Executer.WANT_COMP_HIERARCHY_JSON) > 0)
			toReturn.put (Executer.REQ_WANT_COMP_HIERARCHY_JSON, result (diff.getHierarchyJsonGraph ()));
		
		if ((want & Executer.WANT_COMP_HIERARCHY_GRAPHML) > 0)
			toReturn.put (Executer.REQ_WANT_COMP_HIERARCHY_GRAPHML, result (diff.getHierarchyGraphML ()));
		
		if ((want & Executer.WANT_REPORT_HTML) > 0)
			toReturn.put (Executer.REQ_WANT_REPORT_HTML, result (diff.getHTMLReport ()));
		
		if ((want & Executer.WANT_REPORT_MD) > 0)
			toReturn.put (Executer.REQ_WANT_REPORT_MD, result (diff.getMarkDownReport ()));
		
		if ((want & Executer.WANT_REPORT_RST) > 0)
			toReturn.put (Executer.REQ_WANT_REPORT_RST, result (diff.getReStructuredTextReport ()));
	}

	
	public static String result (String s)
	{
		if (s == null)
			return "";
		return s;
	}
	
}
