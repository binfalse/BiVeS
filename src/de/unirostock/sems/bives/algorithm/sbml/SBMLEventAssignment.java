package de.unirostock.sems.bives.algorithm.sbml;

import de.unirostock.sems.bives.ds.xml.DocumentNode;


public class SBMLEventAssignment extends SBMLEventDetail
{
	public SBMLEventAssignment (DocumentNode assignmentA, DocumentNode assignmentB)
	{
		super (assignmentA, assignmentB);
	}

	@Override
	protected String getType ()
	{
		return "Event Assignment";
	}
}
