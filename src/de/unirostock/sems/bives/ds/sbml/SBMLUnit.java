/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.markup.Markup;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLUnit
	extends SBMLSBase
	implements Markup
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
	public SBMLUnit (DocumentNode documentNode, SBMLModel sbmlModel) throws BivesSBMLParseException, BivesConsistencyException
	{
		super (documentNode, sbmlModel);
		
		String kindStr = documentNode.getAttribute ("kind");
		kind = sbmlModel.getUnitDefinition (kindStr);
		
		if (kind == null || !kind.isBaseUnit ())
			throw new BivesConsistencyException ("Unit kind attribute not defined or not base unit: " + kindStr);


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
	
	/*public String unitToHTMLString ()
	{
		return "(" + multiplier + "&middot;10^" + scale + "&middot;" + kind.name + ")^" + exponent;
	}*/

	@Override
	public String markup (MarkupDocument markupDocument)
	{
		String ret = multiplier == 1 ? "" : Tools.prettyDouble (multiplier, 1) + markupDocument.multiply ();
		ret += scale == 0 ? "" : "10^" + scale + markupDocument.multiply ();
		ret += "[" + kind.name + "]";
		ret += exponent == 1 ? "" : "^" + Tools.prettyDouble (exponent, 1);
		//ret += offset == 0 ? "" : "+offset";
		return "(" + ret + ")";
		//return "(" + multiplier + ""+markupDocument.multiply ()+"10^" + scale + ""+markupDocument.multiply ()+"" + kind.name + ")^" + exponent;
	}
}
