/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.algorithm.general.XyWeighter;
import de.unirostock.sems.bives.ds.sbml.SBMLDocument;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.exception.BivesDocumentParseException;


/**
 * @author Martin Scharm
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
	 */
	public static void main (String[] args) throws ParserConfigurationException, BivesDocumentParseException, FileNotFoundException, SAXException, IOException
	{
		args = new String [] {"test/BSA-ptinst-2012-11-11", "test/BSA-sigbprlysis-2012-11-11"};
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ();
		
		TreeDocument td = new TreeDocument (builder.parse (new FileInputStream (args[0])), new XyWeighter ());
		
		SBMLDocument doc = new SBMLDocument (td);
		
		System.out.println (doc.getLevel ());
	}
	
}
