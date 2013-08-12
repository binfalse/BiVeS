/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Vector;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class HierarchyNetworkComponent
{
	private int id;
	private String labelA, labelB;
	private DocumentNode docA, docB;
	private HierarchyNetwork hn;
	private boolean singleDoc;
	private HierarchyNetworkComponent parentA, parentB;
	private Vector<HierarchyNetworkComponent> kidsA, kidsB;
	private Vector<HierarchyNetworkVariable> varsA, varsB;

	public HierarchyNetworkComponent (HierarchyNetwork hn, String labelA, String labelB, DocumentNode docA, DocumentNode docB)
	{
		this.hn = hn;
		this.id = hn.getNextComponentID();
		this.labelA = labelA;
		this.labelB = labelB;
		this.docA = docA;
		this.docB = docB;
		singleDoc = false;

		kidsA = new Vector<HierarchyNetworkComponent> ();
		kidsB = new Vector<HierarchyNetworkComponent> ();
		varsA = new Vector<HierarchyNetworkVariable> ();
		varsB = new Vector<HierarchyNetworkVariable> ();
	}
	
	public void addVaribaleA (HierarchyNetworkVariable var)
	{
		this.varsA.add (var);
	}
	
	public void addVaribaleB (HierarchyNetworkVariable var)
	{
		this.varsB.add (var);
	}
	
	public void addChildA (HierarchyNetworkComponent component)
	{
		this.kidsA.add (component);
	}
	
	public void addChildB (HierarchyNetworkComponent component)
	{
		this.kidsB.add (component);
	}
	
	public void setParentA (HierarchyNetworkComponent component)
	{
		this.parentA = component;
	}
	
	public void setParentB (HierarchyNetworkComponent component)
	{
		this.parentB = component;
	}
	
	public void setDocA (DocumentNode docA)
	{
		this.docA = docA;
	}
	
	public void setLabelA (String labelA)
	{
		this.labelA = labelA;
	}
	
	public void setDocB (DocumentNode docB)
	{
		this.docB = docB;
	}
	
	public void setLabelB (String labelB)
	{
		this.labelB = labelB;
	}
	
	public DocumentNode getA ()
	{
		return docA;
	}
	
	public DocumentNode getB ()
	{
		return docB;
	}
	
	public String getId ()
	{
		return "s" + id;
	}
	
	public String getLabel ()
	{
		if (labelA == null)
			return labelB;
		if (labelB == null)
			return labelA;
		if (labelA.equals (labelB))
			return labelA;
		return labelA + " -> " + labelB;
	}
	
	public int getModification ()
	{
		if (singleDoc)
			return CRN.UNMODIFIED;
		
		if (labelA == null)
			return CRN.INSERT;
		if (labelB == null)
			return CRN.DELETE;
		if (docA.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED) || docB.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED))
			return CRN.MODIFIED;
		return CRN.UNMODIFIED;
	}

	public void setSingleDocument ()
	{
		singleDoc = true;
	}
	
}
