/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.general.IdConnector;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class SBMLConnectorPreprocessor
	extends Connector
{
	private final static Logger LOGGER = Logger.getLogger(SBMLConnectorPreprocessor.class.getName());
	private Connector preprocessor;

	public SBMLConnectorPreprocessor ()
	{
		super ();
	}
	
	public SBMLConnectorPreprocessor (Connector preprocessor)
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
			id.findConnections ();
	
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
		HashMap<String, DocumentNode> ruleMapper = new HashMap<String, DocumentNode> ();
		
		Vector<DocumentNode> rules = docA.getNodesByTag ("assignmentRule");
		if (rules != null)
			for (DocumentNode rule : rules)
			{
				String var = rule.getAttribute ("variable");
				if (var != null)
				{
					ruleMapper.put (var, rule);
					//System.out.println ("adde var: " + var);
				}
			}
		rules = docB.getNodesByTag ("assignmentRule");
		if (rules != null)
			for (DocumentNode rule : rules)
			{
				//System.out.println ("test rule: " + rule.getXPath ());
				if (conMgmt.getConnectionsForNode (rule) != null && conMgmt.getConnectionsForNode (rule).size () > 0)
					continue;
				String var = rule.getAttribute ("variable");
				//System.out.println ("search var: " + var);
				if (var != null)
				{
					DocumentNode ruleA = ruleMapper.get (var);
					if (ruleA != null)
					{
						//System.out.println ("find var: " + ruleA.getXPath ());
						if (conMgmt.getConnectionsForNode (ruleA) != null && conMgmt.getConnectionsForNode (ruleA).size () > 0)
							continue;
						//System.out.println ("connecte : " + var + " -> " + rule.getXPath () + " -> " + ruleA.getXPath ());
						conMgmt.addConnection (new Connection (ruleA, rule));
					}
				}
			}
		
		ruleMapper.clear ();
		rules = docA.getNodesByTag ("rateRule");
		if (rules != null)
			for (DocumentNode rule : rules)
			{
				String var = rule.getAttribute ("variable");
				if (var != null)
				{
					ruleMapper.put (var, rule);
				}
			}
		rules = docB.getNodesByTag ("rateRule");
		if (rules != null)
			for (DocumentNode rule : rules)
			{
				if (conMgmt.getConnectionsForNode (rule) != null && conMgmt.getConnectionsForNode (rule).size () > 0)
					continue;
				String var = rule.getAttribute ("variable");
				if (var != null)
				{
					DocumentNode ruleA = ruleMapper.get (var);
					if (ruleA != null)
					{
						if (conMgmt.getConnectionsForNode (ruleA) != null && conMgmt.getConnectionsForNode (ruleA).size () > 0)
							continue;
						conMgmt.addConnection (new Connection (ruleA, rule));
					}
				}
			}
		
	}
	
}
