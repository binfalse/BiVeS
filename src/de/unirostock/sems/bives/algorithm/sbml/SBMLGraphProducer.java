/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbml;

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
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class SBMLGraphProducer
	extends Producer
{
	private Element graphRoot;
	private Document graphDocument;
	private HashMap<String, String> entityMapper;
	private int maxSpecies;
	private int maxReaction;
	private static final String INSERT = "1";
	private static final String DELETE = "-1";
	private static final String MODIFIED = "2";
	
	
	private final static Logger LOGGER = Logger.getLogger(SBMLGraphProducer.class.getName());
	
	public SBMLGraphProducer (ConnectionManager conMgmt, TreeDocument docA,
		TreeDocument docB) throws ParserConfigurationException
	{
		super (conMgmt, docA, docB);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			graphDocument = docBuilder.newDocument();
			graphRoot = addGraphMLPreamble (graphDocument);
			entityMapper = new HashMap<String, String> ();
			maxSpecies = 0;
			maxReaction = 0;
	}
	

	/* (non-Javadoc)
	 * @see de.unirostock.sems.xmldiff.algorithm.Producer#produce()
	 */
	@Override
	public String produce ()
	{
		LOGGER.info ("searching for species in A");
		for (TreeNode s : docA.getNodesByTag ("species"))
		{
			LOGGER.info ("species: " + s.getXPath ());
			DocumentNode ds = (DocumentNode) s;
			String id = "s" + ++maxSpecies;
			String name = ds.getAttribute ("name");
			if (name == null)
				name = ds.getId ();
			
			if (s.hasModification (TreeNode.UNMAPPED))
			{
				// delete
				entityMapper.put ("sd" + ds.getId (), id);
				createGraphMLNode (graphRoot,
				id, "species",
				name,
				DELETE, null, null);
			}
			else
			{
				// both have this species in common
				entityMapper.put ("sc" + ds.getId (), id);
				createGraphMLNode (graphRoot,
				id, "species",
				name,
				s.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED) ? MODIFIED : null, null, null);
			}
		}
		
		LOGGER.info ("searching for species in B");
		for (TreeNode s : docB.getNodesByTag ("species"))
		{
			LOGGER.info ("species: " + s.getXPath ());
			DocumentNode ds = (DocumentNode) s;
			String id = "s" + ++maxSpecies;
			String name = ds.getAttribute ("name");
			if (name == null)
				name = ds.getId ();
			
			if (s.hasModification (TreeNode.UNMAPPED))
			{
				// insert
				entityMapper.put ("si" + ds.getId (), id);
				createGraphMLNode (graphRoot,
				id, "species",
				name,
				INSERT, null, null);
			}
		}
		

		// write reactions
		LOGGER.info ("searching for reactions in A");
		for (TreeNode r : docA.getNodesByTag ("reaction"))
		{
			LOGGER.info ("reaction: " + r.getXPath ());
			LOGGER.info ("reaction marker: " + r.getModification () + " mod/submod: " + r.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED));
			
			DocumentNode reaction = (DocumentNode) r;
			
			String reactionID = reaction.getAttribute ("id");
			String name = reaction.getAttribute ("name");
			if (name == null)
				name = reactionID;
			String id = "r" + ++maxReaction;
			
			if (r.hasModification (TreeNode.UNMAPPED))
			{
				// delete
				entityMapper.put ("rd" + reactionID, id);
				createGraphMLNode (graphRoot,
					id, "reaction",
				name,
				DELETE, reaction.getAttribute ("reversible"), reaction.getAttribute ("fast"));
			}
			else
			{
				entityMapper.put ("rc" + reactionID, id);
				createGraphMLNode (graphRoot,
					id, "reaction",
				name,
				r.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED) ? MODIFIED : null, reaction.getAttribute ("reversible"), reaction.getAttribute ("fast"));
			}
			
			
			for (TreeNode c: reaction.getChildren ())
			{
				DocumentNode dn = (DocumentNode) c;
				String dntag = dn.getTagName ();
				String mod = null;
				if (c.getModification () != 0)
					mod = MODIFIED;
				if (c.hasModification (TreeNode.UNMAPPED))
					mod = INSERT;
				
				if (dntag.equals ("listOfReactants"))
				{
					for (TreeNode react : dn.getChildren ())
					{
						DocumentNode reactant = (DocumentNode) react;

						String spec = entityMapper.get ("sd" + reactant.getAttribute ("species"));
						if (spec == null)
							spec = entityMapper.get ("sc" + reactant.getAttribute ("species"));

						String mymod = mod;
						if (mymod == null && reactant.getModification () != 0)
							mymod = MODIFIED;
						if (reactant.hasModification (TreeNode.UNMAPPED))
							mymod = DELETE;
						
						createEdge (graphRoot, spec,
							id, mymod, "none");
					}
				}
				else if (dntag.equals ("listOfProducts"))
				{
					for (TreeNode prod : dn.getChildren ())
					{
						DocumentNode product = (DocumentNode) prod;

						String spec = entityMapper.get ("sd" + product.getAttribute ("species"));
						if (spec == null)
							spec = entityMapper.get ("sc" + product.getAttribute ("species"));

						String mymod = mod;
						if (mymod == null && product.getModification () != 0)
							mymod = MODIFIED;
						if (product.hasModification (TreeNode.UNMAPPED))
							mymod = DELETE;
						
						createEdge (graphRoot,
							id, spec, mymod, "none");
					}
					
				}
				else if (dntag.equals ("listOfModifiers"))
				{
					for (TreeNode modi : dn.getChildren ())
					{
						DocumentNode modifier = (DocumentNode) modi;

						String spec = entityMapper.get ("sd" + modifier.getAttribute ("species"));
						if (spec == null)
							spec = entityMapper.get ("sc" + modifier.getAttribute ("species"));

						String mymod = mod;
						if (mymod == null && modifier.getModification () != 0)
							mymod = MODIFIED;
						if (modifier.hasModification (TreeNode.UNMAPPED))
							mymod = DELETE;
						
						createEdge (graphRoot,
							spec, id, mymod, resolvModSBO (modifier.getAttribute ("sboTerm")));
					}
					
				}
			}
		}

		LOGGER.info ("searching for reactions in B");
		for (TreeNode r : docA.getNodesByTag ("reaction"))
		{
			LOGGER.info ("reaction: " + r.getXPath ());
			LOGGER.info ("reaction marker: " + r.getModification ());
			
			DocumentNode reaction = (DocumentNode) r;
			
			String reactionID = reaction.getAttribute ("id");
			String name = reaction.getAttribute ("name");
			if (name == null)
				name = reactionID;
			String id;
			
			if (r.hasModification (TreeNode.UNMAPPED))
			{
				// delete
				id = "r" + ++maxReaction;
				entityMapper.put ("ri" + reactionID, id);
				createGraphMLNode (graphRoot,
					id, "reaction",
				name,
				INSERT, reaction.getAttribute ("reversible"), reaction.getAttribute ("fast"));
			}
			else
			{
				id = entityMapper.get ("rc" + reactionID);
			}
			
			
			for (TreeNode c: reaction.getChildren ())
			{
				DocumentNode dn = (DocumentNode) c;
				String dntag = dn.getTagName ();
				String mod = null;
				if (c.getModification () != 0)
					mod = MODIFIED;
				if (c.hasModification (TreeNode.UNMAPPED))
					mod = INSERT;
				
				if (dntag.equals ("listOfReactants"))
				{
					for (TreeNode react : dn.getChildren ())
					{
						if (react.getModification () == 0)
							continue;
						DocumentNode reactant = (DocumentNode) react;

						String spec = entityMapper.get ("sd" + reactant.getAttribute ("species"));
						if (spec == null)
							spec = entityMapper.get ("sc" + reactant.getAttribute ("species"));
						
						String mymod = mod;
						if (mymod == null && reactant.getModification () != 0)
							mymod = MODIFIED;
						if (reactant.hasModification (TreeNode.UNMAPPED))
							mymod = INSERT;
						
						createEdge (graphRoot, spec,
							id, mymod, "none");
					}
				}
				else if (dntag.equals ("listOfProducts"))
				{
					for (TreeNode prod : dn.getChildren ())
					{
						if (prod.getModification () == 0)
							continue;
						DocumentNode product = (DocumentNode) prod;

						String spec = entityMapper.get ("sd" + product.getAttribute ("species"));
						if (spec == null)
							spec = entityMapper.get ("sc" + product.getAttribute ("species"));

						String mymod = mod;
						if (mymod == null && product.getModification () != 0)
							mymod = MODIFIED;
						if (product.hasModification (TreeNode.UNMAPPED))
							mymod = INSERT;
						
						createEdge (graphRoot,
							id, spec, mymod, "none");
					}
					
				}
				else if (dntag.equals ("listOfModifiers"))
				{
					for (TreeNode modi : dn.getChildren ())
					{
						if (modi.getModification () == 0)
							continue;
						DocumentNode modifier = (DocumentNode) modi;

						String spec = entityMapper.get ("sd" + modifier.getAttribute ("species"));
						if (spec == null)
							spec = entityMapper.get ("sc" + modifier.getAttribute ("species"));

						String mymod = mod;
						if (mymod == null && modifier.getModification () != 0)
							mymod = MODIFIED;
						if (modifier.hasModification (TreeNode.UNMAPPED))
							mymod = INSERT;
						
						createEdge (graphRoot,
							spec, id, mymod, resolvModSBO (modifier.getAttribute ("sboTerm")));
					}
					
				}
			}
		}
		
		
		try
		{
			return printDocument (new OutputStream()
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
	
	public OutputStream printDocument(OutputStream out) throws IOException, TransformerException
	{
		LOGGER.info ("delivering sbml graphml");
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    transformer.transform(new DOMSource(graphDocument), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    return out;
  }

	/**
	 * Creates the preamble of the graph. Creates some nodes in the document defining the graph and its properties. Creates also the <graph> node to insert the graph itself. This graph-node will be returned afterwards.
	 * 
	 * @param doc
	 *          the document
	 * @return the graph node (root for the graph)
	 */
	private Element addGraphMLPreamble (Document doc)
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
		String ns, String name, String modification, String reversible, String fast)
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
		
		if (reversible != null && reversible.length () > 0)
		{
			name += " (reversible)";
			Element revElement = graphDocument.createElement ("data");
			revElement.setAttribute ("key", "rev");
			revElement.appendChild (graphDocument.createTextNode (reversible));
			element.appendChild (revElement);
		}
		
		if (fast != null && fast.length () > 0)
		{
			name += " (fast)";
			Element fastElement = graphDocument.createElement ("data");
			fastElement.setAttribute ("key", "fast");
			fastElement.appendChild (graphDocument.createTextNode (fast));
			element.appendChild (fastElement);
		}
		
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
		
		Element nsElement = graphDocument.createElement ("data");
		nsElement.setAttribute ("key", "mod");
		nsElement.appendChild (graphDocument.createTextNode (mod));
		element.appendChild (nsElement);

		if (modification != null)
		{
			Element srcElement = graphDocument.createElement ("data");
			srcElement.setAttribute ("key", "vers");
			srcElement.appendChild (graphDocument.createTextNode (modification));
			element.appendChild (srcElement);
		}
		
		parent.appendChild (element);
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
}
