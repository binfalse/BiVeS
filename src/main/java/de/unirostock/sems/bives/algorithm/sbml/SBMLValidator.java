/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.algorithm.ModelValidator;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.tools.Tools;
import de.unirostock.sems.bives.tools.TreeTools;
import de.unirostock.sems.bives.tools.XmlTools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLValidator
	extends ModelValidator
{
	private SBMLDocument doc;
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.ModelValidator#validate(de.unirostock.sems.xmldiff.ds.xml.TreeDocument)
	 */
	@Override
	public boolean validate (TreeDocument d) throws XMLStreamException, ParserConfigurationException, SAXException, IOException
	{
		return validate (TreeTools.printSubDoc (d.getRoot ()));
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.ModelValidator#validate(java.lang.String)
	 */
	@Override
	public boolean validate (String d) throws XMLStreamException, ParserConfigurationException, SAXException, IOException
	{
		/*DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		TreeDocument td = new TreeDocument (builder.parse (new ByteArrayInputStream(d.getBytes ())), new XyWeighter ());
		doc = new SBMLDocument (td);
		/*if (doc.checkConsistency () > 0)
			throw new BivesDocumentParseException ("not a valid SBML file");*/
		return true;
	}


	public String getModelID ()
	{
		return doc.getModel ().getName ();
	}
	
}
