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
public class SBMLParameter
	extends SBMLSbase
{
	private String id;
	private String name; //optional
	private Double value; //optional
	private SBMLUnitDefinition units; //optional
	private boolean constant; //optional
	
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLParameter (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		id = documentNode.getAttribute ("id");
		if (id == null || id.length () < 1)
			throw new BivesSBMLParseException ("parameter "+id+" doesn't provide a valid id.");
		
		name = documentNode.getAttribute ("name");
		

		if (documentNode.getAttribute ("value") != null)
		{
			try
			{
				value = Double.parseDouble (documentNode.getAttribute ("value"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("value of species "+id+" of unexpected format: " + documentNode.getAttribute ("value"));
			}
		}
		
		if (documentNode.getAttribute ("units") != null)
		{
			String tmp = documentNode.getAttribute ("units");
			units = sbmlModel.getUnitDefinition (tmp);
			if (units == null)
				throw new BivesSBMLParseException ("units attribute in species "+id+" not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("constant") != null)
		{
			try
			{
				constant = Boolean.parseBoolean (documentNode.getAttribute ("constant"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("constant of parameter "+id+" of unexpected format: " + documentNode.getAttribute ("constant"));
			}
		}
		else
			constant = false; // level <= 2
		
	}

	public double getValue ()
	{
		return value;
	}
	
	public String getID ()
	{
		return id;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public boolean isConstant ()
	{
		return constant;
	}
}
