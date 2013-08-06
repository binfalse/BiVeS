/**
 * 
 */
package de.unirostock.sems.bives.ds.sbml;

import java.util.HashMap;
import java.util.Vector;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;


/**
 * @author Martin Scharm
 *
 */
public class SBMLModel
	extends SBMLSBase
{
	private SBMLDocument document;

	private HashMap<TreeNode, SBMLSBase> nodeMapper;
	
	private HashMap<String, SBMLFunctionDefinition> listOfFunctionDefinitions;
	private HashMap<String, SBMLUnitDefinition> listOfUnitDefinitions;
	private HashMap<String, SBMLCompartment> listOfCompartments;
	private HashMap<String, SBMLCompartmentType> listOfCompartmentTypes;
	private HashMap<String, SBMLSpecies> listOfSpecies;
	private HashMap<String, SBMLSpeciesType> listOfSpeciesTypes;
	private HashMap<String, SBMLParameter> listOfParameters;
	private Vector<SBMLInitialAssignment> listOfInitialAssignments;
	private Vector<SBMLRule> listOfRules;
	private Vector<SBMLConstraint> listOfConstraints;
	private HashMap<String, SBMLReaction> listOfReactions;
	private Vector<SBMLEvent> listOfEvents;
	private HashMap<String, SBMLSimpleSpeciesReference> listOfSpeciesReferences;

	private String id; //optional
	private String name; //optional
	private SBMLUnitDefinition substanceUnits; //optional
	private SBMLUnitDefinition timeUnits; //optional
	private SBMLUnitDefinition volumeUnits; //optional
	private SBMLUnitDefinition areaUnits; //optional
	private SBMLUnitDefinition lengthUnits; //optional
	private SBMLUnitDefinition extentUnits; //optional
	private SBMLParameter conversionFactor; //optional
	
	/**
	 * @param documentNode
	 * @param sbmlDocument
	 * @throws BivesSBMLParseException
	 * @throws BivesConsistencyException 
	 */
	public SBMLModel (DocumentNode documentNode, SBMLDocument sbmlDocument)
		throws BivesSBMLParseException, BivesConsistencyException
	{
		super (documentNode, null);
		sbmlModel = this;
		this.document = sbmlDocument;
		
		nodeMapper = new HashMap<TreeNode, SBMLSBase> ();
		
		listOfFunctionDefinitions = new  HashMap<String, SBMLFunctionDefinition> ();
		listOfUnitDefinitions = new  HashMap<String, SBMLUnitDefinition> ();
		listOfCompartments = new  HashMap<String, SBMLCompartment> ();
		listOfCompartmentTypes = new  HashMap<String, SBMLCompartmentType> ();
		listOfSpecies = new  HashMap<String, SBMLSpecies> ();
		listOfSpeciesTypes = new  HashMap<String, SBMLSpeciesType> ();
		listOfParameters = new  HashMap<String, SBMLParameter> ();
		listOfInitialAssignments = new  Vector<SBMLInitialAssignment> ();
		listOfRules = new  Vector<SBMLRule> ();
		listOfConstraints = new  Vector<SBMLConstraint> ();
		listOfReactions = new  HashMap<String, SBMLReaction> ();
		listOfEvents = new  Vector<SBMLEvent> ();
		listOfSpeciesReferences = new  HashMap<String, SBMLSimpleSpeciesReference> ();
		
		parseTree ();
		
	}
	
	private void parseTree () throws BivesSBMLParseException, BivesConsistencyException
	{
		DocumentNode modelRoot = documentNode;
		
		// sequence important!
		parseFunctions (modelRoot);
		parseUnits (modelRoot);
		parseCompartmentTypes (modelRoot);
		parseCompartments (modelRoot);
		parseParameters(modelRoot);
		parseSpeciesTypes (modelRoot);
		parseSpecies (modelRoot);
		parseReactions (modelRoot);
		parseInitialAssignments (modelRoot);
		parseRules (modelRoot);
		parseConstraints (modelRoot);
		parseEvents (modelRoot);
		
		parseModelRoot (modelRoot);
	}

	private void parseModelRoot (DocumentNode modelRoot) throws BivesSBMLParseException
	{
		id = documentNode.getAttribute ("id");
		name = documentNode.getAttribute ("name");
		
		if (documentNode.getAttribute ("substanceUnits") != null)
		{
			String tmp = documentNode.getAttribute ("substanceUnits");
			substanceUnits = sbmlModel.getUnitDefinition (tmp);
			if (substanceUnits == null)
				throw new BivesSBMLParseException ("substanceUnits attribute in model root not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("timeUnits") != null)
		{
			String tmp = documentNode.getAttribute ("timeUnits");
			timeUnits = sbmlModel.getUnitDefinition (tmp);
			if (timeUnits == null)
				throw new BivesSBMLParseException ("timeUnits attribute in model root not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("volumeUnits") != null)
		{
			String tmp = documentNode.getAttribute ("volumeUnits");
			volumeUnits = sbmlModel.getUnitDefinition (tmp);
			if (volumeUnits == null)
				throw new BivesSBMLParseException ("volumeUnits attribute in model root not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("areaUnits") != null)
		{
			String tmp = documentNode.getAttribute ("areaUnits");
			areaUnits = sbmlModel.getUnitDefinition (tmp);
			if (areaUnits == null)
				throw new BivesSBMLParseException ("areaUnits attribute in model root not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("lengthUnits") != null)
		{
			String tmp = documentNode.getAttribute ("lengthUnits");
			lengthUnits = sbmlModel.getUnitDefinition (tmp);
			if (lengthUnits == null)
				throw new BivesSBMLParseException ("lengthUnits attribute in model root not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("extentUnits") != null)
		{
			String tmp = documentNode.getAttribute ("extentUnits");
			extentUnits = sbmlModel.getUnitDefinition (tmp);
			if (extentUnits == null)
				throw new BivesSBMLParseException ("extentUnits attribute in model root not defined: " + tmp);
		}
		
		if (documentNode.getAttribute ("conversionFactor") != null)
		{
			String tmp = documentNode.getAttribute ("conversionFactor");
			conversionFactor = sbmlModel.getParameter (tmp);
			if (conversionFactor == null)
				throw new BivesSBMLParseException ("conversionFactor attribute in model root not defined: " + tmp);
		}
		
	}

	private void parseEvents (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> loss = root.getChildrenWithTag ("listOfEvents");
		for (int i = 0; i < loss.size (); i++)
		{
			DocumentNode los = (DocumentNode) loss.elementAt (i);
			
			Vector<TreeNode> node = los.getChildrenWithTag ("event");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLEvent n = new SBMLEvent ((DocumentNode) node.elementAt (j), this);
				listOfEvents.add (n);
			}
		}
	}

	private void parseReactions (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> loss = root.getChildrenWithTag ("listOfReactions");
		for (int i = 0; i < loss.size (); i++)
		{
			DocumentNode los = (DocumentNode) loss.elementAt (i);
			
			Vector<TreeNode> node = los.getChildrenWithTag ("reaction");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLReaction n = new SBMLReaction ((DocumentNode) node.elementAt (j), this);
				listOfReactions.put (n.getID (), n);
			}
		}
		
	}

	private void parseConstraints (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> loss = root.getChildrenWithTag ("listOfConstraints");
		for (int i = 0; i < loss.size (); i++)
		{
			DocumentNode los = (DocumentNode) loss.elementAt (i);
			
			Vector<TreeNode> node = los.getChildrenWithTag ("constraint");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLConstraint n = new SBMLConstraint ((DocumentNode) node.elementAt (j), this);
				listOfConstraints.add (n);
			}
		}
		
	}

	private void parseRules (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> loss = root.getChildrenWithTag ("listOfRules");
		for (int i = 0; i < loss.size (); i++)
		{
			DocumentNode los = (DocumentNode) loss.elementAt (i);
			
			Vector<TreeNode> node = los.getChildrenWithTag ("algebraicRule");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLAlgebraicRule n = new SBMLAlgebraicRule ((DocumentNode) node.elementAt (j), this);
				listOfRules.add (n);
			}
			
			node = los.getChildrenWithTag ("assignmentRule");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLAssignmentRule n = new SBMLAssignmentRule ((DocumentNode) node.elementAt (j), this);
				listOfRules.add (n);
			}
			
			node = los.getChildrenWithTag ("rateRule");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLRateRule n = new SBMLRateRule ((DocumentNode) node.elementAt (j), this);
				listOfRules.add (n);
			}
		}
	}

	private void parseInitialAssignments (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> loss = root.getChildrenWithTag ("listOfInitialAssignments");
		for (int i = 0; i < loss.size (); i++)
		{
			DocumentNode los = (DocumentNode) loss.elementAt (i);
			
			Vector<TreeNode> node = los.getChildrenWithTag ("initialAssignment");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLInitialAssignment n = new SBMLInitialAssignment ((DocumentNode) node.elementAt (j), this);
				listOfInitialAssignments.add (n);
			}
		}
	}

	private void parseSpecies (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> lospeciess = root.getChildrenWithTag ("listOfSpecies");
		for (int i = 0; i < lospeciess.size (); i++)
		{
			DocumentNode lospecies = (DocumentNode) lospeciess.elementAt (i);
			
			Vector<TreeNode> species = lospecies.getChildrenWithTag ("species");
			for (int j = 0; j < species.size (); j++)
			{
				SBMLSpecies s = new SBMLSpecies ((DocumentNode) species.elementAt (j), this);
				listOfSpecies.put (s.getID (), s);
			}
		}
	}

	private void parseSpeciesTypes (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> loss = root.getChildrenWithTag ("listOfSpeciesTypes");
		for (int i = 0; i < loss.size (); i++)
		{
			DocumentNode los = (DocumentNode) loss.elementAt (i);
			
			Vector<TreeNode> node = los.getChildrenWithTag ("speciesType");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLSpeciesType n = new SBMLSpeciesType ((DocumentNode) node.elementAt (j), this);
				listOfSpeciesTypes.put (n.getID (), n);
			}
		}
	}

	private void parseParameters (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> loss = root.getChildrenWithTag ("listOfParameters");
		for (int i = 0; i < loss.size (); i++)
		{
			DocumentNode los = (DocumentNode) loss.elementAt (i);
			
			Vector<TreeNode> node = los.getChildrenWithTag ("parameter");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLParameter n = new SBMLParameter ((DocumentNode) node.elementAt (j), this);
				listOfParameters.put (n.getID (), n);
			}
		}
	}

	private void parseCompartments (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> locompartments = root.getChildrenWithTag ("listOfCompartments");
		for (int i = 0; i < locompartments.size (); i++)
		{
			DocumentNode locompartment = (DocumentNode) locompartments.elementAt (i);
			
			Vector<TreeNode> compartments = locompartment.getChildrenWithTag ("compartment");
			for (int j = 0; j < compartments.size (); j++)
			{
				SBMLCompartment c = new SBMLCompartment ((DocumentNode) compartments.elementAt (j), this);
				listOfCompartments.put (c.getID (), c);
			}
		}
	}

	private void parseCompartmentTypes (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> loss = root.getChildrenWithTag ("listOfCompartmentTypes");
		for (int i = 0; i < loss.size (); i++)
		{
			DocumentNode los = (DocumentNode) loss.elementAt (i);
			
			Vector<TreeNode> node = los.getChildrenWithTag ("compartmentType");
			for (int j = 0; j < node.size (); j++)
			{
				SBMLCompartmentType n = new SBMLCompartmentType ((DocumentNode) node.elementAt (j), this);
				listOfCompartmentTypes.put (n.getID (), n);
			}
		}
	}

	private void parseUnits (DocumentNode root) throws BivesSBMLParseException, BivesConsistencyException
	{
		String [] baseUnits = new String [] {"substance", "volume", "area", "length", "ampere", "farad", "joule", "lux", "radian", "volt", "avogadro", "gram", "katal", "metre", "second", "watt", "becquerel", "gray", "kelvin", "mole", "siemens", "weber", "candela", "henry", "kilogram", "newton", "sievert", "coulomb", "hertz", "litre", "ohm", "steradian", "dimensionless", "item", "lumen", "pascal", "tesla"};
		for (int i = 0; i < baseUnits.length; i++)
		{
			SBMLUnitDefinition ud = new SBMLUnitDefinition (baseUnits[i], this);
			listOfUnitDefinitions.put (ud.getID (), ud);
		}
		
		Vector<TreeNode> lounits = root.getChildrenWithTag ("listOfUnitDefinitions");
		for (int i = 0; i < lounits.size (); i++)
		{
			DocumentNode lounit = (DocumentNode) lounits.elementAt (i);
			
			Vector<TreeNode> units = lounit.getChildrenWithTag ("unitDefinition");
			Vector<String> problems = new Vector<String> ();
			boolean nextRound = true;
			while (nextRound && units.size () > 0)
			{
				nextRound = false;
				problems.clear ();
				for (int j = units.size () - 1; j >= 0; j--)
				{
					SBMLUnitDefinition ud = null;
					try
					{
						ud = new SBMLUnitDefinition ((DocumentNode) units.elementAt (j), this);
						String id = ud.getID ();
						if (listOfUnitDefinitions.get (id) != null)
						{
							if (id.equals ("substance") || id.equals ("volume") || id.equals ("area") || id.equals ("length"))
								LOGGER.warn ("std unit " + id + " redefined");
							else
								throw new BivesSBMLParseException ("Multiple definitions of unit " + ud.getID ());
						}
						//System.out.println ("adde unit " + id);
						listOfUnitDefinitions.put (id, ud);
						units.remove (j);
						nextRound = true;
					}
					catch (BivesConsistencyException ex)
					{
						problems.add (ex.getMessage ());
						continue;
					}
				}
			}
			if (units.size () != 0)
				throw new BivesConsistencyException ("inconsistencies for "+units.size ()+" units, problems: " + problems);
		}
	}

	private void parseFunctions (DocumentNode root) throws BivesSBMLParseException
	{
		Vector<TreeNode> lofunctions = root.getChildrenWithTag ("listOfFunctionDefinitions");
		for (int i = 0; i < lofunctions.size (); i++)
		{
			DocumentNode lofunction = (DocumentNode) lofunctions.elementAt (i);
			
			Vector<TreeNode> functions = lofunction.getChildrenWithTag ("functionDefinition");
			for (int j = 0; j < functions.size (); j++)
			{
				SBMLFunctionDefinition fd = new SBMLFunctionDefinition ((DocumentNode) functions.elementAt (j), this);
				listOfFunctionDefinitions.put (fd.getID (), fd);
			}
		}
	}

	
	public HashMap<String, SBMLFunctionDefinition> getFunctionDefinitions ()
	{
		return listOfFunctionDefinitions;
	}
	
	public HashMap<String, SBMLUnitDefinition> getUnitDefinitions ()
	{
		return listOfUnitDefinitions;
	}
	
	public SBMLUnitDefinition getUnitDefinition (String kind)
	{
		return listOfUnitDefinitions.get (kind);
	}
	
	public SBMLCompartmentType getCompartmentType (String id)
	{
		return listOfCompartmentTypes.get (id);
	}
	
	public HashMap<String, SBMLCompartmentType> getCompartmentTypes ()
	{
		return listOfCompartmentTypes;
	}
	
	public HashMap<String, SBMLCompartment> getCompartments ()
	{
		return listOfCompartments;
	}
	
	public SBMLCompartment getCompartment (String id)
	{
		return listOfCompartments.get (id);
	}
	
	public SBMLSpecies getSpecies (String id)
	{
		return listOfSpecies.get (id);
	}
	
	public HashMap<String, SBMLSpecies> getSpecies ()
	{
		return listOfSpecies;
	}
	
	public SBMLSpeciesType getSpeciesType (String id)
	{
		return listOfSpeciesTypes.get (id);
	}
	
	public HashMap<String, SBMLSpeciesType> getSpeciesTypes ()
	{
		return listOfSpeciesTypes;
	}
	
	public HashMap<String, SBMLParameter> getParameters ()
	{
		return listOfParameters;
	}
	
	public SBMLParameter getParameter (String id)
	{
		return listOfParameters.get (id);
	}
	
	public void registerSpeciesReference (String id, SBMLSimpleSpeciesReference ref)
	{
		listOfSpeciesReferences.put (id, ref);
	}
	
	public SBMLSimpleSpeciesReference getSpeciesReference (String id)
	{
		// search for species reference w/ spec. id
		return listOfSpeciesReferences.get (id);
	}
	public SBMLReaction getReaction (String id)
	{
		return listOfReactions.get (id);
	}
	public HashMap<String, SBMLReaction> getReactions ()
	{
		return listOfReactions;
	}
	
	public Vector<SBMLConstraint> getConstraints()
	{
		return listOfConstraints;
	}
	
	public Vector<SBMLInitialAssignment> getInitialAssignments()
	{
		return listOfInitialAssignments;
	}
	
	public Vector<SBMLEvent> getEvents ()
	{
		return listOfEvents;
	}
	
	public Vector<SBMLRule> getRules ()
	{
		return listOfRules;
	}
	
	public String getID ()
	{
		return id;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public void mapNode (DocumentNode node, SBMLSBase sbase)
	{
		nodeMapper.put (node, sbase);
	}
	
	public SBMLSBase getFromNode (TreeNode node)
	{
		return nodeMapper.get (node);
	}
	
	public static String getSidName (SBMLSBase ref)
	{
		if (ref instanceof SBMLParameter)
			return ((SBMLParameter) ref).getNameAndId ();
		if (ref instanceof SBMLSpecies)
			return ((SBMLSpecies) ref).getNameAndId ();
		if (ref instanceof SBMLCompartment)
			return ((SBMLCompartment) ref).getNameAndId ();
		if (ref instanceof SBMLSimpleSpeciesReference)
			return ((SBMLSimpleSpeciesReference) ref).getSpecies ().getNameAndId ();
		return null;
	}
}
