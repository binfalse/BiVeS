/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Collection;
import java.util.HashMap;
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
	private Vector<HierarchyNetworkVariable> vars;
	//private Vector<HierarchyNetworkVariable> varsA, varsB;
	/*private HashMap<String, Var> varMapper;
	
	class Var
	{
		public HierarchyNetworkVariable varA, varB;
		public Var (HierarchyNetworkVariable varA, HierarchyNetworkVariable varB)
		{
			this.varA = varA;
			this.varB = varB;
		}
	}*/

	public HierarchyNetworkComponent (HierarchyNetwork hn, String labelA, String labelB, DocumentNode docA, DocumentNode docB)
	{
		this.hn = hn;
		this.id = hn.getNextComponentID();
		this.labelA = labelA;
		this.labelB = labelB;
		this.docA = docA;
		this.docB = docB;
		singleDoc = false;
		
		//varMapper = new HashMap<String, Var> ();
		kidsA = new Vector<HierarchyNetworkComponent> ();
		kidsB = new Vector<HierarchyNetworkComponent> ();
		vars = new Vector<HierarchyNetworkVariable> ();
		/*varsB = new Vector<HierarchyNetworkVariable> ();*/
	}
	
	public Vector<HierarchyNetworkVariable> getVariables ()
	{
		return vars;
	}
	
	public HierarchyNetworkComponent getParentA ()
	{
		return parentA;
	}
	
	public HierarchyNetworkComponent getParentB ()
	{
		return parentB;
	}
	
	/*public void addVaribaleA (HierarchyNetworkVariable var)
	{
		Var v = varMapper.get (var.getId ());
		if (v == null)
			varMapper.put (var.getId (), new Var (var, null));
		else
			v.varA = var;
		//this.varsA.add (var);
	}*/
	
	public void addVaribale (HierarchyNetworkVariable var)
	{
		if (vars.contains (var))
			return;
		vars.add (var);
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
		return "c" + id;
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
