/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;


/**
 * @author Martin Scharm
 *
 */
public abstract class GraphTranslator
{
	public abstract Object translate (CRN crn) throws Exception;
	public abstract Object translate (HierarchyNetwork hn) throws Exception;
}
