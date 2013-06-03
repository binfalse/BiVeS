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
	private Vector<MarkupElement> subElements;
	
	public MarkupElement (String header)
	{
		this.header = header;
		values = new Vector<String> ();
		subElements = new Vector<MarkupElement> ();
	}
	
	public void addValue (String value)
	{
		if (value != null)
		values.add (value);
	}
	
	public void addSubElements (MarkupElement element)
	{
		if (element != null)
		subElements.add (element);
	}
	
	public String getHeader ()
	{
		return header;
	}
	public Vector<String> getValues ()
	{
		return values;
	}
	
	public Vector<MarkupElement> getSubElements ()
	{
		return subElements;
	}
}
