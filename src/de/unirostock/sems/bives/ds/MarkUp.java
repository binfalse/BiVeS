/**
 * 
 */
package de.unirostock.sems.bives.ds;


/**
 * @author Martin Scharm
 *
 */
public abstract class MarkUp
{
	public static final String NEWLINE_TXT = System.getProperty("line.separator");
	public static final String NEWLINE_XHTML = "<br/>";
	public static final String NEWLINE_HTML = "<br>";
	public abstract String setHeadline1 (String s);
	public abstract String setHeadline2 (String s);
	public abstract String setHighlight (String s);
	public abstract String setInsert (String s);
	public abstract String setDelete (String s);
	public abstract String setAttribute (String s);
	public abstract String setBulletListItem (String s, int level);
	public abstract String setNumericListItem (String s, int level);
	
	/**
	 * Setups a link. Text might be null.
	 *
	 * @param target the target
	 * @param text the text
	 * @return the string with markup
	 */
	public abstract String setLink (String target, String text);
	public abstract String addLineBreak ();
	public abstract String setParagraph (String s);
	public abstract String setCode (String s);
	public abstract String addHRule ();
}
