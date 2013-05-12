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
public class SBMLCompartmentType
	extends SBMLSbase
{
	private String id;
	private String name; //optional
	
	/**
	 * @param documentNode
	 * @param sbmlModel
	 * @throws BivesSBMLParseException
	 */
	public SBMLCompartmentType (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		id = documentNode.getAttribute ("id");
		if (id == null || id.length () < 1)
			throw new BivesSBMLParseException ("CompartmentType "+id+" doesn't provide a valid id.");
		
		name = documentNode.getAttribute ("name");
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
