/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.Vector;
import java.util.regex.Pattern;


/**
 * @author Martin Scharm
 *
 */
public class MarkupDocument
{
	private String headline;
	private Vector<MarkupSection> sections;
	private Vector<String> header;
	
	public MarkupDocument (String headline)
	{
		this.headline = headline;
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
		return "{{highlight}}" + s + "}}highlight{{";
	}
	
	public String insert (String s)
	{
		return "{{insert}}" + s + "}}insert{{";
	}
	
	public String delete (String s)
	{
		return "{{delete}}" + s + "}}delete{{";
	}
	
	public String attribute (String s)
	{
		return "{{attribute}}" + s + "}}attribute{{";
	}
	
	public String rightArrow ()
	{
		return "{{rightArrow}}";
	}
	
	public Vector<String> getHeader ()
	{
		return header;
	}
	
	public Vector<MarkupSection> getSections ()
	{
		return sections;
	}
	
	public String getHeadline ()
	{
		return headline;
	}
	
	public static final String replaceHighlights (String s, String pre, String post)
	{
		return replace (s, "{{highlight}}", "}}highlight{{", pre, post);
	}
	
	public static final String replaceInserts (String s, String pre, String post)
	{
		return replace (s, "{{insert}}", "}}insert{{", pre, post);
	}
	
	public static final String replaceDeletes (String s, String pre, String post)
	{
		return replace (s, "{{delete}}", "}}delete{{", pre, post);
	}
	
	public static final String replaceAttributes (String s, String pre, String post)
	{
		return replace (s, "{{attribute}}", "}}attribute{{", pre, post);
	}
	
	public static final String replaceRightArrow (String s, String replacement)
	{
		if (replacement == null)
			replacement = "";
		String ret = s.replaceAll (Pattern.quote ("{{rightArrow}}"), replacement);
		return ret;
	}
	
	private static final String replace (String s, String pre, String post, String rpre, String rpost)
	{
		if (rpre == null)
			rpre = "";
		if (rpost == null)
			rpost = "";
		String ret = s.replaceAll (Pattern.quote (pre), rpre);
		ret = ret.replaceAll (Pattern.quote (post), rpost);
		return ret;
		
	}
}
