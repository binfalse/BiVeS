/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

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
public class CellMLValidator
	extends ModelValidator
{
	private TreeDocument d;
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.ModelValidator#validate(de.unirostock.sems.xmldiff.ds.xml.TreeDocument)
	 */
	@Override
	public boolean validate (TreeDocument d) throws BivesDocumentParseException, XMLStreamException
	{
		this.d = d;
		// TODO: check name unique
		// TODO: check each has a name!
		return true;//validate (Tools.getSubDoc (d.getRoot ()));
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.ModelValidator#validate(java.lang.String)
	 */
	@Override
	public boolean validate (String d) throws XMLStreamException, BivesDocumentParseException
	{
		// TODO: not yet implemented
		return true;
	}


	public String getModelID ()
	{
		return d.getRoot ().getAttribute ("name");
	}
	
}
