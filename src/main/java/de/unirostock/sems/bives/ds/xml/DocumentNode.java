/**
 * 
 */
package de.unirostock.sems.bives.ds.xml;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Weighter;
import de.unirostock.sems.bives.ds.MultiNodeMapper;
import de.unirostock.sems.bives.ds.NodeMapper;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * The class DocumentNode, representing a node in an XML tree. Comparable by size of the subtree.
 * 
 * @author Martin Scharm
 *
 */
public class DocumentNode extends TreeNode// implements Comparable<DocumentNode>
{
	
	public static String ID_ATTR = "id";
	
	/**
	 * Sets the id attribute. (you may want to use something like the metaid instead of the id as identifier)
	 *
	 * @param id the new id attribute
	 */
	public static final void setIdAttr (String id)
	{
		ID_ATTR = id;
	}
	
	/** The id. */
	private String tagName, id;
	
	/** The attributes. */
	private HashMap<String, String> attributes;
	
	/** The children. */
	private Vector<TreeNode> children;
	
	/** The children by tag. */
	private HashMap<String, Vector<TreeNode>> childrenByTag;
	
	/** The inter tree connections. */
	// will be managed by connection manager
	//private HashMap<DocumentNode, Connection> interTreeConnections;
	
	
	/** The hash. */
	private String subTreeHash, ownHash;
	
	/** The num leaves. */
	private int sizeSubtree, numLeaves;
	
	private double weight;
	private Weighter weighter;
	
	/**
	 * Gets the tag name.
	 *
	 * @return the tag name
	 */
	public String getTagName ()
	{
		return tagName;
	}
	
	public double getWeight ()
	{
		return weight;
	}
	
	public String getId ()
	{
		return id;
	}
	
	/*public static DocumentNode getDummyNode ()
	{
		return new DocumentNode ();
	}*/
	
	private DocumentNode (DocumentNode toCopy)
	{
		super (TreeNode.DOC_NODE, null, null, 0);
		tagName = toCopy.tagName;
		id = toCopy.id;
		sizeSubtree = toCopy.sizeSubtree;
		numLeaves = toCopy.numLeaves;
		weight = toCopy.weight;
		weighter = toCopy.weighter;
		
		attributes = new HashMap<String, String> ();
		for (String attr : toCopy.attributes.keySet ())
			attributes.put (attr, toCopy.attributes.get (attr));
		
		children = new Vector<TreeNode> ();
		childrenByTag = new HashMap<String, Vector<TreeNode>> ();
		
		for (TreeNode tn : toCopy.getChildren ())
		{
			if (tn.getType () == TreeNode.DOC_NODE)
			{
					DocumentNode c = (DocumentNode) tn;
					DocumentNode cc = new DocumentNode (c);
					children.add (cc);
					cc.parent = this;
					Vector<TreeNode> v = childrenByTag.get (cc.tagName);
					if (v == null)
					{
						v = new Vector<TreeNode> ();
						childrenByTag.put (cc.tagName, v);
					}
					v.add (cc);
			}
			else
			{
					TextNode c = (TextNode) tn;
					TextNode cc = new TextNode (c);
					children.add (cc);
					cc.parent = this;
					Vector<TreeNode> v = childrenByTag.get (TEXT_TAG);
					if (v == null)
					{
						v = new Vector<TreeNode> ();
						childrenByTag.put (TEXT_TAG, v);
					}
					v.add (cc);
			}
		}
		
		doc = null;

		ownHash = toCopy.ownHash;
		subTreeHash = toCopy.subTreeHash;
	}
	
