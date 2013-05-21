/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbmldeprecated;

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

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.unirostock.sems.bives.algorithm.Connection;
import de.unirostock.sems.bives.algorithm.ConnectionManager;
import de.unirostock.sems.bives.algorithm.Interpreter;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.sbmldeprecated.SBMLReport.ModConnection;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class SBMLDiffInterpreter
	extends Interpreter
{
	private HashMap<String, CRNNode> entityMapper;
	//private HashMap<String, SBMLEventDetail> eventMapper;
	private ChemicalReactionNetwork crn;
	private SBMLReport report;
	
	private final static Logger LOGGER = Logger.getLogger(SBMLDiffInterpreter.class.getName());
	
	public SBMLDiffInterpreter (ConnectionManager conMgmt, TreeDocument docA,
		TreeDocument docB) throws ParserConfigurationException
	{
		super (conMgmt, docA, docB);
			entityMapper = new HashMap<String, CRNNode> ();
			crn = new ChemicalReactionNetwork ();
			report = new SBMLReport ();
	}
	
	public String getCRNGraph () throws ParserConfigurationException
	{
		return crn.getGraphML ();
	}
	
	// TODO!!!
	public void annotatePatch ()
	{
		// TODO!!!
	}
	
	public SBMLReport getReport ()
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
		

		String lvA = "L" + docA.getRoot ().getAttribute ("level") + "V" + docA.getRoot ().getAttribute ("version");
		String lvB = "L" + docB.getRoot ().getAttribute ("level") + "V" + docB.getRoot ().getAttribute ("version");
		if (lvA.equals (lvB))
			report.addHeader ("Both documents have same Level/Version: " + lvA);
		else
			report.addHeader ("Level/Version has changed: from <span class='deleted'>" + lvA + "</span> to <span class='inserted'>" + lvB+"</span><br/>");
		
		
		// compartments
		LOGGER.info ("searching for rules in A");
		Vector<DocumentNode> rules = docA.getNodesByTag ("assignmentRule");
		rules.addAll (docA.getNodesByTag ("algebraicRule"));
		rules.addAll (docA.getNodesByTag ("rateRule"));
		for (DocumentNode dp : rules)
		{
			LOGGER.info ("rule: " + dp.getXPath ());

			if (dp.hasModification (TreeNode.UNMAPPED))
			{
				SBMLRule rule = null;
				if (dp.getTagName ().equals ("assignmentRule"))
					rule = new SBMLAssignmentRule (dp.getAttribute ("variable"), null, (DocumentNode) dp.getChildren ().elementAt (0), null);
				if (dp.getTagName ().equals ("algebraicRule"))
					rule = new SBMLAlgebraicRule ((DocumentNode) dp.getChildren ().elementAt (0), null);
				if (dp.getTagName ().equals ("rateRule"))
					rule = new SBMLRateRule (dp.getAttribute ("variable"), null, (DocumentNode) dp.getChildren ().elementAt (0), null);
				
				report.deleteRule (rule);
			}
			else if (dp.hasModification (TreeNode.MODIFIED))
			{
				SBMLRule rule = null;
				
				Vector<Connection> cons = conMgmt.getConnectionsForNode (dp);
				for (Connection con : cons)
				{
					DocumentNode partner = (DocumentNode) con.getPartnerOf (dp);
					if (!partner.getTagName ().equals (dp.getTagName ()))
						throw new UnsupportedOperationException ("different rule types matched...");

					if (dp.getTagName ().equals ("assignmentRule"))
						rule = new SBMLAssignmentRule (dp.getAttribute ("variable"), partner.getAttribute ("variable"), (DocumentNode) dp.getChildren ().elementAt (0), (DocumentNode) partner.getChildren ().elementAt (0));
					if (dp.getTagName ().equals ("algebraicRule"))
						rule = new SBMLAlgebraicRule ((DocumentNode) dp.getChildren ().elementAt (0), (DocumentNode) partner.getChildren ().elementAt (0));
					if (dp.getTagName ().equals ("rateRule"))
						rule = new SBMLRateRule (dp.getAttribute ("variable"), partner.getAttribute ("variable"), (DocumentNode) dp.getChildren ().elementAt (0), (DocumentNode) partner.getChildren ().elementAt (0));
				}
				
				report.modifyRule (rule);
			}
		}
		LOGGER.info ("searching for rules in B");
		rules = docB.getNodesByTag ("assignmentRule");
		rules.addAll (docB.getNodesByTag ("algebraicRule"));
		rules.addAll (docB.getNodesByTag ("rateRule"));
		for (DocumentNode dp : rules)
		{
			if (!dp.hasModification (TreeNode.UNMAPPED))
				continue;
			
			LOGGER.info ("parameter: " + dp.getXPath ());
			SBMLRule rule = null;
			if (dp.getTagName ().equals ("assignmentRule"))
				rule = new SBMLAssignmentRule (null, dp.getAttribute ("variable"), null, (DocumentNode) dp.getChildren ().elementAt (0));
			if (dp.getTagName ().equals ("algebraicRule"))
				rule = new SBMLAlgebraicRule (null, (DocumentNode) dp.getChildren ().elementAt (0));
			if (dp.getTagName ().equals ("rateRule"))
				rule = new SBMLRateRule (null, dp.getAttribute ("variable"), null, (DocumentNode) dp.getChildren ().elementAt (0));
			
			report.insertRule (rule);
		}
		
		
		
		
		
		
		
		// compartments
		LOGGER.info ("searching for compartments in A");
		for (DocumentNode dp : docA.getNodesByTag ("compartment"))
		{
			LOGGER.info ("parameter: " + dp.getXPath ());

			if (dp.hasModification (TreeNode.UNMAPPED))
			{
				report.deleteCompartments (dp);
			}
			else if (dp.hasModification (TreeNode.MODIFIED))
			{
				Vector<Connection> cons = conMgmt.getConnectionsForNode (dp);
				//System.out.println ("modified para: " + p.getXPath ());
				for (Connection con : cons)
					report.modifyCompartments (new ModConnection (dp, (DocumentNode) con.getPartnerOf (dp)));
			}
		}
		LOGGER.info ("searching for compartments in B");
		for (DocumentNode dp : docB.getNodesByTag ("compartment"))
		{
			if (!dp.hasModification (TreeNode.UNMAPPED))
				continue;
			
			LOGGER.info ("parameter: " + dp.getXPath ());
			report.insertCompartments (dp);
		}
		
		
		
		
		
		
		// parameter
		LOGGER.info ("searching for parameter in A");
		for (DocumentNode dp : docA.getNodesByTag ("parameter"))
		{
			LOGGER.info ("parameter: " + dp.getXPath ());

			if (dp.hasModification (TreeNode.UNMAPPED))
			{
				report.deleteParameter (dp);
			}
			else if (dp.hasModification (TreeNode.MODIFIED))
			{
				Vector<Connection> cons = conMgmt.getConnectionsForNode (dp);
				//System.out.println ("modified para: " + p.getXPath ());
				for (Connection con : cons)
					report.modifyParameter (new ModConnection (dp, (DocumentNode) con.getPartnerOf (dp)));
			}
		}
		LOGGER.info ("searching for parameter in B");
		for (DocumentNode dp : docB.getNodesByTag ("parameter"))
		{
			if (!dp.hasModification (TreeNode.UNMAPPED))
				continue;
			
			LOGGER.info ("parameter: " + dp.getXPath ());
			report.insertParameter (dp);
		}
		
		
		
		
		
		
		// parameter
		LOGGER.info ("searching for events in A");
		for (DocumentNode dp : docA.getNodesByTag ("event"))
		{
			LOGGER.info ("event: " + dp.getXPath ());
			
			SBMLEvent e = new SBMLEvent (dp, null);
			
			if (dp.hasModification (TreeNode.UNMAPPED))
			{
				report.deleteEvent (e);
			}
			else if (dp.hasModification (TreeNode.MODIFIED))
			{
				Vector<Connection> cons = conMgmt.getConnectionsForNode (dp);
				//System.out.println ("modified para: " + p.getXPath ());
				for (Connection con : cons)
				{
					e.setEvenetB ((DocumentNode) con.getTreeB ());
					report.modifyEvent (e);
					// just one con possible
					break;
				}
			}
			

			for (TreeNode node : dp.getChildren ())
			{
				DocumentNode dnode = (DocumentNode) node;
				if (dnode.getTagName ().equals ("trigger"))
				{
					if (dnode.hasModification (TreeNode.UNMAPPED))
						e.setTrigger (new SBMLEventTrigger (dnode, null));
					else
					{
						Vector<Connection> cs = conMgmt.getConnectionsForNode (dnode);
						if (cs.size () != 1)
							throw new UnsupportedOperationException ("mapped but not exactly 1 connection... ");
						e.setTrigger (new SBMLEventTrigger (dnode, (DocumentNode) cs.elementAt (0).getPartnerOf (dnode)));
					}
				}
				else if (dnode.getTagName ().equals ("delay"))
				{
					if (dnode.hasModification (TreeNode.UNMAPPED))
						e.setDelay (new SBMLEventDelay (dnode, null));
					else
					{
						Vector<Connection> cs = conMgmt.getConnectionsForNode (dnode);
						if (cs.size () != 1)
							throw new UnsupportedOperationException ("mapped but not exactly 1 connection... ");
						e.setDelay (new SBMLEventDelay (dnode, (DocumentNode) cs.elementAt (0).getPartnerOf (dnode)));
					}
				}
				else if (dnode.getTagName ().equals ("listOfEventAssignments"))
				{
					for (TreeNode node2 : dnode.getChildren ())
					{
						DocumentNode dass = (DocumentNode) node2;
						if (dass.hasModification (TreeNode.UNMAPPED))
							e.addAssignment (new SBMLEventAssignment (dass, null));
						else
						{
							Vector<Connection> cs = conMgmt.getConnectionsForNode (dass);
							if (cs.size () != 1)
								throw new UnsupportedOperationException ("mapped but not exactly 1 connection... ");
							e.addAssignment (new SBMLEventAssignment (dass, (DocumentNode) cs.elementAt (0).getPartnerOf (dass)));
						}
					}
				}
			}
			
		}
		LOGGER.info ("searching for events in B");
		for (DocumentNode dp : docB.getNodesByTag ("event"))
		{
			if (!dp.hasModification (TreeNode.UNMAPPED))
				continue;
			
			LOGGER.info ("event: " + dp.getXPath ());
			
			SBMLEvent e = new SBMLEvent (null, dp);

			report.deleteEvent (e);

			for (TreeNode node : dp.getChildren ())
			{
				DocumentNode dnode = (DocumentNode) node;
				if (dnode.getTagName ().equals ("trigger"))
				{
					e.setTrigger (new SBMLEventTrigger (null, dnode));
				}
				else if (dnode.getTagName ().equals ("delay"))
				{
					e.setDelay (new SBMLEventDelay (null, dnode));
				}
				else if (dnode.getTagName ().equals ("listOfEventAssignments"))
				{
					for (TreeNode node2 : dnode.getChildren ())
					{
						e.addAssignment (new SBMLEventAssignment (null, (DocumentNode) node2));
					}
				}
			}
		}
		
		
		
		
		
		
		// species
		LOGGER.info ("searching for species in A");
		for (DocumentNode ds : docA.getNodesByTag ("species"))
		{

			LOGGER.info ("species: " + ds.getXPath ());

			String name = ds.getAttribute ("name");
			if (name == null)
				name = ds.getId ();
			
			//System.out.println (ds.getXPath () + " -mod-> " + ds.getModification ());

			// new node in crn
			CRNSpecies spec = new CRNSpecies (name, null, ds.getModification (), ds, null);

			if (ds.hasModification (TreeNode.UNMAPPED))
			{
				entityMapper.put ("sd" + ds.getId (), spec);
				// report
				report.deleteSpecies (ds);
			}
			else
			{
				entityMapper.put ("sc" + ds.getId (), spec);
				if (ds.hasModification (TreeNode.MODIFIED))
				{
					// report
					Vector<Connection> cons = conMgmt.getConnectionsForNode (ds);
					for (Connection con : cons)
						report.modifySpecies(new ModConnection (ds, (DocumentNode) con.getPartnerOf (ds)));
				}
			}
			crn.addSpecies (spec);
		}
		
		LOGGER.info ("searching for species in B");
		for (DocumentNode ds : docB.getNodesByTag ("species"))
		{

			LOGGER.info ("species: " + ds.getXPath ());

			String name = ds.getAttribute ("name");
			if (name == null)
				name = ds.getId ();
			
			//System.out.println (ds.getXPath () + " -mod-> " + ds.getModification ());

			// new node in crn
			if (ds.hasModification (TreeNode.UNMAPPED))
			{
				CRNSpecies spec = new CRNSpecies (null, name, ds.getModification (), null, ds);
				entityMapper.put ("si" + ds.getId (), spec);
				crn.addSpecies (spec);
				// report
				report.insertSpecies (ds);
			}
			else
			{
				CRNNode spec = entityMapper.get ("sc" + ds.getId ());
				spec.setTreeB (ds);
				//System.out.println ("adde mod " + ds.getModification () + " to: " + ds.getXPath ());
				spec.addModifications (ds.getModification ());
				spec.setNameB (name);
			}
		}

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// reactions
		LOGGER.info ("searching for reactions in A");
		for (DocumentNode reaction : docA.getNodesByTag ("reaction"))
		{
			LOGGER.info ("reaction: " + reaction.getXPath ());
			LOGGER.info ("reaction marker: " + reaction.getModification () + " mod/submod: " + reaction.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED));
			

			String reactionID = reaction.getAttribute ("id");
			String name = reaction.getAttribute ("name");
			if (name == null)
				name = reactionID;
			
			// new node in crn
			CRNReaction react = new CRNReaction (name, null, reaction.getModification (), reaction.getAttribute ("reversible"), reaction.getAttribute ("fast"), reaction, null);
			crn.addReaction (react);
			

			if (reaction.hasModification (TreeNode.UNMAPPED))
			{
				entityMapper.put ("rd" + reactionID, react);
				// report
				report.deleteReaction (react);
			}
			else
			{
				entityMapper.put ("rc" + reactionID, react);
				if (reaction.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED))
				{
					// report
					report.modifyReaction (react);
				}
				else
				{
					Vector<Connection> cons = conMgmt.getConnectionsForNode (reaction);
					for (Connection con : cons)
						if (con.getPartnerOf (reaction).hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED))
							report.modifyReaction (react);
				}
			}
			

			for (TreeNode c: reaction.getChildren ())
			{
				DocumentNode dn = (DocumentNode) c;
				String dntag = dn.getTagName ();
				// modification of whole list!?
				int mod = c.getModification ();
				

				if (dntag.equals ("listOfReactants"))
				{
					for (TreeNode rea : dn.getChildren ())
					{
						DocumentNode reactant = (DocumentNode) rea;
						if (reactant.getTagName ().equals ("ListOfSpeciesReferences"))
						{
							// v3..
							for (TreeNode rea2 : reactant.getChildren ())
							{
								DocumentNode reactant2 = (DocumentNode) rea2;
								CRNNode spec = entityMapper.get ("sd" + reactant2.getAttribute ("species"));
								if (spec == null)
									spec = entityMapper.get ("sc" + reactant2.getAttribute ("species"));
								
								CRNEdge edge = new CRNEdge (spec, react, CRNEdge.REACTANT, reactant2.getModification (), null, null, reactant2, null);
								react.addReactant (spec, edge);
							}
						}
						else
						{
							CRNNode spec = entityMapper.get ("sd" + reactant.getAttribute ("species"));
							if (spec == null)
								spec = entityMapper.get ("sc" + reactant.getAttribute ("species"));
							
							CRNEdge edge = new CRNEdge (spec, react, CRNEdge.REACTANT, reactant.getModification (), null, null, reactant, null);
							react.addReactant (spec, edge);
						}
					}
				}
				
				else if (dntag.equals ("listOfProducts"))
				{
					for (TreeNode prod : dn.getChildren ())
					{
						DocumentNode product = (DocumentNode) prod;
						if (product.getTagName ().equals ("ListOfSpeciesReferences"))
						{
							// v3..
							for (TreeNode prod2 : product.getChildren ())
							{
								DocumentNode product2 = (DocumentNode) prod2;
								CRNNode spec = entityMapper.get ("sd" + product2.getAttribute ("species"));
								if (spec == null)
									spec = entityMapper.get ("sc" + product2.getAttribute ("species"));
								
								CRNEdge edge = new CRNEdge (react, spec, CRNEdge.PRODUCT, product2.getModification (), null, null, product2, null);
								react.addReactant (spec, edge);
							}
						}
						else
						{
							CRNNode spec = entityMapper.get ("sd" + product.getAttribute ("species"));
							if (spec == null)
								spec = entityMapper.get ("sc" + product.getAttribute ("species"));
	
							CRNEdge edge = new CRNEdge (react, spec, CRNEdge.PRODUCT, product.getModification (), null, null, product, null);
							react.addProduct (spec, edge);
						}
					}
				}
				
				else if (dntag.equals ("listOfModifiers"))
				{
					for (TreeNode modi : dn.getChildren ())
					{
						DocumentNode modifier = (DocumentNode) modi;
						if (modifier.getTagName ().equals ("ListOfSpeciesReferences"))
						{
							// v3..
							for (TreeNode mod2 : modifier.getChildren ())
							{
								DocumentNode modifier2 = (DocumentNode) mod2;
								CRNNode spec = entityMapper.get ("sd" + modifier2.getAttribute ("species"));
								if (spec == null)
									spec = entityMapper.get ("sc" + modifier2.getAttribute ("species"));
								
								String sbo = modifier2.getAttribute ("sboTerm");
								if (sbo == null)
									sbo = "unknown";
								
								CRNEdge edge = new CRNEdge (spec, react, CRNEdge.MODIFIER, modifier2.getModification (), sbo, null, modifier2, null);
								react.addReactant (spec, edge);
							}
						}
						else
						{
							CRNNode spec = entityMapper.get ("sd" + modifier.getAttribute ("species"));
							if (spec == null)
								spec = entityMapper.get ("sc" + modifier.getAttribute ("species"));
							
							String sbo = modifier.getAttribute ("sboTerm");
							if (sbo == null)
								sbo = "unknown";
	
							CRNEdge edge = new CRNEdge (spec, react, CRNEdge.MODIFIER, modifier.getModification (), sbo, null, modifier, null);
							react.addModifier (spec, edge);
						}
					}
				}
				
				else if (dntag.equals ("kineticLaw"))
				{
					for (TreeNode math : dn.getChildren ())
					{
						DocumentNode mathNode = (DocumentNode) math;
						if (mathNode.getTagName ().equals ("math"))
							react.setKineticLawA (mathNode);
					}
				}
			}
		}
		
		

		LOGGER.info ("searching for reactions in B");
		for (DocumentNode reaction : docB.getNodesByTag ("reaction"))
		{
			LOGGER.info ("reaction: " + reaction.getXPath ());
			LOGGER.info ("reaction marker: " + reaction.getModification () + " mod/submod: " + reaction.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED));
			

			String reactionID = reaction.getAttribute ("id");
			String name = reaction.getAttribute ("name");
			if (name == null)
				name = reactionID;
			
			CRNReaction react;
			CRNNode dummy = entityMapper.get ("rc" + reactionID);
			// exists node?
			if (reaction.hasModification (TreeNode.UNMAPPED) || dummy == null)
			{
				react = new CRNReaction (null, name, reaction.getModification (), reaction.getAttribute ("reversible"), reaction.getAttribute ("fast"), null, reaction);
				entityMapper.put ("ri" + reactionID, react);
				crn.addReaction (react);
				// report
				report.insertReaction (react);
			}
			else
			{
				react = (CRNReaction) dummy;
				react.setTreeB (reaction);
				react.addModifications (reaction.getModification ());
				react.setNameB (name);
				//System.out.println ("modify reaction: " + react.getId ());
			}
			

			for (TreeNode c: reaction.getChildren ())
			{
				DocumentNode dn = (DocumentNode) c;
				String dntag = dn.getTagName ();
				// modification of whole list!?
				int mod = c.getModification ();
				

				if (dntag.equals ("listOfReactants"))
				{
					for (TreeNode rea : dn.getChildren ())
					{
						DocumentNode reactant = (DocumentNode) rea;
						if (reactant.getTagName ().equals ("ListOfSpeciesReferences"))
						{
							// v3..
							for (TreeNode rea2 : reactant.getChildren ())
							{
								DocumentNode reactant2 = (DocumentNode) rea2;
								
								CRNNode spec = entityMapper.get ("si" + reactant2.getAttribute ("species"));
								if (spec == null)
									spec = entityMapper.get ("sc" + reactant2.getAttribute ("species"));
								
								CRNEdge edge = react.getReactant (spec);
								if (edge == null)
								{
									edge = new CRNEdge (spec, react, CRNEdge.REACTANT, reactant2.getModification (), null, null, null, reactant2);
									react.addReactant (spec, edge);
								}
								else
								{
									edge.addModifications (reactant2.getModification ());
									edge.setTreeB (reactant2);
								}
							}
						}
						else
						{
							
							CRNNode spec = entityMapper.get ("si" + reactant.getAttribute ("species"));
							if (spec == null)
								spec = entityMapper.get ("sc" + reactant.getAttribute ("species"));
							
							CRNEdge edge = react.getReactant (spec);
							if (edge == null)
							{
								edge = new CRNEdge (spec, react, CRNEdge.REACTANT, reactant.getModification (), null, null, null, reactant);
								react.addReactant (spec, edge);
							}
							else
							{
								edge.addModifications (reactant.getModification ());
								edge.setTreeB (reactant);
							}
						}
					}
				}
				
				else if (dntag.equals ("listOfProducts"))
				{
					for (TreeNode prod : dn.getChildren ())
					{
						DocumentNode product = (DocumentNode) prod;
						if (product.getTagName ().equals ("ListOfSpeciesReferences"))
						{
							// v3..
							for (TreeNode prod2 : product.getChildren ())
							{
								DocumentNode product2 = (DocumentNode) prod2;
								CRNNode spec = entityMapper.get ("si" + product2.getAttribute ("species"));
								if (spec == null)
									spec = entityMapper.get ("sc" + product2.getAttribute ("species"));

								CRNEdge edge = react.getProduct (spec);
								if (edge == null)
								{
									edge = new CRNEdge (react, spec, CRNEdge.PRODUCT, product2.getModification (), null, null, null, product2);
									react.addProduct (spec, edge);
								}
								else
								{
									edge.addModifications (product2.getModification ());
									edge.setTreeB (product2);
								}
							}
						}
						else
						{
							CRNNode spec = entityMapper.get ("si" + product.getAttribute ("species"));
							if (spec == null)
								spec = entityMapper.get ("sc" + product.getAttribute ("species"));

							CRNEdge edge = react.getProduct (spec);
							if (edge == null)
							{
								edge = new CRNEdge (react, spec, CRNEdge.PRODUCT, product.getModification (), null, null, null, product);
								react.addProduct (spec, edge);
							}
							else
							{
								edge.addModifications (product.getModification ());
								edge.setTreeB (product);
							}
						}
					
					

					}
				}
				
				else if (dntag.equals ("listOfModifiers"))
				{
					for (TreeNode modi : dn.getChildren ())
					{
						

						DocumentNode modifier = (DocumentNode) modi;
						if (modifier.getTagName ().equals ("ListOfSpeciesReferences"))
						{
							// v3..
							for (TreeNode mod2 : modifier.getChildren ())
							{
								DocumentNode modifier2 = (DocumentNode) mod2;
								CRNNode spec = entityMapper.get ("si" + modifier2.getAttribute ("species"));
								if (spec == null)
									spec = entityMapper.get ("sc" + modifier2.getAttribute ("species"));

									String sbo = modifier2.getAttribute ("sboTerm");
									if (sbo == null)
										sbo = "unknown";
									
								CRNEdge edge = react.getModifier (spec);
								if (edge == null)
								{
									System.out.println ("adde new mod to react " + reactionID);
									edge = new CRNEdge (spec, react, CRNEdge.MODIFIER, modifier2.getModification (), null, sbo, null, modifier2);
									react.addModifier (spec, edge);
								}
								else
								{
									edge.addModifications (modifier2.getModification ());
									edge.setTreeB (modifier2);
									edge.setModB (sbo);
								}
							}
						}
						else
						{
							CRNNode spec = entityMapper.get ("si" + modifier.getAttribute ("species"));
							if (spec == null)
								spec = entityMapper.get ("sc" + modifier.getAttribute ("species"));

								String sbo = modifier.getAttribute ("sboTerm");
								if (sbo == null)
									sbo = "unknown";
								
							CRNEdge edge = react.getModifier (spec);
							if (edge == null)
							{
								System.out.println ("adde new mod to react " + reactionID);
								edge = new CRNEdge (spec, react, CRNEdge.MODIFIER, modifier.getModification (), null, sbo, null, modifier);
								react.addModifier (spec, edge);
							}
							else
							{
								edge.addModifications (modifier.getModification ());
								edge.setTreeB (modifier);
								edge.setModB (sbo);
							}
						}
					}
						
						
						
						

				}
				
				else if (dntag.equals ("kineticLaw"))
				{
					for (TreeNode math : dn.getChildren ())
					{
						DocumentNode mathNode = (DocumentNode) math;
						if (mathNode.getTagName ().equals ("math"))
							react.setKineticLawB (mathNode);
					}
				}
			}
		}
		
		
		return ;
	}
}
