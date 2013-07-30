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
public class TypesettingMarkDown
	extends Typesetting
{
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.markup.Markup#markup(de.unirostock.sems.bives.markup.MarkupDocument)
	 */
	@Override
	public String markup (MarkupDocument doc)
	{
		//LOGGER.debug ("typesetting doc " + doc.getHeadline ());
		String s = doc.getHeadline () + NL_TXT + "===================" + NL_TXT + NL_TXT;
		//String sub = "";
		
		Vector<String> headers = doc.getHeader ();
		for (String head : headers)
			s += "* " + head + NL_TXT;
		s += NL_TXT;
		
		Vector<MarkupSection> sections = doc.getSections ();
		//LOGGER.debug ("num sections: " + sections.size ());
		for (MarkupSection sec : sections)
			s += markupSection (sec);
		
		s = MarkupDocument.replaceHighlights (s, "*", "*");
		s = MarkupDocument.replaceInserts (s, "<span class='insert'>", "</span>");
		s = MarkupDocument.replaceDeletes (s, "<span class='delete'>", "</span>");
		s = MarkupDocument.replaceAttributes (s, "<span class='attr'>", "</span>");
		s = MarkupDocument.replaceRightArrow (s, "->");
		s = MarkupDocument.replaceMultiplication (s, "*");
		
		return s;
	}
	
	private String markupSection (MarkupSection section)
	{
		//LOGGER.debug ("typesetting section " + section.getHeader ());
		String s = NL_TXT + section.getHeader () + NL_TXT + "-------------------" + NL_TXT + NL_TXT;
		
		Vector<MarkupElement> elements = section.getValues ();
		for (MarkupElement e : elements)
			s += markupElement (e);
		
		return s + NL_TXT + NL_TXT;
	}
	
	private String markupElement (MarkupElement element)
	{
		String s = "* **" + element.getHeader () + "**" + NL_TXT;
		
		Vector<String> values = element.getValues ();
		for (String v : values)
			s += "    * " + v + NL_TXT;
		
		Vector<MarkupElement> subElements = element.getSubElements ();
		for (MarkupElement e : subElements)
			s += "    * " + markupSubElement (e) + NL_TXT;
		
		return s;
	}
	
	private String markupSubElement (MarkupElement element)
	{
		String s = "**" + element.getHeader () + "**" + NL_TXT;
		
		Vector<String> values = element.getValues ();
		for (String v : values)
			s += "        * " + v + "";
		
		return s;
	}
	
}
