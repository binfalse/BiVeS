/**
 * 
 */
package de.unirostock.sems.bives.algorithm.general;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.ClearConnectionManager;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.tools.XmlTools;


/**
 * @author Martin Scharm
 *
 */
public class PatchProducer
	extends Producer
{
	private Patch patch;
	private boolean fullDiff;
	
	public PatchProducer ()
	{
		LOGGER.info ("creating patch producer");
	}
	
	/*public PatchProducer (ConnectionManager conMgmt, TreeDocument docA, TreeDocument docB, boolean fullDiff)
	{
		super (conMgmt, docA, docB);
		this.fullDiff = fullDiff;
		LOGGER.info ("creating patch producer");
	}*/
	
	public void init (ClearConnectionManager conMgmt, TreeDocument docA, TreeDocument docB)
	{
		super.init (conMgmt, docA, docB);
		fullDiff = true;
		LOGGER.info ("creating patch producer: ");// + conMgmt + " " + docA + " " + docB);
	}
	
	public Patch getPatch ()
	{
		return patch;
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Producer#produce()
	 */
	@Override
	public String produce ()
	{
		// loop through all nodes of docA
		// -> matched nodes
		// ----> yes: keep goin'
		// ----> no: add move
		// -> unmatched nodes: add to delete
		// loop through all nodes of docB
		// -> matched nodes: keep going
		// -> unmatched nodes: add to insert
		
		// TODO: consider copy & glue!!! might be requested by some handlers

		LOGGER.info ("producing");
		try
		{
			patch = new Patch (fullDiff);
			
			producePatchA (docA.getRoot ());
			/*TreeNode [] nodesA = docA.getSubtreesBySize ();
			for (TreeNode node : nodesA)
			{
				if ((node.hasModification (TreeNode.UNMAPPED)))
					patch.deleteNode (node);
				else
				{
					if ((node.hasModification (TreeNode.GLUED | TreeNode.COPIED)))
					{
						LOGGER.error ("detected multiple connections of a single node, but copy & glue not supported yet...");
						// TODO: support copy & glue
						throw new UnsupportedOperationException ("copy & glue not supported yet...");
					}
					else
					{
						patch.updateNode (conMgmt.getConnectionForNode (node), conMgmt);
					}
				}
			}*/

			producePatchB (docB.getRoot ());
			/*TreeNode [] nodesB = docB.getSubtreesBySize ();
			for (TreeNode node : nodesB)
			{
				//System.out.println (node.getXPath () + " -> " + node.getModification ());
				//if ((node.getModification () & TreeNode.UNMAPPED) != 0)
				if ((node.hasModification (TreeNode.UNMAPPED)))
					patch.insertNode (node, -1);
				else
				{
					if ((node.getModification () & (TreeNode.GLUED | TreeNode.COPIED)) != 0)
					{
						LOGGER.error ("detected multiple connections of a single node, but copy & glue not supported yet...");
						// TODO: support copy & glue
						throw new UnsupportedOperationException ("copy & glue not supported yet...");
					}
					// else part covered before
				}
			}*/
			

			LOGGER.info ("patch finished, producing xml output");
			
			try
			{
				return XmlTools.prettyPrintDocument (patch.getDocument (), new OutputStream()
				{
				  private StringBuilder string = new StringBuilder();
				  
				  @Override
				  public void write(int b) throws IOException {
				      this.string.append((char) b );
				  }

				  //Netbeans IDE automatically overrides this toString()
				  public String toString(){
				      return this.string.toString();
				  }
  }).toString ();
			}
			catch (IOException e)
			{
				LOGGER.error ("error producing output", e);
			}
			catch (TransformerException e)
			{
				LOGGER.error ("error producing output", e);
			}
			return null;
		}
		catch (ParserConfigurationException e)
		{
			LOGGER.error ("error producing patch", e);
		}
		
		return null;
	}
	
	private void producePatchA (TreeNode node)
	{
		if ((node.hasModification (TreeNode.SUBTREEUNMAPPED)))
			patch.deleteSubtree (node, -1);
		else
		{
			if ((node.hasModification (TreeNode.UNMAPPED)))
				patch.deleteNode (node, -1);
			else
			{
				if ((node.hasModification (TreeNode.GLUED | TreeNode.COPIED)))
				{
					LOGGER.error ("detected multiple connections of a single node, but copy & glue not supported yet...");
					// TODO: support copy & glue
					throw new UnsupportedOperationException ("copy & glue not supported yet...");
				}
				else
				{
					patch.updateNode (conMgmt.getConnectionForNode (node), conMgmt);
				}
			}
			
			if (node.getType () == TreeNode.DOC_NODE)
			{
				DocumentNode dnode = (DocumentNode) node;
				for (TreeNode n : dnode.getChildren ())
					producePatchA (n);
			}
		}
	}
	
	private void producePatchB (TreeNode node)
	{
		if ((node.hasModification (TreeNode.SUBTREEUNMAPPED)))
			patch.insertSubtree (node, -1);
		else
		{
			if ((node.hasModification (TreeNode.UNMAPPED)))
				patch.insertNode (node, -1);
			else
			{
				if ((node.getModification () & (TreeNode.GLUED | TreeNode.COPIED)) != 0)
				{
					LOGGER.error ("detected multiple connections of a single node, but copy & glue not supported yet...");
					// TODO: support copy & glue
					throw new UnsupportedOperationException ("copy & glue not supported yet...");
				}
				// else part covered before
			}
			
			if (node.getType () == TreeNode.DOC_NODE)
			{
				DocumentNode dnode = (DocumentNode) node;
				for (TreeNode n : dnode.getChildren ())
					producePatchB (n);
			}
		}
	}
	
}
