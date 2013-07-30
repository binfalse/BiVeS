/**
 * 
 */
package de.unirostock.sems.bives.ds.xml;

import java.util.Comparator;


/**
 * @author Martin Scharm
 *
 */
public class NodeComparer
{
	public TreeNode nodeA, nodeB;
	public double distance;
	public NodeComparer (TreeNode nodeA, TreeNode nodeB, double distance)
	{
		this.nodeA = nodeA;
		this.nodeB = nodeB;
		this.distance = distance;
	}
	
	public static class NodeComparator implements Comparator<NodeComparer>
	{
		private int reverse;
		
		public NodeComparator ()
		{
			reverse = 1;
		}
		
		public NodeComparator (boolean reverse)
		{
			if (reverse)
				this.reverse = -1;
			else
				this.reverse = 1;
		}

		@Override
		public int compare (NodeComparer o1, NodeComparer o2)
		{
			int ret = 0;
			if (o1.distance < o2.distance)
				ret = -1;
			if (o2.distance > o2.distance)
				ret = 1;
			
			return reverse * ret;
		}
	}
}
