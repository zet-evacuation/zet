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
package statistic.ca.gui;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;


public class Charts {
	
	private static JFreeChart chart;

	public static JFreeChart createBarChart(ChartData data) {
		if(data.getDiagramType().equals("bar")){
			chart = ChartFactory.createBarChart(data.getTitle(), // Title
					data.getYAxisLabel(), // X-Axis label
					"", // Y-Axis label
					data.getCDataSet(), // CategoryDataset
					PlotOrientation.VERTICAL, false, true, false // Show legend
			);
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setBackgroundPaint(Color.LIGHT_GRAY);
			plot.setDomainGridlinePaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.WHITE);
			plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		}
		
		if(data.getDiagramType().equals("pie")){
			chart = ChartFactory.createPieChart(
					data.getTitle(), // Title
					data.getPieDataSet(),
					false, true, false
			);
		}

		chart.setBackgroundPaint(Color.WHITE);

		
		return chart;
	}
}