/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;


import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.NodeComparer;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConnectionException;


/**
 * @author Martin Scharm
 *
 */
public class XyDiffConnector
	extends Connector
{
	private final int MIN_CANDIDATEPARENT_LEVEL = 4;
	private Connector preprocessor;

	public XyDiffConnector ()
	{
		super ();
	}
	
	public XyDiffConnector (Connector preprocessor)
	{
		super ();
		this.preprocessor = preprocessor;
	}
	
	
	
	@Override
	public void init (TreeDocument docA, TreeDocument docB) throws BivesConnectionException
	{
		super.init (docA, docB);
		
		// not yet initialized?
		if (preprocessor == null)
		{
			// then we'll use by default an id-connector...
			IdConnector id = new IdConnector ();
			id.init (docA, docB);
			id.findConnections (true);
	
			conMgmt = id.getConnections ();
		}
		else
		{
			preprocessor.init (docA, docB);
			preprocessor.findConnections ();
	
			conMgmt = preprocessor.getConnections ();
		}
	}
	

	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Connector#findConnections()
	 */
	@Override
	protected void connect () throws BivesConnectionException
	{
		boolean debug = LOGGER.isDebugEnabled();
		if (debug)
			LOGGER.debug (conMgmt.toString ());
		
		// document roots always match...
		if (conMgmt.getConnectionOfNodes (docA.getRoot (), docB.getRoot ()) == null)
			conMgmt.addConnection (new Connection (docA.getRoot (), docB.getRoot ()));

		if (debug)
			LOGGER.debug ("\n\n\n\n\n");
		

		LOGGER.debug ("doing full bottom up");
		FullBottomUp (docB.getRoot ());

		if (debug)
		{
			LOGGER.debug (conMgmt.toString ());
			LOGGER.debug ("\n\n\n\n\n");
		}

		LOGGER.debug ("doing top down");
		topdownMatch (docA.getRoot (), docB.getRoot ());

		if (debug)
		{
			LOGGER.debug (conMgmt.toString ());
			LOGGER.debug ("\n\n\n\n\n");
		}

		LOGGER.debug ("doing optimizations");
		optimize (docA.getRoot ());

		if (debug)
		{
			LOGGER.debug (conMgmt.toString ());
			LOGGER.debug ("\n\n\n\n\n");
		
			LOGGER.debug ("\n\n\n\n\n");
			Vector<TreeNode> unmatched = conMgmt.getUnmatched (docA.getRoot (), new Vector<TreeNode> ());
			for (TreeNode u : unmatched)
				LOGGER.debug (u.getXPath ());
	
			LOGGER.debug ("\n\n\n\n\n");
			unmatched = conMgmt.getUnmatched (docB.getRoot (), new Vector<TreeNode> ());
			for (TreeNode u : unmatched)
				LOGGER.debug (u.getXPath ());
		}
	}
	
	// returns match in other tree
	private TreeNode FullBottomUp (TreeNode nodeB) throws BivesConnectionException
	{
		HashMap<TreeNode, Double> weightByCandidate = new HashMap<TreeNode, Double> ();
		
		// Apply to children
		if (nodeB.getType () == TreeNode.DOC_NODE)
		{
			Vector<TreeNode> children = ((DocumentNode) nodeB).getChildren ();
			for (TreeNode child : children)
			{
				TreeNode childMatch = FullBottomUp (child);
				if (childMatch != null)
				{
					// get the parent of this node
					TreeNode v0childParent = conMgmt.getConnectionForNode (childMatch).getTreeA ().getParent ();
					if (v0childParent != null)
					{
						if (weightByCandidate.get (v0childParent) == null)
							weightByCandidate.put (v0childParent, child.getWeight ());
						else
							weightByCandidate.put (v0childParent, weightByCandidate.get (v0childParent) + child.getWeight ());
					}
				}
			}
		}
		
		// Do self
		if (conMgmt.getConnectionForNode (nodeB) != null)
		{
			TreeNode match = conMgmt.getConnectionForNode (nodeB).getTreeA ();
			LOGGER.debug ("v1 node "+nodeB.getXPath ()+" already has a match, returning " + match.getXPath ());
			return match;
		}
		if (weightByCandidate.size () < 1)
		{
			TreeNode match = null;
			if (conMgmt.getConnectionForNode (nodeB) != null)
			{
				match = conMgmt.getConnectionForNode (nodeB).getTreeA ();
				LOGGER.debug ("v1 node "+nodeB.getXPath ()+" has no matched children, returning " + match.getXPath ());
			}
			else
				LOGGER.debug ("v1 node "+nodeB.getXPath ()+" has no matched children, returning " + match);
			return match;
		}
		
		// Find parent corresponding to largest part of children
		LOGGER.debug ("v0 parents of v0 nodes matching v1 children of v1 node "+nodeB.getXPath ()+" are:");
		double max=-1.0;
		TreeNode bestMatch=null;
		for (TreeNode node : weightByCandidate.keySet ())
		{
			LOGGER.debug ("v0 node "+node.getXPath ()+" with total weight among children of " + weightByCandidate.get (node));
			if (weightByCandidate.get (node) > max)
			{
				bestMatch = node;
				max = weightByCandidate.get (node);
			}
		}
		if (bestMatch == null)
			return null;
		
		LOGGER.debug ("best parent is v0 node "+bestMatch.getXPath ()+" with total weight among children of " + max);
		nodeAssign (bestMatch, nodeB);
		return bestMatch;
	}
	
	private void topdownMatch (TreeNode rootA, TreeNode rootB) throws BivesConnectionException
	{
		PriorityQueue<TreeNode> toMatch = new PriorityQueue<TreeNode> (100, new TreeNode.TreeNodeComparatorBySubtreeSize (true));
		
		toMatch.add (rootB);
		while (toMatch.size () > 0)
		{
			TreeNode nodeID = toMatch.poll ();
			
			String v1hash = nodeID.getSubTreeHash ();
			LOGGER.debug ("Trying new node "+nodeID.getXPath ()+", hash=" + v1hash);
			
			TreeNode matcher = null;
			
		  // consistency check: has it already been done ???
			
			if (conMgmt.getConnectionForNode (nodeID) != null)
			{
				LOGGER.debug ("skipping Full Subtree check because subtree node is already assigned.");
			}
			else
			{
				// Document roots *always* match
				// a 'renameRoot' operation will be added later if necessary
				if (nodeID == rootB)
				{
					conMgmt.addConnection (new Connection (rootA, rootB));
				}
				else
				{
					matcher = getBestCandidate(nodeID, v1hash);
				}
			}
			
			if (matcher != null)
			{
				recursiveAssign (matcher, nodeID);
			}
			// If not found, children will have to be investigated
			else
			{
				// put children in the vector so they'll be taken care of later
				LOGGER.debug ("Subtree rooted at "+nodeID.getXPath ()+" not fully matched, programming children");
				if (nodeID.getType () == TreeNode.DOC_NODE)
				{
					Vector<TreeNode> children = ((DocumentNode) nodeID).getChildren ();
					for (TreeNode child : children)
					{
						toMatch.add (child);
					}
				}
			}
			// Next node to investigate
		}
	}
	
	private void optimize (DocumentNode nodeA) throws BivesConnectionException
	{
		// If node is matched, we can try to do some work
		Connection c = conMgmt.getConnectionForNode (nodeA);
		if (c != null)
		{
			TreeNode tnb = c.getPartnerOf (nodeA);
			if (tnb.getType () != TreeNode.DOC_NODE)
				return;
			DocumentNode nodeB = (DocumentNode) tnb;
			
			// Get Free nodes in v0
			HashMap<String, Vector<DocumentNode>> kidsMapA = new HashMap<String, Vector<DocumentNode>> ();
			Vector<TreeNode> kidsA = nodeA.getChildren ();
			for (TreeNode node : kidsA)
			{
				if (node.getType () != TreeNode.DOC_NODE || conMgmt.getConnectionForNode (node) != null)
					continue;
				DocumentNode dnode = (DocumentNode) node;
				String tag = dnode.getTagName ();
				if (kidsMapA.get (tag) == null)
					kidsMapA.put (tag, new Vector<DocumentNode> ());
				kidsMapA.get (tag).add (dnode);
			}
			
			// Look for similar nodes in v1
			HashMap<String, Vector<DocumentNode>> kidsMapB = new HashMap<String, Vector<DocumentNode>> ();
			Vector<TreeNode> kidsB = nodeB.getChildren ();
			for (TreeNode node : kidsB)
			{
				if (node.getType () != TreeNode.DOC_NODE || conMgmt.getConnectionForNode (node) != null)
					continue;
				DocumentNode dnode = (DocumentNode) node;
				String tag = dnode.getTagName ();
				if (kidsMapB.get (tag) == null)
					kidsMapB.put (tag, new Vector<DocumentNode> ());
				kidsMapB.get (tag).add (dnode);
			}

			// Now match unique children
			for (String tag : kidsMapA.keySet ())
			{
				optimize (kidsMapA.get (tag), kidsMapB.get (tag));
			}
			
			/*std::map<std::string, int>::iterator i ;
			for(i=v0freeChildren.begin(); i!=v0freeChildren.end(); i++) {
				if ((i->second>0)&&(v1freeChildren.find(i->first)!=v1freeChildren.end())) {
					int v1ID = v1freeChildren[i->first];
					if (v1ID>0) {
						vddprintf(("matching v0(%d) with v1(%d)\n", i->second, v1ID));
						nodeAssign(i->second, v1ID);
						}
					}
				}

			// End-if - Assigned(v0nodeID)
			}*/
		} //endif
		
		// Apply recursivly on children
		Vector<TreeNode> children = nodeA.getChildren ();
		for (TreeNode child : children)
		{
			if (child.getType () == TreeNode.DOC_NODE)
				optimize ((DocumentNode) child);
		}
	}// end optimize
	
	private void optimize (Vector<DocumentNode> nodesA, Vector<DocumentNode> nodesB) throws BivesConnectionException
	{
		// try to find mappings of children w/ same tag name and same parents
		if (nodesA == null || nodesB == null || nodesA.size () == 0 || nodesB.size () == 0)
			return;
		
		if (nodesA.size () == 1 && nodesB.size () == 1)
		{
			// lets match both if they are not too different
			DocumentNode nodeA = nodesA.firstElement (), nodeB = nodesB.firstElement ();
			if (nodeA.getAttributeDistance (nodeB) < .9)
			{
				LOGGER.debug ("connect unambiguos nodes during optimization: " + nodeA.getXPath () + " --> " + nodeB.getXPath ());
				conMgmt.addConnection (new Connection (nodeA, nodeB));
			}
			return;
		}
		
		// calculate distances between nodes
		Vector<NodeComparer> distances = new Vector<NodeComparer> ();
		for (DocumentNode nodeA : nodesA)
			for (DocumentNode nodeB : nodesB)
				distances.add (new NodeComparer (nodeA, nodeB, nodeA.getAttributeDistance (nodeB)));
		// sort by distance
		Collections.sort (distances, new NodeComparer.NodeComparator (false));
		
		// greedy connect nodes
		for (NodeComparer comp : distances)
		{
			// stop at too different nodes
			if (comp.distance > 0.9)
				break;
			TreeNode na = comp.nodeA, nb = comp.nodeB;
			if (conMgmt.getConnectionForNode (na) == null && conMgmt.getConnectionForNode (nb) == null)
				conMgmt.addConnection (new Connection (na, nb));
		}
	}
	
	private boolean nodeAssign (TreeNode a, TreeNode b) throws BivesConnectionException
	{
		LOGGER.debug ("Matching old: "+a.getXPath ()+" with new: " + b.getXPath ());
		if (conMgmt.getConnectionForNode (a) != null || conMgmt.getConnectionForNode (b) != null)
		{
			LOGGER.debug ("already assigned");
			return true;
		}
		
		if (a.getType () != b.getType ())
			return false;
		
		if ((a.getType () == TreeNode.DOC_NODE && ((DocumentNode) b).getTagName ().equals (((DocumentNode) a).getTagName ())) || a.getType () == TreeNode.TEXT_NODE)
		{
			conMgmt.addConnection (new Connection (a, b));
			return true;
		}
		return false;
		// statsCantMatchDifferentOwnHash
	}
	
//From a number of old nodes that have the exact same signature, one has to choose which one
//will be considered 'matching' the new node
//Basically, the best is the old node somehow related to new node: parents are matching for example
//If none has this property, and if hash_matching is *meaningfull* ( text length > ??? ) we may consider returning any matching node
//Maybe on a second level parents ?
	private TreeNode getBestCandidate (TreeNode v1nodeID, String selfkey) throws BivesConnectionException
	{

		// nodeRange.first==nodeRange.second) return 0;

		// first pass : finds a node which parent matches v1node parent (usefull because documents roots always match or parent may be matched thanks to its unique label)
		int candidateRelativeLevel = 1 ;
		TreeNode v1nodeRelative = v1nodeID ;

	  /* The relative weight correspond to the ratio of the weight of the subtree over the weight of the entire document */
		double relativeWeight = v1nodeID.getWeight () / docB.getRoot ().getWeight ();
		int maxLevelPath = MIN_CANDIDATEPARENT_LEVEL + (int) (5.0*Math.log((double)docB.getNumNodes ())*relativeWeight) ;

		/* Try to attach subtree to existing match among ancesters
		 * up to maximum level of ancester, depending on subtree weight
		 */
		
		LOGGER.debug ("maxLevel=" + maxLevelPath);
		
		while ( candidateRelativeLevel <= maxLevelPath )
		{
			LOGGER.debug ("    pass parentLevel=" + candidateRelativeLevel);
			
			v1nodeRelative = v1nodeRelative.getParent ();
			if (v1nodeRelative == null)
			{
				LOGGER.debug ("but node doesn't not have ancesters up to this level\n");
				return null;
			}
			LOGGER.debug ("    pass v1nodeRelative=" + v1nodeRelative.getXPath ());
			
			if (conMgmt.getConnectionForNode (v1nodeRelative) == null)
			{
				LOGGER.debug ("but v1 relative at this level has no match");
			}
			else
			{
				/* For the lower levels, use precomputed index tables to acces candidates given the parent */
				
				if (false && candidateRelativeLevel<=MIN_CANDIDATEPARENT_LEVEL)
				{
					// no idea...
				}
				/* For higher levels, try every candidate and this if its ancestor is a match for us */
				else
				{
					Vector<TreeNode> theList = docA.getNodesByHash (selfkey);
					if (theList == null || theList.size () < 1)
					{
						LOGGER.debug ("  no candidates for hash");
						return null;
					}
					LOGGER.debug ("  num candidates: "+theList.size ()+"");
					if (theList.size () > 50)
						LOGGER.debug ("warning, it seems that there are too many candidates("+theList.size ()+")");
					for (int i = 0; i < theList.size (); i++)
					{
						TreeNode candidate = theList.elementAt (i);
						if (conMgmt.getConnectionForNode (candidate) == null)
						{// Node still not assigned
							LOGGER.debug ("("+candidate.getXPath ()+")");
							TreeNode candidateRelative = candidate;
							for (int j = 0; j < candidateRelativeLevel; j++)
							{
								candidateRelative = candidateRelative.getParent ();
								if (candidateRelative == null)
									break;
							}
							// if relative is ok at required level, test matching
							if (candidateRelative != null)
							{
								if (conMgmt.getConnectionOfNodes (candidateRelative, v1nodeRelative) != null)
								{
									LOGGER.debug (" taken because some relatives ( level= "+candidateRelativeLevel+" ) are matching");
									if (candidateRelativeLevel>1)
									{
										LOGGER.debug ("    level>1 so forcing parents matching in the hierarchie");
										forceParentsAssign( candidate, v1nodeID, candidateRelativeLevel );
									}
									return candidate;
								}
							}
						}
					} //try next candidate
				}//end MIN(Precomputed)<relativelevel<MAX
				
			} //end ancestor is matched
			candidateRelativeLevel++;
		} // endwhile: next level
		return null;
	}
	
	private void recursiveAssign (TreeNode v0nodeID, TreeNode v1nodeID) throws BivesConnectionException
	{
		if (v0nodeID == null || v1nodeID == null)
		{
			LOGGER.debug ("recursiveAssign::bad arguments ("+v0nodeID+", "+v0nodeID+")");
			return;
		}
		
		nodeAssign (v0nodeID, v1nodeID);

		if (v0nodeID.getType () == TreeNode.DOC_NODE && v1nodeID.getType () == TreeNode.DOC_NODE)
		{
			Vector<TreeNode> v0children = ((DocumentNode) v0nodeID).getChildren ();
			Vector<TreeNode> v1children = ((DocumentNode) v1nodeID).getChildren ();
			if (v0children.size () != v1children.size ())
			{
				LOGGER.debug ("recursiveAssign::diff # children: " + v0children.size () +" -vs- "+ v1children.size ());
			}
			for (int i = 0; i < v0children.size (); i++)
			{
				recursiveAssign(v0children.elementAt (i), v1children.elementAt (i));
			}
		}
	}
	
	private void forceParentsAssign (TreeNode v0nodeID, TreeNode v1nodeID, int level) throws BivesConnectionException
	{
		if (v0nodeID == null ||  v1nodeID == null)
		{
			LOGGER.debug ("forceParentsAssign::bad arguments");
			return;
		}
		TreeNode v0ascendant = v0nodeID ;
		TreeNode v1ascendant = v1nodeID ;
		
		for (int i = 0; i < level - 1; i++)
		{
			v0ascendant = v0ascendant.getParent ();
			v1ascendant = v1ascendant.getParent ();
			if (v0ascendant==null||v1ascendant==null)
				return;
			
			if (conMgmt.getConnectionForNode (v0ascendant) != null)
			{
				LOGGER.debug ("forceParentsAssign stopped at level "+i+" because v0 ascendant is already assigned");
				return;
			}
			if (conMgmt.getConnectionForNode (v1ascendant) != null)
			{
				LOGGER.debug ("forceParentsAssign stopped at level "+i+" because v1 ascendant is already assigned");
				return;
			}
			
			if (!nodeAssign( v0ascendant, v1ascendant))
			{
				LOGGER.debug ("forceParentsAssign stopped because relatives ("+v0ascendant.getXPath ()+", "+v1ascendant.getXPath ()+") do not have the same label");
				return;
			}
			
			/*
			if (v0ascendant.getTagName ().equals (v1ascendant.getTagName ()))
			{
				nodeAssign( v0ascendant, v1ascendant );
			}
			else
			{
				debug ("forceParentsAssign stopped because relatives ("+v0ascendant.getXPath ()+", "+v1ascendant.getXPath ()+") do not have the same label");
				return;
			}*/
		}
	}
}
