/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import java.util.Set;
import java.util.Vector;

import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.ds.MultiNodeMapper;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConnectionException;


/**
 * Class to connect all nodes with the same label in two trees. As result a node in tree A with label <em>a</em> will be connected to all nodes in tree B which are also labeled with <em>a</em>.
 * 
 * <pre>
 * tree A:    a a b
 * connect:    X  |
 * tree B:    a a b
 * </pre>
 * 
 * @author Martin Scharm
 * 
 *
 */
@Deprecated
public class LabelConnector
	extends Connector
{
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Connector#findConnections()
	 */
	@Override
	protected void connect () throws BivesConnectionException
	{
		Set<String> tags = docA.getOccuringTags ();
		
		for (String tag : tags)
		{
			Vector<DocumentNode> nB = docB.getNodesByTag (tag);
			if (nB == null)
				continue;
			
			Vector<DocumentNode> nA = docA.getNodesByTag (tag);
			for (TreeNode b: nB)
			{
				for (TreeNode a: nA)
				{
					conMgmt.addConnection (new Connection (a, b));
				}
			}
		}
	}
	
}
