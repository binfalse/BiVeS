/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.Vector;


/**
 * @author Martin Scharm
 *
 */
public class MarkupElement
{
	private String header;
	private Vector<String> values;
	
	public MarkupElement (String header)
	{
		this.header = header;
		values = new Vector<String> ();
	}
	
	public void addValue (String value)
	{
		values.add (value);
	}
}
