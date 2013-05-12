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
public class SBMLUnit
	extends SBMLSbase
{
	private SBMLUnitDefinition kind;
	private double exponent;
	private int scale;
	private double multiplier;

	
	/**
	 * Instantiates a new SBML unit derived from a base unit.
	 *
	 * @param documentNode the DocumentNode defining this unit 
	 * @throws BivesSBMLParseException 
	 */
	public SBMLUnit (DocumentNode documentNode, SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		String kindStr = documentNode.getAttribute ("kind");
		kind = sbmlModel.getUnitDefinition (kindStr);
		
		if (kind == null || !kind.isBaseUnit ())
			throw new BivesSBMLParseException ("Unit kind attribute not defined or not base unit: " + kindStr);


		if (documentNode.getAttribute ("multiplier") != null)
		{
			try
			{
				multiplier = Double.parseDouble (documentNode.getAttribute ("multiplier"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("multiplier of unexpected format: " + documentNode.getAttribute ("multiplier"));
			}
		}
		else
			multiplier = 1; // level <= 2

		if (documentNode.getAttribute ("scale") != null)
		{
			try
			{
				scale = Integer.parseInt (documentNode.getAttribute ("scale"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("scale of unexpected format: " + documentNode.getAttribute ("scale"));
			}
		}
		else
			scale = 0; // level <= 2
		
		if (documentNode.getAttribute ("exponent") != null)
		{
			try
			{
				exponent = Double.parseDouble (documentNode.getAttribute ("exponent"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("exponent of unexpected format: " + documentNode.getAttribute ("exponent"));
			}
		}
		else
			exponent = 1; // level <= 2
	}
}
