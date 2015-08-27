/**
 * 
 */
package de.unirostock.sems.bives.test.upstream;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.unirostock.sems.bives.tools.BivesTools;

/**
 * @author Martin Scharm
 *
 */
@RunWith(JUnit4.class)
public class TestTools
{

	/**
	 * Test version.
	 */
	@Test
	public void  testVersion ()
	{
		String version = BivesTools.getBivesVersion ();
		
		assertTrue ("sbml wasn't compiled into the framework?", version.contains ("SBML"));
		assertTrue ("cellml wasn't compiled into the framework?", version.contains ("CellML"));
		assertTrue ("core module wasn't compiled into the framework?", version.contains ("Core"));
		assertTrue ("framework wasn't compiled into the framework?", version.contains ("FrameWork"));
	}
	
}
