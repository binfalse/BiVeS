/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class CRNReaction
	extends CRNNode
{
	private final static Logger LOGGER = Logger.getLogger(CRNReaction.class.getName());
	private String reversible;
	private String fast;
	private HashMap<CRNNode, CRNEdge> reactants;
	private HashMap<CRNNode, CRNEdge> products;
	private HashMap<CRNNode, CRNEdge> modifiers;
	private DocumentNode kineticLawA;
	private DocumentNode kineticLawB;
	
	
	public CRNReaction (String nameA, String nameB, int modification, String reversible, String fast, DocumentNode treeA, DocumentNode treeB)
	{
		super ("r" + ++maxReaction, nameA, nameB, modification, treeA, treeB);
		this.reversible = reversible;
		this.fast = fast;
		reactants = new HashMap <CRNNode, CRNEdge> ();
		products = new HashMap <CRNNode, CRNEdge> ();
		modifiers = new HashMap <CRNNode, CRNEdge> ();
	}
	
	public void setKineticLawB (DocumentNode law)
	{
		kineticLawB = law;
	}
	
	public void setKineticLawA (DocumentNode law)
	{
		kineticLawA = law;
	}
	
	public void addProduct (CRNNode node, CRNEdge edge)
	{
		products.put (node, edge);
	}
	
	public void addModifier (CRNNode node, CRNEdge edge)
	{
		modifiers.put (node, edge);
	}
	
	public void addReactant (CRNNode node, CRNEdge edge)
	{
		reactants.put (node, edge);
	}

	
	public CRNEdge getProduct (CRNNode node)
	{
		return products.get (node);
	}
	
	public CRNEdge getModifier (CRNNode node)
	{
		return modifiers.get (node);
	}
	
	public CRNEdge getReactant (CRNNode node)
	{
		return reactants.get (node);
	}
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.ds.CRNNode#createGraphMl(org.w3c.dom.Document, org.w3c.dom.Element)
	 */
	@Override
	public void createGraphMl (Document graphDocument, Element parent)
	{
		// create core node
		Element element = super.createGraphMlCore (graphDocument, parent);
		
		// plus: we are a reaction!
		Element nsElement = graphDocument.createElement ("data");
		nsElement.setAttribute ("key", "ns");
		nsElement.appendChild (graphDocument.createTextNode ("reaction"));
		element.appendChild (nsElement);
		
		// plus: fast and reversible!?
		if (reversible != null && reversible.length () > 0)
		{
			Element revElement = graphDocument.createElement ("data");
			revElement.setAttribute ("key", "rev");
			revElement.appendChild (graphDocument.createTextNode (reversible));
			element.appendChild (revElement);
		}
		
		if (fast != null && fast.length () > 0)
		{
			Element fastElement = graphDocument.createElement ("data");
			fastElement.setAttribute ("key", "fast");
			fastElement.appendChild (graphDocument.createTextNode (fast));
			element.appendChild (fastElement);
		}
		
		for (CRNEdge e : reactants.values ())
			e.createGraphMl (graphDocument, parent);
		
		for (CRNEdge e : products.values ())
			e.createGraphMl (graphDocument, parent);
		
		for (CRNEdge e : modifiers.values ())
			e.createGraphMl (graphDocument, parent);
	}

	@Override
	public String reportHTML (String cssclass)
	{
		String ret = "<tr><td class='"+cssclass+"'>";
		
		ret += Tools.genTableIdCol (treeA, treeB);
		
		ret += "</td><td class='"+cssclass+"'>";

			ret += Tools.genAttributeHtmlStats (treeA, treeB);
		
		String sub = "";
		if (reactants.size () > 0)
		{
			for (CRNNode reactant : reactants.keySet ())
			{
				sub += reactant.reportHTML (cssclass) + " + ";
			}
			if (sub.length () > 3)
				ret += sub.substring (0, sub.length () - 2);
		}
		else
			ret += "&Oslash;";
		ret += " &rarr; ";
		if (products.size () > 0)
		{
			sub = "";
			for (CRNNode reactant : products.keySet ())
			{
				sub += reactant.reportHTML (cssclass) + " + ";
			}
			if (sub.length () > 3)
				ret += sub.substring (0, sub.length () - 2);
		}
		else
			ret += "&Oslash;";
		if (modifiers.size () > 0)
		{
			ret += "<br />Modifiers: ";
	
			sub = "";
			for (CRNEdge mods: modifiers.values ())
			{
				sub += mods.reportHTML (cssclass) + ", ";
			}
			if (sub.length () > 2)
				ret += sub.substring (0, sub.length () - 2);
		}
		ret += "<br />";
		
		ret += Tools.genMathHtmlStats (kineticLawA, kineticLawB);
		
		return ret + "</td></tr>";
	}
	
}
