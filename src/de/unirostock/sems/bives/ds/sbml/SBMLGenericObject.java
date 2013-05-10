/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.ds.xml.DocumentNode;


/**
 * @author Martin Scharm
 *
 */
public class SBMLGenericObject
{
	protected DocumentNode documentNode;
	protected SBMLModel sbmlModel;
	
	public SBMLGenericObject (DocumentNode documentNode, SBMLModel sbmlModel)
	{
		this.documentNode = documentNode;
		this.sbmlModel = sbmlModel;
	}
	
	public DocumentNode getDocumentNode ()
	{
		return documentNode;
	}
}
