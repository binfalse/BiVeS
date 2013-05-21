/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbmldeprecated;

import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class SBMLModelViz
{
	private final static Logger LOGGER = Logger.getLogger(SBMLModelViz.class.getName());
	public SBMLModelViz ()
	{
		
	}
	
	public String getGraphML (TreeDocument doc) throws ParserConfigurationException
	{
		ChemicalReactionNetwork crn = new ChemicalReactionNetwork ();
		HashMap<String, CRNNode> entityMapper = new HashMap<String, CRNNode> ();

		// species
		LOGGER.info ("searching for species in A");
		for (DocumentNode ds : doc.getNodesByTag ("species"))
		{

			LOGGER.info ("species: " + ds.getXPath ());

			String name = ds.getAttribute ("name");
			if (name == null)
				name = ds.getId ();
			
			CRNSpecies spec = new CRNSpecies (name, null, ds.getModification (), ds, null);
			entityMapper.put ("s" + ds.getId (), spec);
			crn.addSpecies (spec);
		}
		
		// reactions
		for (DocumentNode reaction : doc.getNodesByTag ("reaction"))
		{
			String reactionID = reaction.getAttribute ("id");
			String name = reaction.getAttribute ("name");
			if (name == null)
				name = reactionID;
			
			// new node in crn
			CRNReaction react = new CRNReaction (name, name, reaction.getModification (), reaction.getAttribute ("reversible"), reaction.getAttribute ("fast"), reaction, reaction);
			crn.addReaction (react);
			

			entityMapper.put ("r" + reactionID, react);
			

			for (TreeNode c: reaction.getChildren ())
			{
				DocumentNode dn = (DocumentNode) c;
				String dntag = dn.getTagName ();

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
								CRNNode spec = entityMapper.get ("s" + reactant2.getAttribute ("species"));
								
								CRNEdge edge = new CRNEdge (spec, react, CRNEdge.REACTANT, reactant2.getModification (), null, null, reactant2, reactant2);
								react.addReactant (spec, edge);
							}
						}
						else
						{
							CRNNode spec = entityMapper.get ("s" + reactant.getAttribute ("species"));
							
							CRNEdge edge = new CRNEdge (spec, react, CRNEdge.REACTANT, reactant.getModification (), null, null, reactant, reactant);
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
								CRNNode spec = entityMapper.get ("s" + product2.getAttribute ("species"));
								
								CRNEdge edge = new CRNEdge (react, spec, CRNEdge.PRODUCT, product2.getModification (), null, null, product2, product2);
								react.addReactant (spec, edge);
							}
						}
						else
						{
							CRNNode spec = entityMapper.get ("s" + product.getAttribute ("species"));
	
							CRNEdge edge = new CRNEdge (react, spec, CRNEdge.PRODUCT, product.getModification (), null, null, product, product);
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
								CRNNode spec = entityMapper.get ("s" + modifier2.getAttribute ("species"));
								
								String sbo = modifier2.getAttribute ("sboTerm");
								if (sbo == null)
									sbo = "unknown";
								
								CRNEdge edge = new CRNEdge (spec, react, CRNEdge.MODIFIER, modifier2.getModification (), sbo, sbo, modifier2, modifier2);
								react.addReactant (spec, edge);
							}
						}
						else
						{
							CRNNode spec = entityMapper.get ("s" + modifier.getAttribute ("species"));
							
							String sbo = modifier.getAttribute ("sboTerm");
							if (sbo == null)
								sbo = "unknown";
	
							CRNEdge edge = new CRNEdge (spec, react, CRNEdge.MODIFIER, modifier.getModification (), sbo, sbo, modifier, modifier);
							react.addModifier (spec, edge);
						}
					}
				}
			}
		}
		return crn.getGraphML ();
	}
}
