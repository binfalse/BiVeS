/**
 * 
 */
package de.unirostock.sems.bives.ds;

import de.unirostock.sems.bives.ds.xml.DocumentNode;


/**
 * @author Martin Scharm
 *
 */
public class MathML
{
	private DocumentNode math;
	
	public MathML (DocumentNode math)
	{
		this.math = math;
	}
	
	public DocumentNode getMath ()
	{
		return math;
	}
}
