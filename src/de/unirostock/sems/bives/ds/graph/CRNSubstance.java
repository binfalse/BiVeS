/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import de.unirostock.sems.bives.ds.SBOTerm;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class CRNSubstance
{
	private int id;
	private String labelA, labelB;
	private DocumentNode docA, docB;
	private CRN crn;
	private boolean singleDoc;

	public CRNSubstance (CRN crn, String labelA, String labelB, DocumentNode docA, DocumentNode docB)
	{
		this.crn = crn;
		this.id = crn.getNextSubstanceID ();
		this.labelA = labelA;
		this.labelB = labelB;
		this.docA = docA;
		this.docB = docB;
		singleDoc = false;
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
	
	public String getSBO ()
	{
		String a = docA.getAttribute ("sboTerm");
		String b = docA.getAttribute ("sboTerm");
		if (a == null || b == null || !a.equals (b))
			return "";
		return a;
	}
	
	public int getModification ()
	{
		if (singleDoc)
			return CRN.UNMODIFIED;
		
		if (labelA == null)
			return CRN.INSERT;
		if (labelB == null)
			return CRN.DELETE;
		if (docA.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED)|| docB.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED))
			return CRN.MODIFIED;
		return CRN.UNMODIFIED;
	}

	public void setSingleDocument ()
	{
		singleDoc = true;
	}
}
