/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.HTMLMarkup;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLParameter
	extends SBMLGenericIdNameObject
	implements SBMLDiffReporter, HTMLMarkup
{
	private Double value; //optional
	private SBMLUnitDefinition units; //optional
	private boolean constant; //optional
	
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 */
	public SBMLParameter (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		id = documentNode.getAttribute ("id");
		if (id == null || id.length () < 1)
			throw new BivesSBMLParseException ("parameter "+id+" doesn't provide a valid id.");
		
		name = documentNode.getAttribute ("name");
		

		if (documentNode.getAttribute ("value") != null)
		{
			try
			{
				value = Double.parseDouble (documentNode.getAttribute ("value"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("value of species "+id+" of unexpected format: " + documentNode.getAttribute ("value"));
			}
		}
		
		if (documentNode.getAttribute ("units") != null)
		{
			String tmp = documentNode.getAttribute ("units");
			units = sbmlModel.getUnitDefinition (tmp);
			if (units == null)
				throw new BivesSBMLParseException ("units attribute in species "+id+" not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("constant") != null)
		{
			try
			{
				constant = Boolean.parseBoolean (documentNode.getAttribute ("constant"));
			}
			catch (Exception e)
			{
				throw new BivesSBMLParseException ("constant of parameter "+id+" of unexpected format: " + documentNode.getAttribute ("constant"));
			}
		}
		else
			constant = true; // level <= 2
		
	}

	public double getValue ()
	{
		return value;
	}
	
	public boolean isConstant ()
	{
		return constant;
	}
	
	public String htmlMarkup ()
	{
		return getNameAndId () + "=" + value + " " + units.htmlMarkup () + (constant ? " [const]" : "");
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLParameter a = (SBMLParameter) docA;
		SBMLParameter b = (SBMLParameter) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";
		
		String idA = a.getNameAndId (), idB = b.getNameAndId ();
		String ret = "<tr><td>";
		if (idA.equals (idB))
			ret += idA;
		else
			ret += "<span class='"+CLASS_DELETED+"'>" + idA + "</span> &rarr; <span class='"+CLASS_DELETED+"'>" + idB + "</span> ";
		ret += "</td><td>";
		
		ret += Tools.genAttributeHtmlStats (a.documentNode, b.documentNode);
		
		return ret + "</td></tr>";
	}
	
	@Override
	public String reportInsert ()
	{
		return "<tr><td><span class='"+CLASS_INSERTED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_INSERTED+"'>inserted</span></td></tr>";
	}
	
	@Override
	public String reportDelete ()
	{
		return "<tr><td><span class='"+CLASS_DELETED+"'>" + getNameAndId () + "</span></td><td><span class='"+CLASS_DELETED+"'>deleted</span></td></tr>";
	}
}
