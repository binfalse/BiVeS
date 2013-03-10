/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class CRNSpecies
	extends CRNNode
{
	private final static Logger LOGGER = Logger.getLogger(CRNSpecies.class.getName());
	
	public CRNSpecies (String nameA, String nameB, int modification, DocumentNode treeA, DocumentNode treeB)
	{
		
		super ("s" + ++maxSpecies, nameA, nameB, modification, treeA, treeB);
	}

	@Override
	public void createGraphMl (Document graphDocument, Element parent)
	{
		// create core node
		Element element = super.createGraphMlCore (graphDocument, parent);
		
		// plus: we are a species!
		Element nsElement = graphDocument.createElement ("data");
		nsElement.setAttribute ("key", "ns");
		nsElement.appendChild (graphDocument.createTextNode ("species"));
		element.appendChild (nsElement);
	}

	@Override
	public String reportHTML (String cssclass)
	{
		if (treeA == null)
			return "<span class='inserted "+cssclass+"'>" + treeB.getId () +"</span>";
		if (treeB == null)
			return "<span class='deleted "+cssclass+"'>" + treeA.getId () +"</span>";
		return treeA.getId ();
	}
	
}
