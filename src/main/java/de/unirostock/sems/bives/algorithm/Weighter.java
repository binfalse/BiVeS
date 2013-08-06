/**
 * 
 */
package de.unirostock.sems.bives.algorithm;

import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TextNode;


/**
 * @author Martin Scharm
 *
 */
public abstract class Weighter
{
	
	/**
	 * Calculates the weight of a DocumentNode.
	 *
	 * @param node the node
	 * @return the weight
	 */
	public abstract double getWeight (DocumentNode node);
	
	/**
	 * Calculates the weight of a TextNode.
	 *
	 * @param node the node
	 * @return the weight
	 */
	public abstract double getWeight (TextNode node);
}
