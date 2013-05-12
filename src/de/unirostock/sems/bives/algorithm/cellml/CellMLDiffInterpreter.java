/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Interpreter;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.sbml.SBMLReport.ModConnection;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class CellMLDiffInterpreter
	extends Interpreter
{
	private CellMLReport report;
	
	
	public CellMLDiffInterpreter (ConnectionManager conMgmt, TreeDocument docA,
		TreeDocument docB) throws ParserConfigurationException
	{
		super (conMgmt, docA, docB);
		report = new CellMLReport ();
	}
	
	/*public String getCRNGraph () throws ParserConfigurationException
	{
		return crn.getGraphML ();
	}*/
	
	// TODO!!!
	public void annotatePatch ()
	{
		// TODO!!!
	}
	
	public CellMLReport getReport ()
	{
		return report;
	}
	

	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Producer#produce()
	 */
	@Override
	public void interprete ()
	{
		// id's are quite critical!
		

		report.addHeader ("Please keep in mind, that CellML is not fully supported yet.");
		

		LOGGER.info ("searching for components in A");
		for (DocumentNode component : docA.getNodesByTag ("component"))
		{
			LOGGER.info ("component: " + component.getXPath ());

			if (component.hasModification (TreeNode.UNMAPPED))
			{
				report.deleteComponent (new CellMLComponent (component, null, null));
			}
			else
			{
				Vector<Connection> cons = conMgmt.getConnectionsForNode (component);
				for (Connection con : cons)
				{
					DocumentNode partner = (DocumentNode) con.getPartnerOf (component);
					if (((component.getModification () | partner.getModification ()) & (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED)) != 0)
						report.modifyComponent (new CellMLComponent (component, partner, conMgmt));
				}
			}
		}
		LOGGER.info ("searching for compartments in B");
		for (DocumentNode component : docB.getNodesByTag ("component"))
		{
			if (!component.hasModification (TreeNode.UNMAPPED))
				continue;
			
			LOGGER.info ("parameter: " + component.getXPath ());
			report.insertComponent (new CellMLComponent (null, component, null));
		}
		
		
		
		
		return ;
	}
}
