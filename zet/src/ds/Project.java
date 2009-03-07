/*
 * Project.java
 * Created on 26. November 2007, 21:32
 */
package ds;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;
import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.AssignmentType;
import ds.z.BuildingPlan;
import ds.z.EvacuationPlan;
import ds.z.event.ChangeEvent;
import ds.z.event.ChangeListener;
import ds.z.event.ChangeReporter;
import ds.z.event.EvacuationAreaCreatedEvent;
import io.z.ProjectConverter;
import io.z.XMLConverter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import localization.Localization;

/**
 * The central Projekt class for the Z format. All information about
 * the evacuation scenario is linked together in this class.
 */
@XStreamAlias ("project")
@XMLConverter (ProjectConverter.class)
public class Project implements Serializable, ChangeListener, ChangeReporter {

	private static XStream xml_convert;

	public static XStream getXStream () {
		return xml_convert;
	}

	static {
		xml_convert = new XStream ();
		xml_convert.setMode (XStream.ID_REFERENCES);

		//Configure aliases for external classes (Java API)
		xml_convert.useAttributeFor (java.awt.Point.class, "x");
		xml_convert.useAttributeFor (java.awt.Point.class, "y");

		//Configure aliases for all ds.* classes
		//For this purpose the current location of the bytecode is searched for
		//all class names. These are loaded and their annotaions are examined.

		// Load "ds" because the JARClassLoader won't load "" (the FileClassLoader does)
		URL pack = Project.class.getClassLoader ().getResource ("ds");
		String url = pack.toExternalForm ();
		// The artificially delete the "ds" again
		url = url.substring (0, url.lastIndexOf ('/') + 1);

		// Scan JARs iteratively
		if (url.startsWith ("jar:")) {
			try {
				String jar_filename = url.substring (url.indexOf (':') + 1,
						url.lastIndexOf ('!'));
				JarFile jar = new JarFile (new File (new URI (jar_filename)));

				Enumeration enu = jar.entries ();
				while (enu.hasMoreElements ()) {
					String entry = ((ZipEntry)enu.nextElement ()).getName ();
					if (entry.endsWith (".class")) {
						try {
							String classname = entry.replace ('/', '.').substring (
									0, entry.lastIndexOf ('.'));
							Class fromFile = Project.class.getClassLoader ().loadClass (classname);
							processClassObject (fromFile);
						} catch (Exception ex) {
							System.out.println (" Problem while scanning classes: Class file" + entry +
									"\n" + ex.getLocalizedMessage ());
						}
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException (Localization.getInstance (
				).getString ("ds.InitProjectException" +ex.getLocalizedMessage ()));
			}
		} else if (url.startsWith ("file:")) {
			try {
				File dir = new File (new URI (url));
				assert (dir.isDirectory ());
				scanPackage (dir, "");
			} catch (URISyntaxException ex) {
				throw new RuntimeException (Localization.getInstance (
				).getString ("ds.InitProjectException"));
			}
		} else {
			throw new IllegalStateException (Localization.getInstance (
			).getString ("ds.DeterminingJarException"));
		}
	}

	/** Crawls the given directory for class files and processes them. */
	private static void scanPackage (File dir, String pack) {
		for (File f : dir.listFiles ()) {
			if (f.isDirectory ()) {
				scanPackage (f, pack + f.getName () + ".");
			} else if (f.getName ().endsWith (".class")) {
				try {
					String classname = f.getName ().substring (0, f.getName ().lastIndexOf ('.'));
					Class fromFile = Project.class.getClassLoader ().loadClass (pack + classname);
					processClassObject (fromFile);
				} catch (Exception ex) {
					System.out.println ("Problem while scanning classes: Class file" + f.getName () +
							"\n" + ex.getLocalizedMessage ());
				}
			}
		}
	}

	/** Used to initalize the XStream related stuff concerning the given type. */
	private static void processClassObject (Class type) throws Exception {
		// Inhibit alias processing on the converter classes themselves
		// as this will lead to unwanted behaviour in XStream
		if (!com.thoughtworks.xstream.converters.Converter.class.isAssignableFrom (type)) {
			Annotations.configureAliases (xml_convert, type);

			if (type.isAnnotationPresent (XMLConverter.class)) {
				xml_convert.registerConverter ((Converter)((XMLConverter)type.getAnnotation (
						XMLConverter.class)).value ().getConstructor (
						Mapper.class, ReflectionProvider.class).newInstance (
						xml_convert.getMapper (), xml_convert.getReflectionProvider ()));
			}
		}
	}
	@XStreamOmitField ()
	private transient ArrayList<ChangeListener> changeListeners;
	@XStreamOmitField ()
	private transient File projectFile;
	private BuildingPlan plan;
	private EvacuationPlan currentEvacuationPlan;
	private Assignment currentAssignment;
	private ArrayList<Assignment> assignments;
	private ArrayList<EvacuationPlan> evacuationPlans;

	public Project () {
		changeListeners = new ArrayList<ChangeListener> ();

		plan = new BuildingPlan ();
		plan.addChangeListener (this);
		assignments = new ArrayList<Assignment> (10);
		evacuationPlans = new ArrayList<EvacuationPlan> (10);
	}

	/** {@inheritDoc}
	 * @param e 
	 */
	public void throwChangeEvent (ChangeEvent e) {
		// Workaround: Notify only the listeners who are registered at the time when this method starts
		// Some problems occur with the GUI: if a new assignment is created,
		// the listeners of this project are changed in order to get new assignment lists etc.
		// this invalidates the lists.
		ChangeListener[] listenerCopy = changeListeners.toArray ( new ChangeListener[changeListeners.size ()] );

		for (ChangeListener c : listenerCopy) {
			c.stateChanged (e);
		}
	}

	/** {@inheritDoc} */
	public void addChangeListener (ChangeListener c) {
		if (!changeListeners.contains (c)) {
			changeListeners.add (c);
		}
	}

	/** {@inheritDoc} */
	public void removeChangeListener (ChangeListener c) {
		changeListeners.remove (c);
	}

	/** See {@link ds.z.event.ChangeListener#stateChanged (ChangeEvent)} for details.
	 * @param e 
	 */
	public void stateChanged (ChangeEvent e) {
		if( e instanceof EvacuationAreaCreatedEvent ) {
			for( Assignment a : this.getAssignments() )
				for( AssignmentType t : a.getAssignmentTypes() )
					for( AssignmentArea aa : t.getAssignmentAreas() )
						if( aa.getExitArea() != null && aa.getExitArea().equals( e.getSource() ) ) {
							aa.setExitArea( null );			
						}
		}
		// Simply forward the events
		throwChangeEvent (e);
	}
	
	/**
	 * Adds a new EvacuationPlan to the Project. If the Project hasn't had a
	 * "CurrentEvacuationPlan" previously, then the new EvacuationPlan is made
	 *  the current one.
	 *
	 * @param val the evacuation plan
	 * @exception IllegalArgumentException - Is thrown when the EvacuationPlan "val" is already
	 * registered at the Project. */
	public void addEvacuationPlan (EvacuationPlan val) throws IllegalArgumentException {
		if (evacuationPlans.contains (val)) {
			throw new IllegalArgumentException (Localization.getInstance (
			).getString ("ds.PlanAlreadyExistsException"));
		} else {
			if (currentEvacuationPlan == null) {
				currentEvacuationPlan = val;
			}

			evacuationPlans.add (val);
			throwChangeEvent (new ChangeEvent (this));
		}
	}

	/** Deletes an EvacuationPlan from the Project. If you deleted the "current"
	 * EvacuationPlan, the first EvacuationPlan in the List of the remaining ones will
	 * be selected to become "current", if the List is empty, the "currentEvacuationPlan"
	 * will be set to null.
	 *
	 * @param val 
	 * @exception IllegalArgumentException - Is thrown when the EvacuationPlan "val" is not
	 * registered at the Project. */
	public void deleteEvacuationPlan (EvacuationPlan val) throws IllegalArgumentException {
		if (!evacuationPlans.remove (val)) {
			throw new IllegalArgumentException (Localization.getInstance (
			).getString ("ds.PlanNotRegisteredException"));
		} else {
			val.delete ();

			if (val == currentEvacuationPlan) {
				if (evacuationPlans.size () > 0) {
					currentEvacuationPlan = evacuationPlans.get (0);
				} else {
					currentEvacuationPlan = null;
				}
			}

			throwChangeEvent (new ChangeEvent (this));
		}
	}

	public List<EvacuationPlan> getEvacuationPlans () {
		return Collections.unmodifiableList (evacuationPlans);
	}

	public EvacuationPlan getCurrentEvacuationPlan () {
		return currentEvacuationPlan;
	}

	/** Sets the EvacuationPlan that is to be used.
	 * @param val 
	 * @exception IllegalArgumentException Is thrown when val is not in the
	 * List of EvacuationPlans that is maintained by the Project.
	 */
	public void setCurrentEvacuationPlan (EvacuationPlan val) throws IllegalArgumentException {
		if (evacuationPlans.contains (val)) {
			this.currentEvacuationPlan = val;
			throwChangeEvent (new ChangeEvent (this));
		} else {
			throw new IllegalArgumentException (Localization.getInstance (
			).getString ("ds.PlanNotInProjectException"));
		}
	}

	/** Adds a new Assignment to the Project. If the Project hasn't had a "CurrentAssignment"
	 * previously, then the new Assignment is made the current one.
	 *
	 * @param val 
	 * @exception IllegalArgumentException - Is thrown if the Assignment "val" is already
	 * registered at the Project.
	 */
	public void addAssignment (Assignment val) throws IllegalArgumentException {
		if (assignments.contains (val)) {
			throw new IllegalArgumentException (Localization.getInstance (
			).getString ("ds.AssignmentAlreadyRegisteredException"));
		} else {
			if (currentAssignment == null) {
				currentAssignment = val;
			}

			assignments.add (val);
			val.addChangeListener (this);
			throwChangeEvent (new ChangeEvent (this));
		}
	}

	/** Deletes an Assignment from the Project. If you deleted the "current"
	 * Assignment, the first Assignment in the List of the remaining ones will
	 * be selected to become "current", if the List is empty, the "currentAssignment"
	 * will be set to null.
	 *
	 * @param val 
	 * @exception IllegalArgumentException - Is thrown when the Assignment "val" is not
	 * registered at the Project. */
	public void deleteAssignment (Assignment val) throws IllegalArgumentException {
		if (!assignments.remove (val)) {
			throw new IllegalArgumentException (Localization.getInstance (
			).getString ("ds.AssignmentNotRegisteredException"));
		} else {
			val.removeChangeListener (this);
			val.delete ();

			if (val == currentAssignment) {
				if (assignments.size () > 0) {
					currentAssignment = assignments.get (0);
				} else {
					currentAssignment = null;
				}
			}

			throwChangeEvent (new ChangeEvent (this));
		}
	}

	public List<Assignment> getAssignments () {
		return Collections.unmodifiableList (assignments);
	}

	public Assignment getCurrentAssignment () {
		if (currentAssignment == null) {
			if (assignments.size () > 0) {
				currentAssignment = assignments.get (0);
			}
		}
		return currentAssignment;
	}

	/** Sets the Assignment that is to be used. Gives notice to the previous and the new currentAssignment
	 * that it is not any more respectively now the current assignment.
	 * @param val 
	 * @exception IllegalArgumentException - Is thrown when val is not in the
	 * List of Assignments that is maintained by the Project.
	 */
	public void setCurrentAssignment (Assignment val) throws IllegalArgumentException {
		if (assignments.contains (val)) {
			this.currentAssignment.setInactive ();
			this.currentAssignment = val;
			this.currentAssignment.setActive ();
			throwChangeEvent (new ChangeEvent (this));
		} else {
			throw new IllegalArgumentException (Localization.getInstance (
			).getString ("ds.AssignmentNotInProjectException"));
		}
	}
			
	/** @exception IOException - Is thrown when the I/O-Operation fails. */
	public void save () throws IOException {
		save (projectFile);
	}

	/** @param file The location where the Project shall be stored.
	 * @exception IOException - Is thrown when the I/O-Operation fails. */
	public void save (File file) throws IOException {
		PrintWriter output = new PrintWriter (file);

		// Set project file before saving, to get it into the saved file
		projectFile = file;

		xml_convert.toXML (this, output);
	}

	/**
	 * @param projectFile 
	 * @exception IOException - Is thrown when the I/O-Operation fails.
	 * @return The Project that was stored in the denoted file.
	 */
	public static Project load (File projectFile) throws IOException {
		FileReader input = new FileReader (projectFile);
		Project p = (Project)xml_convert.fromXML (input);
		return p;
	}

	public File getProjectFile () {
		return projectFile;
	}

	public void setProjectFile (File val) {
		this.projectFile = val;
	}

	public BuildingPlan getPlan () {
		return plan;
	}

	@Override
	public boolean equals (Object o) {
		if (o instanceof Project) {
			Project p = (Project)o;

			//This is redundant and only for speeding up the method
			if (assignments.size () != p.assignments.size () ||
					evacuationPlans.size () != p.evacuationPlans.size ()) {
				return false;
			}

			//Here comes the real comparison - Iteratively compare all subobjects
			//The order in the lists subobjects_me/p MUST be the same
			LinkedList subobjects_me = new LinkedList ();
			subobjects_me.add (currentAssignment);
			subobjects_me.add (currentEvacuationPlan);
			subobjects_me.add (plan);
			subobjects_me.add (projectFile);
			subobjects_me.add (assignments);
			subobjects_me.add (evacuationPlans);

			LinkedList subobjects_p = new LinkedList ();
			subobjects_p.add (p.currentAssignment);
			subobjects_p.add (p.currentEvacuationPlan);
			subobjects_p.add (p.plan);
			subobjects_p.add (p.projectFile);
			subobjects_p.add (p.assignments);
			subobjects_p.add (p.evacuationPlans);

			while (subobjects_me.size () > 0 && subobjects_p.size () > 0) {
				Object subobj_me = subobjects_me.poll ();
				Object subobj_p = subobjects_p.poll ();

				//This is not an implementation error - Lists also have a proper equals method
				boolean subobj_equal = (subobj_me == null) ? subobj_p == null : subobj_me.equals (subobj_p);
				if (!subobj_equal) {
					return false;
				}
			}
			//Return whether no elements are left on one side (that would imply inequality)
			return (subobjects_me.size () == 0 && subobjects_p.size () == 0);
		} else {
			return false;
		}
	}
}
