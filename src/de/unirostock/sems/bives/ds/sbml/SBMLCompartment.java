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
public class SBMLCompartment
	extends SBMLSbase
{
	private String id;
	private String name; //optional
	private double spatialDimensions; //optional
	private double size; //optional
	private SBMLUnitDefinition units; //optional
	private boolean constant;

	public SBMLCompartment (DocumentNode documentNode, SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		id = documentNode.getAttribute ("id");
		if (id == null || id.length () < 1)
			throw new BivesSBMLParseException ("Compartment "+id+" doesn't provide a valid id.");
		
		name = documentNode.getAttribute ("name");
		
		if (documentNode.getAttribute ("spatialDimensions") != null)
		{
			try
			{
				spatialDimensions = Double.parseDouble (documentNode.getAttribute ("spatialDimensions"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("spatialDimensions in compartment "+id+" of unexpected format: " + documentNode.getAttribute ("spatialDimensions"));
			}
		}
		
		if (documentNode.getAttribute ("size") != null)
		{
			try
			{
				size = Double.parseDouble (documentNode.getAttribute ("size"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("size in compartment "+id+" of unexpected format: " + documentNode.getAttribute ("size"));
			}
		}
		
		if (documentNode.getAttribute ("units") != null)
		{
			String unitStr = documentNode.getAttribute ("units");
			units = sbmlModel.getUnitDefinition (unitStr);
			
			if (units == null)
				throw new BivesSBMLParseException ("Unit attribute in compartment "+id+" not defined: " + unitStr);
		}
		
		if (documentNode.getAttribute ("constant") != null)
		{
			try
			{
				constant = Boolean.parseBoolean (documentNode.getAttribute ("constant"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("constant attr in compartment "+id+" of unexpected format: " + documentNode.getAttribute ("constant"));
			}
		}
		else
			constant = true; // level <= 2
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
