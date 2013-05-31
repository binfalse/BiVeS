/**
 * 
 */
package de.unirostock.sems.bives.algorithm.cellml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	
	public CellMLModel (CellMLDocument doc, DocumentNode rootNode) throws CellMLReadException, BivesConsistencyException, BivesLogicalException, IOException, URISyntaxException
	{
		super (rootNode, null);
		this.doc = doc;
		readDocument (rootNode);
		this.model = this;
		/*this.baseUri = baseUri;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	  DocumentBuilder db = dbf.newDocumentBuilder();
	  Document doc = db.parse(file);
	  readDocument (doc);*/
	}
	
	private void readDocument (DocumentNode root) throws CellMLReadException, BivesConsistencyException, BivesLogicalException, IOException, URISyntaxException
	{
		//Element root = doc.getDocumentElement ();
		
		hierarchy = new CellMLHierarchy (this);
		
		// imports
		readImports (root);
		
		// units
		readUnits (root);
		
		// components
		readComponents (root);
		
		// manage groups
		readGroups (root);
		
		// manage connections
		readConnections (root);
	}
	
	private void readUnits (DocumentNode root) throws BivesConsistencyException, CellMLReadException
	{
		Vector<TreeNode> kids = root.getChildrenWithTag ("units");
		for (TreeNode kid : kids)
		{
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;

			unitDict.addUnit (null, new CellMLUserUnit (model, unitDict, null, (DocumentNode) kid));
		}
	}
	
	private void readImports (DocumentNode root) throws CellMLReadException, IOException, URISyntaxException
	{
		Vector<TreeNode> kids = root.getChildrenWithTag ("import");
		for (TreeNode kid : kids)
		{
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;

			CellMLImporter importer = new CellMLImporter ((DocumentNode) kid, this);
			importer.parse ();
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

			CellMLComponent component = new CellMLComponent (this, (DocumentNode) kid);
			hierarchy.addUnencapsulatedComponent (component);
			components.put (component.getName (), component);
		}
	}
	
	public whatever exportWhatever ()
	{
		
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
	
	public void addComponent (CellMLComponent component)
	{
		components.put (component.getName (), component);
	}
	
	
}
