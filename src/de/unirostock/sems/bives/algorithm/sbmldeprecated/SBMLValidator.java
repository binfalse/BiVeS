/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbmldeprecated;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import de.unirostock.sems.bives.algorithm.ModelValidator;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.tools.Tools;


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
	public boolean validate (TreeDocument d) throws BivesDocumentParseException, XMLStreamException
	{
		return validate (Tools.printSubDoc (d.getRoot ()));
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.ModelValidator#validate(java.lang.String)
	 */
	@Override
	public boolean validate (String d) throws XMLStreamException, BivesDocumentParseException
	{
		doc = SBMLReader.read (d);
		if (doc.checkConsistency () > 0)
			throw new BivesDocumentParseException ("not a valid SBML file");
		return true;
	}


	public String getModelID ()
	{
		return doc.getModel ().getName ();
	}
	
}