	/**
	 * Instantiates a new document node.
	 *
	 * @param element the element
	 * @param parent the parent
	 * @param numChild the num child
	 * @param level the level
	 * @param pathMapper the path mapper
	 * @param idMapper the id mapper
	 * @param hashMapper the hash mapper
	 * @param tagMapper the tag mapper
	 * @param subtreesBySize 
	 * @throws BivesDocumentParseException 
	 */
	public DocumentNode (Element element, DocumentNode parent, TreeDocument doc, Weighter w, int numChild, int level)//, NodeMapper<TreeNode> pathMapper, NodeMapper<DocumentNode> idMapper, MultiNodeMapper<TreeNode> hashMapper, MultiNodeMapper<DocumentNode> tagMapper, Vector<TreeNode> subtreesBySize) throws BivesDocumentParseException
	{
		super (TreeNode.DOC_NODE, parent, doc, level);
		// init objects
		//interTreeConnections = new HashMap<DocumentNode, Connection> ();
		attributes = new HashMap<String, String> ();
		children = new Vector<TreeNode> ();
		tagName = element.getTagName ();
		//tagMapper.addNode (tagName, this);
		sizeSubtree = numLeaves = 0;
		weighter = w;
		// create xpath
		if (parent == null)
			xPath = "";
		else
			xPath = parent.getXPath ();
		xPath += "/" + tagName + "[" + numChild + "]";
		//pathMapper.putNode (xPath, this);
		
		// find attributes
		NamedNodeMap a = element.getAttributes();
		int numAttrs = a.getLength();
		for (int i = 0; i < numAttrs; i++)
		{
			Attr attr = (Attr) a.item(i);
			attributes.put (attr.getNodeName(), attr.getNodeValue());
		}
		
		// id mapper
		id = attributes.get (ID_ATTR);
		/*if (id != null)
		{
			if (idMapper.getNode (id) != null)
				doc.setIdsNotUnique ();
				//throw new BivesDocumentParseException ("multiple entities w/ same id: " + id);
			idMapper.putNode (id, this);
		}*/
		
		// add kids
		NodeList kids = element.getChildNodes ();
		int numKids = kids.getLength ();
		childrenByTag = new HashMap<String, Vector<TreeNode>> ();
		for (int i = 0; i < numKids; i++)
		{
			Node current = kids.item (i);
			if (current.getNodeType() == Node.ELEMENT_NODE)
			{
				Element cur = (Element) current;
				if (childrenByTag.get (cur.getTagName ()) == null)
					childrenByTag.put (cur.getTagName (), new Vector<TreeNode> ());
				DocumentNode kid = new DocumentNode (cur, this, doc, w, childrenByTag.get (cur.getTagName ()).size () + 1, level + 1);//, pathMapper, idMapper, hashMapper, tagMapper, subtreesBySize);
				
				children.add (kid);
				childrenByTag.get (cur.getTagName ()).add (kid);
				//subTreeHash += kid.getSubTreeHash ();
				sizeSubtree += kid.getSizeSubtree () + 1;
				numLeaves += kid.getNumLeaves ();
			}
			if (current.getNodeType() == Node.TEXT_NODE)
			{
				String text = current.getNodeValue ().trim ();
				
				// lets discard whitespace-only nodes
				if (text.length () < 1)
					continue;
				
				if (childrenByTag.get ("text()") == null)
					childrenByTag.put ("text()", new Vector<TreeNode> ());
				
				TextNode kid = new TextNode (text, this, doc, childrenByTag.get (TEXT_TAG).size () + 1, w, level + 1);//, pathMapper, hashMapper, tagMapper, subtreesBySize);
				children.add (kid);
				childrenByTag.get ("text()").add (kid);
				//subTreeHash += kid.getSubTreeHash ();
				sizeSubtree += 1;
				numLeaves += 1;
				//Element cur = (Element) current;
				/*if (text == null)
					text = "";
				text += current.getNodeValue ().trim ();*/
			}
		}
		calcHash ();
		if (numLeaves == 0)
			numLeaves = 1;
		
		//subtreesBySize.add (this);
		
		/*DocumentNode dn = hashMapper.getNode (hash);
		if (dn != null)
		{
			System.out.println ("-------- same: " + xPath + "    " + dn.getXPath ());
		}*/
		//hashMapper.addNode (subTreeHash, this);
		weight = w.getWeight (this);
		doc.integrate (this);
	}
	
	protected void reSetupStructureDown (TreeDocument doc, int numChild)
	{
		if (this.doc != null)
			this.doc.separate (this);
		this.doc = doc;
		//LOGGER.debug (parent.xPath);
		this.xPath = parent.xPath + "/" + tagName + "[" + numChild + "]";
		this.level = parent.level + 1;
		
		for (String tag : childrenByTag.keySet ())
		{
			Vector<TreeNode> kids = childrenByTag.get (tag);
			for (int i = 0; i < kids.size (); i++)
				kids.elementAt (i).reSetupStructureDown (doc, i+1);
		}
		
		this.doc.integrate (this);
	}
	
