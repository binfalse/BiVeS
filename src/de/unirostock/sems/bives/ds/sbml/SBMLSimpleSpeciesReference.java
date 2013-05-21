/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;


/**
 * @author Martin Scharm
 *
 */
public class SBMLSimpleSpeciesReference
extends SBMLSBase
implements SBMLDiffReporter
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

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLSimpleSpeciesReference a = (SBMLSimpleSpeciesReference) docA;
		SBMLSimpleSpeciesReference b = (SBMLSimpleSpeciesReference) docB;
		//if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
		//	return species.getNameAndId ();
		
		String retA = a.species.getNameAndId ();
		String retB = b.species.getNameAndId ();
		
		if (retA.equals (retB))
			return retA;
		else
			return "<span class='"+CLASS_DELETED+"'>" + retA + "</span> + <span class='"+CLASS_INSERTED +"'>" + retB + "</span>";
	}

	@Override
	public String reportInsert ()
	{
		return "<span class='"+CLASS_INSERTED+"'>" + species.getNameAndId () + "</span>";
	}

	@Override
	public String reportDelete ()
	{
		return "<span class='"+CLASS_DELETED+"'>" + species.getNameAndId () + "</span>";
	}
}
