/**
 * 
 */
package de.unirostock.sems.bives.ds;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;


/**
 * @author Martin Scharm
 *
 */
public class ModelType
{
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_SBML = 1;
	public static final int TYPE_CELLML = 2;
	
	public static int getModelType (TreeDocument doc)
	{
		DocumentNode root = doc.getRoot ();
		String ns = root.getAttribute ("xmlns");
		if (root.getTagName ().equals ("sbml") && ns != null && ns.toLowerCase ().contains ("sbml"))
			return TYPE_SBML;
		if (root.getTagName ().equals ("model") && ns != null && ns.toLowerCase ().contains ("cellml"))
			return TYPE_CELLML;
		return TYPE_UNKNOWN;
	}
}
