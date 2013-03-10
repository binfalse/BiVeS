package de.unirostock.sems.bives.algorithm.sbml;

import de.unirostock.sems.bives.ds.xml.DocumentNode;


public class SBMLEventDelay extends SBMLEventDetail
{
	public SBMLEventDelay (DocumentNode delayA, DocumentNode delayB)
	{
		super (delayA, delayB);
	}

	@Override
	protected String getType ()
	{
		return "Event Delay";
	}
}
