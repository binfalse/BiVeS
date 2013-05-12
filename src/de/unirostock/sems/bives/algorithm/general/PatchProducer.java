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
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.tools.Tools;


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
	
	public void init (ConnectionManager conMgmt, TreeDocument docA, TreeDocument docB)
	{
		super.init (conMgmt, docA, docB);
		fullDiff = true;
		LOGGER.info ("creating patch producer");
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
			
			TreeNode [] nodesA = docA.getSubtreesBySize ();
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
						patch.updateNode (conMgmt.getConnectionsForNode (node).elementAt (0), conMgmt);
					}
				}
			}
			
			TreeNode [] nodesB = docB.getSubtreesBySize ();
			for (TreeNode node : nodesB)
			{
				if ((node.getModification () & TreeNode.UNMAPPED) != 0)
					patch.insertNode (node);
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
			}
			

			LOGGER.info ("patch finished, producing xml output");
			
			try
			{
				return Tools.prettyPrintDocument (patch.getDocument (), new OutputStream()
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
			catch (IOException | TransformerException e)
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
	
}
