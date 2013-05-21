/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLReaction
	extends SBMLGenericIdNameObject
	implements SBMLDiffReporter
{
	private boolean reversible;
	private boolean fast;
	private SBMLCompartment compartment; //optional

	private SBMLListOf listOfReactantsNode;
	private SBMLListOf listOfProductsNode;
	private SBMLListOf listOfModifiersNode;
	private Vector<SBMLSpeciesReference> listOfReactants;
	private Vector<SBMLSpeciesReference> listOfProducts;
	private Vector<SBMLSimpleSpeciesReference> listOfModifiers;
	private SBMLKineticLaw kineticLaw;
	
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLReaction (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		id = documentNode.getAttribute ("id");
		if (id == null || id.length () < 1)
			throw new BivesSBMLParseException ("parameter "+id+" doesn't provide a valid id.");
		
		name = documentNode.getAttribute ("name");
		
		if (documentNode.getAttribute ("reversible") != null)
		{
			try
			{
				reversible = Boolean.parseBoolean (documentNode.getAttribute ("reversible"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("reversible attr of reaction "+id+" of unexpected format: " + documentNode.getAttribute ("reversible"));
			}
		}
		else
			reversible = true; // level <= 2
		
		if (documentNode.getAttribute ("fast") != null)
		{
			try
			{
				fast = Boolean.parseBoolean (documentNode.getAttribute ("fast"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("fast attr of reaction "+id+" of unexpected format: " + documentNode.getAttribute ("fast"));
			}
		}
		else
			fast = false; // level <= 2

		String tmp = documentNode.getAttribute ("compartment");
		if (tmp != null)
		{
			compartment = sbmlModel.getCompartment (tmp);
			if (compartment == null)
				throw new BivesSBMLParseException ("no valid compartment for species "+id+" defined: " + tmp);
		}
		
		listOfReactants = new Vector<SBMLSpeciesReference> ();
		listOfProducts = new Vector<SBMLSpeciesReference> ();
		listOfModifiers = new Vector<SBMLSimpleSpeciesReference> ();
		
		Vector<TreeNode> nodes = documentNode.getChildrenWithTag ("listOfReactants");
		for (int i = 0; i < nodes.size (); i++)
		{
			listOfReactantsNode = new SBMLListOf ((DocumentNode) nodes.elementAt (i), sbmlModel);
			Vector<TreeNode> subnodes = ((DocumentNode) nodes.elementAt (i)).getChildrenWithTag ("speciesReference");
			for (int j = 0; j < subnodes.size (); j++)
			{
				SBMLSpeciesReference sr = new SBMLSpeciesReference ((DocumentNode) subnodes.elementAt (j), sbmlModel);
				listOfReactants.add (sr);
			}
		}
		
		nodes = documentNode.getChildrenWithTag ("listOfProducts");
		for (int i = 0; i < nodes.size (); i++)
		{
			listOfProductsNode = new SBMLListOf ((DocumentNode) nodes.elementAt (i), sbmlModel);
			Vector<TreeNode> subnodes = ((DocumentNode) nodes.elementAt (i)).getChildrenWithTag ("speciesReference");
			for (int j = 0; j < subnodes.size (); j++)
			{
				SBMLSpeciesReference sr = new SBMLSpeciesReference ((DocumentNode) subnodes.elementAt (j), sbmlModel);
				listOfProducts.add (sr);
			}
		}
		
		nodes = documentNode.getChildrenWithTag ("listOfModifiers");
		for (int i = 0; i < nodes.size (); i++)
		{
			listOfModifiersNode = new SBMLListOf ((DocumentNode) nodes.elementAt (i), sbmlModel);
			Vector<TreeNode> subnodes = ((DocumentNode) nodes.elementAt (i)).getChildrenWithTag ("modifierSpeciesReference");
			for (int j = 0; j < subnodes.size (); j++)
			{
				SBMLSimpleSpeciesReference sr = new SBMLSimpleSpeciesReference ((DocumentNode) subnodes.elementAt (j), sbmlModel);
				listOfModifiers.add (sr);
			}
		}

		nodes = documentNode.getChildrenWithTag ("kineticLaw");
		if (nodes.size () > 1)
			throw new BivesSBMLParseException ("reaction "+id+" has "+nodes.size ()+" kinetic law elements. (expected not more tha one element)");
		if (nodes.size () == 1)
			kineticLaw = new SBMLKineticLaw ((DocumentNode) nodes.elementAt (0), sbmlModel);
	}
	
	public boolean isReversible ()
	{
		return reversible;
	}
	
	public boolean isFast ()
	{
		return fast;
	}
	
	public SBMLKineticLaw getKineticLaw ()
	{
		return kineticLaw;
	}
	
	public SBMLListOf getListOfReactantsNode ()
	{
		return listOfReactantsNode;
	}
	
	public SBMLListOf getListOfProductsNode ()
	{
		return listOfProductsNode;
	}
	
	public SBMLListOf getListOfModifiersNode ()
	{
		return listOfModifiersNode;
	}
	
	public Vector<SBMLSpeciesReference> getReactants ()
	{
		return listOfReactants;
	}
	
	public Vector<SBMLSpeciesReference> getProducts ()
	{
		return listOfProducts;
	}
	
	public Vector<SBMLSimpleSpeciesReference> getModifiers ()
	{
		return listOfModifiers;
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLReaction a = (SBMLReaction) docA;
		SBMLReaction b = (SBMLReaction) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";
		
		String idA = a.getNameAndId (), idB = b.getNameAndId ();
		String ret = "<tr><td>";
		if (idA.equals (idB))
			ret += idA;
		else
			ret += "<span class='"+CLASS_DELETED+"'>" + idA + "</span> &rarr; <span class='"+CLASS_DELETED+"'>" + idB + "</span> ";
		ret += "</td><td>";
		
		ret += Tools.genAttributeHtmlStats (a.documentNode, b.documentNode);

		Vector<SBMLSpeciesReference> aS = a.listOfReactants;
		Vector<SBMLSpeciesReference> bS = b.listOfReactants;
		String sub = "";
		for (SBMLSpeciesReference sr : aS)
		{
			if (sub.length () > 0)
				sub += " + ";
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
				sub += sr.reportDelete ();
			else
			{
				Connection c = conMgmt.getConnectionForNode (sr.getDocumentNode ());
				SBMLSpeciesReference partner = (SBMLSpeciesReference) b.sbmlModel.getFromNode (c.getPartnerOf (sr.getDocumentNode ()));
				sub += sr.reportMofification (conMgmt, sr, partner);
			}
		}
		for (SBMLSpeciesReference sr : bS)
		{
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
			{
				if (sub.length () > 0)
					sub += " + ";
				sub += sr.reportInsert ();
			}
		}
		if (sub.length () > 0)
			ret += sub + " &rarr; ";
		else
			ret += "&Oslash; &rarr; ";

		aS = a.listOfProducts;
		bS = b.listOfProducts;
		sub = "";
		for (SBMLSpeciesReference sr : aS)
		{
			if (sub.length () > 0)
				sub += " + ";
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
			{
				//System.out.println ("reporting delete for " + sr.getDocumentNode ().getXPath ());
				sub += sr.reportDelete ();
			}
			else
			{
				//System.out.println ("reporting mod for " + sr.getDocumentNode ().getXPath ());
				Connection c = conMgmt.getConnectionForNode (sr.getDocumentNode ());
				SBMLSpeciesReference partner = (SBMLSpeciesReference) b.sbmlModel.getFromNode (c.getPartnerOf (sr.getDocumentNode ()));
				sub += sr.reportMofification (conMgmt, sr, partner);
			}
		}
		for (SBMLSpeciesReference sr : bS)
		{
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
			{
				//System.out.println ("reporting ins for " + sr.getDocumentNode ().getXPath ());
				if (sub.length () > 0)
					sub += " + ";
				sub += sr.reportInsert ();
			}
		}
		if (sub.length () > 0)
			ret += sub;
		else
			ret += "&Oslash;";
		
		ret += "<br/>";
		

		Vector<SBMLSimpleSpeciesReference> aM = a.listOfModifiers;
		Vector<SBMLSimpleSpeciesReference> bM = b.listOfModifiers;
		sub = "";
		for (SBMLSimpleSpeciesReference sr : aM)
		{
			if (sub.length () > 0)
				sub += "; ";
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
				sub += sr.reportDelete ();
			else
			{
				Connection c = conMgmt.getConnectionForNode (sr.getDocumentNode ());
				SBMLSimpleSpeciesReference partner = (SBMLSimpleSpeciesReference) b.sbmlModel.getFromNode (c.getPartnerOf (sr.getDocumentNode ()));
				sub += sr.reportMofification (conMgmt, sr, partner);
			}
		}
		for (SBMLSimpleSpeciesReference sr : bM)
		{
			if (sub.length () > 0)
				sub += "; ";
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
				sub += sr.reportInsert ();
		}
		if (sub.length () > 0)
			ret += "Modifiers: " + sub + "<br/>";
		
		if (a.kineticLaw != null && b.kineticLaw != null)
			ret += "<strong>Kinetic Law:</strong><br/>" + a.kineticLaw.reportMofification (conMgmt, a.kineticLaw, b.kineticLaw);
		else if (a.kineticLaw != null)
			ret += "<strong>Kinetic Law:</strong><br/>" + a.kineticLaw.reportDelete ();
		else if (b.kineticLaw != null)
			ret += "<strong>Kinetic Law:</strong><br/>" + b.kineticLaw.reportInsert ();
		
		return ret + "</td></tr>";
	}

	@Override
	public String reportInsert ()
	{
		return "<tr><td><span class='"+CLASS_INSERTED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_INSERTED+"'>inserted</span></td></tr>";
	}

	@Override
	public String reportDelete ()
	{
		return "<tr><td><span class='"+CLASS_DELETED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_DELETED+"'>deleted</span></td></tr>";
	}
}