	protected void reSetupStructureUp ()
	{
		if (this.doc != null)
			this.doc.separate (this);
		
		calcHash ();
		numLeaves = 0;
		sizeSubtree = 0;
		for (TreeNode kid : children)
		{
			if (kid.type == TreeNode.DOC_NODE)
			{
				DocumentNode k = (DocumentNode) kid;
				numLeaves += k.numLeaves;
				sizeSubtree += k.getSizeSubtree () + 1;
			}
			else
			{
				numLeaves++;
				sizeSubtree++;
			}
		}
			
		if (numLeaves == 0)
			numLeaves = 1;

		if (this.doc != null)
			this.doc.integrate (this);
		weight = weighter.getWeight (this);
		if (parent != null)
			parent.reSetupStructureUp ();
	}
	
	
	public void addChild (DocumentNode toAdd)
	{
		// integrate subtree
		toAdd.parent = this;
		if (childrenByTag.get (toAdd.getTagName ()) == null)
			childrenByTag.put (toAdd.getTagName (), new Vector<TreeNode> ());
		children.add (toAdd);
		toAdd.reSetupStructureDown (doc, childrenByTag.get (toAdd.getTagName ()).size () + 1);
		childrenByTag.get (toAdd.getTagName ()).add (toAdd);
		
		// update parents
		reSetupStructureUp ();
		
		// resort subtreesizes
		doc.resortSubtrees ();
	}
	
	public void rmChild (DocumentNode toRemove) throws BivesConsistencyException
	{
		Vector<TreeNode> nodes = childrenByTag.get (toRemove.getTagName ());
		if (nodes == null)
			throw new BivesConsistencyException ("removing a child that isn't a child");
		if (!nodes.remove (toRemove) || !children.remove (toRemove))
			throw new BivesConsistencyException ("removing a child that isn't a child");

		// update parents
		reSetupStructureUp ();
		
		// resort subtreesizes
		doc.resortSubtrees ();
	}
	
	/**
	 * Extracts this subtree. Creates a DocumentNode that has no parent, e.g. to transfer it to another document.
	 *
	 * @return the copy of this DocumentNode
	 */
	public DocumentNode extract ()
	{
		return new DocumentNode (this);
	}
	
	/**
	 * Gets the calculated hash of this subtree.
	 *
	 * @return the hash
	 */
	public String getSubTreeHash ()
	{
		return subTreeHash;
	}
	
	/**
	 * Gets the calculated hash of this single element (ignoring subtree).
	 *
	 * @return the hash
	 */
	public String getOwnHash ()
	{
		return ownHash;
	}
	
	/**
	 * Gets the size of this subtree (number of nodes under the current node, current node excluded).
	 *
	 * @return the size of the subtree
	 */
	public int getSizeSubtree ()
	{
		return sizeSubtree;
	}
	
	/**
	 * Gets the number of leaves under this subtree. If this is a leave it will return 1.
	 *
	 * @return the num leaves
	 */
	public int getNumLeaves ()
	{
		return numLeaves;
	}
	
	/**
	 * Calc hash.
	 *
	 * @return the string
	 */
	private void calcHash ()
	{
		String h = tagName;
		for (String a : attributes.keySet ())
			h += ";" + a + "=" + attributes.get (a);
		ownHash = Tools.hash (h);
		
		//subTreeHash = "";
		for (TreeNode kid : children)
			h += kid.getSubTreeHash ();
		//if (subTreeHash != null)
		//h += subTreeHash;
		/*if (text != null)
			h += text;*/
		
		subTreeHash = Tools.hash (h);
	}
	
	/**
	 * Gets the attribute. Don't use it to get the id, use getId () instead!
	 *
	 * @param attr the name of the attribute
	 * @return the value of the attribute
	 */
	public String getAttribute (String attr)
	{
		return attributes.get (attr);
	}
	
	/**
	 * Overrides an attribute.
	 *
	 * @param attr the name of the attribute
	 * @param value the new value
	 */
	public void setAttribute (String attr, String value)
	{
		attributes.put (attr, value);
		reSetupStructureUp ();
	}
	
	public Set<String> getAttributes ()
	{
		return attributes.keySet ();
	}
	
	
	/**
	 * Checks if this node is a child of some other node (multilevel). Both nodes have to be from the same origin document and the XPath of the current node has to start with the parent's XPath.
	 *
	 * @param parent the parent in question
	 * @return true, if is this is a child of parent
	 */
	public boolean isChildOf (DocumentNode parent)
	{
		return doc == parent.doc && xPath.startsWith (parent.xPath);
	}
	
	public int getNumChrildren ()
	{
		return children.size ();
	}
	
	public Vector<TreeNode> getChildren ()
	{
		return children;
	}
	
	public Vector<TreeNode> getChildrenWithTag (String tag)
	{
		Vector<TreeNode> ret = childrenByTag.get (tag);
		if (ret == null)
			return new Vector<TreeNode> ();
		return ret;
	}
	
