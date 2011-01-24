/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * Project.java
 * Created on 26. November 2007, 21:32
 */

package ds;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import ds.z.ZLocalization;
import ds.z.Assignment;
import ds.z.BuildingPlan;
import ds.z.EvacuationPlan;
import ds.z.ZFormatObject;
import io.z.ProjectConverter;
import io.z.XMLConverter;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The central project class for the Z format. All information about
 * the evacuation scenario is linked together in this class.
 */
@XStreamAlias( "project" )
@XMLConverter( ProjectConverter.class )
public class Project implements Serializable, ZFormatObject {
	@XStreamOmitField()
	private transient File projectFile;
	/** The name of the project. */
	private String name = "New Project";
	/** The building plan belonging to the project. */
	private BuildingPlan plan;
	/** The currently active evacuation plan for the building. */
	private EvacuationPlan currentEvacuationPlan;
	/** The currently active assignment. */
	private Assignment currentAssignment;
	/** The list of possible assignments for the project. */
	private ArrayList<Assignment> assignments;
	/** The list of possible evacuation plans for the project. */
	private ArrayList<EvacuationPlan> evacuationPlans;
	/** Additionally stored visualization parameters for the project. */
	private VisualProperties vp;

	public Project() {
		plan = new BuildingPlan();
		assignments = new ArrayList<Assignment>( 10 );
		evacuationPlans = new ArrayList<EvacuationPlan>( 10 );
		vp = new VisualProperties();
	}

