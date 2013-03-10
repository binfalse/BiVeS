/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.xml.TreeDocument;


/**
 * @author Martin Scharm
 *
 */
public abstract class Producer
{
	protected ConnectionManager conMgmt;
	protected TreeDocument docA;
	protected TreeDocument docB;
	
	public Producer (ConnectionManager conMgmt, TreeDocument docA, TreeDocument docB)
	{
		this.conMgmt = conMgmt;
		this.docA = docA;
		this.docB = docB;
	}
	
	public abstract String produce ();
}
