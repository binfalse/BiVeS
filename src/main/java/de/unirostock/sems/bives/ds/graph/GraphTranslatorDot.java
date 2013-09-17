/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

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
import de.unirostock.sems.bives.ds.SBOTerm;
import de.unirostock.sems.bives.markup.Typesetting;
import de.unirostock.sems.bives.tools.Tools;
import de.unirostock.sems.bives.tools.XmlTools;


/**
 * @author Martin Scharm
 * 
 * TODO: compartments
 * TODO: hierarchy
 *
 */
public class GraphTranslatorDot
	extends GraphTranslator
{
	private String dotStr;
	
	public GraphTranslatorDot () throws ParserConfigurationException
	{
		dotStr = "";
	}
	
	private static String getDotPostamble ()
	{
		return "\tlabel=\"Diff Graph created by BiVeS\";"+Typesetting.NL_TXT+"}";
	}
	
	private static String getDotPreamble ()
	{
		return "##Command to produce the pic: `neato -Tpng thisfile > thisfile.png`"+Typesetting.NL_TXT+Typesetting.NL_TXT+
			"digraph BiVeSexport {" + Typesetting.NL_TXT + "\tgraph [overlap=false];" + Typesetting.NL_TXT + "\tedge [len=1.3];"
			+ Typesetting.NL_TXT + "\tnode [fontsize=11];"+Typesetting.NL_TXT;
	}
	
	/*private static String setNodeTypeSpecies ()
	{
		return "\tnode [shape=circle];" + Typesetting.NL_TXT;
	}
	
	private static String setNodeTypeReaction ()
	{
		return "\tnode [shape=diamond];" + Typesetting.NL_TXT;
	}*/
	
	private static String addNode (String id, String name, int version, boolean species)
	{
		String ret = id + "[label=\""+name+"\"";
		switch (version)
		{
			case 1:
				ret += ",color=blue";
				break;
			case 2:
				ret += ",color=yellow";
				break;
			case -1:
				ret += ",color=red";
				break;
		}
		
		if (species)
			ret += ",shape=circle";
		else
			ret += ",shape=diamond";
			
		ret += "];" + Typesetting.NL_TXT;
		return "\t"+ret;
	}
	
	private static String addEdge (String from, String to, int version, String modification)
	{
		String ret = from + "->" + to;
		String sub = null;
		switch (version)
		{
			case 1:
				sub = "color=blue";
				break;
			case 2:
				sub = "color=yellow";
				break;
			case -1:
				sub = "color=red";
				break;
		}
		String sub2 = null;
		if (modification != null)
		{
			if (modification.equals (SBOTerm.MOD_INHIBITOR))
				sub2 = "style=dashed,arrowType=tee";
			else if (modification.equals (SBOTerm.MOD_STIMULATOR))
				sub2 = "style=dashed,arrowType=normal";
			else if (modification.equals (SBOTerm.MOD_UNKNOWN))
				sub2 = "style=dashed,arrowType=odot";
		}
		
		if (sub != null && sub2 != null)
			ret += "["+sub+","+sub2+"]";
		else if (sub != null)
			ret += "["+sub+"]";
		else if (sub2 != null)
			ret += "["+sub2+"]";
			
		
		ret += ";" + Typesetting.NL_TXT;
		return "\t"+ret;
		
	}
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.ds.graph.GraphTranslator#translate(de.unirostock.sems.bives.ds.graph.CRN)
	 */
	@Override
	public String translate (CRN crn)
	{
		dotStr = getDotPreamble ();
		
		Vector<String> edges = new Vector<String> ();
		HashMap<CRNCompartment, Vector<String>> compartments = new HashMap<CRNCompartment, Vector<String>> ();
		for (CRNCompartment c : crn.getCompartments ())
		{
			compartments.put (c, new Vector<String> ());
		}
		
		
		
		//dotStr += setNodeTypeSpecies ();
		for (CRNSubstance s : crn.getSubstances ())
		{
			//dotStr += addNode (s.getId (), s.getLabel (), s.getModification (), true);
			
			CRNCompartment compartment = s.getCompartment ();
			if (compartment != null)
				compartments.get (compartment).add (addNode (s.getId (), s.getLabel (), s.getModification (), true));
			else
				dotStr += addNode (s.getId (), s.getLabel (), s.getModification (), true);
		}

		//dotStr += setNodeTypeReaction ();
		for (CRNReaction r : crn.getReactions ())
		{
			CRNCompartment compartment = r.getCompartment ();
			if (compartment != null)
				compartments.get (compartment).add (addNode (r.getId (), r.getLabel (), r.getModification (), false));
			else
				dotStr += addNode (r.getId (), r.getLabel (), r.getModification (), false);
			
			for (CRNReaction.SubstanceRef s : r.getInputs ())
				edges.add (addEdge (s.subst.getId (), r.getId (), s.getModification (), SBOTerm.MOD_NONE));
			
			for (CRNReaction.SubstanceRef s : r.getOutputs ())
				edges.add (addEdge (r.getId (), s.subst.getId (), s.getModification (), SBOTerm.MOD_NONE));
			
			for (CRNReaction.ModifierRef s : r.getModifiers ())
			{
				if (s.getModification () == CRN.MODIFIED)
				{
					edges.add (addEdge (s.subst.getId (), r.getId (), CRN.DELETE, s.getModTermA ()));
					edges.add (addEdge (s.subst.getId (), r.getId (), CRN.INSERT, s.getModTermB ()));
				}
				else
					edges.add (addEdge (s.subst.getId (), r.getId (), s.getModification (), s.getModTerm ()));
			}
		}
		
		for (CRNCompartment compartment : compartments.keySet ())
		{
			dotStr += createCompartment (compartment, compartments.get (compartment));
		}
		
		for (String e : edges)
			dotStr += e;
		
		dotStr += getDotPostamble ();
		
		return dotStr;
	}

	private String createCompartment (CRNCompartment compartment,
		Vector<String> nodeVector)
	{
		String ret = "\tsubgraph cluster" + compartment.getId () + " {" + Typesetting.NL_TXT;
		ret += "\t\tlabel = \""+compartment.getLabel ()+"\";" + Typesetting.NL_TXT;
		ret += "\t\tcolor=lightgrey;" + Typesetting.NL_TXT;
		for (String n : nodeVector)
			ret += "\t" + n;
		ret += "\t}" + Typesetting.NL_TXT;
		return ret;
	}

	@Override
	public String translate (HierarchyNetwork hn)
	{
		dotStr = getDotPreamble ();
		//HashMap<HierarchyNetworkComponent, Vector<String>> components = new HashMap<HierarchyNetworkComponent, Vector<String>> ();
		Collection<HierarchyNetworkComponent> components = hn.getComponents ();
		for (HierarchyNetworkComponent c : components)
		{
			//components.put (c, new Vector<String> ());
			dotStr += "\tsubgraph cluster" + c.getId () + " {" + Typesetting.NL_TXT;
			dotStr += "\t\tlabel = \""+c.getLabel ()+"\";" + Typesetting.NL_TXT;
			dotStr += "\t\tcolor=lightgrey;" + Typesetting.NL_TXT;

			Vector<HierarchyNetworkVariable> vars = c.getVariables ();
			for (HierarchyNetworkVariable var : vars)
			{
				dotStr += "\t" + addNode (var.getId (), var.getLabel (), var.getModification (), true);
			}
			dotStr += "\t}" + Typesetting.NL_TXT;
		}
		
		//HashMap<HierarchyNetworkComponent, Element> componentMapper = new HashMap<HierarchyNetworkComponent, Element> ();
		//HashMap<HierarchyNetworkVariable, Element> varMapper = new HashMap<HierarchyNetworkVariable, Element> ();
		
		/*Collection<HierarchyNetworkComponent> components = hn.getComponents ();
		for (HierarchyNetworkComponent comp : components)
		{
			LOGGER.info ("creating comp: " + comp.getId ());
			Element node = createGraphMLNode (graphRoot, comp.getId (), null, comp.getLabel (), comp.getModification () + "");
			Element subtree = createGraphRoot (true);
			node.appendChild (subtree);
			//componentMapper.put (comp, node);
			
			Vector<HierarchyNetworkVariable> vars = comp.getVariables ();
			for (HierarchyNetworkVariable var : vars)
			{
				LOGGER.info ("creating var: " + var.getId ());
				Element vNode = createGraphMLNode (graphRoot, var.getId (), null, var.getLabel (), var.getModification () + "");
				subtree.appendChild (vNode);
				//varMapper.put (var, vNode);
			}
		}*/
		

		for (HierarchyNetworkComponent comp : components)
		{
			//Element node = componentMapper.get (comp);
			
			HierarchyNetworkComponent parA = comp.getParentA (), parB = comp.getParentB ();
			if (parA != null || parB != null)
			{
				if (parA == parB)
				{
					// connect w/o mod
					dotStr += addEdge ("cluster" + parA.getId (), "cluster" + comp.getId (), CRN.UNMODIFIED, SBOTerm.MOD_NONE);
				}
				else
				{
					if (parA != null)
					{
						// connect delete
						dotStr += addEdge ("cluster" + parA.getId (), "cluster" + comp.getId (), CRN.DELETE, SBOTerm.MOD_NONE);
					}
					if (parB != null)
					{
						// connect insert
						dotStr += addEdge ("cluster" + parA.getId (), "cluster" + comp.getId (), CRN.INSERT, SBOTerm.MOD_NONE);
					}
				}
			}
			
			Vector<HierarchyNetworkVariable> vars = comp.getVariables ();
			for (HierarchyNetworkVariable var : vars)
			{
				//Element vNode = varMapper.get (var);
				HashMap<HierarchyNetworkVariable, HierarchyNetworkVariable.VarConnection> cons = var.getConnections ();
				
				for (HierarchyNetworkVariable con : cons.keySet ())
				{
					LOGGER.info ("connecting var: " + var.getId () + " -> " + con.getId ());
					dotStr += addEdge (con.getId (), var.getId (), cons.get (con).getModificationInt (), SBOTerm.MOD_NONE);
				}
			}
		}

		dotStr += getDotPostamble ();
		return dotStr;
	}
}
