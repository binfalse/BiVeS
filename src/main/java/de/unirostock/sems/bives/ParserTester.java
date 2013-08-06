package de.unirostock.sems.bives;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import de.unirostock.sems.bives.tools.DocumentClassifier;



/*

filter:

file=


/bin/grep -v clone-bmdb $file | /bin/grep -v "\[BivesSBMLParseException -> sbml document doesn't start with sbml tag.\].*\[BivesCellMLParseException -> cellml document does not define a model\]" > $file.filter1

cat $file.filter1 | sed 's/\[BivesSBMLParseException -> sbml document doesn.t start with sbml tag.\]//' > $file.filter2

# cat $file.filter2 | sed 's/\/home.*cellmlclones\///' > $file.filter3
cat $file.filter2 | sed 's/\/home[^\[]*cellmlclones\///' > $file.filter3

/bin/grep -v "encapsulation failed: child wants to have to parents" $file.filter3 > $file.filter4

/bin/grep -v "No such file or directory" $file.filter4 > $file.filter5




 */

public class ParserTester
{
	private class FileEvaluation
	{
		public FileEvaluation (long time, File file, int type, String version)
		{
			this.time = time;
			this.type = type;
			this.file = file;
			this.version = version;
			this.fileSize = file.length ();
		}
		public long time;
		public long fileSize;
		public int type;
		public File file;
		public String version;
		Vector<Exception> ex; 
		
		public String println ()
		{
			return time + "\t" + fileSize + "\t" + type + "\t\"" + file.getAbsolutePath () + "\"";
		}
		
		public String errln ()
		{
			String fileName = file.getName ();
			String path = file.getParent ().replaceAll (System.getProperty( "user.home" ) + "/education/stuff/biomodels/cellmlclones/", "").replace (System.getProperty( "user.home" ) + "/education/stuff/biomodels/clone-bmdb/models/models/", "");
			String ext = "unknown";
			int i = fileName.lastIndexOf('.');
			if (i > 0)
				ext = fileName.substring(i+1);
			
			String ret = "";
			
			if (ex != null)
				for (Exception e: ex)
				{
					String cn = e.getClass ().getName ();
					i = cn.lastIndexOf ('.');
					if (i > 0)
						cn = cn.substring (i + 1);
					ret += " [" + cn + " -> " + e.getMessage () + "]";
				}
			if (version != null)
				return ext + "\t " + ret + "\t " + path + " " + fileName + " @" + version;
			
			return ext + "\t " + ret + "\t " + path + " " + fileName;
		}
	}
	
	private static String [] include = {".xml", ".cellml", ".sbml"};
	private static String [] exclude = {".session.xml"};
	
	//private int fileCounter;
	private Vector<FileEvaluation> errors;
	private Vector<FileEvaluation> evals;
	
	private DocumentClassifier classifier;
	
	private ParserTester () throws ParserConfigurationException
	{
		classifier = new DocumentClassifier ();
		evals = new Vector<FileEvaluation> ();
		errors = new Vector<FileEvaluation> ();
	}
	
	private void testBiomodelsDB (File file)
	{
		if (file.isDirectory ())
		{
			System.out.println ("\t-> " + file.getAbsolutePath ());
			File [] files = file.listFiles ();
			for (File f : files)
				testBiomodelsDB (f);
		}
		else
		{
			//fileCounter++;
			long time = System.currentTimeMillis ();
			int type = classifier.classify (file);
			FileEvaluation ev = new FileEvaluation (System.currentTimeMillis () - time, file, type, null);
			
			
			if (type != (DocumentClassifier.SBML | DocumentClassifier.XML))
			{
				//System.out.println ("unexpected: " + file);
				//Vector<Exception> ex = classifier.getExceptions ();
				/*for (Exception e : ex)
					System.out.println ("  " + e.getMessage ());*/
				errors.add (ev);
				ev.ex = classifier.getExceptions ();
			}
			else
				evals.add (ev);
		}
		
	}
	
	private void testCellMLRepos (File parent) throws IOException
	{
		//int tmp = 1;
		File [] repos = parent.listFiles ();
		for (File f : repos)
			if (f.isDirectory ())
			{
				testCellMLRepo (f);
				//if (tmp++ == 3)
					//return ;
			}
	}
	
