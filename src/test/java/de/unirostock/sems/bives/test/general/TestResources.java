/**
 * 
 */
package de.unirostock.sems.bives.test.general;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Random;
import java.util.UUID;


/**
 * @author martin
 *
 */
public class TestResources
{
	public static Random rand = new Random(); 
	private static String testDir = "test" + File.separatorChar;
	private static File nonexistentFile;
	private static File invalidSbmlFile;
	private static String [] validSbml = new String []
		{
		"TestModel_for_IB2013-version-one",
		"TestModel_for_IB2013-version-two",
		"TestModel_for_IB2013-version-three",
		"Teusink-2013Dec20.xml",
		"potato.xml"
		};
	private static String [] validCellML = new String []
		{
		"bhalla_iyengar_1999_j_v1.cellml",
		"bhalla_iyengar_1999_j_v2.cellml",
		"bhalla_iyengar_1999_j_v3.cellml",
		"bhalla_iyengar_1999_j_v4.cellml",
		"aguda_b_1999.cellml"
		};
	
	
	
	public static File getInvalidXmlFile ()
	{
		if (invalidSbmlFile == null)
			invalidSbmlFile = new File (testDir + "invalid_sbml_model.xml");

		assertTrue ("invalid xml file " + invalidSbmlFile.getAbsolutePath () + " doesn't exist", invalidSbmlFile.exists () && invalidSbmlFile.canRead ());
		
		return invalidSbmlFile;
	}
	
	
	
	public static File getNonexistentFile ()
	{
		if (nonexistentFile == null)
		{
			nonexistentFile = new File ("/tmp/" + UUID.randomUUID ().toString ());
			while (nonexistentFile.exists ())
				nonexistentFile = new File ("/tmp/" + UUID.randomUUID ().toString ());
		}
		return nonexistentFile;
	}



	public static File getValidSbmlFile ()
	{
		File f = new File ("test/" + validSbml[rand.nextInt (validSbml.length)]);
		assertTrue ("valid sbml file " + f.getAbsolutePath () + " doesn't exist", f.exists () && f.canRead ());
		return f;
	}
}
