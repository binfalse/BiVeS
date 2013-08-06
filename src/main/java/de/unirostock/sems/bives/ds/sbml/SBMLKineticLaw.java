package de.unirostock.sems.bives.ds.sbml;

import java.util.HashMap;
import java.util.Vector;

import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;
import de.unirostock.sems.bives.tools.Tools;


public class SBMLKineticLaw
	extends SBMLSBase
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

	public void reportMofification (ClearConnectionManager conMgmt, SBMLKineticLaw a, SBMLKineticLaw b, MarkupElement me, MarkupDocument markupDocument)
	{
		if (a.getDocumentNode ().getModification () == 0 && b.getDocumentNode ().getModification () == 0)
			return;

		String ret = "";
		HashMap<String, SBMLParameter> locParA = a.listOfLocalParameters;
		HashMap<String, SBMLParameter> locParB = b.listOfLocalParameters;
		for (String key : locParA.keySet ())
		{
			if (locParB.get (key) == null)
				me.addValue ("local parameter: " + markupDocument.delete (locParA.get (key).markup (markupDocument)));
				//ret += "<span class='"+CLASS_DELETED+"'>local parameter: " + locParA.get (key).htmlMarkup () + "</span><br/>";
			else
			{
				SBMLParameter parA = locParA.get (key);
				String aS = parA.markup (markupDocument);
				String bS = locParB.get (key).markup (markupDocument);
				if (!aS.equals (bS))
					me.addValue ("local parameter: " + parA.getNameAndId ()+ " modified from " +markupDocument.delete (aS) + " to " + markupDocument.insert (bS));
					//ret += "local parameter: "+parA.getNameAndId ()+" modified from <span class='"+CLASS_DELETED+"'>" + aS + "</span> to <span class='"+CLASS_INSERTED+"'>" + bS + "</span><br/>";
			}
		}
		for (String key : locParB.keySet ())
		{
			if (locParA.get (key) == null)
				me.addValue ("local parameter: " + markupDocument.insert (locParB.get (key).markup (markupDocument)));
				//ret += "<span class='"+CLASS_INSERTED+"'>local parameter: " + locParA.get (key).htmlMarkup () + "</span><br/>";
		}
		
		if (a.math != null && b.math != null)
			Tools.genMathHtmlStats (a.math.getDocumentNode (), b.math.getDocumentNode (), me, markupDocument);
		else if (a.math != null)
			Tools.genMathHtmlStats (a.math.getDocumentNode (), null, me, markupDocument);
		else if (b.math != null)
			Tools.genMathHtmlStats (null, b.math.getDocumentNode (), me, markupDocument);
		
		//return ret;
	}

	public void reportInsert (MarkupElement me, MarkupDocument markupDocument)
	{
		for (SBMLParameter locPar : listOfLocalParameters.values ())
			me.addValue ("local parameter: " + markupDocument.insert (locPar.markup (markupDocument)));
			//ret += "<span class='"+CLASS_DELETED+"'>local parameter: " + locPar.htmlMarkup () + "</span><br/>";
		if (math != null)
			Tools.genAttributeHtmlStats (null, math.getDocumentNode (), me, markupDocument);
		/*String ret = "";
		for (SBMLParameter locPar : listOfLocalParameters.values ())
			ret += "<span class='"+CLASS_INSERTED+"'>local parameter: " + locPar.htmlMarkup () + "</span><br/>";
		if (math != null)
			ret += Tools.genAttributeHtmlStats (null, math.getMath ());
		return ret;*/
	}

	public void reportDelete (MarkupElement me, MarkupDocument markupDocument)
	{
		for (SBMLParameter locPar : listOfLocalParameters.values ())
			me.addValue ("local parameter: " + markupDocument.delete (locPar.markup (markupDocument)));
			//ret += "<span class='"+CLASS_DELETED+"'>local parameter: " + locPar.htmlMarkup () + "</span><br/>";
		if (math != null)
			Tools.genAttributeHtmlStats (math.getDocumentNode (), null, me, markupDocument);
	}
	
}
