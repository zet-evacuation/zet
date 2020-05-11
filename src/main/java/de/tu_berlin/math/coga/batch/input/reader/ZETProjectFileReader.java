/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.batch.input.reader;

import org.zetool.components.batch.input.reader.InputFileReader;
import de.zet_evakuierung.model.ProjectLoader;
import de.zet_evakuierung.model.Project;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An input file reader for ZET-Project files.
 *
 * @author Martin Groß
 */
public class ZETProjectFileReader extends InputFileReader<Project> {

	@Override
	public Class<Project> getTypeClass() {
		return Project.class;
	}



    /**
     * Stores the properties of this file.
     */
    private String[] properties;

    /**
     * Returns the number of floors, the number of exists and the maximal number
     * of evacuees in the instance. If it has not been done yet, this requires
     * parsing the whole file due to ZET files being header-less, which makes
     * this getProperties() potentially as slow as reading the whole file.
     * @return an array containing the number of floors, the number of exists
     * and the maximal number of evacuees in the instance in the first, second
     * and third field of the algorithm.
     */
    @Override
    public String[] getProperties() {
        if (properties == null) {
            properties = new String[3];
            if (!isProblemSolved()) {
                run();
            }
            properties[0] = "" + getSolution().getBuildingPlan().floorCount();
            properties[1] = "" + getSolution().getBuildingPlan().getEvacuationAreasCount();
            properties[2] = "" + getSolution().getBuildingPlan().maximalEvacuees();
        }
        return properties;
    }

    /**
     * Loads the file using the ProjectLoader class. This ignores the
     * optimization hint parameter.
     * @param problem the input file.
     * @return the project contained in the file.
     */
    @Override
    protected Project runAlgorithm(File problem) {
        try {
            return ProjectLoader.load(problem);
        } catch (IOException ex) {
					// TODO: global logger benutzen oder dem default logger einen output anghängen...
	          Logger.getLogger(ZETProjectFileReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
