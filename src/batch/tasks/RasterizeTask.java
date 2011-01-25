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
 * RasterizeTask.java
 * Created on 24.01.2008, 21:02:06
 */

package batch.tasks;

import ds.z.Project;
import gui.ZETMain;

/**
 * Performs the rasterization of a {@link ds.z.BuildingPlan}.
 * @author Jan-Philipp Kappmeier
 */
public class RasterizeTask implements Runnable {
	/** The project that should be rastered. */
	private Project project;
	
	/** Creates a new instance of the rasterization task. */
	public RasterizeTask( Project p ) {
		if( p == null ) {
			throw new java.lang.IllegalArgumentException ("Project is null.");
		}
		project = p;
	}
	
	/** Performs rasterization. */
	public void run() {
		try {
			project.getBuildingPlan().rasterize();
		} catch( Exception ex ) {
			ZETMain.sendError( ex.getLocalizedMessage () );
		}
	}	
}
