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
		LOGGER.setLogFile ("/tmp/cellml-repo-kram/MoreTests.log");
		LOGGER.setLogToStdErr (false);
		
		String cellMlBase = System.getProperty( "user.home" ) + "/education/stuff/biomodels/cellmlclones/";
		String biomodelsBase = "";
		
		String [] cellMlTests = new String []
		{
      // absolute paths & 404's ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9nYXJtZW5kaWEtdG9ycmVzX2dvbGRiZXRlcl9qYWNxdWV0XzIwMDcK/garmendiatorres_2007_1.1model.cellml"
      // unit in import not defined "aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3cvbWF0dGhpYXNrb2VuaWcvTWV0YWJvbGljQ29tcG9uZW50TGlicmFyeQo=/CellML-source/CellMLTools/models/Gamma.cellml"// @28
      // ok
      // double import of same component -> thats a real issue, dont know how to fix it. keep the exception
			// "aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3cvbWF0dGhpYXNrb2VuaWcvTWV0YWJvbGljQ29tcG9uZW50TGlicmFyeQo=/examples/Example_MassAction_2.cellml" // @28
      // want to import unit that is not defined in imported doc "aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3cvbWNvbzAwMS92aXJ0dWFsYmFjdGVyaWFscGhvdG9ncmFwaHkK/Bioenvironment_OmpRpFormation.cellml"//@5
      // unknown unit "aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3cvbWNvbzAwMS92aXJ0dWFsYmFjdGVyaWFscGhvdG9ncmFwaHkK/Main.cellml" // @5
      // double import of same component -> thats a real issue, dont know how to fix it. keep the exception
      // ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3cvYW5kcmUvbmlja2Vyc29uLTIwMDgK/cellml/models/2004_tenTusscher-version-2/experiments/single-stimulus-endocardial.xml" //@0
      // double import of same component -> thats a real issue, dont know how to fix it. keep the exception
      // ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3cvYW5kcmUvVlBILU1JUAo=/experiments/periodic-stimulus.xml" //@1
      // units not defined ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS93YXVnaF9zaGVycmF0dF8yMDA2Cg==/waugh_sherratt_2006_b.cellml"// @0
      // initial conc not valid ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9hb25fY29ydGFzc2FfMjAwMgo=/aon_cortassa_2002.cellml" //@1
      // multi definition of units ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9hMQo=/models/1962_noble/decomposed-n62/time_model.xml" // @82
      // multiimport of components -> multiparent ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9hMQo=/models/2004_tenTusscher/experiments/single-stimulus-M.xml" //@82
      // ok ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9ib25kYXJlbmtvX3N6aWdldGlfYmV0dF9raW1fcmFzbXVzc29uXzIwMDQK/bondarenko_szigeti_bett_kim_rasmusson_2004_b.cellml" // @0
      // ok ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9iZWFyZF8yMDA1Cg==/beard_2005.cellml"// @9
      // exception ok ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9jYXJkaW92YXNjdWxhcl9jaXJjdWxhdGlvbl93aW5ka2Vzc2VsCg==/ParaSys.cellml" // @0
      // initial conc not valid ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9kZXlvdW5nX2tlaXplcl8xOTkyCg==/deyoung_keizer_1992.cellml"// @4
      // multiple connection problem -> thats a real issue, dont know how to fix it. keep the exception
      // ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9lbGVjdHJlY29ibHUK/ElectrEcoBluModel_NoFeedback.cellml" // @0
      // ok ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9mZW50b25fa2FybWFfMTk5OAo=/fenton_karma_1998_GP.cellml" // @2-10
      // their fault ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9ndXl0b25fZWxlY3Ryb2x5dGVzXzIwMDgK/parameters.cellml" //@4
      // their fault ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9TVlBfMDAwMDAwMTkK/Bioenvironment_RxR1P1MM_PYOFormation.cellml" //@3
      // behoben ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9uaWVkZXJlcl9odW50ZXJfc21pdGhfMjAwNgo=/niederer_hunter_smith_2006.cellml" // @0
      // there was an error with scaling factor in units ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9uaWVkZXJlcl9zbWl0aF8yMDA3Cg==/niederer_smith_2007.cellml"// @3
      // imports base unit: volt ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9uZXBocm9uCg==/mackenzie_loo_panayotova-heiermann_wright_1996/experiments/figure-03.xml" //@0
      // typo in import -> cameltoe ,"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9jYXJkaW92YXNjdWxhcl9jaXJjdWxhdGlvbl93aW5ka2Vzc2VsCg==/ModelWindKessel.cellml"
		};
		
		/*File file = new File (cellMlBase +
			//"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3cvbWF0dGhpYXNrb2VuaWcvTWV0YWJvbGljQ29tcG9uZW50TGlicmFyeQo=/examples/Example_MassAction_2.cellml"
			//"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9ib25kYXJlbmtvX3N6aWdldGlfYmV0dF9raW1fcmFzbXVzc29uXzIwMDQK/bondarenko_szigeti_bett_kim_rasmusson_2004_b.cellml"
			//"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9uaWVkZXJlcl9odW50ZXJfc21pdGhfMjAwNgo=/niederer_hunter_smith_2006.cellml"
			//"aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9mZW50b25fa2FybWFfMTk5OAo=/fenton_karma_1998_GP.cellml"
			// "aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9uaWVkZXJlcl9odW50ZXJfc21pdGhfMjAwNgo=/niederer_hunter_smith_2006.cellml"
			// "aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9nYXJtZW5kaWEtdG9ycmVzX2dvbGRiZXRlcl9qYWNxdWV0XzIwMDcK/garmendiatorres_2007_1.1model.cellml"
			// "aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9jYXJkaW92YXNjdWxhcl9jaXJjdWxhdGlvbl93aW5ka2Vzc2VsCg==/ModelWindKessel.cellml"
			);*/
		
		DocumentClassifier classifier = new DocumentClassifier ();
		
		int failes = 0;
		for (String s : cellMlTests)
		{
			if (!testCellMl (classifier, new File (cellMlBase + s)))
				failes++;
		}

		System.out.println (failes + " of " + cellMlTests.length + " documents failed the tests");
		
		
		// TODO: flatten & reread
		
		/*System.out.println (file.getAbsolutePath () + " has type " + classifier.classify (file));
		
		if (classifier.getExceptions () != null)
			for (Exception e: classifier.getExceptions ())
				e.printStackTrace ();*/
	}
	
	private static boolean testCellMl (DocumentClassifier classifier, File file)
	{
		int type = classifier.classify (file);
		
		if ((type & DocumentClassifier.CELLML) > 0)
		{
			System.out.println ("ok: " + file.getAbsolutePath ());
			return true;
		}
		else
		{
			System.out.println (file.getAbsolutePath ());
			if (classifier.getExceptions () != null)
				for (Exception e: classifier.getExceptions ())
					e.printStackTrace ();
			return false;
		}
	}
	
}
