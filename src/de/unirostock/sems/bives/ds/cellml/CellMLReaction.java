/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.util.Vector;

import org.w3c.dom.Element;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.CellMLReadException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLReaction
extends CellMLEntity
{
	
	// The <reaction> element may define a reversible attribute, the value of which indicates whether or not the reaction is reversible. The default value of the reversible attribute is "yes".
	private boolean reversible;
	//The reaction element contains multiple <variable_ref> elements, each of which references a variable that participates in the reaction.
	private Vector<CellMLReactionSubstance> variable_refs;
	private CellMLComponent component;
	
	public CellMLReaction (CellMLModel model, CellMLComponent component, DocumentNode node) throws BivesConsistencyException, CellMLReadException
	{
		super (node, model);
		this.component = component;
		
		if (node.getAttribute ("reversible") == null || !node.getAttribute ("reversible").equals ("no"))
			reversible = true;
		else
			reversible = false;
		
		// TODO: foreach variable_ref
		Vector<TreeNode> kids = node.getChildrenWithTag ("variable_ref");
		for (TreeNode kid : kids)
		{
			variable_refs.add (new CellMLReactionSubstance (component, (DocumentNode) kid));
		}
	}
}
