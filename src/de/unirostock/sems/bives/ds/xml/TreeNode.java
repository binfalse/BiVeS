/**
 * 
 */
package de.unirostock.sems.bives.ds.xml;

import java.util.Comparator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;


/**
 * @author Martin Scharm
 *
 */
public abstract class TreeNode
{
	public static final int UNCHANGED = 0;
	public static final int UNMAPPED = 1;
	public static final int MOVED = 2;
	public static final int MODIFIED = 4;
	public static final int SUB_MODIFIED = 8;
	public static final int COPIED = 16;
	public static final int GLUED = 32;
	public static final int KIDSSWAPPED = 64;
	public static final int SWAPPEDKID = 128;
	
	public static final int DOC_NODE = 1;
	public static final int TEXT_NODE = 2;
	
	public static class TreeNodeComparatorBySubtreeSize implements Comparator<TreeNode>
	{
		private int reverse;
		
		public TreeNodeComparatorBySubtreeSize ()
		{
			reverse = 1;
		}
		
		public TreeNodeComparatorBySubtreeSize (boolean reverse)
		{
			if (reverse)
				this.reverse = -1;
			else
				this.reverse = 1;
		}

		@Override
		public int compare (TreeNode o1, TreeNode o2)
		{
			return reverse * pCompare (o1, o2);
		}
		
		private int pCompare (TreeNode o1, TreeNode o2)
		{
			if (o2.getType () == TreeNode.TEXT_NODE)
				return 1;
			if (o1.getType () == TreeNode.TEXT_NODE)
				return -1;
			
			//DocumentNode O1 = (DocumentNode) o1, O2 = (DocumentNode) o2;
			int sub1 = ((DocumentNode) o1).getSizeSubtree (), sub2 = ((DocumentNode) o2).getSizeSubtree ();
			
			// first based on subtree
			if (sub1 < sub2)
				return -1;
			if (sub1 > sub2)
				return 1;
			
			int a1 = ((DocumentNode) o1).getAttributes ().size (), a2 = ((DocumentNode) o2).getAttributes ().size ();
			
			// if that equals, compare number of arguments
			if (a1 < a2)
				return -1;
			if (a1 > a2)
				return 1;
			// ok ok, they have equal priority...
			return 0;
		}
		
	}
	
	protected int modified;
	
	protected int type;
	/** The x path. */
	protected String xPath;
	
	/** The parent. */
	protected DocumentNode parent;
	
	public TreeNode (int type, DocumentNode parent)
	{
		this.type = type;
		this.parent = parent;
		this.modified = UNCHANGED;
	}
	
	public int getModification ()
	{
		return modified;
	}
	
	public void rmModification (int mod)
	{
		this.modified &= ~mod;
	}
	
	public void addModification (int mod)
	{
		this.modified |= mod;
	}
	
	public void setModification (int mod)
	{
		this.modified = mod;
	}
	
	public boolean hasModification (int mod)
	{
		return (this.modified & mod) > 0;
	}
	
	public int getType ()
	{
		return type;
	}
	public abstract double getWeight ();
	public abstract String getOwnHash ();
	public abstract String getSubTreeHash ();

	
	public DocumentNode getParent ()
	{
		return parent;
	}
	
	/**
	 * Gets the x path.
	 *
	 * @return the x path
	 */
	public String getXPath ()
	{
		return xPath;
	}
	
	public boolean isRoot ()
	{
		return parent == null;
	}
	
	public void resetModifications ()
	{
		this.modified = UNCHANGED;
		if (type == DOC_NODE)
		{
			Vector<TreeNode> kids = ((DocumentNode) this).getChildren ();
			for (TreeNode kid : kids)
				kid.resetModifications ();
		}
	}
	
	public abstract boolean evaluate (ClearConnectionManager conMgmr);
	protected abstract boolean contentDiffers (TreeNode tn);
	public boolean networkDiffers (TreeNode tn, ClearConnectionManager conMgmr, Connection c)
	{
		//System.out.println ("checking : " + getXPath () + " -> " + tn.getXPath ());
		DocumentNode p = getParent ();
		DocumentNode tnp = tn.getParent ();
		
		// both root?
		if (p == null && tnp == null)
			return false;
		
		// one root?
		if (p == null || tnp == null)
			return true;
		
		//System.out.println ("netw diff p : " + p.getXPath () + " -> " + tnp.getXPath ());

		
		// parents connected and same child no.?
		if (!conMgmr.parentsConnected (c))
		//if ( (c))
		{
			/*System.out.println ("nodes: " + ((DocumentNode) this).getAttribute ("species") + "->" + getXPath () + " --- " + ((DocumentNode) tn).getAttribute ("species") + "->" + tn.getXPath ());
			System.out.println ("parents: " + p.getXPath () + " --- " + tnp.getXPath ());
		System.out.println ("p1: " + conMgmr.getConnectionOfNodes (p, tnp));
		System.out.println ("p2: " + conMgmr.parentsConnected (c));
			System.out.println ("parents not connected: ");*/
			if (p != null)
				p.addModification (SUB_MODIFIED);
			if (tnp != null)
				tnp.addModification (SUB_MODIFIED);
			return true;
		}
		
		//System.out.println ("par connected");
		
		if (p.getNoOfChild (this) != tnp.getNoOfChild (tn))
		{
			p.addModification (KIDSSWAPPED);
			tnp.addModification (KIDSSWAPPED);
			addModification (SWAPPEDKID);
			tn.addModification (SWAPPEDKID);
			//return true;
		}
		
		//System.out.println ("same child no");
		
		return false;
	}
	
	public abstract String dump (String prefix);
	
	public abstract void getSubDoc (Document doc, Element parent);
}
