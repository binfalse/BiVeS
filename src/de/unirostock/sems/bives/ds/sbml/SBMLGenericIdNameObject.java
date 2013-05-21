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
public abstract class SBMLGenericIdNameObject
extends SBMLSBase
{
	protected String id;
	protected String name; // optional
	
	public SBMLGenericIdNameObject (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
	}
	
	/**
	 * Gets the name (if defined) or the id (if name undefined).
	 *
	 * @return the name or id
	 */
	public final String getNameOrId ()
	{
		if (name == null)
			return id;
		return name;
	}
	
	/**
	 * Gets the name (if defined) and the id as <code>NAME (ID)</code>
	 *
	 * @return the name and id
	 */
	public final String getNameAndId ()
	{
		if (name == null)
			return id;
		return id + " (" + name + ")";
	}
	
	public final String getID ()
	{
		return id;
	}
	
	public final String getName ()
	{
		return name;
	}
}
