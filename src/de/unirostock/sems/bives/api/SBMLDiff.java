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
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.exception.BivesSBMLParseException;

/**
 * @author Martin Scharm
 *
 */
public class SBMLDiff extends Diff
{
	private SBMLDocument doc1;
	private SBMLDocument doc2;
	
	public SBMLDiff (File a, File b) throws BivesDocumentParseException, FileNotFoundException, ParserConfigurationException, SAXException, IOException
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
	 */
	public static void main(String[] args) throws ParserConfigurationException, BivesDocumentParseException, FileNotFoundException, SAXException, IOException, BivesConnectionException
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
				System.out.println (diff.getReport());
				break;
			default:
				System.out.println(diff.getDiff());
		}
		
	}




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






	@Override
	public String getGraphML() throws ParserConfigurationException {
		SBMLGraphProducer producer = new SBMLGraphProducer ();
		producer.init (connections, doc1, doc2);
		return producer.produce ();
	}




	@Override
	public String getReport() {
		SBMLDiffInterpreter inter = new SBMLDiffInterpreter (connections, doc1, doc2);
		inter.interprete ();
		return inter.getReport ().generateHTMLReport ();
	}

}
