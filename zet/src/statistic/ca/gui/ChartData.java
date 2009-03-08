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

import java.util.ArrayList;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class ChartData {
	
	private String diagramType;

	private String title;
	
	private String yAxisLabel;
	
	private static DefaultCategoryDataset cDataSet;
	
	private static DefaultPieDataset pieDataSet;
	
	
	public ChartData(String diagramType, String title, String yAxisLabel, ArrayList<Double> values, ArrayList<String> assignments){
		cDataSet = new DefaultCategoryDataset();
		pieDataSet = new DefaultPieDataset();
		this.diagramType = diagramType;
		this.title = title;
		this.yAxisLabel = yAxisLabel;
		for(int i = 0; i < values.size(); i++){
			cDataSet.addValue(values.get(i).doubleValue(),"",assignments.get(i));
			pieDataSet.setValue(assignments.get(i),values.get(i).doubleValue());
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYAxisLabel() {
		return yAxisLabel;
	}

	public void setYAxisLabel(String axisLabel) {
		yAxisLabel = axisLabel;
	}

	public CategoryDataset getCDataSet() {
		return cDataSet;
	}

	public void setCDataSet(DefaultCategoryDataset dataSet) {
		cDataSet = dataSet;
	}

	public String getDiagramType() {
		return diagramType;
	}

	public void setDiagramType(String diagramType) {
		this.diagramType = diagramType;
	}

	public static DefaultPieDataset getPieDataSet() {
		return pieDataSet;
	}

	public static void setPieDataSet(DefaultPieDataset pieDataSet) {
		ChartData.pieDataSet = pieDataSet;
	}
	
	
}
