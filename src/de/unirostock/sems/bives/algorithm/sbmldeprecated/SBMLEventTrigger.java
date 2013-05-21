package de.unirostock.sems.bives.algorithm.sbmldeprecated;

import de.unirostock.sems.bives.ds.xml.DocumentNode;


public class SBMLEventTrigger extends SBMLEventDetail
{
	public SBMLEventTrigger (DocumentNode nodeA, DocumentNode nodeB)
	{
		super (nodeA, nodeB);
	}

	@Override
	protected String getType ()
	{
		return "Event Trigger";
	}
	
	
	
}
