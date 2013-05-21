/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLSpecies
	extends SBMLGenericIdNameObject
	implements SBMLDiffReporter
{
	private SBMLCompartment compartment;
	private Double initialAmount; //optional
	private Double initialConcentration; //optional
	private	SBMLUnitDefinition substanceUnits; //optional
	private boolean hasOnlySubstanceUnits;
	private boolean boundaryCondition;
	private boolean constant;
	private Integer charge; //optional
	private	SBMLParameter conversionFactor; //optional
	private SBMLSpeciesType speciesType; //optional
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLSpecies (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		id = documentNode.getAttribute ("id");
		if (id == null || id.length () < 1)
			throw new BivesSBMLParseException ("species "+id+" doesn't provide a valid id.");
		
		name = documentNode.getAttribute ("name");
		
		String tmp = documentNode.getAttribute ("compartment");
		compartment = sbmlModel.getCompartment (tmp);
		if (compartment == null)
			throw new BivesSBMLParseException ("no valid compartment for species "+id+" defined: " + tmp);
		
		initialAmount = null;
		initialConcentration = null;

		tmp = documentNode.getAttribute ("speciesType");
		if (tmp != null)
		{
			speciesType = sbmlModel.getSpeciesType (tmp);
			if (speciesType == null)
				throw new BivesSBMLParseException ("no valid speciesType for species "+id+" defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("charge") != null)
		{
			try
			{
				charge = Integer.parseInt (documentNode.getAttribute ("charge"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("charge of species "+id+" of unexpected format: " + documentNode.getAttribute ("charge"));
			}
		}
		
		if (documentNode.getAttribute ("initialAmount") != null)
		{
			try
			{
				initialAmount = Double.parseDouble (documentNode.getAttribute ("initialAmount"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("initialAmount of species "+id+" of unexpected format: " + documentNode.getAttribute ("initialAmount"));
			}
		}
		
		if (documentNode.getAttribute ("initialConcentration") != null)
		{
			try
			{
				initialConcentration = Double.parseDouble (documentNode.getAttribute ("initialConcentration"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("initialConcentration of species "+id+" of unexpected format: " + documentNode.getAttribute ("initialConcentration"));
			}
		}
		
		if (initialAmount != null && initialConcentration != null)
			throw new BivesSBMLParseException ("initialAmount AND initialConcentration of species "+id+" defined. ");
		
		if (documentNode.getAttribute ("substanceUnits") != null)
		{
			tmp = documentNode.getAttribute ("substanceUnits");
			substanceUnits = sbmlModel.getUnitDefinition (tmp);
			if (substanceUnits == null)
				throw new BivesSBMLParseException ("substanceUnits attribute in species "+id+" not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("hasOnlySubstanceUnits") != null)
		{
			try
			{
				hasOnlySubstanceUnits = Boolean.parseBoolean (documentNode.getAttribute ("hasOnlySubstanceUnits"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("hasOnlySubstanceUnits of species "+id+" of unexpected format: " + documentNode.getAttribute ("hasOnlySubstanceUnits"));
			}
		}
		else
			hasOnlySubstanceUnits = false; // level <= 2

		
		if (documentNode.getAttribute ("boundaryCondition") != null)
		{
			try
			{
				boundaryCondition = Boolean.parseBoolean (documentNode.getAttribute ("boundaryCondition"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("boundaryCondition of species "+id+" of unexpected format: " + documentNode.getAttribute ("boundaryCondition"));
			}
		}
		else
			boundaryCondition = false; // level <= 2

		
		if (documentNode.getAttribute ("constant") != null)
		{
			try
			{
				constant = Boolean.parseBoolean (documentNode.getAttribute ("constant"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("constant attr of species "+id+" of unexpected format: " + documentNode.getAttribute ("constant"));
			}
		}
		else
			constant = false; // level <= 2

		
		if (documentNode.getAttribute ("conversionFactor") != null)
		{
			tmp = documentNode.getAttribute ("conversionFactor");
			conversionFactor = sbmlModel.getParameter (tmp);
			if (conversionFactor == null)
				throw new BivesSBMLParseException ("conversionFactor attribute in species "+id+" not defined: " + tmp);
			if (!conversionFactor.isConstant ())
				throw new BivesSBMLParseException ("conversionFactor attribute in species "+id+" is not constant: " + tmp);
		}
	}
	
	/**
	 * Returns the initial amount of this species or null if not defined.
	 *
	 * @return the initial amount
	 */
	public double getInitialAmount ()
	{
		return initialAmount;
	}
	
	/**
	 * Returns the initial concentration of this species or null if not defined.
	 *
	 * @return the initial concentration
	 */
	public double getInitialConcentration ()
	{
		return initialConcentration;
	}
	
	public boolean canHaveAssignmentRule ()
	{
		return !constant;
	}
	
	public boolean canBeReactantOrProduct ()
	{
		return boundaryCondition || (!boundaryCondition && !constant);
	}
	
	public boolean isConstant ()
	{
		return constant;
	}
	
	public boolean hasBoundaryCondition ()
	{
		return boundaryCondition;
	}
	
	public boolean hasOnlySubstanceUnits ()
	{
		return hasOnlySubstanceUnits;
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLSpecies a = (SBMLSpecies) docA;
		SBMLSpecies b = (SBMLSpecies) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";
		
		String idA = a.getNameAndId (), idB = b.getNameAndId ();
		String ret = "<tr><td>";
		if (idA.equals (idB))
			ret += idA;
		else
			ret += "<span class='"+CLASS_DELETED+"'>" + idA + "</span> &rarr; <span class='"+CLASS_INSERTED +"'>" + idB + "</span> ";
		ret += "</td><td>";
		
		ret += Tools.genAttributeHtmlStats (a.documentNode, b.documentNode);
		
		return ret + "</td></tr>";
	}

	@Override
	public String reportInsert ()
	{
		return "<tr><td><span class='"+CLASS_INSERTED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_INSERTED+"'>inserted</span></td></tr>";
	}

	@Override
	public String reportDelete ()
	{
		return "<tr><td><span class='"+CLASS_DELETED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_DELETED+"'>deleted</span></td></tr>";
	}
}
