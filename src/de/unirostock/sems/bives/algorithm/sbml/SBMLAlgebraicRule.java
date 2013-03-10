/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

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
public class SBMLAlgebraicRule
	extends SBMLRule
{
	private final static Logger LOGGER = Logger.getLogger(SBMLAlgebraicRule.class.getName());
	
	public SBMLAlgebraicRule (DocumentNode ruleA, DocumentNode ruleB)
	{
		super (ruleA, ruleB);
	}
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.ds.SBMLRule#getType()
	 */
	@Override
	public String getType (String cssclass)
	{
		return "Algebraic Rule";
	}
	
	
	
}
