/**
 * 
 */
package de.unirostock.sems.bives.markup;


/**
 * @author Martin Scharm
 *
 */
public abstract class Typesetting
{
	public static final String NL_TXT = System.getProperty("line.separator");
	public static final String NL_XHTML = "<br/>";
	public static final String NL_HTML = "<br>";
	public abstract String markup (MarkupDocument doc);
}
