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
public class CRNEdge implements DiffReporter
{
	public static final int REACTANT = 1;
	public static final int PRODUCT = 2;
	public static final int MODIFIER = 3;
	
	private final static Logger LOGGER = Logger.getLogger(CRNEdge.class.getName());
	private CRNNode source;
	private CRNNode target;
	private int modification;
	private String modA;
	private String modB;
	private DocumentNode treeA, treeB;
	private int type;
	
	public CRNEdge (CRNNode source, CRNNode target, int type, int modification, String modA, String modB, DocumentNode treeA, DocumentNode treeB)
	{
		this.type = type;
		this.source = source;
		this.target = target;
		this.modification = modification;
		this.modA = modA;
		this.modB = modB;
		this.treeA = treeA;
		this.treeB = treeB;
	}
	
	public void setModB (String mod)
	{
		this.modB = mod;
	}
	
	public void setModA (String mod)
	{
		this.modA = mod;
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
		this.modification |= mod;
	}
	
	
	

	public void createGraphMl (Document graphDocument, Element parent)
	{
		String mod = null;
		if (treeA == null)
			mod = ChemicalReactionNetwork.INSERT;
		if (treeB == null)
			mod = ChemicalReactionNetwork.DELETE;
		
		if (type != MODIFIER)
		{
			createGraphMl (graphDocument, parent, null, mod);
			return;
		}
		
		
		
		String sboA = ChemicalReactionNetwork.resolvModSBO (modA);
		String sboB = ChemicalReactionNetwork.resolvModSBO (modB);
		
		if (sboA.equals (sboB))
			createGraphMl (graphDocument, parent, sboA, mod);
		else
		{
			if (mod != null)
			{
				if (treeA == null)
					createGraphMl (graphDocument, parent, sboB, ChemicalReactionNetwork.INSERT);
				else
					createGraphMl (graphDocument, parent, sboA, ChemicalReactionNetwork.DELETE);
			}
			else
			{
				createGraphMl (graphDocument, parent, sboA, ChemicalReactionNetwork.DELETE);
				createGraphMl (graphDocument, parent, sboB, ChemicalReactionNetwork.INSERT);
			}
		}
	}
	private void createGraphMl (Document graphDocument, Element parent, String sbo, String mod)
	{

		LOGGER.debug ("create gml edge: " + source + " -> " + target + " mod: " + modification + " sbo: " + sbo);
		Element element = graphDocument.createElement ("edge");
		
		element.setAttribute ("source", source.getId ());
		element.setAttribute ("target", target.getId ());
		
		if (sbo != null)
		{
			Element nsElement = graphDocument.createElement ("data");
			nsElement.setAttribute ("key", "mod");
			nsElement.appendChild (graphDocument.createTextNode (sbo));
			element.appendChild (nsElement);
		}
		
		//if (mod == null)
			//mod = ChemicalReactionNetwork.resolvModification (modification, treeA, treeB);
		if (mod != null)
		{
			Element srcElement = graphDocument.createElement ("data");
			srcElement.setAttribute ("key", "vers");
			srcElement.appendChild (graphDocument.createTextNode (mod));
			element.appendChild (srcElement);
		}
		
		parent.appendChild (element);
	}
	
	

	@Override
	public String reportHTML (String cssclass)
	{
		if (type == MODIFIER)
		{
			if (treeA == null)
				return "<span class='inserted "+cssclass+"'>" + treeB.getAttribute ("species") + " ("+ChemicalReactionNetwork.resolvModSBO (modB)+")</span>";
			if (treeB == null)
				return "<span class='deleted "+cssclass+"'>" + treeA.getAttribute ("species") + " ("+ChemicalReactionNetwork.resolvModSBO (modA)+")</span>";
			
			String sboA = ChemicalReactionNetwork.resolvModSBO (modA);
			String sboB = ChemicalReactionNetwork.resolvModSBO (modB);
			if (sboA.equals (sboB))
				return treeA.getAttribute ("species") + " ("+sboA+")";
			else
				return treeA.getAttribute ("species") + " (<span class='deleted "+cssclass+"'>"+sboA+"</span> &rarr; <span class='inserted "+cssclass+"'>"+sboB+"</span>)";
		}
		else if (type == REACTANT)
		{
			DocumentNode a = source.getTreeA ();
			if (a == null)
				a = source.getTreeB ();
			
			if (treeA == null)
				return "<span class='inserted "+cssclass+"'>" + a.getId () + "</span>";
			if (treeB == null)
				return "<span class='deleted "+cssclass+"'>" + a.getId () + "</span>";
			return a.getId ();
		}
		else
		{
			// PRODUCT
			DocumentNode a = target.getTreeA ();
			if (a == null)
				a = target.getTreeB ();
			
			if (treeA == null)
				return "<span class='inserted "+cssclass+"'>" + a.getId () + "</span>";
			if (treeB == null)
				return "<span class='deleted "+cssclass+"'>" + a.getId () + "</span>";
			return a.getId ();
		}
	}
}