	private void testCellMLRepo (File repo) throws IOException
	{
		System.out.println ("\t-> " + repo.getAbsolutePath ());
		Vector<String> revisions = new Vector<String> ();
		ProcessBuilder pb = new ProcessBuilder("hg", "log", "--template", "{rev}\n");
		pb.directory(repo);
		Process p = pb.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = br.readLine()) != null)
		{
		  //System.out.println("hg log output: " + line);
		  revisions.add (line);
		}
		
		for (String rev : revisions)
		{
		  //System.out.println("getting: " + rev);
			pb = new ProcessBuilder("hg", "checkout", rev);
			pb.directory(repo);
			p = pb.start();
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = br.readLine()) != null)
			{
			  //System.out.println("hg co output: " + line);
			}
			
			testCellMLRevision (repo, rev);
		}
	}
	
	private void testCellMLRevision (File file, String revision)
	{
		if (file.isDirectory ())
		{
			if (file.getAbsolutePath ().contains (".hg"))
				return;
			
			//System.out.println ("\t-> " + file.getAbsolutePath ());
			File [] files = file.listFiles ();
			for (File f : files)
				//System.out.println ("\t---> " + f.getAbsolutePath ());
				testCellMLRevision (f, revision);
		}
		else
		{
			String fname = file.getName ().toLowerCase ();
			
			// just read some type of files
			for (String s : exclude)
				if (fname.contains (s))
					return;
			boolean cont = false;
			for (String s : include)
				if (fname.contains (s))
				{
					cont = true;
					break;
				}
			if (!cont)
				return;
			
			//System.out.println ("\t---> " + fname);
			
			long time = System.currentTimeMillis ();
			int type = classifier.classify (file);
			FileEvaluation ev = new FileEvaluation (System.currentTimeMillis () - time, file, type, revision);
			
			
			if ((type & DocumentClassifier.XML) != 0)
			{
				// is an xml
				
				if (type != (DocumentClassifier.CELLML | DocumentClassifier.XML) && type != (DocumentClassifier.SBML | DocumentClassifier.XML))
				{
					errors.add (ev);
					ev.ex = classifier.getExceptions ();
				}
				else
					evals.add (ev);
			}
			
			/*if ((type & DocumentClassifier.XML) != 0 && type != (DocumentClassifier.CELLML | DocumentClassifier.XML) && type != (DocumentClassifier.SBML | DocumentClassifier.XML))
			{
				errors.add (ev);
				ev.ex = classifier.getExceptions ();
			}
			else
				evals.add (ev);*/

			/*if (ev.ex != null)
				for (Exception e: ev.ex)
					e.printStackTrace ();*/
			
		}
	}
	
	
	
	public static void main (String [] args) throws ParserConfigurationException, IOException
	{
		String date = new SimpleDateFormat("MM-dd-HH-mm-ss").format(new Date());
		File log = File.createTempFile ("BivesParserTester-" + date + "-", ".log");
		File errlog = File.createTempFile ("BivesParserTester-" + date + "-", ".err");
		
		
		ParserTester pt = new ParserTester ();
		
		/*
		 * BiomodelsDB
		 */
		
		long time = System.currentTimeMillis ();
		
		pt.testBiomodelsDB (new File (System.getProperty( "user.home" ) + "/education/stuff/biomodels/clone-bmdb/models/models"));
		
		time = System.currentTimeMillis () - time;
		int seconds = (int)  (( time / 1000) % 60);
		int minutes = (int) ((( time / 1000) / 60) % 60);
		int hours   = (int) ((( time / 1000) / 60) / 60);
		
		System.out.println ("tested " + pt.evals.size () + " files in " + hours + ":" + minutes + ":" + seconds);
		
		if (pt.evals.size () != 9230)
			System.err.println ("expected to test 9230 files, but just tested " + pt.evals.size ());
		
		if (pt.errors.size () > 0)
			System.err.println ("found " + pt.errors.size () + " errors");
		
		int perr = pt.errors.size ();
		int pnum = pt.evals.size ();

		/*
		 * CellML ModelServer
		 * TODO: also try to flatten the model and reread it!
		 */
		

		time = System.currentTimeMillis ();
		
		pt.testCellMLRepos (new File (System.getProperty( "user.home" ) + "/education/stuff/biomodels/cellmlclones"));
		
		time = System.currentTimeMillis () - time;
		seconds = (int)  (( time / 1000) % 60);
		minutes = (int) ((( time / 1000) / 60) % 60);
		hours   = (int) ((( time / 1000) / 60) / 60);
		
		System.out.println ("tested " + (pt.evals.size () - pnum) + " files in " + hours + ":" + minutes + ":" + seconds);
		
		if (pt.evals.size () - pnum != 36085)
			System.err.println ("expected to test 9230 files, but tested " + (pt.evals.size () - pnum));
		
		if (pt.errors.size () -  perr > 0)
			System.err.println ("found " + (pt.errors.size () - perr) + " errors");
		
		

		/*
		 * write stats
		 */
		
		
		BufferedWriter bw = new BufferedWriter (new FileWriter (log));
		for (FileEvaluation fe : pt.evals)
		{
			bw.write (fe.println ());
			bw.newLine ();
		}
		bw.close ();
		
		bw = new BufferedWriter (new FileWriter (errlog));
		for (FileEvaluation fe : pt.errors)
		{
			bw.write (fe.errln ());
			bw.newLine ();
		}
		bw.close ();

		System.out.println ("exported stats to: " + log.getAbsolutePath ());
		System.out.println ("exported errors to: " + errlog.getAbsolutePath ());
		
		
		
	}
}
