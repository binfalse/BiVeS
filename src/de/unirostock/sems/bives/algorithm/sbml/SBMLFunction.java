/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.unirostock.sems.bives.algorithm.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public abstract class SBMLFunction implements DiffReporter
{
	private final static Logger LOGGER = Logger.getLogger(SBMLFunction.class.getName());
	
	private DocumentNode functionA, functionB;
	private String idA, idB;
	private String nameA, nameB;
	
	public abstract String getType ();

	public SBMLFunction (DocumentNode functionA, String idA, String nameA, DocumentNode functionB, String idB, String nameB)
	{
		this.functionA = functionA;
		this.functionB = functionB;
		this.idA = idA;
		this.idB = idB;
		this.nameA = nameA;
		this.nameB = nameB;
	}
	
	public String reportHTML (String cssclass)
	{
		return "<tr><td>" + Tools.genTableIdCol (functionA, functionB) +
			"</td><td class='"+cssclass+"'>" + getRuleHtml () +
				"</td></tr>";
	}
	
	public String getRuleHtml ()
	{
			String ret = "";
			ret += Tools.genAttributeHtmlStats (functionA, functionB);
			ret += Tools.genMathHtmlStats (functionA, functionB);
			
			return ret;
	}


	public void setIdB (String id)
	{
		idB = id;
	}


	public void setIdA (String id)
	{
		idA = id;
	}


	public void setNameB (String name)
	{
		nameB = name;
	}


	public void setNameA (String name)
	{
		nameA = name;
	}


	public void setFunctionB (DocumentNode function)
	{
		functionB = function;
	}


	public void setFunctionA (DocumentNode function)
	{
		functionA = function;
	}
}
