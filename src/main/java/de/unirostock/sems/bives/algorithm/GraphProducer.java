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
		crn = new CRN ();
		hn = new HierarchyNetwork ();
		this.single = single;
	}
	
	public CRN getCRN ()
	{
		return crn;
	}
	
	public HierarchyNetwork getHierarchy ()
	{
		return hn;
	}
	
	protected abstract void produceCRN ();
	
	protected abstract void produceHierachyGraph ();
}
