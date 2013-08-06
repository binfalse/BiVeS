/**
 * 
 */
package de.unirostock.sems.bives.ds;

import java.util.HashMap;
import java.util.Set;

import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class NodeMapper<T>
{
	private HashMap<String, T> mapper;
	
	public NodeMapper ()
	{
		mapper = new HashMap<String, T> ();
	}
	
	public Set<String> getIds ()
	{
		return mapper.keySet ();
	}
	
	public void putNode (String id, T node)
	{
		mapper.put (id, node);
	}

	public void rmNode (String id)
	{
		mapper.remove (id);
	}
	
	public T getNode (String id)
	{
		return mapper.get (id);
	}
	
	public String toString ()
	{
		StringBuilder s = new StringBuilder (" ");
		for (String a : mapper.keySet ())
			s.append (a + " =>> " + mapper.get (a).toString () + "");
		return s.toString ();
	}
}
