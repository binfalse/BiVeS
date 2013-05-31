/**
 * 
 */
package de.unirostock.sems.bives.ds;


/**
 * @author Martin Scharm
 *
 */
public class MarkUPMarkDown
	extends MarkUp
{

	@Override
	public String setHeadline1 (String s)
	{
		return s + NEWLINE_TXT + "===========" + NEWLINE_TXT + NEWLINE_TXT;
	}

	@Override
	public String setHeadline2 (String s)
	{
		return s + NEWLINE_TXT + "-----------" + NEWLINE_TXT + NEWLINE_TXT;
	}

	@Override
	public String setHighlight (String s)
	{
		return "**" + s + "**";
	}

	@Override
	public String setInsert (String s)
	{
		return "<span class='insert'>" + s + "</span>";
	}

	@Override
	public String setDelete (String s)
	{
		return "<span class='delete'>" + s + "</span>";
	}

	@Override
	public String setAttribute (String s)
	{
		return "<span class='attribute'>" + s + "</span>";
	}

	@Override
	public String setBulletListItem (String s, int level)
	{
		String ret = "";
		for (int i = 0; i < level; i++)
			ret += "    ";
		return ret + "* " + s;
	}

	@Override
	public String setNumericListItem (String s, int level)
	{
		String ret = "";
		for (int i = 0; i < level; i++)
			ret += "    ";
		return ret + "1. " + s;
	}

	@Override
	public String setLink (String target, String text)
	{
		if (text == null)
			text = target;
		return "["+text+"]("+target+")";
	}

	@Override
	public String addLineBreak ()
	{
		return "  " + NEWLINE_TXT;
	}

	@Override
	public String setParagraph (String s)
	{
		return NEWLINE_TXT + NEWLINE_TXT + s + NEWLINE_TXT + NEWLINE_TXT;
	}

	@Override
	public String setCode (String s)
	{
		return "`" + s + "`";
	}

	@Override
	public String addHRule ()
	{
		return NEWLINE_TXT + NEWLINE_TXT + "----------------------------------" + NEWLINE_TXT + NEWLINE_TXT;
	}
	
}
