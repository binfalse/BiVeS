/**
 * 
 */
package de.unirostock.sems.bives.ds;


/**
 * @author Martin Scharm
 *
 */
public class SBOTerm
{
	public static final String MOD_STIMULATOR = "stimulator";
	public static final String MOD_UNKNOWN = "unknown";
	public static final String MOD_INHIBITOR = "inhibitor";
	public static final String MOD_NONE = "none";
	
	private String SBOTerm;
	
	public SBOTerm (String SBOTerm)
	{
		this.SBOTerm = SBOTerm;
	}
	
	public String getSBOTerm ()
	{
		return SBOTerm;
	}
	
	
	public String resolvModifier ()
	{
		if (SBOTerm == null || !SBOTerm.startsWith ("SBO:"))
			return MOD_UNKNOWN;
		
		try
		{
			// TODO: resolve the stuff dynamically from db...
			switch (Integer.parseInt (SBOTerm.substring (4)))
			{
				case 459: // stimulator
				case 13: // catalyst
				case 460: // enzymatic catalyst (is a)
				case 461: // essential activator (is a)
				case 535: // binding activator (is a)
				case 534: // catalytic activator (is a)
				case 533: // specific activator (is a)
				case 462: // non-essential activator (is a)
				case 21: // potentiator (is a)
					return MOD_STIMULATOR;
				case 20: // inhibitor (is a)
				case 206: // competitive inhibitor (is a)
				case 207: // non-competitive inhibitor (is a)
				case 537: // complete inhibitor (is a)
				case 536: // partial inhibitor (is a)
					return MOD_INHIBITOR;
			}
		}
		catch (NumberFormatException e)
		{
			
		}
		return MOD_UNKNOWN;
	}
}
