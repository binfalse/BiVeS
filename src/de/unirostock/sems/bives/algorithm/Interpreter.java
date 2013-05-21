/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.xml.TreeDocument;


/**
 * @author Martin Scharm
 *
 */
public abstract class Interpreter
{
	protected ClearConnectionManager conMgmt;
	protected TreeDocument docA;
	protected TreeDocument docB;
	
	public Interpreter (ClearConnectionManager conMgmt, TreeDocument docA, TreeDocument docB)
	{
		this.conMgmt = conMgmt;
		this.docA = docA;
		this.docB = docB;
	}
	
	public abstract void interprete ();
}
