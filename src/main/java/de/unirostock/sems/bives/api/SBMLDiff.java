/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.sbml.SBMLConnector;
import de.unirostock.sems.bives.algorithm.sbml.SBMLDiffInterpreter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLGraphProducer;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorDot;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorGraphML;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.TypesettingHTML;
import de.unirostock.sems.bives.markup.TypesettingMarkDown;

/**
 * @author Martin Scharm
 *
 */
public class SBMLDiff extends Diff
{
	protected SBMLDocument doc1;
	protected SBMLDocument doc2;
	
	public SBMLDiff (File a, File b) throws BivesDocumentParseException, FileNotFoundException, ParserConfigurationException, SAXException, IOException, BivesConsistencyException
	{
		super (a,b);
		doc1 = new SBMLDocument (treeA);
		doc2 = new SBMLDocument (treeB);
	}
	
	public SBMLDiff (String a, String b) throws BivesDocumentParseException, FileNotFoundException, ParserConfigurationException, SAXException, IOException, BivesConsistencyException
	{
		super (a,b);
		doc1 = new SBMLDocument (treeA);
		doc2 = new SBMLDocument (treeB);
	}
	
	
	

	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 * @throws BivesDocumentParseException 
	 * @throws BivesConnectionException 
	 * @throws BivesConsistencyException 
	 */
	/*public static void main(String[] args) throws ParserConfigurationException, BivesDocumentParseException, FileNotFoundException, SAXException, IOException, BivesConnectionException, BivesConsistencyException
	{
		//args = new String [] {"test/TestModel_for_IB2013-version-one", "--graphml", "test/TestModel_for_IB2013-version-two"};
		
		
		ArgsParser p = new Diff.ArgsParser (args);
		
		SBMLDiff diff = new SBMLDiff (p.getFileA(), p.getFileB());
		diff.mapTrees();
		
		switch (p.getAction())
		{
			case Diff.PROD_ALL:
				System.out.println(diff.getJSON());
				break;
			case Diff.PROD_GRAPH:
				System.out.println(diff.getGraphML());
				break;
			case Diff.PROD_REPORT:
				System.out.println (diff.getHTMLReport());
				break;
			default:
				System.out.println(diff.getDiff());
		}
		
	}*/




	@Override
	public boolean mapTrees() throws BivesSBMLParseException, BivesConnectionException {

		SBMLConnector con = new SBMLConnector (doc1, doc2);
		
		con.init (treeA, treeB);
		con.findConnections ();
		connections = con.getConnections();
		
		
		treeA.getRoot ().resetModifications ();
		treeA.getRoot ().evaluate (connections);
		
		treeB.getRoot ().resetModifications ();
		treeB.getRoot ().evaluate (connections);
		
		return true;
	}



	protected SBMLGraphProducer graphProducer;
	protected SBMLDiffInterpreter interpreter;


	@Override
	public String getCRNGraphML() throws ParserConfigurationException {
		if (graphProducer == null)
			graphProducer = new SBMLGraphProducer (connections, doc1, doc2);
		return new GraphTranslatorGraphML ().translate (graphProducer.getCRN ());
	}




	@Override
	public String getMarkDownReport() {
		if (interpreter == null)
		{
			interpreter = new SBMLDiffInterpreter (connections, doc1, doc2);
			interpreter.interprete ();
		}
		return new TypesettingMarkDown ().markup (interpreter.getReport ());
	}




	@Override
	public String getHTMLReport() {
		if (interpreter == null)
		{
			interpreter = new SBMLDiffInterpreter (connections, doc1, doc2);
			interpreter.interprete ();
		}
		return new TypesettingHTML ().markup (interpreter.getReport ());
	}




	@Override
	public String getCRNDotGraph () throws ParserConfigurationException
	{
		if (graphProducer == null)
			graphProducer = new SBMLGraphProducer (connections, doc1, doc2);
		return new GraphTranslatorDot ().translate (graphProducer.getCRN ());
	}

	@Override
	public String getHierarchyGraphML () throws ParserConfigurationException
	{
		return null;
	}

}
