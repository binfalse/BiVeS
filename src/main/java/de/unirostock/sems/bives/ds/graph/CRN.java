/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class CRN
{
	public static final int UNMODIFIED = 0;
	public static final int INSERT = 1;
	public static final int DELETE = -1;
	public static final int MODIFIED = 2;
	
	
	private int reactionID;
	private int substanceID;
	private int compartmentID;
	private HashMap<TreeNode, CRNReaction> crnR;
	private HashMap<TreeNode, CRNSubstance> crnS;
	private HashMap<TreeNode, CRNCompartment> crnC;
	private HashMap<CRNCompartment, CRNCompartment> ucrnC;
	private HashMap<CRNReaction, CRNReaction> ucrnR;
	private HashMap<CRNSubstance, CRNSubstance> ucrnS;
	
	public CRN ()
	{
		reactionID = 0;
		substanceID = 0;
		compartmentID = 0;
		crnR = new HashMap<TreeNode, CRNReaction> ();
		crnS = new HashMap<TreeNode, CRNSubstance> ();
		crnC = new HashMap<TreeNode, CRNCompartment> ();
		ucrnR = new HashMap<CRNReaction, CRNReaction> ();
		ucrnS = new HashMap<CRNSubstance, CRNSubstance> ();
		ucrnC = new HashMap<CRNCompartment, CRNCompartment> ();
	}
	
	public static String modToString (int modification)
	{
		switch (modification)
		{
			case INSERT:
				return "inserted";
			case DELETE:
				return "deleted";
			case MODIFIED:
				return "modified";
		}
		return "unmodified";
	}
	
	public Collection<CRNSubstance> getSubstances ()
	{
		return ucrnS.values ();
	}
	
	public Collection<CRNReaction> getReactions ()
	{
		return ucrnR.values ();
	}
	
	public Collection<CRNCompartment> getCompartments ()
	{
		return ucrnC.values ();
	}
	
	public int getNextSubstanceID ()
	{
		return ++substanceID;
	}
	
	public int getNextCompartmentID ()
	{
		return ++compartmentID;
	}
	
	public int getNextReactionID ()
	{
		return ++reactionID;
	}
	

	
	public void setReaction (TreeNode node, CRNReaction react)
	{
		crnR.put (node, react);
		ucrnR.put (react, react);
	}
	
	public void setSubstance (TreeNode node, CRNSubstance subst)
	{
		crnS.put (node, subst);
		ucrnS.put (subst, subst);
	}
	
	public void setCompartment (TreeNode node, CRNCompartment compartment)
	{
		crnC.put (node, compartment);
		ucrnC.put (compartment, compartment);
	}
	
	public CRNSubstance getSubstance (TreeNode node)
	{
		//for (TreeNode tn : crnS.keySet ())
			//System.out.println ("have: " + tn.getXPath ());
		return crnS.get (node);
	}
	
	public CRNReaction getReaction (TreeNode node)
	{
		return crnR.get (node);
	}
	
	public CRNCompartment getCompartment (TreeNode node)
	{
		return crnC.get (node);
	}

	public void setSingleDocument ()
	{
		for (CRNReaction r : crnR.values ())
			r.setSingleDocument ();
		for (CRNSubstance s : crnS.values ())
			s.setSingleDocument ();
		for (CRNCompartment c : crnC.values ())
			c.setSingleDocument ();
	}
}
