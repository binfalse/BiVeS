/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class ChemicalReactionNetwork
{
	private final static Logger LOGGER = Logger.getLogger(ChemicalReactionNetwork.class.getName());
	private Vector<CRNSpecies> species;
	private Vector<CRNReaction> reactions;
	public static final String UNMODIFIED = "0";
	public static final String INSERT = "1";
	public static final String DELETE = "-1";
	public static final String MODIFIED = "2";
	
	public ChemicalReactionNetwork ()
	{
		species = new Vector<CRNSpecies> ();
		reactions = new Vector<CRNReaction> ();
	}
	
	public void addSpecies (CRNSpecies species)
	{
		this.species.add (species);
	}
	
	public void addReaction (CRNReaction reaction)
	{
		this.reactions.add (reaction);
	}
	
	public String getGraphML () throws ParserConfigurationException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	Document graphDocument = docBuilder.newDocument();
	Element graphRoot = addGraphMLPreamble (graphDocument);
	
	for (CRNSpecies s : species)
		s.createGraphMl (graphDocument, graphRoot);
	
	for (CRNReaction r : reactions)
		r.createGraphMl (graphDocument, graphRoot);
	

	try
	{
		return Tools.prettyPrintDocument (graphDocument, new OutputStream()
		{
		  private StringBuilder string = new StringBuilder();
		  
		  @Override
		  public void write(int b) throws IOException {
		      this.string.append((char) b );
		  }

		  //Netbeans IDE automatically overrides this toString()
		  public String toString(){
		      return this.string.toString();
		  }
}).toString ();
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
		keyEl.setAttribute ("attr.name", "surce");
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
		defEl.appendChild (doc.createTextNode ("none"));
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
	 * Resolve a modifier SBO-ID.
	 * 
	 * @param id
	 *          the id
	 * @return the modifier ({@code stimulator}, {@code inhibitor} or {@code unknown})
	 */
	public static String resolvModSBO (String id)
	{
		if (id == null || !id.startsWith ("SBO:"))
			return "unknown";
		
		try
		{
			// TODO: resolve the stuff dynamically from db...
			switch (Integer.parseInt (id.substring (4)))
			{
				case 459: // stimulator
				case 13: // catalyst
				case 460: // enzymatic catalyst (is a)
				case 461: // essential activator (is a)
				case 535: // binding activator (is a)
				case 534: // catalytic activator (is a)
				case 533: // specific activator (is a)
				case 462: // non-essential activator (is a)
				case 21: // potentiator (is a)
					return "stimulator";
				case 20: // inhibitor (is a)
				case 206: // competitive inhibitor (is a)
				case 207: // non-competitive inhibitor (is a)
				case 537: // complete inhibitor (is a)
				case 536: // partial inhibitor (is a)
					return "inhibitor";
			}
		}
		catch (NumberFormatException e)
		{
			
		}
		return "unknown";
	}

	public static String resolvModification (int modification, TreeNode treeA, TreeNode treeB)
	{
		if ((modification & TreeNode.UNMAPPED) != 0)
		{
			if (treeA == null)
				return INSERT;
			else if (treeB == null)
				return DELETE;
			else
				throw new UnsupportedOperationException ("unmapped, but from a and from b!?");
		}
		if ((modification & (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED)) != 0)
			return MODIFIED;
		return UNMODIFIED;
	}
}
