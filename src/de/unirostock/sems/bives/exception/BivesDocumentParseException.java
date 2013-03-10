/**
 * 
 */
package de.unirostock.sems.bives.exception;

import java.io.IOException;


/**
 * @author Martin Scharm
 *
 */
public class BivesDocumentParseException
	extends IOException
{
	public BivesDocumentParseException (String msg)
	{
		super (msg);
	}
}
