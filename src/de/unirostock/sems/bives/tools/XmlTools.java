/**
 * 
 */
package de.unirostock.sems.bives.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import de.unirostock.sems.bives.tools.Tools.SimpleOutputStream;


/**
 * @author Martin Scharm
 *
 */
public class XmlTools
{
	
	public static String prettyPrintDocument(Document doc) throws IOException, TransformerException
	{
		return XmlTools.prettyPrintDocument (doc, new SimpleOutputStream ()).toString ();
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

	public static OutputStream prettyPrintDocument (Document doc, OutputStream out) throws IOException, TransformerException
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
	
}