	public HashMap<String, Vector<TreeNode>> getChildrenTagMap ()
	{
		return childrenByTag;
	}
	
	public int getNoOfChild (TreeNode kid)
	{
		return children.indexOf (kid) + 1;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		StringBuilder attr = new StringBuilder (" ");
		for (String a : attributes.keySet ())
			attr.append (a + "=\"" + attributes.get (a) + "\" ");
		attr = new StringBuilder ("<" + tagName + attr.toString () + ">\t(" + xPath + ")\t"+subTreeHash+"\n");
		for (int i = 0; i < children.size (); i++)
			attr.append (children.elementAt (i));
		return attr.toString () + "</" + tagName + ">\n";
	}
	
	
	public boolean evaluate (ClearConnectionManager conMgmr)
	{
		LOGGER.debug ("evaluate " + xPath);
		
		//setModification (UNCHANGED);
		boolean kidChanged = false;
		for (TreeNode child: children)
		{
			kidChanged |= child.evaluate (conMgmr);
		}
		if (kidChanged)
			addModification (SUB_MODIFIED);
		LOGGER.debug ("evaluate kids changed: " + kidChanged);
		
		Connection con = conMgmr.getConnectionForNode (this);
		if (con == null)
		{
			LOGGER.debug ("evaluate " + xPath + " is unmapped");
			addModification (UNMAPPED);
			return true;
		}

		TreeNode partner = con.getPartnerOf (this);
		//System.out.println ("evaluate " + xPath + " is mapped to " + partner.getXPath ());
		LOGGER.debug ("evaluate " + xPath + " is mapped to " + partner.getXPath ());
		// changed?
		if (contentDiffers (partner))
			addModification (MODIFIED);
		// moved?
		if (networkDiffers (partner, conMgmr, con))
		{
			//System.out.println ("netw differs : " + xPath + " -> " + partner.getXPath ());
			addModification (MOVED);
		}
		
		/*if (cons.size () == 1)
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
				//System.out.println ("netw differs : " + xPath + " -> " + partner.getXPath ());
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
		}*/
		
		LOGGER.debug ("mod: " + modified);
		
		return modified != 0;
	}

	@Override
	protected boolean contentDiffers (TreeNode tn)
	{
		if (tn.type != type)
			return true;
		
		DocumentNode dn = (DocumentNode) tn;
		
		// tag?
		if (!tagName.equals (dn.tagName))
			return true;
		
		// attributes?
		if (attributes.size () != dn.attributes.size ())
			return true;
		for (String attr: attributes.keySet ())
		{
			if (dn.attributes.get (attr) == null || !dn.attributes.get (attr).equals (attributes.get (attr)))
				return true;
		}
		
		return false;
	}


	@Override
	public String dump (String prefix)
	{
		String s = prefix + xPath + " -> " + modified + "\n";
		for (TreeNode child: children)
		{
			s += child.dump (prefix + "\t");
		}
		return s;
	}

	@Override
	public void getSubDoc (Document doc, Element parent)
	{
		Element node = doc.createElement (tagName);
		for (String att : attributes.keySet ())
			node.setAttribute (att, attributes.get (att));
		if (parent == null)
			doc.appendChild (node);
		else
			parent.appendChild (node);
		for (TreeNode kid : children)
			kid.getSubDoc (doc, node);
	}
	
	/**
	 * Calculates the distance of attributes. Returns a double in [0,1]. If all attributes match the distance will be 0, if none of the attributes match the distance will be 1.
	 *
	 * @param cmp the node to compare
	 * @return the attribute distance in [0,1]
	 */
	public double getAttributeDistance (DocumentNode cmp)
	{
		if (attributes.size () == 0 && cmp.attributes.size () == 0)
			return 0;
		double unmatch = 0.;
		for (String name : attributes.keySet ())
		{
			if (cmp.attributes.get (name) == null)
				unmatch += 1;
			else if (!cmp.attributes.get (name).equals (attributes.get (name)))
				unmatch += 2;
		}
		for (String name : cmp.attributes.keySet ())
		{
			if (attributes.get (name) == null)
				unmatch += 1;
		}
		return unmatch / (double)(attributes.size () + cmp.attributes.size ());
	}

	@Override
	public void getNodeStats (HashMap<String, Integer> map)
	{
		Integer i = map.get (tagName);
		if (i == null)
			map.put (tagName, 1);
		else
			map.put (tagName, i + 1);
		
		for (TreeNode child : children)
			child.getNodeStats (map);
	}
}
