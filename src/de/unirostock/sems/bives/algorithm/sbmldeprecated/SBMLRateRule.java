/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbmldeprecated;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLRateRule
	extends SBMLRule
{
	private final static Logger LOGGER = Logger.getLogger(SBMLRateRule.class.getName());
	private String varA, varB;
	
	public SBMLRateRule (String varA, String varB, DocumentNode ruleA, DocumentNode ruleB)
	{
		super (ruleA, ruleB);
		this.varA = varA;
		this.varB = varB;
	}
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.ds.SBMLRule#getType()
	 */
	@Override
	public String getType (String cssclass)
	{
		if (varA == null)
			return "Rate Rule for <span class='inserted "+cssclass+"'>" + varB +"</span>";
		if (varB == null)
			return "Rate Rule for <span class='deleted "+cssclass+"'>" + varA +"</span>";
		if (varA.equals (varB))
			return "Rate Rule for " + varA;
		return "Rate Rule initial for <span class='deleted "+cssclass+"'>" + varA + "</span> now for <span class='inserted "+cssclass+"'>" + varB +"</span>";
	}
	
	
	
	public void setVariableA (String var)
	{
		varA = var;
	}
	
	public void setVariableB (String var)
	{
		varB = var;
	}
	
}
