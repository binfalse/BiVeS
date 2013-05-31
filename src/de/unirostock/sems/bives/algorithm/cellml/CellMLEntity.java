/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.util.Vector;

import de.unirostock.sems.bives.ds.RDF;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class CellMLEntity
{
	protected CellMLModel model;
	private Vector<RDF> rdfDescription;
	// might be null
	private DocumentNode node;
	
	public CellMLEntity (DocumentNode node, CellMLModel model)
	{
		this.model = model;
		this.node = node;
		rdfDescription = new Vector<RDF> ();
		
		Vector<TreeNode> kids= node.getChildrenWithTag ("rdf:RDF");
		for (TreeNode kid : kids)
		{
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;
			rdfDescription.add (new RDF ((DocumentNode) kid));
		}
	}
	
	public DocumentNode getNode ()
	{
		return node;
	}
}
