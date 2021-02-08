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
 */package de.zet_evakuierung.visualization;

import org.zetool.opengl.framework.abs.VisualizationModelProvider;

/**
 * Base class for visualizations. Stores the {@link VisualizationModelProvider} suitable for the implementing models.
 *
 * @author Jan-Philipp Kappmeier
 * @param <W> the {@link VisualizationModelProvider}
 */
public class AbstractVisualizationModel<W extends VisualizationModelProvider> {

    protected final W visualizationModel;

    public AbstractVisualizationModel(W visualizationModel) {
        this.visualizationModel = visualizationModel;
    }

}