/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import java.util.HashMap;
import java.util.Vector;


import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.Connector;
import de.unirostock.sems.bives.algorithm.general.IdConnector;
import de.unirostock.sems.bives.algorithm.general.XyDiffConnector;
import de.unirostock.sems.bives.ds.sbml.SBMLAssignmentRule;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLModel;
import de.unirostock.sems.bives.ds.sbml.SBMLRateRule;
import de.unirostock.sems.bives.ds.sbml.SBMLReaction;
import de.unirostock.sems.bives.ds.sbml.SBMLRule;
import de.unirostock.sems.bives.ds.sbml.SBMLSBase;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConnectionException;


/**
 * @author Martin Scharm
 *
 */
public class SBMLConnectorPreprocessor
	extends Connector
{
	private Connector preprocessor;
	private SBMLDocument sbmlDocA, sbmlDocB;

	public SBMLConnectorPreprocessor (SBMLDocument sbmlDocA, SBMLDocument sbmlDocB)
	{
		super ();
		this.sbmlDocA = sbmlDocA;
		this.sbmlDocB = sbmlDocB;
	}
	
	public SBMLConnectorPreprocessor (Connector preprocessor)
	{
		super ();
		this.preprocessor = preprocessor;
	}
	
	@Override
	public void init (TreeDocument docA, TreeDocument docB) throws BivesConnectionException
	{
		super.init (sbmlDocA.getTreeDocument (), sbmlDocA.getTreeDocument ());
		
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
	protected void connect () throws BivesConnectionException
	{
		SBMLModel modelA = sbmlDocA.getModel ();
		SBMLModel modelB = sbmlDocB.getModel ();

		// ractions
		HashMap<String, SBMLReaction> reactionsA = modelA.getReactions ();
		HashMap<String, SBMLReaction> reactionsB = modelB.getReactions ();
		for (String id : reactionsA.keySet ())
		{
			SBMLReaction rB = reactionsB.get (id);
			if (rB == null)
				continue;
			SBMLReaction rA = reactionsA.get (id);
			
			if (conMgmt.getConnectionForNode (rA.getDocumentNode ()) == null)
				conMgmt.addConnection (new Connection (rA.getDocumentNode (), rB.getDocumentNode ()));
			
			SBMLSBase loA = rA.getListOfReactantsNode (), loB = rB.getListOfReactantsNode ();
			if (loA != null && loB != null)
				conMgmt.addConnection (new Connection (loA.getDocumentNode (), loB.getDocumentNode ()));
			
			loA = rA.getListOfProductsNode ();
			loB = rB.getListOfProductsNode ();
			if (loA != null && loB != null)
				conMgmt.addConnection (new Connection (loA.getDocumentNode (), loB.getDocumentNode ()));
			
			loA = rA.getListOfModifiersNode ();
			loB = rB.getListOfModifiersNode ();
			if (loA != null && loB != null)
				conMgmt.addConnection (new Connection (loA.getDocumentNode (), loB.getDocumentNode ()));
		}
		
		
		// rules
		HashMap<SBMLSBase, SBMLRule> aRuleMapper = new HashMap<SBMLSBase, SBMLRule> ();
		HashMap<SBMLSBase, SBMLRule> rRuleMapper = new HashMap<SBMLSBase, SBMLRule> ();
		
		Vector<SBMLRule> rules = modelA.getRules ();
		for (SBMLRule rule : rules)
		{
			if (rule.getRuleType () == SBMLRule.ASSIGNMENT_RULE)
			{
				aRuleMapper.put (((SBMLAssignmentRule) rule).getVariable (), rule);
			}
			if (rule.getRuleType () == SBMLRule.RATE_RULE)
			{
				rRuleMapper.put (((SBMLRateRule) rule).getVariable (), rule);
			}
		}
		rules = modelB.getRules ();
		for (SBMLRule rule : rules)
		{
			if (rule.getRuleType () == SBMLRule.ASSIGNMENT_RULE)
			{
				SBMLRule a = aRuleMapper.get (((SBMLAssignmentRule) rule).getVariable ());
				if (a != null)
					conMgmt.addConnection (new Connection (a.getDocumentNode (), rule.getDocumentNode ()));
			}
			if (rule.getRuleType () == SBMLRule.RATE_RULE)
			{
				SBMLRule a = rRuleMapper.get (((SBMLRateRule) rule).getVariable ());
				if (a != null)
					conMgmt.addConnection (new Connection (a.getDocumentNode (), rule.getDocumentNode ()));
			}
		}
		
	}
	
}
