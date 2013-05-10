package de.unirostock.sems.bives.ds.sbml;

import java.util.HashMap;
import java.util.Vector;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;


public class SBMLKineticLaw
	extends SBMLSbase
{
	private SBMLMathML math;
	private HashMap<String, SBMLParameter> listOfLocalParameters;
	
	public SBMLKineticLaw (DocumentNode documentNode, SBMLModel sbmlModel)
		throws BivesSBMLParseException
	{
		super (documentNode, sbmlModel);
		
		Vector<TreeNode> nodes = documentNode.getChildrenWithTag ("math");
		if (nodes.size () != 1)
			throw new BivesSBMLParseException ("kinetic law has "+nodes.size ()+" math elements. (expected exactly one element)");
		math = new SBMLMathML ((DocumentNode) nodes.elementAt (0));
		
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
	
	public SBMLMathML getMath ()
	{
		return math;
	}
	
}
