/**
 * 
 */
package de.unirostock.sems.bives.tools;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.ds.xml.DocumentNode;
import de.unirostock.sems.bives.ds.xml.TreeDocument;
import de.unirostock.sems.bives.tools.Tools.SimpleOutputStream;


/**
 * @author Martin Scharm
 *
 */
public class TreeTools
{
	
	public static Document getDoc (TreeDocument td)
	{
		return getSubDoc (td.getRoot ());
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
			return XmlTools.printDocument (d);
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
			return XmlTools.prettyPrintDocument (d, new Tools.SimpleOutputStream ()).toString ();
		}
		catch (Exception e)
		{
			LOGGER.error ("error creating subdoc", e);
			return "error creating doc: " + e.getMessage ();
		}
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
	
}
