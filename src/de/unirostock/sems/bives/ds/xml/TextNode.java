/**
 * 
 */
package de.unirostock.sems.bives.ds.xml;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Weighter;
import de.unirostock.sems.bives.ds.MultiNodeMapper;
import de.unirostock.sems.bives.ds.NodeMapper;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class TextNode
	extends TreeNode
{
	/** The hash. */
	private String ownHash;
	private String text;
	
	private double weight;
	
	public String getText ()
	{
		return text;
	}
	
	public TextNode (String text, DocumentNode parent, int numChild, Weighter w, NodeMapper<TreeNode> pathMapper, MultiNodeMapper<TreeNode> hashMapper, MultiNodeMapper<DocumentNode> tagMapper, Vector<TreeNode> subtreesBySize)
	{
		super (TreeNode.TEXT_NODE, parent);
		this.text = text;//element.getNodeValue ();
		// create xpath
		if (parent == null)
			xPath = "";
		else
			xPath = parent.getXPath ();
		xPath += "/text()[" + numChild + "]";
		pathMapper.putNode (xPath, this);
		//tagMapper.addNode ("text()", this);
		
		ownHash = Tools.hash (text);
		
		hashMapper.addNode (ownHash, this);
		weight = w.getWeight (this);
		
		subtreesBySize.add (this);
	}

	@Override
	public double getWeight ()
	{
		return weight;
	}
	
	public String getOwnHash ()
	{
		return ownHash;
	}
	
	/**
	 * Gets the calculated hash of this subtree, in TextNodes it equals the own hash.
	 *
	 * @return the hash
	 */
	public String getSubTreeHash ()
	{
		return ownHash;
	}
	
	
	public boolean evaluate (ConnectionManager conMgmr)
	{
		setModification (UNCHANGED);
		
		Vector<Connection> cons = conMgmr.getConnectionsForNode (this);
		if (cons == null || cons.size () == 0)
		{
			addModification (UNMAPPED);
			return true;
		}
		
		if (cons.size () == 1)
		{
			Connection c = cons.elementAt (0);
			TreeNode partner = c.getPartnerOf (this);
			
			// changed?
			if (contentDiffers (partner))
				addModification (MODIFIED);
			
			// mapped, glued?
			// must have a connection
			if (conMgmr.getConnectionsForNode (partner).size () > 1)
			{
				addModification (GLUED);
			}
			// moved?
			if (networkDiffers (partner, conMgmr, c))
			{
				addModification (MOVED);
			}
		}
		if (cons.size () > 1)
		{
			// check if each of them has only 1 connection, otherwise there's smth wrong
			for (Connection c : cons)
			{
				TreeNode partner = c.getPartnerOf (this);
				if (conMgmr.getConnectionsForNode (partner).size () != 1)
					throw new UnsupportedOperationException ("moved and glued!?");
				if ((modified & MODIFIED) == 0 && contentDiffers (partner))
					addModification (MODIFIED);
			}
			addModification (MOVED);
			addModification (COPIED);
		}
		
		return (modified & (MODIFIED | MOVED | UNMAPPED)) != 0;
	}
	
	protected boolean contentDiffers (TreeNode tn)
	{
		if (tn.type != type)
			return true;
		if (!text.equals (((TextNode)tn).text))
			return true;
		return false;
	}

	@Override
	public String dump (String prefix)
	{
		return prefix + xPath + " -> " + modified + "\n";
	}

	@Override
	public void getSubDoc (Document doc, Element parent)
	{
		parent.appendChild (doc.createTextNode (text));
	}
}
