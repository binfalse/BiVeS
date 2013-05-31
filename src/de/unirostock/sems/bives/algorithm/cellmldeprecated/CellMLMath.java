/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellmldeprecated;

import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class CellMLMath implements DiffReporter
{
	private DocumentNode nodeA, nodeB;

	public CellMLMath (DocumentNode nodeA, DocumentNode nodeB)
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

	@Override
	public String reportHTML (String cssclass)
	{
		return "<tr><td>Math</td><td>"+Tools.genMathHtmlStats (nodeA, nodeB)+"</td></tr>";
		/*
		if (nodeA == null)
		{
			return "<tr><td>Math <span class='inserted'>inserted</span></td><td>"+Tools.printSubDoc (nodeB)+"</td></tr>";
		}
		if (nodeB == null)
		{
			return "<tr><td>Math <span class='deleted'>deleted</span></td><td><span class='deleted'>"+Tools.printSubDoc (nodeA)+"</span></td></tr>";
		}
		

		return "<tr><td>Math updated</td><td>from <span class='deleted'>"+Tools.printSubDoc (nodeA)+"</span> to <span class='inserted'>"+Tools.printSubDoc (nodeB)+"</span></td></tr>";*/
	}
}
