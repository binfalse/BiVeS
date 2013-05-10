/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.Vector;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;


/**
 * @author Martin Scharm
 *
 */
public class SBMLReaction
	extends SBMLSbase
{
	private String id;
	private String name; //optional
	private boolean reversible;
	private boolean fast;
	private SBMLCompartment compartment; //optional

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
	
	public String getID ()
	{
		return id;
	}
	
	public String getName ()
	{
		return name;
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
}
