/**
 * 
 */
package de.unirostock.sems.bives.ds.cellml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.CellMLReadException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLModel
extends CellMLEntity
{
	// The <model> element has a name attribute that allows the model to be unambiguously referenced.
	private String name;
	
	// A modeller may import parts of another valid CellML model, as described in
	//private Vector<CellMLImport> imports;
	
	private CellMLDocument doc;
	
	// A modeller can declare a set of units to use in the model
	private CellMLUnitDictionary unitDict;
	
	
	// Components are the smallest functional units in a model. Each component may contain variables that represent the key properties of the component and/or mathematics that describe the behaviour of the portion of the system represented by that component.
	private HashMap<String, CellMLComponent> components;
	
	private CellMLHierarchy hierarchy;
	
	private Vector<CellMLConnection> connections;
	
	private boolean containsImports;
	
	public CellMLModel (CellMLDocument doc, DocumentNode rootNode) throws CellMLReadException, BivesConsistencyException, BivesLogicalException, IOException, URISyntaxException, ParserConfigurationException, SAXException
	{
		super (rootNode, null);
		this.doc = doc;
		containsImports = false;
		name = rootNode.getAttribute ("name");
		unitDict = new CellMLUnitDictionary (this);
		components = new HashMap<String, CellMLComponent> ();
		hierarchy = new CellMLHierarchy (this);
		connections = new Vector<CellMLConnection> ();
		
		readDocument (rootNode);
		this.model = this;
		/*this.baseUri = baseUri;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	  DocumentBuilder db = dbf.newDocumentBuilder();
	  Document doc = db.parse(file);
	  readDocument (doc);*/
	}
	
	public boolean containsImports ()
	{
		return containsImports;
	}
	
	private void readDocument (DocumentNode root) throws CellMLReadException, BivesConsistencyException, BivesLogicalException, IOException, URISyntaxException, ParserConfigurationException, SAXException
	{
		//Element root = doc.getDocumentElement ();
		
		
		// imports
		LOGGER.info ("reading imports in " + doc.getBaseUri ());
		readImports (root);

		LOGGER.info ("after import:");
		for (String c : components.keySet ())
			LOGGER.info ("comp: " + c + " -> " + components.get (c).getName ());
		
		// units
		LOGGER.info ("reading units in " + doc.getBaseUri ());
		readUnits (root);
		
		// components
		LOGGER.info ("reading components in " + doc.getBaseUri ());
		readComponents (root);
		
		// manage groups
		LOGGER.info ("reading groups in " + doc.getBaseUri ());
		readGroups (root);
		
		// manage connections
		LOGGER.info ("reading connections in " + doc.getBaseUri ());
		readConnections (root);
	}
	
	private void readUnits (DocumentNode root) throws BivesConsistencyException, CellMLReadException
	{
		Vector<TreeNode> kids = root.getChildrenWithTag ("units");
		// units might be in unordered seq -> first unit might depend on last unit
		boolean nextRound = true;
		while (nextRound && kids.size () > 0)
		{
			nextRound = false;
			for (int i = kids.size () - 1; i >= 0; i--)
			{
				TreeNode kid = kids.elementAt (i);
				if (kid.getType () != TreeNode.DOC_NODE)
					continue;
				try
				{
					unitDict.addUnit (null, new CellMLUserUnit (model, unitDict, null, (DocumentNode) kid));
				}
				catch (BivesConsistencyException ex)
				{
					continue;
				}
				kids.remove (i);
				nextRound = true;
			}
			
		}
		if (kids.size () != 0)
			throw new BivesConsistencyException ("inconsistencies for "+kids.size ()+" units, e.q. "+kids.elementAt (0).getXPath ());
		
		/*for (TreeNode kid : kids)
		{

			unitDict.addUnit (null, new CellMLUserUnit (model, unitDict, null, (DocumentNode) kid));
		}*/
	}
	
	private void readImports (DocumentNode root) throws CellMLReadException, IOException, URISyntaxException, ParserConfigurationException, SAXException, BivesConsistencyException, BivesLogicalException
	{
		Vector<TreeNode> kids = root.getChildrenWithTag ("import");
		for (TreeNode kid : kids)
		{
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;

			CellMLImporter importer = new CellMLImporter ((DocumentNode) kid, this);
			importer.parse ();
			containsImports = true;
		}
	}

	private void readConnections (DocumentNode root) throws CellMLReadException, BivesConsistencyException, BivesLogicalException
	{
		Vector<TreeNode> kids = root.getChildrenWithTag ("connection");
		for (TreeNode kid : kids)
		{
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;

			CellMLConnection.parseConnection (this, hierarchy, (DocumentNode) kid);
		}
	}

	private void readGroups (DocumentNode root) throws CellMLReadException, BivesLogicalException
	{
		Vector<TreeNode> kids = root.getChildrenWithTag ("group");
		for (TreeNode kid : kids)
		{
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;

			hierarchy.parseGroup ((DocumentNode) kid);
		}
	}

	private void readComponents (DocumentNode root) throws BivesConsistencyException, CellMLReadException, BivesLogicalException
	{
		Vector<TreeNode> kids = root.getChildrenWithTag ("component");
		for (TreeNode kid : kids)
		{
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;

			addComponent (new CellMLComponent (this, (DocumentNode) kid));
			//components.put (component.getName (), component);
		}
	}
	
	public void addUnit (CellMLUserUnit unit) throws BivesConsistencyException
	{
		unitDict.addUnit (null, unit);
	}
	
	public CellMLUnitDictionary getUnits ()
	{
		return unitDict;
	}
	
	public CellMLDocument getDocument ()
	{
		return doc;
	}
	
	

	public CellMLComponent getComponent (String name)
	{
		return components.get (name);
	}
	
	public void addComponent (CellMLComponent component) throws BivesConsistencyException, BivesLogicalException
	{
		if (components.get (component.getName ()) != null)
			throw new BivesConsistencyException ("two components using the same name! ("+component.getName ()+")");
		components.put (component.getName (), component);
		hierarchy.addUnencapsulatedComponent (component);
	}
	
	public void debug (String prefix)
	{
		System.out.println (prefix + "model: " + name);
		unitDict.debug (prefix + "  ");
		for (CellMLComponent c : components.values ())
			c.debug (prefix + "  ");
	}
}
