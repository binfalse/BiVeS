/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class CellMLVariable implements DiffReporter
{
	private DocumentNode nodeA, nodeB;

	public CellMLVariable (DocumentNode nodeA, DocumentNode nodeB)
	{
		this.nodeA = nodeA;
		this.nodeB = nodeB;
	}
	
	public void setTreeA (DocumentNode nodeA)
	{
		this.nodeA = nodeA;
	}
	public void setTreeB (DocumentNode nodeB)
	{
		this.nodeB = nodeB;
	}
	
	public void connect (ClearConnectionManager conMgmt) throws BivesConnectionException
	{
		if (nodeA == null || nodeB == null)
			return;
		//System.out.println ("connecting: " + nodeA.getXPath () + " -> " + nodeB.getXPath ());
		conMgmt.addConnection (new Connection (nodeA, nodeB));
	}

	@Override
	public String reportHTML (String cssclass)
	{
		if (nodeA == null)
		{
			return "<tr><td>Variable <span class='inserted'>" + nodeB.getAttribute ("name") + "</span> was inserted</td><td></td></tr>";
		}
		if (nodeB == null)
		{
			return "<tr><td>Variable <span class='deleted'>" + nodeA.getAttribute ("name") + "</span> was deleted</td><td></td></tr>";
		}
		
		String tmp = Tools.genAttributeHtmlStats (nodeA, nodeB);
		if (tmp.length () < 1 && nodeA.getAttribute ("name").equals (nodeB.getAttribute ("name")))
			return "";
		
		String ret = "<tr><td>Variable ";
		if (nodeA.getAttribute ("name").equals (nodeB.getAttribute ("name")))
			ret += nodeA.getAttribute ("name");
		else
			ret += "<span class='deleted'>" + nodeA.getAttribute ("name") + "</span> &rarr; <span class='inserted'>" + nodeB.getAttribute ("name") + "</span>";
		ret += "</td><td>";
		
		return ret + tmp + "</td></tr>";
	}
}
