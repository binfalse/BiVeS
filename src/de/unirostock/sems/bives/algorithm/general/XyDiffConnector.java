/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;


import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;


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
	public void init (TreeDocument docA, TreeDocument docB)
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
	protected void connect ()
	{
		boolean debug = LOGGER.isDebugEnabled();
		if (debug)
			LOGGER.debug (conMgmt.toString ());
		
		// document roots always match...
		if (conMgmt.getConnectionOfNodes (docA.getRoot (), docB.getRoot ()) == null)
			conMgmt.addConnection (new Connection (docA.getRoot (), docB.getRoot ()));

		if (debug)
			System.out.println ("\n\n\n\n\n");
		
		
		FullBottomUp (docB.getRoot ());

		if (debug)
		{
			LOGGER.debug (conMgmt.toString ());
			LOGGER.debug ("\n\n\n\n\n");
		}
		
		topdownMatch (docA.getRoot (), docB.getRoot ());

		if (debug)
		{
			LOGGER.debug (conMgmt.toString ());
			LOGGER.debug ("\n\n\n\n\n");
		}
		
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
	private TreeNode FullBottomUp (TreeNode nodeB)
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
					TreeNode v0childParent = conMgmt.getConnectionsForNode (childMatch).elementAt (0).getTreeA ().getParent ();
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
		if (conMgmt.getConnectionsForNode (nodeB) != null)
		{
			TreeNode match = conMgmt.getConnectionsForNode (nodeB).elementAt (0).getTreeA ();
			LOGGER.debug ("v1 node "+nodeB.getXPath ()+" already has a match, returning " + match.getXPath ());
			return match;
		}
		if (weightByCandidate.size () < 1)
		{
			TreeNode match = null;
			if (conMgmt.getConnectionsForNode (nodeB) != null)
			{
				match = conMgmt.getConnectionsForNode (nodeB).elementAt (0).getTreeA ();
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
	
	private void topdownMatch (TreeNode rootA, TreeNode rootB)
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
			
			if (conMgmt.getConnectionsForNode (nodeID) != null)
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
	
	private void optimize (TreeNode rootA)
	{
		
	}
	
	private boolean nodeAssign (TreeNode a, TreeNode b)
	{
		LOGGER.debug ("Matching old: "+a.getXPath ()+" with new: " + b.getXPath ());
		if (conMgmt.getConnectionsForNode (a) != null || conMgmt.getConnectionsForNode (b) != null)
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
	private TreeNode getBestCandidate (TreeNode v1nodeID, String selfkey)
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
			
			if (conMgmt.getConnectionsForNode (v1nodeRelative) == null)
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
						if (conMgmt.getConnectionsForNode (candidate) == null)
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
		} // next level
		return null;
	}
	
	private void recursiveAssign (TreeNode v0nodeID, TreeNode v1nodeID)
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
	
	private void forceParentsAssign (TreeNode v0nodeID, TreeNode v1nodeID, int level)
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
			
			if (conMgmt.getConnectionsForNode (v0ascendant) != null)
			{
				LOGGER.debug ("forceParentsAssign stopped at level "+i+" because v0 ascendant is already assigned");
				return;
			}
			if (conMgmt.getConnectionsForNode (v1ascendant) != null)
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
