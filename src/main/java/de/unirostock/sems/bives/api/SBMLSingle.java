/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.sbml.SBMLConnector;
import de.unirostock.sems.bives.algorithm.sbml.SBMLDiffInterpreter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLGraphProducer;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorDot;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorGraphML;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorJson;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.Typesetting;
import de.unirostock.sems.bives.markup.TypesettingHTML;
import de.unirostock.sems.bives.markup.TypesettingMarkDown;
import de.unirostock.sems.bives.markup.TypesettingReStructuredText;

/**
 * @author Martin Scharm
 *
 */
public class SBMLSingle extends Single
{
	protected SBMLDocument doc1;
	
	public SBMLSingle (File a) throws BivesDocumentParseException, FileNotFoundException, ParserConfigurationException, SAXException, IOException, BivesConsistencyException
	{
		super (a);
		doc1 = new SBMLDocument (treeA);
	}
	
	public SBMLSingle (String a) throws BivesDocumentParseException, FileNotFoundException, ParserConfigurationException, SAXException, IOException, BivesConsistencyException
	{
		super (a);
		doc1 = new SBMLDocument (treeA);
	}
	
	public SBMLSingle (SBMLDocument a) throws BivesDocumentParseException, FileNotFoundException, ParserConfigurationException, SAXException, IOException, BivesConsistencyException
	{
		super (a.getTreeDocument ());
		doc1 = a;
	}



	protected SBMLGraphProducer graphProducer;


	@Override
	public String getCRNGraphML() throws ParserConfigurationException {
		if (graphProducer == null)
			graphProducer = new SBMLGraphProducer (doc1);
		return new GraphTranslatorGraphML ().translate (graphProducer.getCRN ());
	}

	@Override
	public Object getCRNGraph (GraphTranslator gt) throws Exception
	{
		if (graphProducer == null)
			graphProducer = new SBMLGraphProducer (doc1);
		return gt.translate (graphProducer.getCRN ());
	}


	@Override
	public String getCRNDotGraph ()
	{
		if (graphProducer == null)
			graphProducer = new SBMLGraphProducer (doc1);
		return new GraphTranslatorDot ().translate (graphProducer.getCRN ());
	}

	@Override
	public String getCRNJsonGraph ()
	{
		if (graphProducer == null)
			graphProducer = new SBMLGraphProducer (doc1);
		return new GraphTranslatorJson ().translate (graphProducer.getCRN ());
	}

	@Override
	public Object getHierarchyGraph (GraphTranslator gt)
	{
		return null;
	}

	@Override
	public String getHierarchyGraphML () throws ParserConfigurationException
	{
		return null;
	}

	@Override
	public String getHierarchyDotGraph ()
	{
		return null;
	}

	@Override
	public String getHierarchyJsonGraph ()
	{
		return null;
	}

}
