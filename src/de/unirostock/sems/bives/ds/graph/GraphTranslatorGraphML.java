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
import de.unirostock.sems.bives.tools.Tools;
import de.unirostock.sems.bives.tools.XmlTools;


/**
 * @author Martin Scharm
 *
 */
public class GraphTranslatorGraphML
	extends GraphTranslator
{
	
	private Element graphRoot;
	private Document graphDocument;
	
	public GraphTranslatorGraphML () throws ParserConfigurationException
	{
		graphDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		graphRoot = addGraphMLPreamble (graphDocument);
		
	}
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.ds.graph.GraphTranslator#translate(de.unirostock.sems.bives.ds.graph.CRN)
	 */
	@Override
	public String translate (CRN crn)
	{
		for (CRNSubstance s : crn.getSubstances ())
		{
			createGraphMLNode (graphRoot, s.getId (), "species", s.getLabel (), s.getModification () + "");
		}
		
		for (CRNReaction r : crn.getReactions ())
		{
			createGraphMLNode (graphRoot, r.getId (), "reaction", r.getLabel (), r.getModification () + "");
			
			for (CRNReaction.SubstanceRef s : r.getInputs ())
				createEdge (graphRoot, s.subst.getId (), r.getId (), s.getModification () + "", SBOTerm.MOD_NONE);
			
			for (CRNReaction.SubstanceRef s : r.getOutputs ())
				createEdge (graphRoot, r.getId (), s.subst.getId (), s.getModification () + "", SBOTerm.MOD_NONE);
			
			for (CRNReaction.ModifierRef s : r.getModifiers ())
			{
				if (s.getModification () == CRN.MODIFIED)
				{
					createEdge (graphRoot, s.subst.getId (), r.getId (), CRN.DELETE + "", s.getModTermA ());
					createEdge (graphRoot, s.subst.getId (), r.getId (), CRN.INSERT + "", s.getModTermB ());
				}
				else
					createEdge (graphRoot, s.subst.getId (), r.getId (), s.getModification () + "", s.getModTerm ());
			}
		}
		
		try
		{
			return XmlTools.prettyPrintDocument (graphDocument);
		}
		catch (IOException | TransformerException e)
		{
			LOGGER.error ("error printing graphml", e);
		}
		return null;
	}

	/**
	 * Creates the preamble of the graph. Creates some nodes in the document defining the graph and its properties. Creates also the <graph> node to insert the graph itself. This graph-node will be returned afterwards.
	 * 
	 * @param doc
	 *          the document
	 * @return the graph node (root for the graph)
	 */
	public static Element addGraphMLPreamble (Document doc)
	{
		Element graphML = doc.createElement("graphml");
		//graphML.setNamespace (Namespace.getNamespace ("http://graphml.graphdrawing.org/xmlns"));
		doc.appendChild (graphML);
		
		// key zur beschreibung des nodenamens
		Element keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "name");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "name");
		keyEl.setAttribute ("attr.type", "string");
		graphML.appendChild (keyEl);
		
		// key zur beschreibung des nodeset
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "ns");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "node set");
		keyEl.setAttribute ("attr.type", "string");
		Element defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("species"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung des reversible-attributs
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "rev");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "reversible");
		keyEl.setAttribute ("attr.type", "boolean");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("false"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung des fast-attributs
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "fast");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "fast");
		keyEl.setAttribute ("attr.type", "boolean");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("false"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung der struktur
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "vers");
		keyEl.setAttribute ("for", "all");
		keyEl.setAttribute ("attr.name", "version");
		keyEl.setAttribute ("attr.type", "int");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("0"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung der modifier von edges
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "mod");
		keyEl.setAttribute ("for", "edge");
		keyEl.setAttribute ("attr.name", "modifier");
		keyEl.setAttribute ("attr.type", "string");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode (SBOTerm.MOD_NONE));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// key zur beschreibung der initialAmounts von species
		keyEl = doc.createElement("key");
		keyEl.setAttribute ("id", "init");
		keyEl.setAttribute ("for", "node");
		keyEl.setAttribute ("attr.name", "initial amount");
		keyEl.setAttribute ("attr.type", "double");
		defEl = doc.createElement("default");
		defEl.appendChild (doc.createTextNode ("0"));
		keyEl.appendChild (defEl);
		graphML.appendChild (keyEl);
		
		// <graph>
		keyEl = doc.createElement("graph");
		keyEl.setAttribute ("id", "G");
		keyEl.setAttribute ("edgedefault", "directed");
		graphML.appendChild (keyEl);
		
		return keyEl;
	}
	
	/**
	 * Inserts a new node to the graph. This node will automatically appended to the {@code parent}s children. The arguments {@code reversible} and {@code fast} are intended for reaction-nodes. Non-reation nodes might leave them null.
	 * 
	 * @param graphDocument
	 *          the relevant document
	 * @param parent
	 *          the parent node
	 * @param id
	 *          the ID of the node
	 * @param ns
	 *          the node set
	 * @param name
	 *          the name of the node
	 * @param src
	 *          the source document (reference doc or diff), should be -1, 0 or 1
	 * @param reversible
	 *          reaction only: is this reaction reversible? (should be null for non-reactions)
	 * @param fast
	 *          reaction only: the fast attribute (should be null for non-reactions)
	 */
	private void createGraphMLNode (Element parent, String id,
		String ns, String name, String modification)//, Boolean reversible)//, Boolean fast)
	{
		LOGGER.debug ("create gml node: " + id + " mod: " + modification);
		Element element = graphDocument.createElement ("node");
		
		element.setAttribute ("id", id);
		
		Element nsElement = graphDocument.createElement ("data");
		nsElement.setAttribute ("key", "ns");
		nsElement.appendChild (graphDocument.createTextNode (ns));
		element.appendChild (nsElement);
		
		if (modification != null)
		{
			Element srcElement = graphDocument.createElement ("data");
			srcElement.setAttribute ("key", "vers");
			srcElement.appendChild (graphDocument.createTextNode (modification));
			element.appendChild (srcElement);
		}
		
		/*if (reversible != null)
		{
			//name += " (reversible)";
			Element revElement = graphDocument.createElement ("data");
			revElement.setAttribute ("key", "rev");
			revElement.appendChild (graphDocument.createTextNode (reversible ? "true" : "false"));
			element.appendChild (revElement);
		}*/
		
		/*if (fast != null)
		{
			//name += " (fast)";
			Element fastElement = graphDocument.createElement ("data");
			fastElement.setAttribute ("key", "fast");
			fastElement.appendChild (graphDocument.createTextNode (fast ? "true" : "false"));
			element.appendChild (fastElement);
		}*/
		
		Element nameElement = graphDocument.createElement ("data");
		nameElement.setAttribute ("key", "name");
		nameElement.appendChild (graphDocument.createTextNode (name));
		element.appendChild (nameElement);
		
		
		parent.appendChild (element);
	}
	
	
	/**
	 * Inserts a new edge to the graph. This edge will automatically appended to the {@code parent}s children.
	 * 
	 * @param graphDocument
	 *          the doc
	 * @param parent
	 *          the parent
	 * @param source
	 *          the source
	 * @param target
	 *          the target
	 * @param src
	 *          the src
	 * @param mod
	 *          the mod
	 */
	private void createEdge (Element parent, String source,
		String target, String modification, String mod)
	{
		LOGGER.debug ("create gml edge: " + source + " -> " + target + " mod: " + modification);
		Element element = graphDocument.createElement ("edge");
		
		element.setAttribute ("source", source);
		element.setAttribute ("target", target);
		
		if (mod != null)
		{
			Element nsElement = graphDocument.createElement ("data");
			nsElement.setAttribute ("key", "mod");
			nsElement.appendChild (graphDocument.createTextNode (mod));
			element.appendChild (nsElement);
		}
		
		if (modification != null)
		{
			Element srcElement = graphDocument.createElement ("data");
			srcElement.setAttribute ("key", "vers");
			srcElement.appendChild (graphDocument.createTextNode (modification));
			element.appendChild (srcElement);
		}
		
		parent.appendChild (element);
	}
}