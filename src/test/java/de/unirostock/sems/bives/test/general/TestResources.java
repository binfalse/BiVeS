/**
 * 
 */
package de.unirostock.sems.bives.test.general;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Random;
import java.util.UUID;


/**
 * The Class TestResources.
 *
 * @author Martin Scharm
 */
public class TestResources
{
	
	/** The rand. */
	public static Random rand = new Random(); 
	
	/** The test dir. */
	private static String testDir = "test" + File.separatorChar;
	
	/** The nonexistent file. */
	private static File nonexistentFile;
	
	/** The invalid sbml file. */
	private static File invalidSbmlFile;
	
	/** The valid sbml. */
	public static String [] validSbml = new String []
		{
		"TestModel_for_IB2013-version-one",
		"TestModel_for_IB2013-version-two",
		"TestModel_for_IB2013-version-three",
		"Teusink-2013Dec20.xml",
		"potato.xml"
		};
	
	/** The valid cell ml. */
	public static String [] validCellML = new String []
		{
		"bhalla_iyengar_1999_j_v1.cellml",
		"bhalla_iyengar_1999_j_v2.cellml",
		"bhalla_iyengar_1999_j_v3.cellml",
		"bhalla_iyengar_1999_j_v4.cellml",
		"aguda_b_1999.cellml"
		};
	
	
	
	/**
	 * Gets the invalid xml file.
	 *
	 * @return the invalid xml file
	 */
	public static File getInvalidXmlFile ()
	{
		if (invalidSbmlFile == null)
			invalidSbmlFile = new File (testDir + "invalid_sbml_model.xml");

		assertTrue ("invalid xml file " + invalidSbmlFile.getAbsolutePath () + " doesn't exist", invalidSbmlFile.exists () && invalidSbmlFile.canRead ());
		
		return invalidSbmlFile;
	}
	
	
	
	/**
	 * Gets the nonexistent file.
	 *
	 * @return the nonexistent file
	 */
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



	/**
	 * Gets the valid sbml file.
	 *
	 * @return the valid sbml file
	 */
	public static File getValidSbmlFile ()
	{
		File f = new File ("test/" + validSbml[rand.nextInt (validSbml.length)]);
		assertTrue ("valid sbml file " + f.getAbsolutePath () + " doesn't exist", f.exists () && f.canRead ());
		return f;
	}
}
