/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.algorithm.cellml.CellMLConnector;
import de.unirostock.sems.bives.algorithm.cellml.CellMLDiffInterpreter;
import de.unirostock.sems.bives.algorithm.cellml.CellMLGraphProducer;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorDot;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorGraphML;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorJson;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.exception.BivesImportException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.Typesetting;
import de.unirostock.sems.bives.markup.TypesettingHTML;
import de.unirostock.sems.bives.markup.TypesettingMarkDown;
import de.unirostock.sems.bives.markup.TypesettingReStructuredText;

/**
 * TODO: not implemented yet
 * 
 * @author Martin Scharm
 *
 */
public class CellMLDiff extends Diff
{
	private CellMLDocument doc1, doc2;

	public CellMLDiff(File a, File b) throws ParserConfigurationException,
			BivesDocumentParseException, FileNotFoundException, SAXException,
			IOException, BivesCellMLParseException, BivesConsistencyException, BivesLogicalException, URISyntaxException, BivesImportException {
		super(a, b);
		doc1 = new CellMLDocument (treeA);
		doc2 = new CellMLDocument (treeB);
	}

	public CellMLDiff(String a, String b) throws ParserConfigurationException,
			BivesDocumentParseException, FileNotFoundException, SAXException,
			IOException, BivesCellMLParseException, BivesConsistencyException, BivesLogicalException, URISyntaxException, BivesImportException {
		super(a, b);
		doc1 = new CellMLDocument (treeA);
		doc2 = new CellMLDocument (treeB);
	}

	public CellMLDiff(CellMLDocument a, CellMLDocument b) throws ParserConfigurationException,
			BivesDocumentParseException, FileNotFoundException, SAXException,
			IOException, BivesCellMLParseException, BivesConsistencyException, BivesLogicalException, URISyntaxException, BivesImportException {
		super(a.getTreeDocument (), b.getTreeDocument ());
		doc1 = a;
		doc2 = b;
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#mapTrees()
	 */
	@Override
	public boolean mapTrees() throws BivesConnectionException {
		CellMLConnector con = new CellMLConnector (doc1, doc2);
		
		con.init (treeA, treeB);
		con.findConnections ();
		connections = con.getConnections();
		
		
		treeA.getRoot ().resetModifications ();
		treeA.getRoot ().evaluate (connections);
		
		treeB.getRoot ().resetModifications ();
		treeB.getRoot ().evaluate (connections);
		
		return true;
	}
	
	protected CellMLGraphProducer graphProducer;
	protected CellMLDiffInterpreter interpreter;

	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getGraphML()
	 */
	@Override
	public String getCRNGraphML() throws ParserConfigurationException {
		if (graphProducer == null)
			graphProducer = new CellMLGraphProducer (connections, doc1, doc2);
		return new GraphTranslatorGraphML ().translate (graphProducer.getCRN ());
	}

	/* (non-Javadoc)
	 */
	@Override
	public Object getHierarchyGraph(GraphTranslator gt) throws Exception {
		if (graphProducer == null)
			graphProducer = new CellMLGraphProducer (connections, doc1, doc2);
		return gt.translate (graphProducer.getHierarchy ());
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getGraphML()
	 */
	@Override
	public String getHierarchyGraphML() throws ParserConfigurationException {
		if (graphProducer == null)
			graphProducer = new CellMLGraphProducer (connections, doc1, doc2);
		return new GraphTranslatorGraphML ().translate (graphProducer.getHierarchy ());
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getReport()
	 */
	@Override
	public String getMarkDownReport() {
		if (interpreter == null)
		{
			interpreter = new CellMLDiffInterpreter (connections, doc1, doc2);
			interpreter.interprete ();
		}
		return  new TypesettingMarkDown ().markup (interpreter.getReport ());
	}

	@Override
	public String getReStructuredTextReport ()
	{
		if (interpreter == null)
		{
			interpreter = new CellMLDiffInterpreter (connections, doc1, doc2);
			interpreter.interprete ();
		}
		return  new TypesettingReStructuredText ().markup (interpreter.getReport ());
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getReport()
	 */
	@Override
	public String getHTMLReport() {
		if (interpreter == null)
		{
			interpreter = new CellMLDiffInterpreter (connections, doc1, doc2);
			interpreter.interprete ();
		}
		return  new TypesettingHTML ().markup (interpreter.getReport ());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	@Override
	public Object getCRNGraph (GraphTranslator gt) throws Exception
	{
		if (graphProducer == null)
			graphProducer = new CellMLGraphProducer (connections, doc1, doc2);
		return gt.translate (graphProducer.getCRN ());
	}

	@Override
	public String getCRNDotGraph ()
	{
		if (graphProducer == null)
			graphProducer = new CellMLGraphProducer (connections, doc1, doc2);
		return new GraphTranslatorDot ().translate (graphProducer.getCRN ());
	}

	@Override
	public String getCRNJsonGraph ()
	{
		if (graphProducer == null)
			graphProducer = new CellMLGraphProducer (connections, doc1, doc2);
		return new GraphTranslatorJson ().translate (graphProducer.getCRN ());
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getGraphML()
	 */
	@Override
	public String getHierarchyDotGraph()
	{
		if (graphProducer == null)
			graphProducer = new CellMLGraphProducer (connections, doc1, doc2);
		return new GraphTranslatorDot ().translate (graphProducer.getHierarchy ());
	}

	@Override
	public String getHierarchyJsonGraph ()
	{
		if (graphProducer == null)
			graphProducer = new CellMLGraphProducer (connections, doc1, doc2);
		return new GraphTranslatorJson ().translate (graphProducer.getHierarchy ());
	}

	@Override
	public String getReport (Typesetting ts)
	{
		if (interpreter == null)
		{
			interpreter = new CellMLDiffInterpreter (connections, doc1, doc2);
			interpreter.interprete ();
		}
		return ts.markup (interpreter.getReport ());
	}

}
