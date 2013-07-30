/**
 * 
 */
package de.unirostock.sems.bives;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.tools.DocumentClassifier;


/**
 * @author Martin Scharm
 *
 */
public class MoreTests
{
	public static void main (String [] args) throws ParserConfigurationException
	{
		LOGGER.addLevel (LOGGER.DEBUG);
		LOGGER.addLevel (LOGGER.INFO);
		LOGGER.addLevel (LOGGER.WARN);
		
		String cellMlBase = System.getProperty( "user.home" ) + "/education/stuff/biomodels/cellmlclones/";
		String biomodelsBase = "";
		
		
		File file = new File (cellMlBase +
			"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9uaWVkZXJlcl9odW50ZXJfc21pdGhfMjAwNgo=/niederer_hunter_smith_2006.cellml"
			// "aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9nYXJtZW5kaWEtdG9ycmVzX2dvbGRiZXRlcl9qYWNxdWV0XzIwMDcK/garmendiatorres_2007_1.1model.cellml"
			// "aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9jYXJkaW92YXNjdWxhcl9jaXJjdWxhdGlvbl93aW5ka2Vzc2VsCg==/ModelWindKessel.cellml"
			);
		
		DocumentClassifier classifier = new DocumentClassifier ();
		
		System.out.println (file.getAbsolutePath () + " has type " + classifier.classify (file));
		
		if (classifier.getExceptions () != null)
			for (Exception e: classifier.getExceptions ())
				e.printStackTrace ();
	}
}
