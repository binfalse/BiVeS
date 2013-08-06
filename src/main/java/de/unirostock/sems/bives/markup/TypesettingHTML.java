/**
 * 
 */
package de.unirostock.sems.bives.markup;

import java.util.Vector;

import de.binfalse.bflog.LOGGER;


/**
 * @author Martin Scharm
 *
 */
public class TypesettingHTML
	extends Typesetting
{
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.markup.Markup#markup(de.unirostock.sems.bives.markup.MarkupDocument)
	 */
	@Override
	public String markup (MarkupDocument doc)
	{
		//LOGGER.debug ("typesetting doc " + doc.getHeadline ());
		String s = "<h1>" + doc.getHeadline () + "</h1>";
		String sub = "";
		
		Vector<String> headers = doc.getHeader ();
		for (String head : headers)
			sub += "<li>" + head + "</li>";
		if (sub.length () > 0)
			s += "<ul>" + sub + "</ul>";
		
		Vector<MarkupSection> sections = doc.getSections ();
		//LOGGER.debug ("num sections: " + sections.size ());
		for (MarkupSection sec : sections)
			s += markupSection (sec);
		
		s = MarkupDocument.replaceHighlights (s, "<strong>", "</strong>");
		s = MarkupDocument.replaceInserts (s, "<span class='insert'>", "</span>");
		s = MarkupDocument.replaceDeletes (s, "<span class='delete'>", "</span>");
		s = MarkupDocument.replaceAttributes (s, "<span class='attr'>", "</span>");
		s = MarkupDocument.replaceRightArrow (s, "&rarr;");
		s = MarkupDocument.replaceMultiplication (s, "&middot;");
		
		return s;
	}
	
	private String markupSection (MarkupSection section)
	{
		//LOGGER.debug ("typesetting section " + section.getHeader ());
		String s = "<h2>" + section.getHeader () + "</h2>";
		String sub = "";
		
		Vector<MarkupElement> elements = section.getValues ();
		for (MarkupElement e : elements)
			sub += markupElement (e);
		
		if (sub.length () > 0)
			return s + "<table>" + sub + "</table>";
		return "";
	}
	
	private String markupElement (MarkupElement element)
	{
		String s = "<tr><td>" + element.getHeader () + "</td><td>";
		String sub = "";
		
		Vector<String> values = element.getValues ();
		for (String v : values)
			sub += "<li>" + v + "</li>";
		
		Vector<MarkupElement> subElements = element.getSubElements ();
		for (MarkupElement e : subElements)
			sub += "<li>" + markupSubElement (e) + "</li>";
		
		if (sub.length () > 0)
			s += "<ul>" + sub + "</ul>";
		
		return s + "</td></tr>";
	}
	
	private String markupSubElement (MarkupElement element)
	{
		String s = "<strong>" + element.getHeader () + "</strong>";
		String sub = "";
		
		Vector<String> values = element.getValues ();
		for (String v : values)
			sub += "<li>" + v + "</li>";
		
		if (sub.length () > 0)
			s += "<ul>" + sub + "</ul>";
		
		return s;
	}
	
}
