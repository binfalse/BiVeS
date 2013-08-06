/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.ds.SBOTerm;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;


/**
 * @author Martin Scharm
 *
 */
public abstract class SBMLSBase
	extends SBMLGenericObject
{
	private String metaid;
	private SBOTerm sboTerm;
	private SBMLXHTML notes;
	private DocumentNode annotation;
	
	public SBMLSBase(DocumentNode documentNode, SBMLModel sbmlModel) throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		if (sbmlModel != null)
			sbmlModel.mapNode (documentNode, this);

		if (documentNode != null)
		{
			metaid = documentNode.getAttribute ("metaid");
			if (documentNode.getAttribute ("sboTerm") != null)
				sboTerm = new SBOTerm (documentNode.getAttribute ("sboTerm"));
			
			Vector<TreeNode> nodeList = documentNode.getChildrenWithTag ("notes");
			if (nodeList.size () > 1)
				throw new BivesSBMLParseException ("SBase with "+nodeList.size ()+" notes. (expected max one notes)");
			if (nodeList.size () == 1)
			{
				notes = new SBMLXHTML ();
				DocumentNode root = (DocumentNode) nodeList.elementAt (0);
				Vector<TreeNode> kids = root.getChildren ();
				for (TreeNode n : kids)
					notes.addXHTML (n);
			}
			
			nodeList = documentNode.getChildrenWithTag ("annotation");
			if (nodeList.size () > 1)
				throw new BivesSBMLParseException ("SBase with "+nodeList.size ()+" annotations. (expected max one annotation)");
			if (nodeList.size () == 1)
				annotation = (DocumentNode) nodeList.elementAt (0);
		}
	}
	
	public SBOTerm getSBOTerm ()
	{
		return sboTerm;
	}
	
	public String getMetaId ()
	{
		return metaid;
	}
	
	public SBMLXHTML getNotes ()
	{
		return notes;
	}
	
	public DocumentNode getAnnotation ()
	{
		return annotation;
	}
	
	protected String reportAnnotation ()
	{
		return "";
	}
	
	protected String reportNotes ()
	{
		return "";
	}
}
