/**
 * 
 */
package de.unirostock.sems.bives.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.exception.BivesDocumentParseException;

/**
 * TODO: not implemented yet
 * 
 * @author Martin Scharm
 *
 */
public class CellMLDiff extends Diff
{
	private CellMLDocument doc1, doc2;

	public CellMLDiff(File a, File b) throws ParserConfigurationException,
			BivesDocumentParseException, FileNotFoundException, SAXException,
			IOException {
		super(a, b);
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#mapTrees()
	 */
	@Override
	public boolean mapTrees() throws Exception {
		return false;
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getGraphML()
	 */
	@Override
	public String getGraphML() throws ParserConfigurationException {
		return "not implemented yet";
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.api.Diff#getReport()
	 */
	@Override
	public String getReport() {
		return "not implemented yet";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
