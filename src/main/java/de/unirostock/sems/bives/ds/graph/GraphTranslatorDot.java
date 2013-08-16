/**
 * 
 */
package de.unirostock.sems.bives.ds.graph;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

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
	
	private static String setNodeTypeSpecies ()
	{
		return "\tnode [shape=circle];" + Typesetting.NL_TXT;
	}
	
	private static String setNodeTypeReaction ()
	{
		return "\tnode [shape=diamond];" + Typesetting.NL_TXT;
	}
	
	private static String addNode (String id, String name, int version)
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
		return "\t\t"+ret;
		
	}
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.ds.graph.GraphTranslator#translate(de.unirostock.sems.bives.ds.graph.CRN)
	 */
	@Override
	public String translate (CRN crn)
	{
		dotStr = getDotPreamble ();
		
		dotStr += setNodeTypeSpecies ();
		for (CRNSubstance s : crn.getSubstances ())
		{
			dotStr += addNode (s.getId (), s.getLabel (), s.getModification ());
		}

		dotStr += setNodeTypeReaction ();
		for (CRNReaction r : crn.getReactions ())
		{
			dotStr += addNode (r.getId (), r.getLabel (), r.getModification ());
			
			for (CRNReaction.SubstanceRef s : r.getInputs ())
				dotStr += addEdge (s.subst.getId (), r.getId (), s.getModification (), SBOTerm.MOD_NONE);
			
			for (CRNReaction.SubstanceRef s : r.getOutputs ())
				dotStr += addEdge (r.getId (), s.subst.getId (), s.getModification (), SBOTerm.MOD_NONE);
			
			for (CRNReaction.ModifierRef s : r.getModifiers ())
			{
				if (s.getModification () == CRN.MODIFIED)
				{
					dotStr += addEdge (s.subst.getId (), r.getId (), CRN.DELETE, s.getModTermA ());
					dotStr += addEdge (s.subst.getId (), r.getId (), CRN.INSERT, s.getModTermB ());
				}
				else
					dotStr += addEdge (s.subst.getId (), r.getId (), s.getModification (), s.getModTerm ());
			}
		}
		
		dotStr += getDotPostamble ();
		
		return dotStr;
	}

	@Override
	public Object translate (HierarchyNetwork hn) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
}
