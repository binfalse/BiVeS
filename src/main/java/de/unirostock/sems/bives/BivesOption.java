/**
 * 
 */
package de.unirostock.sems.bives;


/**
 * @author Martin Scharm
 *
 */
public class BivesOption
{
	
	/** The description. */
	public String description;
	
	/** The value. */
	public int value;
	
	/** Should that be shown in the help. */
	public boolean showInHelp;
	
	/**
	 * Instantiates a new option.
	 *
	 * @param value the value
	 * @param description the description
	 */
	public BivesOption (int value, String description)
	{
		this.description = description;
		this.value = value;
		this.showInHelp = true;
	}
	
	/**
	 * Instantiates a new option.
	 *
	 * @param value the value
	 * @param description the description
	 * @param showInHelp should this option be shown in help?
	 */
	public BivesOption (int value, String description, boolean showInHelp)
	{
		this.description = description;
		this.value = value;
		this.showInHelp = showInHelp;
	}
	
}
