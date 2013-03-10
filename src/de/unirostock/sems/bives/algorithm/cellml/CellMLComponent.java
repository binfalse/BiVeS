/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class CellMLComponent implements DiffReporter
{
	private String nameA, nameB;
	private DocumentNode nodeA;
	private DocumentNode nodeB;
	private HashMap <String, CellMLVariable> variables;
	private HashMap <String, CellMLMath> maths;
	
	public CellMLComponent (DocumentNode nodeA, DocumentNode nodeB, ConnectionManager conMgmr)
	{
		variables = new HashMap <String, CellMLVariable> ();
		maths = new HashMap <String, CellMLMath> ();
				this.nodeA = nodeA;
				this.nodeB = nodeB;
		if (nodeA != null && nodeB != null && conMgmr != null)
			setUpAB (conMgmr);
		else
		{
			if (nodeA != null)
				setUpA ();
			if (nodeB != null)
				setUpB ();
		}
	}
	
	public void setTreeA (DocumentNode nodeA)
	{
		this.nodeA = nodeA;
		if (nodeA != null)
		setUpA ();
	}
	public void setTreeB (DocumentNode nodeB)
	{
		this.nodeB = nodeB;
		if (nodeB != null)
		setUpB ();
	}
	
	public String getNameA ()
	{
		return nameA;
	}
	public String getNameB ()
	{
		return nameB;
	}

	private String getRandomMathIdentifier ()
	{
		String name = UUID.randomUUID().toString();
		while (maths.get (name) != null)
			name = UUID.randomUUID().toString();
		return name;
	}
	private String getRandomVariableIdentifier ()
	{
		String name = UUID.randomUUID().toString();
		while (variables.get (name) != null)
			name = UUID.randomUUID().toString();
		return name;
	}
	
	private void setUpAB (ConnectionManager conMgmr)
	{
		nameA = nodeA.getAttribute ("name");
		nameB = nodeB.getAttribute ("name");

		Vector<TreeNode> vars = nodeA.getChildrenWithTag ("variable");
		if (vars != null)
			for (TreeNode child : vars)
			{
				if (child.getType () != TreeNode.DOC_NODE)
					continue;
				
				if (child.hasModification (TreeNode.UNMAPPED))
				{
					variables.put (getRandomVariableIdentifier (), new CellMLVariable ((DocumentNode) child, null));
				}
				else
				{
					Vector<Connection> cons = conMgmr.getConnectionsForNode (child);
					TreeNode child2 = cons.elementAt (0).getPartnerOf (child);
					if (child.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED) || child2.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED))
						variables.put (getRandomVariableIdentifier (), new CellMLVariable ((DocumentNode) child, (DocumentNode) child2));
				}
			}
		vars = nodeB.getChildrenWithTag ("variable");
		if (vars != null)
			for (TreeNode child : vars)
			{
				if (child.getType () != TreeNode.DOC_NODE)
					continue;
				
				if (child.hasModification (TreeNode.UNMAPPED))
				{
					variables.put (getRandomVariableIdentifier (), new CellMLVariable (null, (DocumentNode) child));
				}
			}
		
		
		vars = nodeA.getChildrenWithTag ("math");
		if (vars != null)
			for (TreeNode child : vars)
			{
				if (child.getType () != TreeNode.DOC_NODE)
					continue;
				
				if (child.hasModification (TreeNode.UNMAPPED))
				{
					maths.put (getRandomMathIdentifier (), new CellMLMath ((DocumentNode) child, null));
				}
				else
				{
					Vector<Connection> cons = conMgmr.getConnectionsForNode (child);
					TreeNode child2 = cons.elementAt (0).getPartnerOf (child);
					if (child.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED) || child2.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED))
						maths.put (getRandomMathIdentifier (), new CellMLMath ((DocumentNode) child, (DocumentNode) child2));
				}
			}
		vars = nodeB.getChildrenWithTag ("math");
		if (vars != null)
			for (TreeNode child : vars)
			{
				if (child.getType () != TreeNode.DOC_NODE)
					continue;
				
				if (child.hasModification (TreeNode.UNMAPPED))
				{
					maths.put (getRandomMathIdentifier (), new CellMLMath (null, (DocumentNode) child));
				}
			}
	}

	private void setUpA ()
	{
		nameA = nodeA.getAttribute ("name");
		
		Vector<TreeNode> vars = nodeA.getChildrenWithTag ("variable");
		if (vars != null)
			for (TreeNode child : vars)
			{
				if (child.getType () != TreeNode.DOC_NODE)
					continue;
				String name = ((DocumentNode) child).getAttribute ("name");
				if (variables.get (name) != null)
					variables.get (name).setTreeA ((DocumentNode) child);
				else
					variables.put (name, new CellMLVariable ((DocumentNode) child, null));
			}
		
	}
	private void setUpB ()
	{
		//System.out.println ("0");
		nameB = nodeB.getAttribute ("name");
		//System.out.println ("1");
		Vector<TreeNode> vars = nodeB.getChildrenWithTag ("variable");
		//System.out.println ("2");
		if (vars != null)
			for (TreeNode child : vars)
			{
				//System.out.println ("3");
				if (child.getType () != TreeNode.DOC_NODE)
					continue;
				String name = ((DocumentNode) child).getAttribute ("name");
				//System.out.println ("4");
				if (variables.get (name) != null)
					variables.get (name).setTreeB ((DocumentNode) child);
				else
					variables.put (name, new CellMLVariable (null, (DocumentNode) child));
				//System.out.println ("5");
			}
		
	}
	
	public void connect (ConnectionManager conMgmt)
	{
		if (nodeA == null || nodeB == null)
			return;
		conMgmt.addConnection (new Connection (nodeA, nodeB));
		//System.out.println ("connecting: " + nodeA.getXPath () + " -> " + nodeB.getXPath ());
		
		for (CellMLVariable var : variables.values ())
			var.connect (conMgmt);
	}

	@Override
	public String reportHTML (String cssclass)
	{
		if (nodeA == null)
		{
			String ret = "<tr><td class='highlighttd'>Component <span class='inserted'>" + nameB + "</span> was inserted</td><td></td></tr>";

			for (CellMLVariable var : variables.values ())
				ret += var.reportHTML (cssclass);
			return ret;
		}
		if (nodeB == null)
		{
			String ret = "<tr><td class='highlighttd'>Component <span class='deleted'>" + nameA + "</span> was inserted</td><td></td></tr>";

			for (CellMLVariable var : variables.values ())
				ret += var.reportHTML (cssclass);
			return ret;
		}

		String ret = "<tr><td class='highlighttd'>Component ";
		if (nameA.equals (nameB))
			ret += nameA;
		else
			ret += "<span class='deleted'>" + nameA + "</span> &rarr; <span class='inserted'>" + nameB + "</span>";
		ret += "</td><td>";
		ret += Tools.genAttributeHtmlStats (nodeA, nodeB);
		ret += "</td></tr>";
		
		for (CellMLVariable var : variables.values ())
			ret += var.reportHTML (cssclass);
		for (CellMLMath math : maths.values ())
			ret += math.reportHTML (cssclass);
		return ret;
	}
}
