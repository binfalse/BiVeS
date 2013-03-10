/**
 * 
 */
package de.unirostock.sems.bives.ds;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class MultiNodeMapper<T>
{
	private HashMap<String, Vector<T>> mapper;
	
	public MultiNodeMapper ()
	{
		mapper = new HashMap<String, Vector<T>> ();
	}
	
	public Set<String> getIds ()
	{
		return mapper.keySet ();
	}
	
	public void addNode (String id, T node)
	{
		Vector<T> nodes = mapper.get (id);
		if (nodes == null)
		{
			nodes =new Vector<T> ();
			mapper.put (id, nodes);
		}
		nodes.add (node);
	}
	
	public Vector<T> getNodes (String id)
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
