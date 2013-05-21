/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbmldeprecated;

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
public abstract class SBMLRule implements DiffReporter
{
	private final static Logger LOGGER = Logger.getLogger(SBMLRule.class.getName());
	
	private DocumentNode ruleA, ruleB;
	
	public abstract String getType (String cssclass);

	public SBMLRule (DocumentNode ruleA, DocumentNode ruleB)
	{
		this.ruleA = ruleA;
		this.ruleB = ruleB;
	}
	
	public String reportHTML (String cssclass)
	{
		return "<tr><td class='"+cssclass+"'>" + getType (cssclass) +
			"</td><td class='"+cssclass+"'>" + getRuleHtml (cssclass) +
				"</td></tr>";
	}
	
	public String getRuleHtml (String cssclass)
	{
			String ret = "";
				ret += Tools.genAttributeHtmlStats (ruleA, ruleB);
			ret += Tools.genMathHtmlStats (ruleA, ruleB);
			
			return ret;
	}


	public void setRuleB (DocumentNode rule)
	{
		ruleB = rule;
	}


	public void setRuleA (DocumentNode rule)
	{
		ruleA = rule;
	}
}
