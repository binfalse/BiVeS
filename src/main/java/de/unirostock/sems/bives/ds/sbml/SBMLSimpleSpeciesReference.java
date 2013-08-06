/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.SBOTerm;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.markup.MarkupDocument;


/**
 * @author Martin Scharm
 *
 */
public class SBMLSimpleSpeciesReference
extends SBMLSBase
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

	public String reportMofification (ClearConnectionManager conMgmt, SBMLSimpleSpeciesReference a, SBMLSimpleSpeciesReference b, MarkupDocument markupDocument)
	{
		/*SBMLSimpleSpeciesReference a = (SBMLSimpleSpeciesReference) docA;
		SBMLSimpleSpeciesReference b = (SBMLSimpleSpeciesReference) docB;*/
		//if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
		//	return species.getNameAndId ();
		
		SBOTerm sboA = a.getSBOTerm (), sboB = b.getSBOTerm ();
		String retA = a.species.getID () + (sboA == null? "("+SBOTerm.resolvModifier (SBOTerm.MOD_UNKNOWN)+")" : "("+sboA.resolvModifier ()+")");
		String retB = b.species.getID () + (sboB == null? "("+SBOTerm.resolvModifier (SBOTerm.MOD_UNKNOWN)+")" : "("+sboB.resolvModifier ()+")");
		
		if (retA.equals (retB))
			return retA;
		else
			return markupDocument.delete (retA) + " + " + markupDocument.insert (retB);
	}

	public String reportInsert (MarkupDocument markupDocument)
	{
		SBOTerm sbo = getSBOTerm ();
		return markupDocument.insert (species.getID () + (sbo == null? "("+SBOTerm.resolvModifier (SBOTerm.MOD_UNKNOWN)+")" : "("+sbo.resolvModifier ()+")"));
	}

	public String reportDelete (MarkupDocument markupDocument)
	{
		SBOTerm sbo = getSBOTerm ();
		return markupDocument.delete (species.getID () + (sbo == null? "("+SBOTerm.resolvModifier (SBOTerm.MOD_UNKNOWN)+")" : "("+sbo.resolvModifier ()+")"));
	}
}
