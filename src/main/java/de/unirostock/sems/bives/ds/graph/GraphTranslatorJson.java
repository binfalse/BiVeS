/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.SBOTerm;


/**
 * @author Martin Scharm
 * 
 * TODO: compartments
 * TODO: hierarchy
 *
 */
public class GraphTranslatorJson
	extends GraphTranslator
{
	private JSONArray nodes;
	private JSONArray edges;
	private JSONObject graph;
	
	public GraphTranslatorJson ()
	{
	}
	
	@SuppressWarnings("unchecked")
	private void addNode (String parent, String id, String name, int version, boolean species)
	{
		JSONObject node = new JSONObject ();

		String classes = species ? "species" : "reaction";
		switch (version)
		{
			case 1:
				classes += " bives-inserted";
				break;
			case 2:
				classes += " bives-modified";
				break;
			case -1:
				classes += " bives-deleted";
				break;
		}
		node.put ("classes", classes);
	
		JSONObject data = new JSONObject ();
		data.put ("id", id);
		data.put ("name", name);
		if (parent != null)
			data.put ("parent", parent);
		
		node.put ("data", data);
		
		nodes.add (node);
	}
	
	@SuppressWarnings("unchecked")
	private void addEdge (String from, String to, int version, String modification)
	{

		JSONObject edge = new JSONObject ();

		String classes = "";

		if (modification != null)
		{
			if (modification.equals (SBOTerm.MOD_INHIBITOR))
				classes = "bives-inhibitor";
			else if (modification.equals (SBOTerm.MOD_STIMULATOR))
				classes = "bives-stimulator";
			else if (modification.equals (SBOTerm.MOD_UNKNOWN))
				classes = "bives-unkwnmod";
			else
				classes = "bives-ioedge";
		}
		else
			classes = "bives-ioedge";
		
		
		switch (version)
		{
			case 1:
				classes += " bives-inserted";
				break;
			case 2:
				classes += " bives-modified";
				break;
			case -1:
				classes += " bives-deleted";
				break;
		}
		edge.put ("classes", classes);
	
		JSONObject data = new JSONObject ();
		data.put ("source", from);
		data.put ("target", to);
		
		edge.put ("data", data);
		
		edges.add (edge);
	}
	
	
	@SuppressWarnings("unchecked")
	private void createCompartment (CRNCompartment c)
	{
			JSONObject node = new JSONObject ();

			String classes = "compartment";
			switch (c.getModification ())
			{
				case 1:
					classes += " bives-inserted";
					break;
				case 2:
					classes += " bives-modified";
					break;
				case -1:
					classes += " bives-deleted";
					break;
			}
			node.put ("classes", classes);
		
			JSONObject data = new JSONObject ();
			data.put ("id", c.getId ());
			data.put ("name", c.getLabel ());
			
			node.put ("data", data);
			
			nodes.add (node);
	}
	
	
	@SuppressWarnings("unchecked")
	private void createCompartment (HierarchyNetworkComponent c)
	{
			JSONObject node = new JSONObject ();

			String classes = "compartment";
			switch (c.getModification ())
			{
				case 1:
					classes += " bives-inserted";
					break;
				case 2:
					classes += " bives-modified";
					break;
				case -1:
					classes += " bives-deleted";
					break;
			}
			node.put ("classes", classes);
		
			JSONObject data = new JSONObject ();
			data.put ("id", c.getId ());
			data.put ("name", c.getLabel ());
			
			node.put ("data", data);
			
			nodes.add (node);
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void startNewGraph ()
	{
		graph = new JSONObject ();
		nodes = new JSONArray ();
		edges = new JSONArray ();
		
		JSONObject elements = new JSONObject ();
		elements.put ("nodes", nodes);
		elements.put ("edges", edges);
		
		graph.put ("elements", elements);
	}
	
	public JSONObject getJsonObject ()
	{
		return graph;
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.ds.graph.GraphTranslator#translate(de.unirostock.sems.bives.ds.graph.CRN)
	 */
	@Override
	public String translate (CRN crn)
	{
		startNewGraph ();
		//Vector<String> edges = new Vector<String> ();
		//HashMap<CRNCompartment, Vector<String>> compartments = new HashMap<CRNCompartment, Vector<String>> ();
		for (CRNCompartment c : crn.getCompartments ())
		{
			createCompartment (c);
			//compartments.put (c, new Vector<String> ());
		}
		
		
		
		//dotStr += setNodeTypeSpecies ();
		for (CRNSubstance s : crn.getSubstances ())
		{
			//dotStr += addNode (s.getId (), s.getLabel (), s.getModification (), true);
			
			CRNCompartment compartment = s.getCompartment ();
			if (compartment != null)
				addNode (compartment.getId (), s.getId (), s.getLabel (), s.getModification (), true);
				//compartments.get (compartment).add (addNode (s.getId (), s.getLabel (), s.getModification (), true));
			else
				addNode (null, s.getId (), s.getLabel (), s.getModification (), true);
		}

		//dotStr += setNodeTypeReaction ();
		for (CRNReaction r : crn.getReactions ())
		{
			CRNCompartment compartment = r.getCompartment ();
			if (compartment != null)
				addNode (compartment.getId (), r.getId (), r.getLabel (), r.getModification (), false);
			else
				addNode (null, r.getId (), r.getLabel (), r.getModification (), false);
			
			for (CRNReaction.SubstanceRef s : r.getInputs ())
				addEdge (s.subst.getId (), r.getId (), s.getModification (), SBOTerm.MOD_NONE);
			
			for (CRNReaction.SubstanceRef s : r.getOutputs ())
				addEdge (r.getId (), s.subst.getId (), s.getModification (), SBOTerm.MOD_NONE);
			
			for (CRNReaction.ModifierRef s : r.getModifiers ())
			{
				if (s.getModification () == CRN.MODIFIED)
				{
					addEdge (s.subst.getId (), r.getId (), CRN.DELETE, s.getModTermA ());
					addEdge (s.subst.getId (), r.getId (), CRN.INSERT, s.getModTermB ());
				}
				else
					addEdge (s.subst.getId (), r.getId (), s.getModification (), s.getModTerm ());
			}
		}
		
		return graph.toJSONString ();
	}

	@Override
	public String translate (HierarchyNetwork hn)
	{
		startNewGraph ();
		//HashMap<HierarchyNetworkComponent, Vector<String>> components = new HashMap<HierarchyNetworkComponent, Vector<String>> ();

		//LOGGER.info ("translating");
		
		
		Collection<HierarchyNetworkComponent> components = hn.getComponents ();
		for (HierarchyNetworkComponent c : components)
		{
			//LOGGER.info ("component: " + c.getId ());
			createCompartment (c);

			Vector<HierarchyNetworkVariable> vars = c.getVariables ();
			for (HierarchyNetworkVariable var : vars)
			{
				//LOGGER.info ("var: " + var.getId ());
				addNode (c.getId (), var.getId (), var.getLabel (), var.getModification (), false);
				
				
				//Element vNode = varMapper.get (var);
				HashMap<HierarchyNetworkVariable, HierarchyNetworkVariable.VarConnection> cons = var.getConnections ();
				
				for (HierarchyNetworkVariable con : cons.keySet ())
				{
					//LOGGER.info ("connecting var: " + var.getId () + " -> " + con.getId ());
					addEdge (con.getId (), var.getId (), cons.get (con).getModificationInt (), SBOTerm.MOD_NONE);
				}
			}
			
			
			HierarchyNetworkComponent parA = c.getParentA (), parB = c.getParentB ();
			if (parA != null || parB != null)
			{
				if (parA == parB)
				{
					// connect w/o mod
					addEdge (parA.getId (), c.getId (), CRN.UNMODIFIED, SBOTerm.MOD_NONE);
				}
				else
				{
					if (parA != null)
					{
						// connect delete
						addEdge (parA.getId (), c.getId (), CRN.DELETE, SBOTerm.MOD_NONE);
					}
					if (parB != null)
					{
						// connect insert
						addEdge (parA.getId (), c.getId (), CRN.INSERT, SBOTerm.MOD_NONE);
					}
				}
			}
			
		}

		return graph.toJSONString ();
	}
}
