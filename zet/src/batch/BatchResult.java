/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package batch;

import com.thoughtworks.xstream.XStream;
import ds.Project;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** Wrapper class for the results of a batch run.
 *
 * @author Timon
 */
public class BatchResult {
	private boolean storeEntriesInFiles = false;
	/** Stores either the concrete results (!storeEntriesInFiles) of is just empty 
	 * (storeEntriesInFiles) except for the currently loaded index.
	 */
	private ArrayList<BatchResultEntry> results = new ArrayList<BatchResultEntry> ();
	/** The names of the results are stored separately to be able to extract a
	 * list of Results wihtout loading each result separately when using 'storeEntriesInFiles'.
	 */
	private ArrayList<String> entryNames = new ArrayList<String> ();
	/** Whether a given entry stores a ca result.
	 */
	private ArrayList<Boolean> entryCA = new ArrayList<Boolean> ();
	/** Whether a given entry stores a graph result.
	 */
	private ArrayList<Boolean> entryGraph = new ArrayList<Boolean> ();
	/** The file names for storing the entries.
	 */
	private ArrayList<File> tempFiles = new ArrayList<File> ();
	/** The index of the entry that is currently loaded. 
	 */
	private int loadedIndex = -1;
	
	/* @param storeEntriesInFiles Whether this batch should save it's result 
	 * entries in temporary files. */
	public BatchResult (boolean storeEntriesInFiles) {
		this.storeEntriesInFiles = storeEntriesInFiles;
	}
	/** @throws IOException When saving a temporary file fails in "storeEntriesInFiles"-Mode.
	 * 
	 * @param result The new entry that should be added. In "storeEntriesInFiles"-Mode there will
	 * be at maximum this and the most recently loaded result set in memory at the same time.
	 */
	public void addResult (BatchResultEntry result) throws IOException {
		entryNames.add (result.getName ());
		entryCA.add (result.getCa () != null);
		entryGraph.add (result.getNetworkFlowModel () != null);
			
		if (storeEntriesInFiles) {
			//Delte last value
			clearLoadedIndex ();
			
			// Add the new one
			results.add (result);
			loadedIndex = results.size () - 1;
			
			// Save the new one
			File newFile = File.createTempFile (Integer.toString (result.hashCode ()), ".zettmp");
			newFile.deleteOnExit ();
			tempFiles.add (newFile);
			System.out.print ("Saving to " + newFile.getAbsolutePath () + " ... ");
			result.save (newFile);
			System.out.println ("Completed!");
		} else {
			results.add (result);
		}
	}
	/**
	 * @throws IOException When loading a temporary file fails in "storeEntriesInFiles"-Mode.
	 */
	public BatchResultEntry getResult (int index) throws IOException {
		if (storeEntriesInFiles && loadedIndex != index) {
			// Delete old value
			clearLoadedIndex ();

			// Load the new one
			System.out.print ("Loading " + tempFiles.get (index).getAbsolutePath () + " ... ");
			results.set (index, BatchResultEntry.load (tempFiles.get (index)));
			System.out.println ("Completed!");
			loadedIndex = index;
		}
		return results.get (index);
	}

	/** Discard the currently loaded result entry (if any is loaded). */
	private void clearLoadedIndex () {
		if (loadedIndex >= 0) {
			results.set (loadedIndex, null);
			System.gc ();
			loadedIndex = -1;
		}
	}
	
	/** @return An unmodifiable list of the current entry names. */
	public List<String> getEntryNames () {
		return Collections.unmodifiableList (entryNames);
	}
	
	public boolean entryHasCa (int index) {
		return entryCA.get (index);
	}
	public boolean entryHasGraph (int index) {
		return entryGraph.get (index);
	}
	
	/* Saves BatchResultEntries in GZIPped XML format.<br>
	 * In case that the BatchResult operates in 'storeEntriesInFiles' mode
	 * all temporary files must be moved to the folder that contains
	 * 'selectedFile'.
	 */
	public void save (File selectedFile) throws IOException {
		// Move all the temporary files to the saving place of our new file.
		if (storeEntriesInFiles) {
			boolean moved = false;
			for (int i = 0; i < tempFiles.size (); i++) {
				File newFile = new File (selectedFile.getAbsolutePath () + "_" + Integer.toString (i) + ".zettmp");
				
				moved = tempFiles.get (i).renameTo (newFile);
				
				// Moving files fails on windows, so we copy the old file
				if (!moved) {
					BufferedReader read = new BufferedReader (new FileReader (tempFiles.get (i)));
					PrintWriter write = new PrintWriter (newFile);
					String s;
					
					while ((s = read.readLine ()) != null) {
						write.print (s);
					}
					read.close ();
					write.flush ();
					write.close ();
					tempFiles.get (i).delete ();
				}
				
				// Remember where we stored the result entries.
				// We store absolute adresses here, but when loading the file, we only use the file name
				tempFiles.set (i, newFile);
			}
		}
		
		// Save the XML data in GZIP format
		GZIPOutputStream output = new GZIPOutputStream (
				new BufferedOutputStream (new FileOutputStream (selectedFile)));
		XStream xml_convert = new XStream ();
		xml_convert.setMode (XStream.ID_REFERENCES);
		xml_convert.toXML( this, output );
		output.flush ();
		output.close ();
	}
	
	/** Deletes all temporary files. Should be called before object destruction. */
	public void dispose () {
		if (storeEntriesInFiles) {
			for (File f : tempFiles) {
				f.delete ();
			}
		}
	}
	
	/* Loads BatchResults from GZIPped XML format. */
	public static BatchResult load (File selectedFile) throws IOException {
		GZIPInputStream input = new GZIPInputStream (new BufferedInputStream (
				new FileInputStream (selectedFile)));
		XStream xml_convert = new XStream ();
		xml_convert.setMode (XStream.ID_REFERENCES);
		BatchResult res = (BatchResult)xml_convert.fromXML( input );
		
		for (int i = 0; i < res.tempFiles.size (); i++) {
			String name = res.tempFiles.get (i).getName ();
			File actualFile = new File (selectedFile.getParentFile (), name);
			
			if (!actualFile.exists ()) {
				throw new FileNotFoundException ("Could not find the result entry file '" + 
						actualFile.getAbsolutePath () + "'");
			} else {
				res.tempFiles.set (i, actualFile);
			}
		} 
		
		input.close ();
		return res;
	}

	/** Whether this batch saves it's result entries in temporary files. */
	public boolean doesStoreEntriesInFiles () {
		return storeEntriesInFiles;
	}
}