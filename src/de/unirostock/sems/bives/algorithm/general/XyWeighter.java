/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import de.unirostock.sems.bives.algorithm.Weighter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TextNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class XyWeighter
	extends Weighter
{
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Weighter#getWeight(de.unirostock.sems.xmldiff.xml.DocumentNode)
	 */
	@Override
	public double getWeight (DocumentNode node)
	{
		//double weight = 0;
		
		/*String txt = node.getText ();
		if (txt != null && txt.length () > 0)
			weight = Math.log (txt.length ());*/
		
		/*weight += node.getSizeSubtree () + 1;
		
		return weight;*/
		
		return node.getSizeSubtree () + 1;
	}
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Weighter#getWeight(de.unirostock.sems.xmldiff.xml.TextNode)
	 */
	@Override
	public double getWeight (TextNode node)
	{
		return Math.log (node.getText ().length ());
	}
	
}
