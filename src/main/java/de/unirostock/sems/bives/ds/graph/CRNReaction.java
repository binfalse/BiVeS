/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import de.unirostock.sems.bives.ds.SBOTerm;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class CRNReaction
{
	private int id;
	private String labelA, labelB;
	private DocumentNode docA, docB;
	private CRN crn;
	private boolean singleDoc;
	private boolean reversible;
	private CRNCompartment compartmentA, compartmentB;

	private HashMap<CRNSubstance, SubstanceRef> in;
	private HashMap<CRNSubstance, SubstanceRef> out;
	private HashMap<CRNSubstance, ModifierRef> mod;
	
	public String getSBO ()
	{
		String a = docA.getAttribute ("sboTerm");
		String b = docA.getAttribute ("sboTerm");
		if (a == null || b == null || !a.equals (b))
			return "";
		return a;
	}
	
	public boolean isReversible ()
	{
		return reversible;
	}
	
	public class SubstanceRef
	{
		public CRNSubstance subst;
		public boolean a, b;
		public SBOTerm modTermA, modTermB;
		public SubstanceRef (CRNSubstance subst, boolean a, boolean b, SBOTerm modTermA, SBOTerm modTermB)
		{
			this.subst = subst;
			this.a = a;
			this.b = b;
			this.modTermA = modTermA;
			this.modTermB = modTermB;
		}
		
		public String getSBO ()
		{
			if (modTermA == null && modTermB == null)
				return "";
			if (modTermA == null)
				return modTermB.getSBOTerm ();
			return modTermA.getSBOTerm ();
		}
		
		public int getModification ()
		{
			if (singleDoc)
				return CRN.UNMODIFIED;
			
			if (a && b)
				return CRN.UNMODIFIED;
			if (a)
				return CRN.DELETE;
			return CRN.INSERT;
		}
	}
	
	public class ModifierRef
	{
		public CRNSubstance subst;
		public boolean a, b;
		public SBOTerm modTermA, modTermB;
		public ModifierRef (CRNSubstance subst, boolean a, boolean b, SBOTerm modTermA, SBOTerm modTermB)
		{
			this.subst = subst;
			this.a = a;
			this.b = b;
			this.modTermA = modTermA;
			this.modTermB = modTermB;
		}
		
		public String getSBO ()
		{
			if (modTermA == null && modTermB == null)
				return "";
			if (modTermA == null)
				return modTermB.getSBOTerm ();
			return modTermA.getSBOTerm ();
		}
		
		public String getSBOA ()
		{
			if (modTermA == null)
				return "";
			return modTermA.getSBOTerm ();
		}
		
		public String getSBOB ()
		{
			if (modTermB == null)
				return "";
			return modTermB.getSBOTerm ();
		}
		
		public String getModTerm ()
		{
			if (modTermA == null && modTermB == null)
				return SBOTerm.MOD_UNKNOWN;
			if (modTermA == null)
				return modTermB.resolvModifier ();
			return modTermA.resolvModifier ();
		}
		
		public String getModTermB ()
		{
			if (modTermB == null)
				return SBOTerm.MOD_UNKNOWN;
			return modTermB.resolvModifier ();
		}
		
		public String getModTermA ()
		{
			if (modTermA == null)
				return SBOTerm.MOD_UNKNOWN;
			return modTermA.resolvModifier ();
		}
		
		public int getModification ()
		{
			if (singleDoc)
				return CRN.UNMODIFIED;
			
			if (a && b)
			{
				if (modTermA == null && modTermB == null)
					return CRN.UNMODIFIED;
				if (modTermA != null && modTermB != null && modTermA.resolvModifier ().equals (modTermB.resolvModifier ()))
						return CRN.UNMODIFIED;
				return CRN.MODIFIED;
			}
			if (a)
				return CRN.DELETE;
			return CRN.INSERT;
		}
		
	}
	
	public CRNReaction (CRN crn, String labelA, String labelB, DocumentNode docA, DocumentNode docB, boolean reversible)
	{
		this.crn = crn;
		this.id = crn.getNextReactionID ();
		this.labelA = labelA;
		this.labelB = labelB;
		this.docA = docA;
		this.docB = docB;
		in = new HashMap<CRNSubstance, SubstanceRef> ();
		out = new HashMap<CRNSubstance, SubstanceRef> ();
		mod = new HashMap<CRNSubstance, ModifierRef> ();
		singleDoc = false;
		this.reversible = reversible;
	}
	
	public void setCompartmentA (CRNCompartment compartment)
	{
		this.compartmentA = compartment;
	}
	
	public void setCompartmentB (CRNCompartment compartment)
	{
		this.compartmentB = compartment;
	}
	
	public CRNCompartment getCompartment ()
	{
		if (compartmentA != null && compartmentA == compartmentB)
				return compartmentA;
		
		boolean sameCompartment = true;
		CRNCompartment compartment = null;
		
		if (sameCompartment)
			for (CRNSubstance sub : in.keySet ())
			{
				if (compartment == null)
					compartment = sub.getCompartment ();
				else
				{
					if (compartment != sub.getCompartment ())
					{
						sameCompartment = false;
					}
				}
			}
		
		if (sameCompartment)
			for (CRNSubstance sub : out.keySet ())
			{
				if (compartment == null)
					compartment = sub.getCompartment ();
				else
				{
					if (compartment != sub.getCompartment ())
					{
						sameCompartment = false;
					}
				}
			}
		
		if (sameCompartment)
			for (CRNSubstance sub : mod.keySet ())
			{
				if (compartment == null)
					compartment = sub.getCompartment ();
				else
				{
					if (compartment != sub.getCompartment ())
					{
						sameCompartment = false;
					}
				}
			}
		
		if (sameCompartment)
			return compartment;
		return null;
	}
	
	public void setDocA (DocumentNode docA)
	{
		this.docA = docA;
	}
	
	public void setDocB (DocumentNode docB)
	{
		this.docB = docB;
	}
	
	public void setLabelA (String labelA)
	{
		this.labelA = labelA;
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
	
	public void addInputA (CRNSubstance subst, SBOTerm sbo)
	{
		SubstanceRef r = in.get (subst);
		if (r == null)
			in.put (subst, new SubstanceRef (subst, true, false, sbo, null));
		else
		{
			r.a = true;
		}
	}
	
	public void addOutputA (CRNSubstance subst, SBOTerm sbo)
	{
		SubstanceRef r = out.get (subst);
		if (r == null)
			out.put (subst, new SubstanceRef (subst, true, false, sbo, null));
		else
		{
			r.a = true;
		}
	}
	
	public void addModA (CRNSubstance subst, SBOTerm sbo)
	{
		ModifierRef r = mod.get (subst);
		if (r == null)
			mod.put (subst, new ModifierRef (subst, true, false, sbo, null));
		else
		{
			r.a = true;
			r.modTermA = sbo;
		}
	}
	
	public void addInputB (CRNSubstance subst, SBOTerm sbo)
	{
		SubstanceRef r = in.get (subst);
		if (r == null)
			in.put (subst, new SubstanceRef (subst, false, true, null, sbo));
		else
		{
			r.b = true;
		}
	}
	
	public void addOutputB (CRNSubstance subst, SBOTerm sbo)
	{
		SubstanceRef r = out.get (subst);
		if (r == null)
			out.put (subst, new SubstanceRef (subst, false, true, null, sbo));
		else
		{
			r.b = true;
		}
	}
	
	public void addModB (CRNSubstance subst, SBOTerm sbo)
	{
		ModifierRef r = mod.get (subst);
		if (r == null)
			mod.put (subst, new ModifierRef (subst, false, true, null, sbo));
		else
		{
			r.b = true;
			r.modTermB = sbo;
		}
	}
	
	public String getId ()
	{
		return "r" + id;
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
		
		if (docA == null)
			return CRN.INSERT;
		if (docB == null)
			return CRN.DELETE;
		if (docA.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED)|| docB.hasModification (TreeNode.MODIFIED|TreeNode.SUB_MODIFIED))
			return CRN.MODIFIED;
		return CRN.UNMODIFIED;
	}
	
	public Collection<SubstanceRef> getInputs ()
	{
		return in.values ();
	}
	
	public Collection<SubstanceRef> getOutputs ()
	{
		return out.values ();
	}
	
	public Collection<ModifierRef> getModifiers ()
	{
		return mod.values ();
	}

	public void setSingleDocument ()
	{
		singleDoc = true;
	}
}
