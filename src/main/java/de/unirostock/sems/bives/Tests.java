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
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.algorithm.Producer;
import de.unirostock.sems.bives.algorithm.cellml.CellMLConnector;
import de.unirostock.sems.bives.algorithm.cellml.CellMLDiffInterpreter;
import de.unirostock.sems.bives.algorithm.cellml.CellMLGraphProducer;
import de.unirostock.sems.bives.algorithm.general.PatchProducer;
import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLConnector;
import de.unirostock.sems.bives.algorithm.sbml.SBMLDiffInterpreter;
import de.unirostock.sems.bives.algorithm.sbml.SBMLGraphProducer;
import de.unirostock.sems.bives.api.CellMLDiff;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.api.RegularDiff;
import de.unirostock.sems.bives.api.SBMLDiff;
import de.unirostock.sems.bives.ds.ModelType;
import de.unirostock.sems.bives.ds.cellml.CellMLDocument;
import de.unirostock.sems.bives.ds.graph.GraphTranslatorGraphML;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesConsistencyException;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;
import de.unirostock.sems.bives.exception.BivesFlattenException;
import de.unirostock.sems.bives.exception.BivesImportException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.bives.exception.BivesCellMLParseException;
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
	 * @throws BivesCellMLParseException 
	 * @throws BivesConnectionException 
	 * @throws TransformerException 
	 * @throws BivesFlattenException 
	 */
	public static void main (String[] args) throws ParserConfigurationException, BivesDocumentParseException, FileNotFoundException, SAXException, IOException, BivesCellMLParseException, BivesConsistencyException, BivesLogicalException, URISyntaxException, BivesConnectionException, TransformerException, BivesFlattenException
	{
		//LOGGER.addLevel (LOGGER.DEBUG);
		//LOGGER.addLevel (LOGGER.INFO);
		LOGGER.addLevel (LOGGER.ERROR);
		//LOGGER.setLogToStdErr (true);
		LOGGER.info ("test");
		
		//testCellML ();
		
		//testSBML ();
		
		//*
		testSBMLApi ();
		/*/
		testCellMLApi ();
		/* /
		testRegularApi ();
		//*/
	}
	private static void testRegularApi () throws BivesConnectionException, BivesDocumentParseException, FileNotFoundException, ParserConfigurationException, SAXException, IOException
	{
		File file1 = new File ("test/bhalla_iyengar_1999_j_v1.cellml");
		File file2 = new File ("test/TestModel_for_IB2013-version-one");
		

		/*DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		TreeDocument treeA = new TreeDocument (builder.parse (new FileInputStream (file1)), new XyWeighter (), file1.toURI ());
		TreeDocument treeB = new TreeDocument (builder.parse (new FileInputStream (file2)), new XyWeighter (), file2.toURI ());

		System.out.println (ModelType.getModelType (treeA));
		System.out.println (ModelType.getModelType (treeB));*/
		
		RegularDiff differ = new RegularDiff (file1, file2);
		differ.mapTrees ();
		
		String patch = differ.getDiff ();
		System.out.println (patch);
	}
	
	private static void testCellMLApi () throws BivesConnectionException, BivesDocumentParseException, FileNotFoundException, ParserConfigurationException, SAXException, IOException, BivesCellMLParseException, BivesConsistencyException, BivesLogicalException, URISyntaxException, BivesImportException 
	{
		File file1 = new File ("test/bhalla_iyengar_1999_j_v1.cellml");
		File file2 = new File ("test/bhalla_iyengar_1999_j_v3.cellml");
		
		CellMLDiff differ = new CellMLDiff (file1, file2);
		differ.mapTrees ();

		/** /
		String graph = differ.getCRNDotGraph ();
		System.out.println (graph);
		/**/

		/** /
		String graph = differ.getCRNGraphML ();
		System.out.println (graph);
		/**/

		/** /
		String html = differ.getHTMLReport ();
		System.out.println (html);
		/**/

		/**/
		String md = differ.getMarkDownReport ();
		System.out.println (md);
		/**/

		/**/
		String patch = differ.getDiff ();
		System.out.println (patch);
		/**/
	}
	
	
	private static void testSBMLApi () throws BivesDocumentParseException, FileNotFoundException, ParserConfigurationException, SAXException, IOException, BivesConnectionException, BivesConsistencyException
	{
		File file1 = new File ("test/TestModel_for_IB2013-version-one");
		File file2 = new File ("test/TestModel_for_IB2013-version-two");
		//File file2 = new File ("test/TestModel_for_IB2013-version-three");
		//File file1 = new File ("test/ulfs-models/lacz-a.xml");
		//File file2 = new File ("test/ulfs-models/lacz-b.xml");
		//File file1 = new File ("test/some-test1-sbml.xml");
		//File file2 = new File ("test/some-test2-sbml.xml");
		
		//File file1 = new File ("test/Novak1997_CellCycle-R3");
		//File file2 = new File ("test/Novak1997_CellCycle-R37");
		
		SBMLDiff differ = new SBMLDiff (file1, file2);
		differ.mapTrees ();
		
		/**/
		String graph = differ.getCRNDotGraph ();
		System.out.println (graph);
		/**/

		/**/
		String html = differ.getHTMLReport ();
		System.out.println (html);
		/**/

		/**/
		String md = differ.getMarkDownReport ();
		System.out.println (md);
		/**/

		/**/
		String patch = differ.getDiff ();
		System.out.println (patch);
		/**/
	}
	
	
	private static void testSBML () throws BivesDocumentParseException, FileNotFoundException, SAXException, IOException, ParserConfigurationException, BivesConnectionException, BivesConsistencyException
	{
		
		File file1 = new File ("test/TestModel_for_IB2013-version-one");
		URI uri1 = file1.toURI ();
		//System.out.println (uri1);
		
		TreeDocument td1 = new TreeDocument (DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ().parse (new FileInputStream (file1)), new XyWeighter (), uri1);
		

		File file2 = new File ("test/TestModel_for_IB2013-version-two");
		URI uri2 = file2.toURI ();
		//System.out.println (uri2);
		
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
		TypesettingMarkDown mdown = new TypesettingMarkDown ();
		TypesettingHTML mhtml = new TypesettingHTML ();
		System.out.println (mhtml.markup (mdoc));
		System.out.println (mdown.markup (mdoc));
		
		
    Producer producer = new PatchProducer ();
		producer.init (con2.getConnections (), td1, td2);
		System.out.println (producer.produce ());
		
		
    SBMLGraphProducer gp = new SBMLGraphProducer (con2.getConnections (), sdoc1, sdoc2);
		System.out.println (new GraphTranslatorGraphML ().translate (gp.getCRN ()));
	}


	private static void testCellML () throws BivesDocumentParseException, FileNotFoundException, SAXException, IOException, ParserConfigurationException, BivesCellMLParseException, BivesConsistencyException, BivesLogicalException, URISyntaxException, TransformerException, BivesFlattenException, BivesConnectionException, BivesImportException
	{
		// has reactions:
		// /home/martin/education/phd/stuff/cellml/repository-server/workspace_bhalla_iyengar_1999/bhalla_iyengar_1999_j.cellml
		
		// has recursive imports:
		// /home/martin/education/phd/stuff/cellml/repository-server/w_andre_HH/experiments/gK-050.xml

		//File file = new File ("/tmp/bhalla_iyengar_1999/bhalla_iyengar_1999_j.cellml");
		//File file = new File ("/home/martin/education/phd/stuff/cellml/repository-server/workspace_bhalla_iyengar_1999/bhalla_iyengar_1999_j.cellml");
		//File file = new File ("/home/martin/education/phd/stuff/cellml/repository-server/w_andre_HH/experiments/gK-050.xml");
		//File file = new File ("/tmp/HH/experiments/gK-050.xml");
		File file1 = new File ("/tmp/bhalla_iyengar_1999_j_v1.cellml");
		URI uri1 = file1.toURI ();
		//System.out.println (uri1);
		File file2 = new File ("/tmp/bhalla_iyengar_1999_j_v2.cellml");
		URI uri2 = file2.toURI ();
		//System.out.println (uri2);
		
		TreeDocument td1 = new TreeDocument (DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ().parse (new FileInputStream (file1)), new XyWeighter (), uri1);
		
		TreeDocument td2 = new TreeDocument (DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ().parse (new FileInputStream (file2)), new XyWeighter (), uri2);
		
		//BufferedReader br = new BufferedReader (new FileReader (new File (uri.toURL ().getFile ())));
		
		CellMLDocument cdoc1 = new CellMLDocument (td1);
		if (cdoc1.containsImports ())
			cdoc1 = flatten (cdoc1);
		
		CellMLDocument cdoc2 = new CellMLDocument (td2);
		if (cdoc2.containsImports ())
			cdoc2 = flatten (cdoc2);

		td1 = cdoc1.getTreeDocument ();
		td2 = cdoc2.getTreeDocument ();

		CellMLConnector con2 = new CellMLConnector (cdoc1, cdoc2);
		con2.init (td1, td2);
		con2.findConnections ();
		
		td1.getRoot ().resetModifications ();
		td1.getRoot ().evaluate (con2.getConnections ());
		
		td2.getRoot ().resetModifications ();
		td2.getRoot ().evaluate (con2.getConnections ());
		

		
    Producer producer = new PatchProducer ();
		producer.init (con2.getConnections (), td1, td2);
		//System.out.println (producer.produce ());
		
		
		CellMLGraphProducer gp = new CellMLGraphProducer (con2.getConnections (), cdoc1, cdoc2);
		System.out.println (new GraphTranslatorGraphML ().translate (gp.getCRN ()));

		CellMLDiffInterpreter inter = new CellMLDiffInterpreter (con2.getConnections (), cdoc1, cdoc2);
		inter.interprete ();
		MarkupDocument mdoc = inter.getReport ();
		TypesettingMarkDown mdown = new TypesettingMarkDown ();
		TypesettingHTML mhtml = new TypesettingHTML ();
		System.out.println (mhtml.markup (mdoc));
		System.out.println (mdown.markup (mdoc));
		
		//cdoc.debug ("");
		
		//System.out.println (uri);
		
		/*args = new String [] {"test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		TreeDocument td = new TreeDocument (builder.parse (new FileInputStream (args[0])), new XyWeighter ());
		
		SBMLDocument doc = new SBMLDocument (td);
		
		System.out.println (doc.getLevel ());*/
	}
	
	private static CellMLDocument flatten (CellMLDocument cdoc) throws IOException, BivesFlattenException, BivesConsistencyException, TransformerException, SAXException, ParserConfigurationException, BivesCellMLParseException, BivesLogicalException, URISyntaxException, BivesImportException
	{

		File tmpfile = File.createTempFile ("bives", "flattened");
		//tmpfile.deleteOnExit ();
		
		cdoc.flatten ();
		cdoc.write (tmpfile);
		
		TreeDocument td = new TreeDocument (DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ().parse (new FileInputStream (tmpfile)), new XyWeighter (), tmpfile.toURI ());
		return new CellMLDocument (td);
	}
	
}
