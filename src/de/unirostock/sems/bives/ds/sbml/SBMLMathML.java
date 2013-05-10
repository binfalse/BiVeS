/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.ds.xml.DocumentNode;


/**
 * @author Martin Scharm
 *
 */
public class SBMLMathML
{
	private DocumentNode math;
	/**
	 * 
	 */
	public SBMLMathML (DocumentNode math)
	{
		this.math = math;
	}
	
	public DocumentNode getMath ()
	{
		return math;
	}
}
