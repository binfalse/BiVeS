/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.Vector;


/**
 * @author Martin Scharm
 *
 */
public class MarkupDocument
{
	private Vector<MarkupSection> sections;
	private Vector<String> header;
	
	public MarkupDocument ()
	{
		sections = new Vector<MarkupSection> ();
		header = new Vector<String> ();
	}
	
	public void addHeader (String header)
	{
		this.header.add (header);
	}
	
	public void addSection (MarkupSection section)
	{
		sections.add (section);
	}
	
	public String highlight (String s)
	{
		return "[{highlight}]" + s + "}]highlight[{";
	}
	
	public String insert (String s)
	{
		return "[{insert}]" + s + "}]insert[{";
	}
	
	public String delete (String s)
	{
		return "[{delete}]" + s + "}]delete[{";
	}
	
	public String attribute (String s)
	{
		return "[{attribute}]" + s + "}]attribute[{";
	}
}
