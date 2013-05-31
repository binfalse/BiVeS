package de.unirostock.sems.bives.ds.sbml;

import java.util.HashMap;
import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.tools.Tools;


public class SBMLKineticLaw
	extends SBMLSBase
	implements SBMLDiffReporter
{
	private MathML math;
	private HashMap<String, SBMLParameter> listOfLocalParameters;
	
	public SBMLKineticLaw (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		Vector<TreeNode> nodes = documentNode.getChildrenWithTag ("math");
		if (nodes.size () != 1)
			throw new BivesSBMLParseException ("kinetic law has "+nodes.size ()+" math elements. (expected exactly one element)");
		math = new MathML ((DocumentNode) nodes.elementAt (0));
		
		listOfLocalParameters = new HashMap<String, SBMLParameter> ();
		
		nodes = documentNode.getChildrenWithTag ("listOfLocalParameters");
		for (int i = 0; i < nodes.size (); i++)
		{
			Vector<TreeNode> paras = ((DocumentNode) nodes.elementAt (i)).getChildrenWithTag ("localParameter");
			for (int j = 0; j < paras.size (); j++)
			{
				SBMLParameter p = new SBMLParameter ((DocumentNode) paras.elementAt (j), sbmlModel);
				listOfLocalParameters.put (p.getID (), p);
			}
		}
	}
	
	public MathML getMath ()
	{
		return math;
	}

	@Override
	public String reportMofification (ClearConnectionManager conMgmt, SBMLDiffReporter docA, SBMLDiffReporter docB)
	{
		SBMLKineticLaw a = (SBMLKineticLaw) docA;
		SBMLKineticLaw b = (SBMLKineticLaw) docB;
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return "";

		String ret = "";
		HashMap<String, SBMLParameter> locParA = a.listOfLocalParameters;
		HashMap<String, SBMLParameter> locParB = b.listOfLocalParameters;
		for (String key : locParA.keySet ())
		{
			if (locParB.get (key) == null)
				ret += "<span class='"+CLASS_DELETED+"'>local parameter: " + locParA.get (key).htmlMarkup () + "</span><br/>";
			else
			{
				SBMLParameter parA = locParA.get (key);
				String aS = parA.htmlMarkup ();
				String bS = locParB.get (key).htmlMarkup ();
				if (!aS.equals (bS))
					ret += "local parameter: "+parA.getNameAndId ()+" modified from <span class='"+CLASS_DELETED+"'>" + aS + "</span> to <span class='"+CLASS_INSERTED+"'>" + bS + "</span><br/>";
			}
		}
		for (String key : locParB.keySet ())
		{
			if (locParA.get (key) == null)
				ret += "<span class='"+CLASS_INSERTED+"'>local parameter: " + locParA.get (key).htmlMarkup () + "</span><br/>";
		}
		
		if (a.math != null && b.math != null)
			ret += Tools.genMathHtmlStats (a.math.getMath (), b.math.getMath ());
		else if (a.math != null)
			ret += Tools.genMathHtmlStats (a.math.getMath (), null);
		else if (b.math != null)
			ret += Tools.genMathHtmlStats (null, b.math.getMath ());
		
		return ret;
	}

	@Override
	public String reportInsert ()
	{
		String ret = "";
		for (SBMLParameter locPar : listOfLocalParameters.values ())
			ret += "<span class='"+CLASS_INSERTED+"'>local parameter: " + locPar.htmlMarkup () + "</span><br/>";
		if (math != null)
			ret += Tools.genAttributeHtmlStats (null, math.getMath ());
		return ret;
	}

	@Override
	public String reportDelete ()
	{
		String ret = "";
		for (SBMLParameter locPar : listOfLocalParameters.values ())
			ret += "<span class='"+CLASS_DELETED+"'>local parameter: " + locPar.htmlMarkup () + "</span><br/>";
		if (math != null)
			ret += Tools.genAttributeHtmlStats (math.getMath (), null);
		return ret;
	}
	
}
