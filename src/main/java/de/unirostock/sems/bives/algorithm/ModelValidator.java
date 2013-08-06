/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.xml.TreeDocument;


/**
 * @author Martin Scharm
 *
 */
public abstract class ModelValidator
{
	public abstract boolean validate (TreeDocument d) throws Exception;
	public abstract boolean validate (String d) throws Exception;
}
