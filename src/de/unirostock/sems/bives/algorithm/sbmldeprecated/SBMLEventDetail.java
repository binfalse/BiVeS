package de.unirostock.sems.bives.algorithm.sbmldeprecated;

import java.util.HashSet;
import java.util.Set;

import de.unirostock.sems.bives.algorithm.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.tools.Tools;


public abstract class SBMLEventDetail implements DiffReporter
{
	private DocumentNode mathA;
	private DocumentNode mathB;
	private DocumentNode detailA;
	private DocumentNode detailB;
	
	public SBMLEventDetail (DocumentNode detailA, DocumentNode detailB)
	{
		this.detailA = detailA;
		this.detailB = detailB;
	}
	
	public void setA (DocumentNode nodeA)
	{
		this.detailA = nodeA;
	}
	
	public void setB (DocumentNode nodeB)
	{
		this.detailB = nodeB;
	}
	
	protected abstract String getType ();
	
	private void setUp ()
	{
		if (detailA != null)
			for (TreeNode node : detailA.getChildren ())
			{
				DocumentNode m = (DocumentNode) node;
				if (m.getTagName ().equals ("math"))
					mathA = m;
			}
		if (detailB != null)
			for (TreeNode node : detailB.getChildren ())
			{
				DocumentNode m = (DocumentNode) node;
				if (m.getTagName ().equals ("math"))
					mathB = m;
			}
	}

	@Override
	public String reportHTML (String cssclass)
	{
		setUp ();
		

		if (detailA == null)
		{
			return "inserted " + getType () + ((mathB == null) ? "" : ": " + Tools.printSubDoc (mathB));
		}
		if (detailB == null)
		{
			return "deleted " + getType () + ((mathA == null) ? "" : ": " + Tools.printSubDoc (mathA));
		}
		
		String ret = "modified event:<br/>";

		ret += Tools.genAttributeHtmlStats (detailA, detailB);
		
		
		ret += Tools.genMathHtmlStats (mathA, mathB);
		
		
		return ret;
	}
}
