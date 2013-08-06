/**
 * 
 */
package de.unirostock.sems.bives.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import de.binfalse.bflog.LOGGER;


/**
 * @author martin
 *
 */
public class FileRetriever
{
	public static boolean FIND_LOCAL = true;
	public static boolean FIND_REMOTE = true;
	
	public static URI getUri (String href, URI base) throws URISyntaxException, IOException
	{
		URI theUri = new URI (href);
		
		// is full
		if (theUri.isAbsolute ())
			return theUri;
		
		
		// href is absolut in fs
		if (href.startsWith ("/"))
		{
			return new URI ("file://" + href);
		}

		// is realtive
		if (base == null || !base.isAbsolute ())
			throw new IOException ("don't know where this relative path points to: "+href+" (no base provided, or base also relative).");
		
		
		return base.resolve (theUri);
	}
	
	
	private static boolean isLocal (String uri)
	{
		if (uri.startsWith ("file:"))
			return true;
		if (uri.matches ("^\\S+://.*"))
			return false;
		return true;
	}
	
	private static void copy (URI from, File to) throws IOException
	{
		if (!FIND_LOCAL)
			throw new IOException ("local resolving disabled");
		
		
		BufferedWriter bw = new BufferedWriter (new FileWriter (to));
		BufferedReader br = new BufferedReader (new FileReader (
			from.toURL ().getFile ()));
		while (br.ready ())
		{
			bw.write (br.readLine ());
			bw.newLine ();
		}
		bw.close ();
		// System.out.println(br.readLine());
		br.close ();
	}
	
	private static void download (URI from, File to) throws IOException
	{
		if (!FIND_REMOTE)
			throw new IOException ("remote resolving disabled");
		
		URL website = from.toURL ();
		ReadableByteChannel rbc = Channels.newChannel (website.openStream ());
		FileOutputStream fos = new FileOutputStream (to);
		fos.getChannel ().transferFrom (rbc, 0, 1 << 24);
		fos.close ();
	}
	
	
	/**
	 * Retrieves a file from an URI. 
	 *
	 * @param file the URI to the file
	 * @param base the URI that will be used if base
	 * @param dest the destination to write to
	 * @return 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException thrown if file or base have a strange format 
	 */
	public static void getFile (URI file, File dest) throws IOException, URISyntaxException
	{
		if (!dest.canWrite ())
			throw new IOException ("cannot write to file: " + dest.getAbsolutePath ());
		
		LOGGER.info ("trying to retrieve file from: " + file + " to: " + dest);
		
			// file: -> copy
		if (file.getScheme ().toLowerCase ().startsWith ("file:"))
			copy (file, dest);
		// otherwise download
		else
			download (file, dest); 
		
		
		/*URI theFile = new URI (file);
		
		// is full
		if (theFile.isAbsolute ())
		{
			if (file.startsWith ("file:"))
				copy (theFile, dest);
			// otherwise download
			else
				download (theFile, dest); 
		}
		else if (base == null)
		{
			if (!FIND_LOCAL)
				throw new IOException ("local resolving disabled");
			if (!file.startsWith ("/"))
				throw new IOException ("don't know where this relative path points to: "+file+" (no base provided).");
			theFile = new URI ("file://" + theFile);
			copy (theFile, dest);
		}
		// is realtive
		else if (isLocal (base.toString ()))
		{
			// copy
			if (file.startsWith ("/"))
			{
				theFile = new URI ("file://" + theFile);
				copy (theFile, dest);
			}
			else
			{
				theFile = base.resolve (theFile);
				copy (theFile, dest);
			}
		}
		else
		{
			//System.out.println ("else");
			// download
			theFile = base.resolve (theFile);
			download (theFile, dest);
		}
		
		return theFile;*/
	}
}
