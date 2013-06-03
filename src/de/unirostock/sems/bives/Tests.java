/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLConnector;
import de.unirostock.sems.bives.algorithm.sbml.SBMLDiffInterpreter;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.CellMLReadException;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.TypesettingHTML;
import de.unirostock.sems.bives.markup.TypesettingMarkDown;


/**
 * @author Martin Scharm
 * 
 * # clone all cellml repos
 * date; for i in `wget -q -O - http://models.cellml.org/workspace_list_txt`; do dir=${${i/http:\/\/models.cellml.org\/}//\//_}; echo ">>>>>    $dir    <<<<"; mkdir $dir; cd $dir; hg clone $i .; cd -; done; date
 * 
 * 
 */
public class Tests
{
	
	/**
	 * 
	 */
	public Tests ()
	{
	}
	
	
	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 * @throws BivesDocumentParseException 
	 * @throws URISyntaxException 
	 * @throws BivesLogicalException 
	 * @throws BivesConsistencyException 
	 * @throws CellMLReadException 
	 * @throws BivesConnectionException 
	 */
	public static void main (String[] args) throws ParserConfigurationException, BivesDocumentParseException, FileNotFoundException, SAXException, IOException, CellMLReadException, BivesConsistencyException, BivesLogicalException, URISyntaxException, BivesConnectionException
	{
		LOGGER.addLevel (LOGGER.DEBUG);
		//LOGGER.addLevel (LOGGER.INFO);
		//LOGGER.setLogToStdErr (true);
		//LOGGER.info ("test");
		
		//testCellML ();
		
		testSBML ();
		
	}
	
	
	
	private static void testSBML () throws BivesDocumentParseException, FileNotFoundException, SAXException, IOException, ParserConfigurationException, BivesConnectionException
	{
		
		File file1 = new File ("test/TestModel_for_IB2013-version-one");
		URI uri1 = file1.toURI ();
		System.out.println (uri1);
		
		TreeDocument td1 = new TreeDocument (DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ().parse (new FileInputStream (file1)), new XyWeighter (), uri1);
		

		File file2 = new File ("test/TestModel_for_IB2013-version-two");
		URI uri2 = file2.toURI ();
		System.out.println (uri2);
		
		TreeDocument td2 = new TreeDocument (DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ().parse (new FileInputStream (file2)), new XyWeighter (), uri2);
		
		SBMLDocument sdoc1 = new SBMLDocument (td1);
		SBMLDocument sdoc2 = new SBMLDocument (td2);
		

		SBMLConnector con2 = new SBMLConnector (sdoc1, sdoc2);
		con2.init (td1, td2);
		con2.findConnections ();
		
		td1.getRoot ().resetModifications ();
		td1.getRoot ().evaluate (con2.getConnections ());
		
		td2.getRoot ().resetModifications ();
		td2.getRoot ().evaluate (con2.getConnections ());
		

		SBMLDiffInterpreter inter = new SBMLDiffInterpreter (con2.getConnections (), sdoc1, sdoc2);
		inter.interprete ();
		MarkupDocument mdoc = inter.getReport ();
		TypesettingMarkDown mhtml = new TypesettingMarkDown ();
		//TypesettingHTML mhtml = new TypesettingHTML ();
		System.out.println (mhtml.markup (mdoc));
		
		
    Producer producer = new PatchProducer ();
		producer.init (con2.getConnections (), td1, td2);
		System.out.println (producer.produce ());
	}


	private static void testCellML () throws BivesDocumentParseException, FileNotFoundException, SAXException, IOException, ParserConfigurationException, CellMLReadException, BivesConsistencyException, BivesLogicalException, URISyntaxException
	{
		
		File file = new File ("/home/martin/education/phd/stuff/cellml/repository-server/w_andre_HH/experiments/gK-050.xml");
		URI uri = file.toURI ();
		System.out.println (uri);
		
		TreeDocument td = new TreeDocument (DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ().parse (new FileInputStream (file)), new XyWeighter (), uri);
		
		//BufferedReader br = new BufferedReader (new FileReader (new File (uri.toURL ().getFile ())));
		
		CellMLDocument cdoc = new CellMLDocument (td);
		cdoc.debug ("");
		
		System.out.println (uri);
		
		/*args = new String [] {"test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		TreeDocument td = new TreeDocument (builder.parse (new FileInputStream (args[0])), new XyWeighter ());
		
		SBMLDocument doc = new SBMLDocument (td);
		
		System.out.println (doc.getLevel ());*/
	}
	
}
