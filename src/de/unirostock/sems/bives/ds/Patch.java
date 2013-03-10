/**
 * 
 */
package de.unirostock.sems.bives.ds;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TextNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class Patch
{
	private final static Logger LOGGER = Logger.getLogger(Patch.class.getName());
	
	private Document xmlDoc;
	private Element insert, delete, update, move, copy, glue;
	private boolean fullDiff;
	
	public Patch () throws ParserConfigurationException
	{
		fullDiff = true;
		init ();
	}
	
	public Patch (boolean fullDiff) throws ParserConfigurationException
	{
		this.fullDiff = fullDiff;
		init ();
	}
	
	public Document getDocument ()
	{
		return xmlDoc;
	}
	
	private void init () throws ParserConfigurationException
	{
		LOGGER.info ("initializing patch w/ fullDiff = " + fullDiff);
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		xmlDoc = docBuilder.newDocument();
		
		// add root element <bives type="fullDiff">
		Element rootElement = xmlDoc.createElement("bives");
		Attr attr = xmlDoc.createAttribute("type");
		attr.setValue("fullDiff"); // TODO: implement shortDiff
		rootElement.setAttributeNode(attr);
		xmlDoc.appendChild (rootElement);
		
		// create nodes for inserts/updates/moves tec
		update = xmlDoc.createElement("update");
		rootElement.appendChild (update);
		
		
		delete = xmlDoc.createElement("delete");
		rootElement.appendChild (delete);
		
		
		insert = xmlDoc.createElement("insert");
		rootElement.appendChild (insert);
		
		
		move = xmlDoc.createElement("move");
		rootElement.appendChild (move);
		
		
		copy = xmlDoc.createElement("copy");
		rootElement.appendChild (copy);
		
		
		glue = xmlDoc.createElement("glue");
		rootElement.appendChild (glue);
		LOGGER.info ("initialized patch");
	}
	
	private Element createAttributeElement (String oldPath, String newPath, String name, String oldValue, String newValue)
	{
		LOGGER.info ("create attribute element for " + oldPath + " -> " + newPath);
		Element attribute = xmlDoc.createElement("attribute");

		Attr attr = xmlDoc.createAttribute("name");
		attr.setValue (name);
		attribute.setAttributeNode(attr);
		
		if (oldValue != null)
		{
			attr = xmlDoc.createAttribute("oldValue");
			attr.setValue (oldValue);
			attribute.setAttributeNode(attr);
		}
		
		if (newValue != null)
		{
			attr = xmlDoc.createAttribute("newValue");
			attr.setValue (newValue);
			attribute.setAttributeNode(attr);
		}

		if (oldPath != null)
		{
			attr = xmlDoc.createAttribute("oldPath");
			attr.setValue (oldPath);
			attribute.setAttributeNode(attr);
		}
		
		if (newPath != null)
		{
			attr = xmlDoc.createAttribute("newPath");
			attr.setValue (newPath);
			attribute.setAttributeNode(attr);
		}
		
		return attribute;
	}
	
	/**
	 * Creates the node element.
	 *
	 * @param oldParent the old parent, set null to omit
	 * @param newParent the new parent, set null to omit
	 * @param oldPath the old path, set null to omit
	 * @param newPath the new path, set null to omit
	 * @param oldChildNo the old child no, set < 1 to omit
	 * @param newChildNo the new child no, set < 1 to omit
	 * @return the element
	 */
	private Element createNodeElement (String oldParent, String newParent, String oldPath, String newPath, int oldChildNo, int newChildNo, String oldTag, String newTag)
	{
		LOGGER.info ("create node element for " + oldPath + " -> " + newPath);
		Element node = xmlDoc.createElement("node");
		
		Attr attr;

		if (oldParent != null)
		{
			attr = xmlDoc.createAttribute("oldParent");
			attr.setValue (oldParent);
			node.setAttributeNode(attr);
		}

		if (newParent != null)
		{
			attr = xmlDoc.createAttribute("newParent");
			attr.setValue (newParent);
			node.setAttributeNode(attr);
		}

		if (oldChildNo > 0)
		{
			attr = xmlDoc.createAttribute("oldChildNo");
			attr.setValue ("" + oldChildNo);
			node.setAttributeNode(attr);
		}
		
		if (newChildNo > 0)
		{
			attr = xmlDoc.createAttribute("newChildNo");
			attr.setValue ("" + newChildNo);
			node.setAttributeNode(attr);
		}
		
		if (oldPath != null)
		{
			attr = xmlDoc.createAttribute("oldPath");
			attr.setValue (oldPath);
			node.setAttributeNode(attr);
		}
		
		if (newPath != null)
		{
			attr = xmlDoc.createAttribute("newPath");
			attr.setValue (newPath);
			node.setAttributeNode(attr);
		}
		
		if (oldTag != null)
		{
			attr = xmlDoc.createAttribute("oldTag");
			attr.setValue (oldTag);
			node.setAttributeNode(attr);
		}
		
		if (newTag != null)
		{
			attr = xmlDoc.createAttribute("newTag");
			attr.setValue (newTag);
			node.setAttributeNode(attr);
		}
		
		return node;
	}
	
	private Element createTextElement (String oldParent, String newParent, String oldPath, String newPath, int oldChildNo, int newChildNo, String oldText, String newText)
	{
		LOGGER.info ("create text element for " + oldPath + " -> " + newPath);
		Element node = xmlDoc.createElement("text");
		Attr attr;

		if (oldParent != null)
		{
			attr = xmlDoc.createAttribute("oldParent");
			attr.setValue (oldParent);
			node.setAttributeNode(attr);
		}

		if (newParent != null)
		{
			attr = xmlDoc.createAttribute("newParent");
			attr.setValue (newParent);
			node.setAttributeNode(attr);
		}

		if (oldChildNo > 0)
		{
			attr = xmlDoc.createAttribute("oldChildNo");
			attr.setValue ("" + oldChildNo);
			node.setAttributeNode(attr);
		}
		
		if (newChildNo > 0)
		{
			attr = xmlDoc.createAttribute("newChildNo");
			attr.setValue ("" + newChildNo);
			node.setAttributeNode(attr);
		}
		
		if (oldPath != null)
		{
			attr = xmlDoc.createAttribute("oldPath");
			attr.setValue (oldPath);
			node.setAttributeNode(attr);
		}
		
		if (newPath != null)
		{
			attr = xmlDoc.createAttribute("newPath");
			attr.setValue (newPath);
			node.setAttributeNode(attr);
		}

		if (fullDiff)
		{
			if (oldText != null)
			{
				Element old = xmlDoc.createElement("oldText");
				old.setTextContent (oldText);
				node.appendChild (old);
			}
			
			if (newText != null)
			{
				Element neu = xmlDoc.createElement("newText");
				neu.setTextContent (newText);
				node.appendChild (neu);
			}
		}
		
		return node;
		
	}

	public void deleteNode (TreeNode toDelete)
	{
		switch (toDelete.getType ())
		{
			case TreeNode.DOC_NODE:
				deleteNode ((DocumentNode) toDelete);
				break;
			case TreeNode.TEXT_NODE:
				deleteNode ((TextNode) toDelete);
				break;
			default:
			{
				LOGGER.error ("unsupported tree node type for deletion...");
				throw new UnsupportedOperationException ("unsupported tree node type...");
			}
		}
	}
	
	private void deleteNode (DocumentNode toDelete)
	{
		LOGGER.info ("deleting node " + toDelete.getXPath ());
		delete.appendChild (createNodeElement (getParentXpath (toDelete), null, toDelete.getXPath (), null, getChildNo (toDelete), -1, toDelete.getTagName (), null));
		
		if (!fullDiff)
			return;
		LOGGER.info ("checking attributes for full diff");
		Set<String> attr = toDelete.getAttributes ();
		for (String a: attr)
			deleteAttribute (toDelete, a);
	}
	
	private void deleteAttribute (DocumentNode node, String attribute)
	{
		LOGGER.info ("deleting attribute " + attribute + " of " + node.getXPath ());
		delete.appendChild (createAttributeElement (node.getXPath (), null, attribute, node.getAttribute (attribute), null));
	}
	
	private void deleteNode (TextNode node)
	{
		LOGGER.info ("deleting text of " + node.getXPath ());
		delete.appendChild (createTextElement (getParentXpath (node), null, node.getXPath (), null, getChildNo (node), -1, node.getText (), null));
	}
	
	public void insertNode (TreeNode toInsert)
	{
		switch (toInsert.getType ())
		{
			case TreeNode.DOC_NODE:
				insertNode ((DocumentNode) toInsert);
				break;
			case TreeNode.TEXT_NODE:
				insertNode ((TextNode) toInsert);
				break;
			default:
			{
				LOGGER.error ("unsupported tree node type for insertion...");
				throw new UnsupportedOperationException ("unsupported tree node type...");
			}
		}
	}
	
	public void insertNode (DocumentNode toInsert)
	{
		LOGGER.info ("inserting node " + toInsert.getXPath ());
		insert.appendChild (createNodeElement (null, getParentXpath (toInsert), null, toInsert.getXPath (), -1, getChildNo (toInsert), null, toInsert.getTagName ()));
		
		if (!fullDiff)
			return;
		LOGGER.info ("checking attributes for full diff");
		Set<String> attr = toInsert.getAttributes ();
		for (String a: attr)
			insertAttribute (toInsert, a);
	}
	
	private void insertAttribute (DocumentNode node, String attribute)
	{
		LOGGER.info ("inserting attribute " + attribute + " of " + node.getXPath ());
		insert.appendChild (createAttributeElement (null, node.getXPath (), attribute, null, node.getAttribute (attribute)));
	}
	
	private void insertNode (TextNode node)
	{
		LOGGER.info ("inserting text of " + node.getXPath ());
		insert.appendChild (createTextElement (null, getParentXpath (node), null, node.getXPath (), -1, getChildNo (node), null, node.getText ()));
	}
	
	public void updateNode (Connection c, ConnectionManager conMgmt)
	{
		TreeNode a = c.getTreeA ();
		TreeNode b = c.getTreeB ();
		LOGGER.info ("updating node " + a.getXPath () + " to become " + b.getXPath ());
		
		if (a.getType () != b.getType ())
		{
			LOGGER.error ("node types differ, not supported");
			throw new UnsupportedOperationException ("cannot update nodes of different type...");
		}
		
		if (((a.getModification () | b.getModification ()) & (TreeNode.GLUED | TreeNode.COPIED)) != 0)
			throw new UnsupportedOperationException ("copy & glue not supported yet...");
		
		boolean moveThem = (a.getModification () & (TreeNode.MOVED | TreeNode.SWAPPEDKID)) != 0;
		if (moveThem && LOGGER.isInfoEnabled ())
			LOGGER.info ("will move them: par: " + conMgmt.parentsConnected (c) + " chNoA: " + getChildNo (a) + " chNoB: " + getChildNo (b));
		
		// text node
		if (a.getType () == TreeNode.TEXT_NODE)
		{
			if ((a.getModification () & TreeNode.MODIFIED) != 0)
			{
				LOGGER.info ("text differs");
				Element e = createTextElement (getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), ((TextNode) a).getText (), ((TextNode) b).getText ());
				
				if (moveThem)
					move.appendChild (e);
				else
					update.appendChild (e);
			}
			else if (moveThem)
			{
				LOGGER.info ("equal text");
				move.appendChild (createTextElement (getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), null, null));
			}
			return;
		}

		// xml node
		
		DocumentNode dA = (DocumentNode) a;
		DocumentNode dB = (DocumentNode) b;
		
		if ((a.getModification () & TreeNode.MODIFIED) == 0)
		{
			// unmodified -> just move
			if (moveThem)
			{
				LOGGER.info ("nodes unmodified");
				move.appendChild (createNodeElement (getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), null, null));
			}
		}
		else
		{
			// matching label? -> update more extreme than move...
			if (!dA.getTagName ().equals (dB.getTagName ()))
			{
				LOGGER.info ("label of nodes differ -> updating");
				update.appendChild (createNodeElement (getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), dA.getTagName (), dB.getTagName ()));
			}
			else if (moveThem)
			{
				LOGGER.info ("label of nodes do not differ -> moving");
				move.appendChild (createNodeElement (getParentXpath (a), getParentXpath (b), a.getXPath (), b.getXPath (), getChildNo (a), getChildNo (b), null, null));
			}
			
			if (fullDiff)
			{
				// arguments
				LOGGER.info ("checking attributes for full diff");

				Set<String> allAttr = new HashSet<String> ();
				allAttr.addAll (dA.getAttributes ());
				allAttr.addAll (dB.getAttributes ());
				for (String attr : allAttr)
				{
					String aA = dA.getAttribute (attr), bA = dB.getAttribute (attr);
					if (aA == null)
						insertAttribute (dB, attr);
					else if (bA == null)
						deleteAttribute (dA, attr);
					else if (!aA.equals (bA))
						update.appendChild (createAttributeElement (a.getXPath (), b.getXPath (), attr, aA, bA));
				}
			}
		}
	}
	
	private int getChildNo (TreeNode n)
	{
		return n.isRoot () ? -1 : n.getParent ().getNoOfChild (n);
	}
	
	private String getParentXpath (TreeNode n)
	{
		return n.isRoot () ? "" : n.getParent ().getXPath ();
	}
}
