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
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesImportException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.tools.FileRetriever;


/**
 * @author Martin Scharm
 *
 */
public class CellMLImporter
extends CellMLEntity
{
	private static HashMap<URI, CellMLDocument> importMapper;
	
	private String href;
	private DocumentNode node;
	
	public CellMLImporter (DocumentNode node, CellMLModel model) throws BivesCellMLParseException
	{
		super (node, model);
		
		this.node = node;
		href = node.getAttribute ("xlink:href");
		if (href == null)
			throw new BivesCellMLParseException ("href attribute in import is empty");
		
		if (importMapper == null)
			importMapper = new HashMap<URI, CellMLDocument> ();
		
	}
	
	public void parse () throws BivesImportException
	{
		try
		{
			pparse ();
		}
		catch (Exception e)
		{
			throw new BivesImportException (href, e);
		}
	}



	public void pparse () throws IOException, URISyntaxException, ParserConfigurationException, SAXException, BivesCellMLParseException, BivesConsistencyException, BivesLogicalException, BivesImportException
	{
		URI baseUri = model.getDocument ().getBaseUri ();
		LOGGER.info ("parsing import from " + href + " (base uri is: "+baseUri+")");
		File tmp = File.createTempFile ("cellmlimporter", "cellml");
		tmp.deleteOnExit ();
		
		URI fileUri = FileRetriever.getUri (href, baseUri);
		CellMLDocument toImport = importMapper.get (fileUri);
		if (toImport == null)
		{
			FileRetriever.getFile (fileUri, tmp);
		  TreeDocument tdoc = new TreeDocument (DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tmp), null, fileUri);
			toImport = new CellMLDocument (tdoc);
			importMapper.put (fileUri, toImport);
		}
		CellMLModel modelToImport = toImport.getModel ();
		
		Vector<Object> doubles = new Vector<Object> ();
		
		
		CellMLUnitDictionary units = modelToImport.getUnits ();
		Vector<TreeNode> kids = node.getChildrenWithTag ("units");
		for (TreeNode kid : kids)
		{
			DocumentNode ukid = (DocumentNode) kid;
			String ref = ukid.getAttribute ("units_ref");
			String name = ukid.getAttribute ("name");
			
			if (ref == null || name == null || ref.length () < 1 || name.length () < 1)
				throw new BivesCellMLParseException ("unit import should define a name _and_ a units_ref! (name: "+name+", units_ref: "+ref+")");
			

			CellMLUnit u = units.getUnit (ref, null);
			if (u == null)
				throw new BivesConsistencyException ("cannot import unit " + ref + " from " + href + " (base uri is: "+baseUri+")");
				
			if (u instanceof CellMLUserUnit)
			{
				CellMLUserUnit uu = (CellMLUserUnit) u;
				if (doubles.contains (uu))
					throw new BivesCellMLParseException ("double import of same unit. not supported yet.");
				doubles.add (uu);
				uu.setName (name);
				Vector<CellMLUserUnit> unitsToImport = uu.getDependencies (new Vector<CellMLUserUnit> ());
				for (CellMLUserUnit unit : unitsToImport)
					model.importDependencyUnit (unit);
				model.importUnit (uu);
				LOGGER.info ("imported unit " + name + " from " + ref + "@" + href);
			}
			else
			{
				throw new BivesConsistencyException ("unit import of base unit detected... ("+u.getName ()+")");
			}
		}
		

		HashMap<String, CellMLComponent> tmpConMapper = new HashMap<String, CellMLComponent> ();
		kids = node.getChildrenWithTag ("component");
		for (TreeNode kid : kids)
		{
			DocumentNode ckid = (DocumentNode) kid;
			String ref = ckid.getAttribute ("component_ref");
			String name = ckid.getAttribute ("name");
			
			if (ref == null || name == null || ref.length () < 1 || name.length () < 1)
				throw new BivesCellMLParseException ("component import should define a name _and_ a component_ref! (name: "+name+", component_ref: "+ref+")");
			
			CellMLComponent c = modelToImport.getComponent (ref);
			if (c == null)
				throw new BivesConsistencyException ("cannot import component " + ref + " from " + href + " (base uri is: "+baseUri+")");

			if (doubles.contains (c))
				throw new BivesCellMLParseException ("double import of same component. not supported yet.");
			doubles.add (c);
			
			//c = c.copy (model, name);
			tmpConMapper.put (c.getName (), c);
			// TODO: kill all connections??
			//c.unconnect ();
			c.setName (name);
			Vector<CellMLUserUnit> unitsToImport = c.getDependencies (new Vector<CellMLUserUnit> ());
			for (CellMLUserUnit unit : unitsToImport)
				model.importDependencyUnit (unit);
			model.importComponent (c);
			LOGGER.info ("imported component " + name + " from " + ref + "@" + href);
		}

		// reconnect a subset
		// TODO: looks like blödsinn
		kids = node.getChildrenWithTag ("connection");
		for (TreeNode kid : kids)
		{
			System.out.println ("hit");
			if (kid.getType () != TreeNode.DOC_NODE)
				continue;

			DocumentNode dkid = (DocumentNode) kid;
			if (CellMLConnection.parseConnection (modelToImport, modelToImport.getHierarchy (), (DocumentNode) dkid, tmpConMapper))
				model.importConnection (dkid);
		}
	}
}
