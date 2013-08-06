/**
 * 
 */
package de.unirostock.sems.bives.exception;


/**
 * @author Martin Scharm
 *
 */
public class BivesImportException
	extends Exception
{
	public BivesImportException (String file, Exception e)
	{
		super ("Exception during import of " + file + ": [" + e.getClass ().getName () + ": " +e.getMessage () + "]");
	}
}
