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

	private HashMap<CRNSubstance, SubstanceRef> in;
	private HashMap<CRNSubstance, SubstanceRef> out;
	private HashMap<CRNSubstance, ModifierRef> mod;
	
	public class SubstanceRef
	{
		public CRNSubstance subst;
		public boolean a, b;
		public SubstanceRef (CRNSubstance subst, boolean a, boolean b)
		{
			this.subst = subst;
			this.a = a;
			this.b = b;
		}
		
		public int getModification ()
		{
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
		public SBOTerm sboA, sboB;
		public ModifierRef (CRNSubstance subst, boolean a, boolean b, SBOTerm sboA, SBOTerm sboB)
		{
			this.subst = subst;
			this.a = a;
			this.b = b;
			this.sboA = sboA;
			this.sboB = sboB;
		}
		
		public String getSboTerm ()
		{
			if (sboA == null && sboB == null)
				return SBOTerm.MOD_UNKNOWN;
			if (sboA == null)
				return sboB.resolvModifier ();
			return sboA.resolvModifier ();
		}
		
		public String getSboTermB ()
		{
			return sboB.resolvModifier ();
		}
		
		public String getSboTermA ()
		{
			return sboA.resolvModifier ();
		}
		
		public int getModification ()
		{
			if (a && b)
			{
				if (sboA == null && sboB == null)
					return CRN.UNMODIFIED;
				if (sboA != null && sboB != null && sboA.resolvModifier ().equals (sboB.resolvModifier ()))
						return CRN.UNMODIFIED;
				return CRN.MODIFIED;
			}
			if (a)
				return CRN.DELETE;
			return CRN.INSERT;
		}
		
	}
	
	public CRNReaction (CRN crn, String labelA, String labelB, DocumentNode docA, DocumentNode docB)
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
	
	public void addInputA (CRNSubstance subst)
	{
		SubstanceRef r = in.get (subst);
		if (r == null)
			in.put (subst, new SubstanceRef (subst, true, false));
		else
		{
			r.a = true;
		}
	}
	
	public void addOutputA (CRNSubstance subst)
	{
		SubstanceRef r = out.get (subst);
		if (r == null)
			out.put (subst, new SubstanceRef (subst, true, false));
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
			r.sboA = sbo;
		}
	}
	
	public void addInputB (CRNSubstance subst)
	{
		SubstanceRef r = in.get (subst);
		if (r == null)
			in.put (subst, new SubstanceRef (subst, false, true));
		else
		{
			r.b = true;
		}
	}
	
	public void addOutputB (CRNSubstance subst)
	{
		SubstanceRef r = out.get (subst);
		if (r == null)
			out.put (subst, new SubstanceRef (subst, false, true));
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
			r.sboB = sbo;
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
}
