/**
 * 
 */
package de.unirostock.sems.bives.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.DiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.ds.xml.TreeNode;
import de.unirostock.sems.bives.markup.MarkupDocument;
import de.unirostock.sems.bives.markup.MarkupElement;


// TODO: Auto-generated Javadoc
/**
 * The Class Tools.
 *
 * @author Martin Scharm
 */
public class Tools
{
	
	
	/**
	 * Get the minimum of 3 values.
	 *
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @return the min
	 */
	public static int minimum(int a, int b, int c)
	{
	    return Math.min (Math.min (a, b), c);
	}

	/**
	 * Compute levenshtein distance of two strings.
	 *
	 * @param str1 the str1
	 * @param str2 the str2
	 * @return the distance
	 */
	public static int computeLevenshteinDistance (String str1, String str2)
	{
	    int [][] distance = new int [str1.length () + 1][str2.length () + 1];
	
	    for (int i = 0; i <= str1.length (); i++)
	            distance[i][0] = i;
	    for (int j = 1; j <= str2.length (); j++)
	            distance[0][j] = j;
	
	    for (int i = 1; i <= str1.length (); i++)
	            for (int j = 1; j <= str2.length (); j++)
	                    distance[i][j] = minimum (
	                                    distance[i - 1][j] + 1,
	                                    distance[i][j - 1] + 1,
	                                    distance[i - 1][j - 1]+ ((str1.charAt (i - 1) == str2.charAt (j - 1)) ? 0 : 1));
	
	    return distance[str1.length ()][str2.length ()];    
	}
	
	/**
	 * Repeat a string.
	 *
	 * @param s the string
	 * @param times the number of times to repeat s
	 * @return the string
	 */
	public static String repeat (String s, int times)
	{
		if (times < 0)
			return "";
		StringBuilder str = new StringBuilder ();
		for (int i = 0; i < times; i++)
			str.append (s);
		return str.toString ();
	}
	
	
	/**
	 * Beautify the display of a double <code>d</code>. If the double is an int
	 * we'll omit the <code>.0</code>. Additionally you may define an int to
	 * neglect (e.g. <code>0</code> or <code>1</code>), thus, if
	 * <code>d == neglect</code> you'll get an empty string. Especially designed
	 * to display equations and stuff (e.g. omit an multiplier
	 * of <code>1</code> or an offset of <code>0</code>).
	 * 
	 * @param d
	 *          the double to print
	 * @param neglect
	 *          an integer to neglect. Can be null if you don't want to omit any
	 *          number
	 * @return the pretty string
	 */
	public static String prettyDouble (Double d, Integer neglect)
	{
		if (d == null)
			return "";
		
		if ((d == Math.rint (d)) && !Double.isInfinite (d) && !Double.isNaN (d))
		{
			int s = d.intValue ();
			if (neglect != null && s == neglect)
				return "";
	    return s + "";
		}
		
		return d.toString ();
	}
	
	
	/**
	 * Hash a message.
	 *
	 * @param msg the message
	 * @return the hash
	 */
	public static String hash (String msg)
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("MD5");
	    md.update(msg.getBytes());
	
	    byte byteData[] = md.digest();
	
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++)
	        sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			return sb.toString ();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * The Class SimpleOutputStream.
	 */
	public static class SimpleOutputStream extends OutputStream
	{

	  /** The string. */
  	private StringBuilder string = new StringBuilder();
	  
	  /* (non-Javadoc)
  	 * @see java.io.OutputStream#write(int)
  	 */
  	@Override
	  public void write(int b) throws IOException
	  {
	      this.string.append((char) b );
	  }

	  //Netbeans IDE automatically overrides this toString()
	  /* (non-Javadoc)
  	 * @see java.lang.Object#toString()
  	 */
  	public String toString()
	  {
	      return this.string.toString();
	  }
	  
	  /**
  	 * Reset.
  	 */
  	public void reset ()
	  {
	  	this.string = new StringBuilder();
	  }
	}
	

  /**
   * Byte to hex.
   *
   * @param data the data
   * @return the string
   */
  public static String byteToHex(byte[] data) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < data.length; i++) {
          int halfbyte = (data[i] >>> 4) & 0x0F;
          int two_halfs = 0;
          do {
              if ((0 <= halfbyte) && (halfbyte <= 9))
                  buf.append((char) ('0' + halfbyte));
              else
                      buf.append((char) ('a' + (halfbyte - 10)));
              halfbyte = data[i] & 0x0F;
          } while(two_halfs++ < 1);
      }
      return buf.toString();
  }

  /**
   * Sha1.
   *
   * @param text the text
   * @return the string
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws UnsupportedEncodingException the unsupported encoding exception
   */
  public static String sha1 (String text) throws NoSuchAlgorithmException,