	/**
	 * Adds a new EvacuationPlan to the Project. If the Project hasn't had a
	 * {@code CurrentEvacuationPlan} previously, then the new EvacuationPlan is
	 * made the current one.
	 * @param val the evacuation plan
	 * @throws IllegalArgumentException if the EvacuationPlan {@code val} is already registered at the Project.
	 */
	public void addEvacuationPlan( EvacuationPlan val ) throws IllegalArgumentException {
		if( evacuationPlans.contains( val ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.PlanAlreadyExistsException" ) );
		if( currentEvacuationPlan == null )
			currentEvacuationPlan = val;
		evacuationPlans.add( val );
	}

	/**
	 * Deletes an EvacuationPlan from the Project. If you deleted the "current"
	 * EvacuationPlan, the first EvacuationPlan in the List of the remaining ones will
	 * be selected to become "current", if the List is empty, the {@code currentEvacuationPlan}
	 * will be set to null.
	 * @param val 
	 * @throws IllegalArgumentException if the EvacuationPlan {@code val} is not registered at the Project.
	 */
	public void deleteEvacuationPlan( EvacuationPlan val ) throws IllegalArgumentException {
		if( !evacuationPlans.remove( val ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.PlanNotRegisteredException" ) );
		val.delete();

		if( val == currentEvacuationPlan )
			currentEvacuationPlan = evacuationPlans.size() > 0 ? evacuationPlans.get( 0 ) : null;
	}

	public List<EvacuationPlan> getEvacuationPlans() {
		return Collections.unmodifiableList( evacuationPlans );
	}

	public EvacuationPlan getCurrentEvacuationPlan() {
		return currentEvacuationPlan;
	}

	/**
	 * Sets the EvacuationPlan that is to be used.
	 * @param val 
	 * @throws IllegalArgumentException if {@code val} is not in the list of EvacuationPlans that is maintained by the Project.
	 */
	public void setCurrentEvacuationPlan( EvacuationPlan val ) throws IllegalArgumentException {
		if( !evacuationPlans.contains( val ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.PlanNotInProjectException" ) );
		this.currentEvacuationPlan = val;
	}

	/**
	 * Adds a new Assignment to the Project. If the Project hasn't had a "CurrentAssignment"
	 * previously, then the new Assignment is made the current one.
	 * @param val
	 * @throws IllegalArgumentException if the Assignment {@code val} is already registered at the Project.
	 */
	public void addAssignment( Assignment val ) throws IllegalArgumentException {
		if( assignments.contains( val ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.AssignmentAlreadyRegisteredException" ) );
		if( currentAssignment == null )
			currentAssignment = val;

		assignments.add( val );
	}

	/**
	 * Deletes an Assignment from the Project. If you deleted the "current"
	 * Assignment, the first Assignment in the List of the remaining ones will
	 * be selected to become "current", if the List is empty, the {@code currentAssignment}
	 * will be set to {@code null}.
	 * @param val 
	 * @throws IllegalArgumentException - Is thrown when the Assignment {@code val} is not registered at the Project.
	 */
	public void deleteAssignment( Assignment val ) throws IllegalArgumentException {
		if( !assignments.remove( val ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.AssignmentNotRegisteredException" ) );
		val.delete();

		if( val == currentAssignment )
			if( assignments.size() > 0 )
				currentAssignment = assignments.get( 0 );
			else
				currentAssignment = null;
	}

	public List<Assignment> getAssignments() {
		return Collections.unmodifiableList( assignments );
	}

	public Assignment getCurrentAssignment() {
		if( currentAssignment == null )
			if( assignments.size() > 0 )
				currentAssignment = assignments.get( 0 );
		return currentAssignment;
	}

	/** Sets the Assignment that is to be used. Gives notice to the previous and the new currentAssignment
	 * that it is not any more respectively now the current assignment.
	 * @param val 
	 * @throws IllegalArgumentException if {@code val} is not in the list of Assignments that is maintained by the Project.
	 */
	public void setCurrentAssignment( Assignment val ) throws IllegalArgumentException {
		if( !assignments.contains( val ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.AssignmentNotInProjectException" ) );

		this.currentAssignment.setInactive();
		this.currentAssignment = val;
		this.currentAssignment.setActive();
	}

	/**
	 * Returns the stored visual properties of the project.
	 * @return the stored visual properties of the project
	 */
	public VisualProperties getVisualProperties() {
		return vp;
	}

	/**
	 * Sets new visual properties for the project.
	 * @param visualProperties the new visual properties
	 */
	public void setVisualProperties( VisualProperties visualProperties ) {
		vp = visualProperties;
	}

	/**
	 * Returns the project file, if a file is loaded. New projects don't have a file.
	 * @return the project file
	 */
	public File getProjectFile() {
		return projectFile;
	}

	public void setProjectFile( File val ) {
		this.projectFile = val;
	}

	public BuildingPlan getBuildingPlan() {
		return plan;
	}

	@Override
	public boolean equals( Object o ) {
		if( o instanceof Project ) {
			Project p = (Project) o;

			//This is redundant and only for speeding up the method
			if( assignments.size() != p.assignments.size() || evacuationPlans.size() != p.evacuationPlans.size() )
				return false;

			//Here comes the real comparison - Iteratively compare all subobjects
			//The order in the lists subobjects_me/p MUST be the same
			LinkedList subobjects_me = new LinkedList();
			subobjects_me.add( currentAssignment );
			subobjects_me.add( currentEvacuationPlan );
			subobjects_me.add( plan );
			subobjects_me.add( projectFile );
			subobjects_me.add( assignments );
			subobjects_me.add( evacuationPlans );
			subobjects_me.add( vp );

			LinkedList subobjects_p = new LinkedList();
			subobjects_p.add( p.currentAssignment );
			subobjects_p.add( p.currentEvacuationPlan );
			subobjects_p.add( p.plan );
			subobjects_p.add( p.projectFile );
			subobjects_p.add( p.assignments );
			subobjects_p.add( p.evacuationPlans );
			subobjects_p.add( p.vp );

			while( subobjects_me.size() > 0 && subobjects_p.size() > 0 ) {
				Object subobj_me = subobjects_me.poll();
				Object subobj_p = subobjects_p.poll();

				//This is not an implementation error - Lists also have a proper equals method
				boolean subobj_equal = (subobj_me == null) ? subobj_p == null : subobj_me.equals( subobj_p );
				if( !subobj_equal )
					return false;
			}
			//Return whether no elements are left on one side (that would imply inequality)
			return (subobjects_me.size() == 0 && subobjects_p.size() == 0);
		} else
			return false;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getPath() {
		return this.getProjectFile().getName().substring( 0, getProjectFile().getName().length()-4 );
	}

	public String getName() {
		return name;
	}
}
