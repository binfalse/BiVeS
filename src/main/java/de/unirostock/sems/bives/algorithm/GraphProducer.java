/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.graph.CRN;
import de.unirostock.sems.bives.ds.graph.HierarchyNetwork;


/**
 * @author Martin Scharm
 *
 */
public abstract class GraphProducer
{
	protected boolean single;
	protected CRN crn;
	protected HierarchyNetwork hn;

	public GraphProducer (boolean single)
	{
		this.single = single;
	}
	
	public CRN getCRN ()
	{
		if (crn == null)
		{
			crn = new CRN ();
			produceCRN ();
		}
		return crn;
	}
	
	public HierarchyNetwork getHierarchy ()
	{
		if (hn == null)
		{
			hn = new HierarchyNetwork ();
			produceHierachyGraph ();
		}
		return hn;
	}
	
	protected abstract void produceCRN ();
	
	protected abstract void produceHierachyGraph ();
}