UnsupportedEncodingException  {
	  MessageDigest md = MessageDigest.getInstance("SHA-1");
	  byte[] sha1hash = new byte[40];
	  md.update(text.getBytes("iso-8859-1"), 0, text.length());
	  sha1hash = md.digest();
	  return byteToHex(sha1hash);
  }
  
  /**
   * Gen math html stats.
   *
   * @param a the a
   * @param b the b
   * @param markupElement the markup element
   * @param markupDocument the markup document
   */
  public static void genMathHtmlStats (DocumentNode a, DocumentNode b, MarkupElement markupElement, MarkupDocument markupDocument)
  {
  	if (a == null && b == null)
  		return;

  	try
  	{
  		//String ret = ""; 
			if (a == null)
			{
				markupElement.addValue ("inserted math: " + markupDocument.insert (TreeTools.transformMathML (b)));
				//ret += "inserted math: <span class='inserted'>" + transformMathML (b) + "</span>";
			}
			else if (b == null)
			{
				markupElement.addValue ("deleted math: " + markupDocument.delete (TreeTools.transformMathML (a)));
				//ret += "deleted math: <span class='deleted'>" + transformMathML (a) + "</span>";
			}
			else if (a.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED))
			{
				//System.out.println ("pre math: " + printSubDoc (a));
				//System.out.println ("post math: " + printMathML (getSubDoc (b)));
				
				markupElement.addValue ("modified math: " + markupDocument.delete (TreeTools.transformMathML (a)) + " to " + markupDocument.insert (TreeTools.transformMathML (b)));
				
				//ret += "modified math from: <span class='deleted'>" + transformMathML (a);
				//ret += "</span> to: <span class='inserted'>" + transformMathML (b) + "</span>";
				
				//ret += "modified math from: <span class='deleted'>" + printSubDoc (a);
				//ret += "</span> to: <span class='inserted'>" + printSubDoc (b) + "</span>";
			}
  	}
  	catch (Exception e)
  	{
  		LOGGER.error ("error generating math", e);
  		markupElement.addValue ("error generating math: " + e.getMessage ());
  	}
  }
  
  /**
   * Gen attribute html stats.
   *
   * @param a the a
   * @param b the b
   * @param markupElement the markup element
   * @param markupDocument the markup document
   */
  public static void genAttributeHtmlStats (DocumentNode a, DocumentNode b, MarkupElement markupElement, MarkupDocument markupDocument)
  {
  	if (a == null || b == null)
  		return;
  	
  	Set<String> allAttr = new HashSet<String> ();
		allAttr.addAll (a.getAttributes ());
		allAttr.addAll (b.getAttributes ());
		for (String attr : allAttr)
		{
			String aA = a.getAttribute (attr), bA = b.getAttribute (attr);
			if (aA == null)
				markupElement.addValue ("Attribute "+ markupDocument.attribute (attr) + " was inserted: "+markupDocument.insert (bA));
			else if (bA == null)
				markupElement.addValue ("Attribute "+ markupDocument.attribute (attr) + " was deleted: "+markupDocument.delete (aA));
			else if (!aA.equals (bA))
				markupElement.addValue ("Attribute "+ markupDocument.attribute (attr) + " has changed: "+markupDocument.delete (aA) +" "+markupDocument.rightArrow ()+" "+ markupDocument.insert (bA));
		}
  }
  
  /*public static String genTableIdCol (DocumentNode a, DocumentNode b)
  {
  	if (a == null)
  		return getIdHtmlReport (null, b.getId ()) + " " + getNameHtmlReport (null, b.getAttribute ("name"));
  	else if (b == null)
  		return getIdHtmlReport (a.getId (), null) + " " + getNameHtmlReport (a.getAttribute ("name"), null);
  	else
  		return getIdHtmlReport (a.getId (), b.getId ()) + " " + getNameHtmlReport (a.getAttribute ("name"), b.getAttribute ("name"));
  }
  
  public static String getNameHtmlReport (String nameA, String nameB)
  {
		if (nameA != null || nameB != null)
		{
			if (nameA == null)
				return "(<span class='inserted'>"+nameB+"</span>)";
			else if (nameB == null)
				return "(<span class='deleted'>"+nameA+"</span>)";
			else if (!nameA.equals (nameB))
				return "(<span class='deleted'>"+nameA+"</span> &rarr; <span class='inserted'>"+nameB+"</span>)";
			else
				return "("+nameA+")";
		}
		return "";
  }
  
  public static String getIdHtmlReport (String idA, String idB)
  {
		if (idA == null)
			return "<span class='inserted'>"+idB+"</span>";
		else if (idB == null)
			return "<span class='deleted'>"+idA+"</span>";
		else if (!idA.equals (idA))
			return "<span class='deleted'>"+idA+"</span> &rarr; <span class='inserted'>"+idB+"</span>";
		else
			return idA;
  }*/
  
  
  /*public static String printMathML (Document doc) throws TransformerException
  {
    File xsltFile = new File("/tmp/mmlctop2_0.xsl");

    // JAXP liest Daten über die Source-Schnittstelle
    Source xsltSource = new StreamSource(xsltFile);

    // das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
    TransformerFactory transFact =
            TransformerFactory.newInstance();
    Transformer trans = transFact.newTransformer(xsltSource);

    SimpleOutputStream out = new SimpleOutputStream ();
    trans.transform(new DOMSource(doc), new StreamResult(out));
    return out.toString ();
}*/
}
