/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;


/**
 * @author Martin Scharm
 *
 */
public class SBMLSimpleSpeciesReference
extends SBMLSbase
{
	protected String id; //optional
	protected String name; //optional
	protected SBMLSpecies species;
	
	public SBMLSimpleSpeciesReference (DocumentNode documentNode,
		SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);

		id = documentNode.getAttribute ("id");
		name = documentNode.getAttribute ("name");
		
		
		String tmp = documentNode.getAttribute ("species");
		species = sbmlModel.getSpecies (tmp);
		if (species == null)
			throw new BivesSBMLParseException ("species reference "+tmp+" is not a valid species.");
		
	}
	
	public SBMLSpecies getSpecies ()
	{
		return species;
	}
}
