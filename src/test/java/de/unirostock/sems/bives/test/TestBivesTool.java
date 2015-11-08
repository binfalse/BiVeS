/**
 * 
 */
package de.unirostock.sems.bives.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unirostock.sems.bives.test.general.CommandLineTest;
import de.unirostock.sems.bives.test.general.GeneralTest;
import de.unirostock.sems.bives.test.sysmoseek.ManchesterTest;
import de.unirostock.sems.bives.test.upstream.TestTools;
import de.unirostock.sems.bives.test.upstream.XmlTest;


/**
 * @author Martin Scharm
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ TestTools.class, XmlTest.class, ManchesterTest.class, GeneralTest.class, CommandLineTest.class })
public class TestBivesTool
{
}
