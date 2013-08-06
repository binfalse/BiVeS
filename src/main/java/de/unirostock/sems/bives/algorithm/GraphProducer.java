/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.graph.CRN;


/**
 * @author Martin Scharm
 *
 */
public abstract class GraphProducer
{
	protected CRN crn;

	public GraphProducer ()
	{
		crn = new CRN ();
	}
	
	public CRN getCRN ()
	{
		return crn;
	}
}
