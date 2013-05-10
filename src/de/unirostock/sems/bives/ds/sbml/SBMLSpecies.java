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
public class SBMLSpecies
	extends SBMLSbase
{
	private String id;
	private String name; //optional
	private SBMLCompartment compartment;
	private Double initialAmount; //optional
	private Double initialConcentration; //optional
	private	SBMLUnitDefinition substanceUnits; //optional
	private boolean hasOnlySubstanceUnits;
	private boolean boundaryCondition;
	private boolean constant;
	private	SBMLParameter conversionFactor; //optional
	
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
	
	public String getID ()
	{
		return id;
	}
	
	public String getName ()
	{
		return name;
	}
}
