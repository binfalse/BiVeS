/**
 * 
 */
package de.unirostock.sems.bives.ds.xml;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Document;

import de.unirostock.sems.bives.algorithm.Weighter;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.ds.MultiNodeMapper;
import de.unirostock.sems.bives.ds.NodeMapper;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;


/**
 * @author Martin Scharm
 *
 */
public class TreeDocument
{
	private DocumentNode root;
	private NodeMapper<DocumentNode> idMapper;
	private NodeMapper<TreeNode>  pathMapper;
	private MultiNodeMapper<TreeNode> hashMapper;
	private MultiNodeMapper<DocumentNode> tagMapper;
	private boolean ordered;
	private Vector<TreeNode> subtreesBySize;

	public TreeDocument (Document d, Weighter w) throws BivesDocumentParseException
	{
		if (w == null)
			w = new XyWeighter (); // default xy
		pathMapper = new NodeMapper<TreeNode> ();
		idMapper = new NodeMapper<DocumentNode> ();
		hashMapper = new MultiNodeMapper<TreeNode> ();
		tagMapper = new MultiNodeMapper<DocumentNode> ();
		subtreesBySize = new Vector<TreeNode> ();
		root = new DocumentNode (d.getDocumentElement (), null, this, w, 1, 0, pathMapper, idMapper, hashMapper, tagMapper, subtreesBySize);
		Collections.sort (subtreesBySize, new TreeNode.TreeNodeComparatorBySubtreeSize ());
		ordered = true;
	}
	public TreeDocument (Document d, Weighter w, boolean ordered) throws BivesDocumentParseException
	{
		if (w == null)
			w = new XyWeighter (); // default xy
		pathMapper = new NodeMapper<TreeNode> ();
		idMapper = new NodeMapper<DocumentNode> ();
		hashMapper = new MultiNodeMapper<TreeNode> ();
		tagMapper = new MultiNodeMapper<DocumentNode> ();
		subtreesBySize = new Vector<TreeNode> ();
		root = new DocumentNode (d.getDocumentElement (), null, this, w, 1, 0, pathMapper, idMapper, hashMapper, tagMapper, subtreesBySize);
		Collections.sort (subtreesBySize, new TreeNode.TreeNodeComparatorBySubtreeSize ());
		this.ordered = ordered;
	}
	
	public void setResetAllModifications ()
	{
		root.resetModifications ();
	}
	
	public DocumentNode getRoot ()
	{
		return root;
	}
	
	public int getNumNodes ()
	{
		return root.getSizeSubtree () + 1;
	}
	
	public double getTreeWeight ()
	{
		return root.getWeight ();
	}
	
	public Vector<DocumentNode> getNodesByTag (String tag)
	{
		Vector<DocumentNode> nodes = tagMapper.getNodes (tag);
		if (nodes == null)
			return new Vector<DocumentNode> ();
		return tagMapper.getNodes (tag);
	}
	
	public TreeNode [] getSubtreesBySize ()
	{
		TreeNode [] tmp = new TreeNode [subtreesBySize.size ()];
		subtreesBySize.toArray (tmp);
		return tmp;
	}
	
	public Vector<TreeNode> getNodesByHash (String hash)
	{
		return hashMapper.getNodes (hash);
	}
	
	public TreeNode getNodeById (String id)
	{
		return idMapper.getNode (id);
	}
	
	public TreeNode getNodeByPath (String path)
	{
		return pathMapper.getNode (path);
	}
	
	public Set<String> getOccuringXPaths ()
	{
		return pathMapper.getIds ();
	}
	
	public Set<String> getOccuringIds ()
	{
		return idMapper.getIds ();
	}
	
	public Set<String> getOccuringTags ()
	{
		return tagMapper.getIds ();
	}
	
	public Set<String> getOccuringHashes ()
	{
		return hashMapper.getIds ();
	}
	
	public String dump ()
	{
		return root.dump ("");
	}
	
	public String toString ()
	{
		String s = root.toString ();
		s += "\n\n\n";
		//s += pathMapper.toString ();
		return s;
	}
}
