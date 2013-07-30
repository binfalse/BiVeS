/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Element;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class CellMLReaction
extends CellMLEntity
implements DiffReporter
{
	
	// The <reaction> element may define a reversible attribute, the value of which indicates whether or not the reaction is reversible. The default value of the reversible attribute is "yes".
	private boolean reversible;
	//The reaction element contains multiple <variable_ref> elements, each of which references a variable that participates in the reaction.
	private Vector<CellMLReactionSubstance> variable_refs;
	private CellMLComponent component;
	
	public CellMLReaction (CellMLModel model, CellMLComponent component, DocumentNode node) throws BivesConsistencyException, BivesCellMLParseException
	{
		super (node, model);
		this.component = component;
		
		if (node.getAttribute ("reversible") == null || !node.getAttribute ("reversible").equals ("no"))
			reversible = true;
		else
			reversible = false;
		
		variable_refs = new Vector<CellMLReactionSubstance> ();
		
		Vector<TreeNode> kids = node.getChildrenWithTag ("variable_ref");
		for (TreeNode kid : kids)
		{
			variable_refs.add (new CellMLReactionSubstance (model, component, (DocumentNode) kid));
		}
	}
	
	public Vector<CellMLReactionSubstance> getSubstances ()
	{
		return variable_refs;
	}
	
	public CellMLComponent getComponent ()
	{
		return component;
	}
	
	public boolean isReversible ()
	{
		return reversible;
	}

	@Override
	public MarkupElement reportMofification (ClearConnectionManager conMgmt,
		DiffReporter docA, DiffReporter docB, MarkupDocument markupDocument)
	{
		CellMLReaction a = (CellMLReaction) docA;
		CellMLReaction b = (CellMLReaction) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return null;
		
		MarkupElement me = new MarkupElement ("Reaction");

		Tools.genAttributeHtmlStats (a.getDocumentNode (), b.getDocumentNode (), me, markupDocument);

		HashMap<String, Integer> inputs = new HashMap<String, Integer> ();
		HashMap<String, Integer> outputs = new HashMap<String, Integer> ();
		HashMap<String, Integer> modifiersStim = new HashMap<String, Integer> ();
		HashMap<String, Integer> modifiersInh = new HashMap<String, Integer> ();
		HashMap<String, Integer> modifiers = new HashMap<String, Integer> ();
		
		Vector<CellMLReactionSubstance> varsA = a.getSubstances ();
		Vector<CellMLReactionSubstance> varsB = b.getSubstances ();
		
		for (CellMLReactionSubstance sub : varsA)
		{
			for (CellMLReactionSubstance.Role role: sub.getRoles ())
			{
				String name = Tools.prettyDouble (role.stoichiometry, 1);
				if (name.length () > 0)
					name += " ";
				name += sub.getVariable ().getName ();
				switch (role.role)
				{
					case CellMLReactionSubstance.ROLE_REACTANT:
						inputs.put (name, -1);
						break;
					case CellMLReactionSubstance.ROLE_PRODUCT:
						outputs.put (name, -1);
						break;
					case CellMLReactionSubstance.ROLE_MODIFIER:
						modifiers.put (name, -1);
						break;
					case CellMLReactionSubstance.ROLE_ACTIVATOR:
					case CellMLReactionSubstance.ROLE_CATALYST:
						modifiersStim.put (name, -1);
						break;
					case CellMLReactionSubstance.ROLE_INHIBITOR:
						modifiersInh.put (name, -1);
						break;
					case CellMLReactionSubstance.ROLE_RATE:
						continue;
				}
			}
		}
		
		for (CellMLReactionSubstance sub : varsB)
		{
			for (CellMLReactionSubstance.Role role: sub.getRoles ())
			{
				String name = Tools.prettyDouble (role.stoichiometry, 1);//role.getStoichiometry ();
				if (name.length () > 0)
					name += " ";
				name += sub.getVariable ().getName ();
//				String name = role.getStoichiometry () + " " + sub.getVariable ().getName ();
				switch (role.role)
				{
					case CellMLReactionSubstance.ROLE_REACTANT:
						if (inputs.get (name) == null)
							inputs.put (name, 1);
						else
							inputs.put (name, 0);
						break;
					case CellMLReactionSubstance.ROLE_PRODUCT:
						if (outputs.get (name) == null)
							outputs.put (name, 1);
						else
							outputs.put (name, 0);
						break;
					case CellMLReactionSubstance.ROLE_MODIFIER:
						if (modifiers.get (name) == null)
							modifiers.put (name, 1);
						else
							modifiers.put (name, 0);
						break;
					case CellMLReactionSubstance.ROLE_ACTIVATOR:
					case CellMLReactionSubstance.ROLE_CATALYST:
						if (modifiersStim.get (name) == null)
							modifiersStim.put (name, 1);
						else
							modifiersStim.put (name, 0);
						break;
					case CellMLReactionSubstance.ROLE_INHIBITOR:
						if (modifiersInh.get (name) == null)
							modifiersInh.put (name, 1);
						else
							modifiersInh.put (name, 0);
						break;
					case CellMLReactionSubstance.ROLE_RATE:
						continue;
				}
			}
		}

		String sub = "", ret = "";

		sub = expandSubstances ("", "", inputs, " + ", markupDocument);
		if (sub.length () > 0)
			ret += sub + " "+markupDocument.rightArrow ()+" ";
		else
			ret += "&Oslash; "+markupDocument.rightArrow ()+" ";

		sub = expandSubstances ("", "", outputs, " + ", markupDocument);
		if (sub.length () > 0)
			ret += sub;
		else
			ret += "&Oslash;";
		me.addValue (ret);

		sub = expandSubstances ("", " (unknown)", modifiers, "; ", markupDocument);
		sub = expandSubstances (sub, " (stimulator)", modifiersStim, "; ", markupDocument);
		sub = expandSubstances (sub, " (inhibitor)", modifiersInh, "; ", markupDocument);
		if (sub.length () > 0)
			me.addValue ("Modifiers: " + sub);
		
		return me;
	}
	
	private String expandSubstances (String sub, String supp, HashMap<String, Integer> map, String collapse, MarkupDocument markupDocument)
	{
		for (String subst : map.keySet ())
		{
			if (sub.length () > 0)
				sub += collapse;
			switch (map.get (subst))
			{
				case -1:
					sub += markupDocument.delete (subst + supp);
					break;
				case 1:
					sub += markupDocument.insert (subst + supp);
					break;
				default:
					sub += subst + supp;
					break;
			}
		}
		return sub;
	}

	@Override
	public MarkupElement reportInsert (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement ("Reactioon: " + markupDocument.insert ("reaction"));
		me.addValue (markupDocument.insert ("inserted"));
		return me;
	}

	@Override
	public MarkupElement reportDelete (MarkupDocument markupDocument)
	{
		MarkupElement me = new MarkupElement ("Reactioon: " + markupDocument.delete ("reaction"));
		me.addValue (markupDocument.delete ("deleted"));
		return me;
	}
}
