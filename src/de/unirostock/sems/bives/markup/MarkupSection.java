/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.Vector;


/**
 * @author Martin Scharm
 *
 */
public class MarkupSection
{
	private String header;
	private Vector<MarkupElement> values;
	
	public MarkupSection (String header)
	{
		this.header = header;
		values = new Vector<MarkupElement> ();
	}
	
	public void addValue (MarkupElement element)
	{
		values.add (element);
	}
	
}
