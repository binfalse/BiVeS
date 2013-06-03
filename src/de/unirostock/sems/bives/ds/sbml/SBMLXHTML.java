/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TextNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLXHTML
{
	// html is beneath
	private Vector<TreeNode> nodes;
	
	public SBMLXHTML ()
	{
		nodes = new Vector<TreeNode> ();
	}
	
	public void addXHTML (TreeNode node)
	{
		nodes.add (node);
	}
	
	public String toString ()
	{
		String ret = "";
		for (TreeNode node : nodes)
		{
			if (node.getType () == TreeNode.DOC_NODE)
				ret += Tools.printPrettySubDoc ((DocumentNode) node);
			else if (node.getType () == TreeNode.TEXT_NODE)
				ret += ((TextNode) node).getText ();
		}
		return ret;
	}
}
