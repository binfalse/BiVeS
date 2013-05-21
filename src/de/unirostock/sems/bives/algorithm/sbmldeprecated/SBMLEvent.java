/**
 * 
 */
package de.unirostock.sems.bives.algorithm.sbmldeprecated;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.unirostock.sems.bives.algorithm.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.tools.Tools;


/**
 * @author Martin Scharm
 *
 */
public class SBMLEvent implements DiffReporter
{
	private final static Logger LOGGER = Logger.getLogger(SBMLEvent.class.getName());
	
	private DocumentNode eventA, eventB;
	private SBMLEventDetail trigger, delay;
	private Vector<SBMLEventDetail> assignments;

	public SBMLEvent (DocumentNode eventA, DocumentNode eventB)
	{
		this.eventA = eventA;
		this.eventB = eventB;
		assignments = new Vector<SBMLEventDetail> ();
		//setUp ();
	}
	
	public void setEvenetA (DocumentNode eventA)
	{
		this.eventA = eventA;
	}
	
	public void setEvenetB (DocumentNode eventB)
	{
		this.eventB = eventB;
	}
	

	public void addAssignment (SBMLEventAssignment assignment)
	{
		this.assignments.add (assignment);
	}
	
	public void setDelay (SBMLEventDelay delay)
	{
		this.delay = delay;
	}
	
	public void setTrigger (SBMLEventTrigger trigger)
	{
		this.trigger = trigger;
	}
	
	/*private void setUp ()
	{
		for (TreeNode node : event.getChildren ())
		{
			DocumentNode dnode = (DocumentNode) node;
			if (dnode.getTagName ().equals ("trigger"))
			{
				trigger = new SBMLEventTrigger (dnode);
			}
			else if (dnode.getTagName ().equals ("delay"))
			{
				delay = new SBMLEventDelay (dnode);
			}
			else if (dnode.getTagName ().equals ("listOfEventAssignments"))
			{
				for (TreeNode node2 : dnode.getChildren ())
				{
					assignments.add (new SBMLEventAssignment ((DocumentNode) node2));
				}
			}
		}
	}*/
	
	
	
	
	
	public String reportHTML (String cssclass)
	{
		return "<tr><td class='"+cssclass+"'>"+Tools.genTableIdCol (eventA, eventB)+"</td><td class='"+cssclass+"'>" + getEventHtml (cssclass) +
				"</td></tr>";
	}
	
	
	
	private String getSubHtml (String cssclass)
	{
		String ret = "";
		if (trigger != null)
			ret += trigger.reportHTML (cssclass) + "</br>";
		if (delay != null)
			ret += delay.reportHTML (cssclass) + "</br>";
		for (SBMLEventDetail ass : assignments)
			ret += ass.reportHTML (cssclass) + "</br>";
		return ret;
	}
	
	
	
	
	
	private String getEventHtml (String cssclass)
	{
			if (eventA == null)
			{
				return "inserted event</br>" + getSubHtml (cssclass);
			}
			if (eventB == null)
			{
				return "deleted event</br>" + getSubHtml (cssclass);
			}
			
			String ret = "modified event:<br/>";

			Tools.genAttributeHtmlStats (eventA, eventB);
			
			return ret + "</br>" + getSubHtml (cssclass);
	}
}
