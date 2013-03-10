/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unirostock.sems.bives.algorithm.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public abstract class CRNNode implements DiffReporter
{
	protected static int maxReaction = 0;
	protected static int maxSpecies = 0;
	
	
	private final static Logger LOGGER = Logger.getLogger(CRNNode.class.getName());
	protected String id;
	protected String nameA;
	protected String nameB;
	protected int modification;
	protected DocumentNode treeA;
	protected DocumentNode	treeB;
	
	public String getId ()
	{
		return id;
	}
	
	public String getDocId ()
	{
		if (treeA == null)
			return treeB.getId ();
		return treeA.getId ();
	}
	
	public void setNameA (String name)
	{
		this.nameA = name;
	}
	
	public void setNameB (String name)
	{
		this.nameB = name;
	}
	
	public void setTreeA (DocumentNode treeA)
	{
		this.treeA = treeA;
	}
	
	public void setTreeB (DocumentNode treeB)
	{
		this.treeB = treeB;
	}
	
	public void addModifications (int mod)
	{
		//System.out.println ("mod pre: " + modification);
		this.modification |= mod;
		//System.out.println ("mod post: " + modification);
	}
	
	public CRNNode (String id, String nameA, String nameB, int modification, DocumentNode treeA, DocumentNode treeB)
	{
		this.id = id;
		this.nameA = nameA;
		this.nameB = nameB;
		this.treeA = treeA;
		this.treeB = treeB;
		this.modification = modification;
	}

	public abstract void createGraphMl (Document graphDocument, Element parent);
	
	
	protected Element createGraphMlCore (Document graphDocument, Element parent)
	{

		LOGGER.debug ("create gml node: " + id + " mod: " + modification);
		Element element = graphDocument.createElement ("node");

		element.setAttribute ("id", id);
		if (nameA != null)
			element.setAttribute ("name", nameA);
		else if (nameB != null)
			element.setAttribute ("name", nameB);
		else
			element.setAttribute ("name", id);
			

		String mod = ChemicalReactionNetwork.resolvModification (modification, treeA, treeB);
		if (mod != null)
		{
			Element srcElement = graphDocument.createElement ("data");
			srcElement.setAttribute ("key", "vers");
			srcElement.appendChild (graphDocument.createTextNode (mod));
			element.appendChild (srcElement);
		}
		
		Element nameElement = graphDocument.createElement ("data");
		nameElement.setAttribute ("key", "name");
		nameElement.appendChild (graphDocument.createTextNode (nameA));
		element.appendChild (nameElement);
		
		
		parent.appendChild (element);
		return element;
	}
}
