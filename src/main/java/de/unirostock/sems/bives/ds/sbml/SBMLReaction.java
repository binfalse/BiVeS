/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.ds.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLReaction
	extends SBMLGenericIdNameObject
	implements DiffReporter
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
	
	public SBMLCompartment getCompartment ()
	{
		return compartment;
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
	public MarkupElement reportMofification (ClearConnectionManager conMgmt, DiffReporter docA, DiffReporter docB, MarkupDocument markupDocument)
	{
		SBMLReaction a = (SBMLReaction) docA;
		SBMLReaction b = (SBMLReaction) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return null;
		
		String idA = a.getNameAndId (), idB = b.getNameAndId ();
		MarkupElement me = null;
		if (idA.equals (idB))
			me = new MarkupElement (idA);
		else
			me = new MarkupElement (markupDocument.delete (idA) + " "+markupDocument.rightArrow ()+" " + markupDocument.insert (idB));
		
		Tools.genAttributeHtmlStats (a.documentNode, b.documentNode, me, markupDocument);

		Vector<SBMLSpeciesReference> aS = a.listOfReactants;
		Vector<SBMLSpeciesReference> bS = b.listOfReactants;
		String sub = "", ret = "";
		for (SBMLSpeciesReference sr : aS)
		{
			if (sub.length () > 0)
				sub += " + ";
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
				sub += sr.reportDelete (markupDocument);
			else
			{
				Connection c = conMgmt.getConnectionForNode (sr.getDocumentNode ());
				SBMLSpeciesReference partner = (SBMLSpeciesReference) b.sbmlModel.getFromNode (c.getPartnerOf (sr.getDocumentNode ()));
				sub += sr.reportMofification (conMgmt, sr, partner, markupDocument);
			}
		}
		for (SBMLSpeciesReference sr : bS)
		{
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
			{
				if (sub.length () > 0)
					sub += " + ";
				sub += sr.reportInsert (markupDocument);
			}
		}
		if (sub.length () > 0)
			ret += sub + " "+markupDocument.rightArrow ()+" ";
		else
			ret += "&Oslash; "+markupDocument.rightArrow ()+" ";

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
				sub += sr.reportDelete (markupDocument);
			}
			else
			{
				//System.out.println ("reporting mod for " + sr.getDocumentNode ().getXPath ());
				Connection c = conMgmt.getConnectionForNode (sr.getDocumentNode ());
				SBMLSpeciesReference partner = (SBMLSpeciesReference) b.sbmlModel.getFromNode (c.getPartnerOf (sr.getDocumentNode ()));
				sub += sr.reportMofification (conMgmt, sr, partner, markupDocument);
			}
		}
		for (SBMLSpeciesReference sr : bS)
		{
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
			{
				//System.out.println ("reporting ins for " + sr.getDocumentNode ().getXPath ());
				if (sub.length () > 0)
					sub += " + ";
				sub += sr.reportInsert (markupDocument);
			}
		}
		if (sub.length () > 0)
			ret += sub;
		else
			ret += "&Oslash;";
		
		me.addValue (ret);
		

		Vector<SBMLSimpleSpeciesReference> aM = a.listOfModifiers;
		Vector<SBMLSimpleSpeciesReference> bM = b.listOfModifiers;
		sub = "";
		for (SBMLSimpleSpeciesReference sr : aM)
		{
			if (sub.length () > 0)
				sub += "; ";
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
				sub += sr.reportDelete (markupDocument);
			else
			{
				Connection c = conMgmt.getConnectionForNode (sr.getDocumentNode ());
				SBMLSimpleSpeciesReference partner = (SBMLSimpleSpeciesReference) b.sbmlModel.getFromNode (c.getPartnerOf (sr.getDocumentNode ()));
				sub += sr.reportMofification (conMgmt, sr, partner, markupDocument);
			}
		}
		for (SBMLSimpleSpeciesReference sr : bM)
		{
			if (sub.length () > 0)
				sub += "; ";
			if (conMgmt.getConnectionForNode (sr.getDocumentNode ()) == null)
				sub += sr.reportInsert (markupDocument);
		}
		if (sub.length () > 0)
			me.addValue ("Modifiers: " + sub);
		
		MarkupElement me2 = new MarkupElement ("Kinetic Law");
		if (a.kineticLaw != null && b.kineticLaw != null)
		{
			a.kineticLaw.reportMofification (conMgmt, a.kineticLaw, b.kineticLaw, me2, markupDocument);
			if (me2.getValues ().size () > 0)
				me.addSubElements (me2);
		}
			//me.addValue (markupDocument.highlight ("Kinetic Law:") + a.kineticLaw.reportMofification (conMgmt, a.kineticLaw, b.kineticLaw, markupDocument));
		else if (a.kineticLaw != null)
		{
			a.kineticLaw.reportDelete (me2, markupDocument);
			me.addSubElements (me2);
		}
			//me.addValue (markupDocument.highlight ("Kinetic Law:") + a.kineticLaw.reportDelete (markupDocument));
		else if (b.kineticLaw != null)
		{
			b.kineticLaw.reportInsert (me2, markupDocument);
			me.addSubElements (me2);
		}
			//me.addValue (markupDocument.highlight ("Kinetic Law:") + b.kineticLaw.reportInsert (markupDocument));
		
		return me;
	}

	@Override
	public MarkupElement reportInsert (MarkupDocument markupDocument)
	{

		
		
		MarkupElement me = new MarkupElement (markupDocument.insert (getNameAndId ()));
		report (markupDocument, me, true);
		return me;
	}

	@Override
	public MarkupElement reportDelete (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement (markupDocument.delete (getNameAndId ()));
		report (markupDocument, me, false);
		return me;
	}
	
	public void report (MarkupDocument markupDocument, MarkupElement me, boolean insert)
	{
		
		StringBuilder ret = new StringBuilder ();
		StringBuilder sub = new StringBuilder ();
		for (SBMLSpeciesReference sr : listOfReactants)
		{
			if (sub.length () > 0)
				sub.append (" + ");
			sub.append (sr.report ());
		}

		if (sub.length () > 0)
			ret.append (sub).append (" ");
		else
			ret.append ("&Oslash; ");
		ret.append (markupDocument.rightArrow ()).append (" ");
		

		sub = new StringBuilder ();
		for (SBMLSpeciesReference sr : listOfProducts)
		{
			if (sub.length () > 0)
				sub.append (" + ");
			sub.append (sr.report ());
		}

		if (sub.length () > 0)
			ret.append (sub);
		else
			ret.append ("&Oslash;");
		
		if (insert)
			me.addValue (markupDocument.insert (ret.toString ()));
		else
			me.addValue (markupDocument.delete (ret.toString ()));
		
		sub = new StringBuilder ();
		for (SBMLSimpleSpeciesReference sr : listOfModifiers)
		{
			if (sub.length () > 0)
				sub.append ("; ");
			sub.append (sr.report ());
		}
		
		if (sub.length () > 0)
		{
			if (insert)
				me.addValue (markupDocument.insert ("Modifiers: " + sub.toString ()));
			else
				me.addValue (markupDocument.delete ("Modifiers: " + sub.toString ()));
		}
	}
}
