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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.sbml.SBMLDiffReporter;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class Tools
{
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

	public static Document getSubDoc (DocumentNode node)
	{
		try 
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document d = docBuilder.newDocument ();
			node.getSubDoc (d, null);
			return d;
		}
		catch (Exception e)
		{
			LOGGER.error ("error creating subdoc", e);
			return null;
		}
	}
	

	public static String printSubDoc (DocumentNode node)
	{
		try 
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document d = docBuilder.newDocument ();
			node.getSubDoc (d, null);
			return printDocument (d);
		}
		catch (Exception e)
		{
			LOGGER.error ("error creating subdoc", e);
			return "error creating doc: " + e.getMessage ();
		}
	}
	
	public static String printPrettySubDoc (DocumentNode node)
	{
		try 
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document d = docBuilder.newDocument ();
			d.createElementNS ("http://www.cellml.org/cellml/1.0", "cellml");
			node.getSubDoc (d, null);
			return prettyPrintDocument (d, new Tools.SimpleOutputStream ()).toString ();
		}
		catch (Exception e)
		{
			LOGGER.error ("error creating subdoc", e);
			return "error creating doc: " + e.getMessage ();
		}
	}
	
	public static String printDocument (Document doc)
	{
		DOMImplementationLS domImplLS = (DOMImplementationLS) doc
	    .getImplementation();
	//LSSerializer serializer = domImplLS.createLSSerializer();
	//return serializer.writeToString(doc);
	
  ByteArrayOutputStream baos = new ByteArrayOutputStream();

	
	LSOutput lso = domImplLS.createLSOutput();
	lso.setByteStream(baos);
	LSSerializer lss = domImplLS.createLSSerializer();
	lss.write(doc, lso);
	return baos.toString ();
	
	}
	
	public static String prettyPrintDocument(Document doc) throws IOException, TransformerException
	{
		return prettyPrintDocument (doc, new SimpleOutputStream ()).toString ();
  }
	
	public static OutputStream prettyPrintDocument(Document doc, OutputStream out) throws IOException, TransformerException
	{
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    return out;
  }
	
	public static class SimpleOutputStream extends OutputStream
	{

	  private StringBuilder string = new StringBuilder();
	  
	  @Override
	  public void write(int b) throws IOException
	  {
	      this.string.append((char) b );
	  }

	  //Netbeans IDE automatically overrides this toString()
	  public String toString()
	  {
	      return this.string.toString();
	  }
	  
	  public void reset ()
	  {
	  	this.string = new StringBuilder();
	  }
	}
	

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

  public static String sha1 (String text) throws NoSuchAlgorithmException,
UnsupportedEncodingException  {
	  MessageDigest md = MessageDigest.getInstance("SHA-1");
	  byte[] sha1hash = new byte[40];
	  md.update(text.getBytes("iso-8859-1"), 0, text.length());
	  sha1hash = md.digest();
	  return byteToHex(sha1hash);
  }
  
  public static String genMathHtmlStats (DocumentNode a, DocumentNode b)
  {
  	if (a == null && b == null)
  		return "";

  	try
  	{
  		String ret = ""; 
			if (a == null)
			{
				ret += "inserted math: <span class='inserted'>" + transformMathML (b) + "</span>";
			}
			else if (b == null)
			{
				ret += "deleted math: <span class='deleted'>" + transformMathML (a) + "</span>";
			}
			else if (a.hasModification (TreeNode.MODIFIED | TreeNode.SUB_MODIFIED))
			{
				//System.out.println ("pre math: " + printSubDoc (a));
				//System.out.println ("post math: " + printMathML (getSubDoc (b)));
				ret += "modified math from: <span class='deleted'>" + transformMathML (a);
				ret += "</span> to: <span class='inserted'>" + transformMathML (b) + "</span>";
				//ret += "modified math from: <span class='deleted'>" + printSubDoc (a);
				//ret += "</span> to: <span class='inserted'>" + printSubDoc (b) + "</span>";
			}
			return ret;
  	}
  	catch (Exception e)
  	{
  		LOGGER.error ("error generating math", e);
  		return "error generating math" + e.getMessage ();
  	}
  }
  
  public static String genAttributeHtmlStats (DocumentNode a, DocumentNode b)
  {
  	if (a == null || b == null)
  		return "";
  	
  	
  	String ret = ""; 
  	Set<String> allAttr = new HashSet<String> ();
		allAttr.addAll (a.getAttributes ());
		allAttr.addAll (b.getAttributes ());
		for (String attr : allAttr)
		{
			String aA = a.getAttribute (attr), bA = b.getAttribute (attr);
			if (aA == null)
				ret += "Attribute <span class='"+SBMLDiffReporter.CLASS_ATTRIBUTE+"'>" + attr + "</span> was inserted: <span class='inserted'>"+bA+"</span><br/>";
			else if (bA == null)
				ret += "Attribute <span class='"+SBMLDiffReporter.CLASS_ATTRIBUTE+"'>" + attr + "</span> was deleted: <span class='deleted'>"+aA+"</span><br/>";
			else if (!aA.equals (bA))
				ret += "Attribute <span class='"+SBMLDiffReporter.CLASS_ATTRIBUTE+"'>" + attr + "</span> has changed: <span class='deleted'>"+aA+"</span> &rarr; <span class='inserted'>"+bA+"</span><br/>";
		}
		return ret;
  }
  
  public static String genTableIdCol (DocumentNode a, DocumentNode b)
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
  }
  
  public static String transformMathML (DocumentNode doc) throws TransformerException
  {

		TransformerFactory tFactory = 
		TransformerFactory.newInstance();
		
		// 2. Use the TransformerFactory to process the stylesheet Source and
		//    generate a Transformer.
		InputStream input = Tools.class.getResourceAsStream("/res/mmlctop2_0.xsl");
		//Transformer transformer = tFactory.newTransformer (new javax.xml.transform.stream.StreamSource("/tmp/mmlctop2_0.xsl"));
		Transformer transformer = tFactory.newTransformer (new javax.xml.transform.stream.StreamSource(input));
		
		// 3. Use the Transformer to transform an XML Source and send the
		//    output to a Result object.
		

    SimpleOutputStream out = new SimpleOutputStream ();
		
		//transformer.transform (new javax.xml.transform.stream.StreamSource("/tmp/math2.xml")/*new DOMSource(doc)*/, new javax.xml.transform.stream.StreamResult(out));
    String math = printSubDoc (doc);
    // xslt cannot namespace
    math = math.replaceAll ("\\S+:\\S+\\s*=\\s*\"[^\"]*\"", "");
    //System.out.println ("pre " + math);
		transformer.transform (new javax.xml.transform.stream.StreamSource(new ByteArrayInputStream(math.getBytes()))/*new DOMSource(doc)*/, new javax.xml.transform.stream.StreamResult(out));
		//System.out.println ("post " + out.toString ());
		return out.toString ();
  }
  
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
