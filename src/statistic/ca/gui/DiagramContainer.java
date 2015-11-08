/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package statistic.ca.gui;

import ds.PropertyContainer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.JFreeChart;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import statistic.ca.gui.JCAStatisticPanel.diagramClick;

/**
 * 
 * @author Matthias Woste, Timon Kelter
 * 
 */
public class DiagramContainer extends JPanel {

	private ArrayList<DiagramTupel> diagramList;
	private HashMap<String, Integer> diagramMapping;
	private JCAStatisticPanel parent;

	public DiagramContainer (JCAStatisticPanel parent) {
		super ();
		this.parent = parent;
		diagramList = new ArrayList<DiagramTupel> ();
		diagramMapping = new HashMap<String, Integer> ();
	}

	public boolean contains (String diagramName) {
		return diagramMapping.containsKey (diagramName);
	}

	public void remove (String diagramName) {
		if (contains (diagramName)) {
			JPanel p = getTupel (diagramName);
			if (p != null) {
				diagramList.remove (p);
				diagramMapping.clear ();
				for (DiagramTupel dt : diagramList) {
					diagramMapping.put (dt.getTitle (), diagramList.indexOf (dt));
				}
				
				super.removeAll ();
				
				setLayout (new GridLayout ((int) (Math.ceil ((double) diagramList.size () / 2)), 2));
				
				for (DiagramTupel dt : diagramList) {
					add (dt.getDiagram ());
					(dt.getDiagram ()).addMouseListener (parent.new diagramClick ());
				}
				
				repaint ();
				validate ();
			}
		}
	}

	public void remove (JPanel diagramToRemove) {
		JPanel toRemove = null;
		DiagramTupel tupelToRemove = null;
		for (DiagramTupel dt : diagramList) {
			if (dt.getDiagram ().equals (diagramToRemove)) {
				toRemove = dt.getDiagram ();
				tupelToRemove = dt;
			}
		}
		if (toRemove == null) {
			return;
		}
		diagramList.remove (tupelToRemove);
		diagramMapping.clear ();
		for (DiagramTupel dia : diagramList) {
			diagramMapping.put (dia.getTitle (), diagramList.indexOf (dia));
		}
		super.removeAll ();

		setLayout (new GridLayout ((int) (Math.ceil ((double) diagramList.size () / 2)), 2));

		for (DiagramTupel d : diagramList) {
			add (d.getDiagram ());
			(d.getDiagram ()).addMouseListener (parent.new diagramClick ());
		}
		repaint ();
		validate ();
	}
	/** Removes all the diagrams from this panel. */
	@Override
	public void removeAll () {
		diagramList.clear ();
		diagramMapping.clear ();
		super.removeAll ();
	}

	public void addChart (String title, JFreeChart chart, JPanel parent) {
		JChartPanel jcp = new JChartPanel ();
		jcp.setPreferredSize (new Dimension (
				(int) ((parent.getWidth () / 2) - 20), 300));
		jcp.setMinimumSize (jcp.getPreferredSize ());
		jcp.setMaximumSize (jcp.getPreferredSize ());
		jcp.setChart (chart);
		
		if (PropertyContainer.getGlobal().getAsBoolean( "statistic.blackAndWhiteDiagrams") && chart.getPlot () instanceof XYPlot) {
			chart.getXYPlot().setRenderer (new ChartRenderer ());
		}
		
		addDiagram (title, jcp);
	}

	public void addTable (String title, JScrollPane table, JPanel parent) {
		JChartPanel jcp = new JChartPanel ();
		jcp.setPreferredSize (new Dimension (
				(int) ((parent.getWidth () / 2) - 20), 300));
		jcp.setMinimumSize (jcp.getPreferredSize ());
		jcp.setMaximumSize (jcp.getPreferredSize ());
		jcp.setChart (null);
		jcp.add (table);
		table.addMouseListener (this.parent.new diagramClick ());
		addDiagram (title, jcp);
	}

	private JPanel getTupel (String title) {
		for (DiagramTupel dt : diagramList) {
			if (dt.getTitle ().equals (title)) {
				return dt.getDiagram ();
			}
		}
		return null;
	}

	public void addDiagram (String title, JPanel d) {
		DiagramTupel newTupel = new DiagramTupel (title, d);
		diagramList.add (newTupel);
		diagramMapping.clear ();
		for (DiagramTupel dt : diagramList) {
			diagramMapping.put (dt.getTitle (), diagramList.indexOf (dt));
		}
		super.removeAll ();

		setLayout (new GridLayout ((int) (Math.ceil ((double) diagramList.size () / 2)), 2));
		setMinimumSize (new Dimension ((int) ((parent.getWidth ()) - 20), (int) (Math.ceil ((double) diagramList.size () / 2)) * 300));

		for (DiagramTupel dt : diagramList) {
			add (dt.getDiagram ());
			(dt.getDiagram ()).addMouseListener (parent.new diagramClick ());
		}
		
		parent.getContentD ().validate ();
		repaint ();
		validate ();
	}

	private class DiagramTupel {

		String title;
		JPanel diagram;

		public DiagramTupel (String t, JPanel d) {
			title = t;
			diagram = d;
		}

		public String getTitle () {
			return title;
		}

		public void setTitle (String title) {
			this.title = title;
		}

		public JPanel getDiagram () {
			return diagram;
		}

		public void setDiagram (JPanel diagram) {
			this.diagram = diagram;
		}
	}

	private class ChartRenderer extends XYLineAndShapeRenderer {
		private int counter = -1;

		public ChartRenderer () {
			// Set a new stroke for each of the data series
			setSeriesStroke (0, new BasicStroke (1.0f, 
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, 
					new float[]{2.0f}, 0.0f));
			setSeriesStroke (1, new BasicStroke (1.0f, 
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, 
					new float[]{4.0f}, 0.0f));
			setSeriesStroke (2, new BasicStroke (1.0f, 
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, 
					new float[]{2.0f, 5.0f, 2.0f}, 0.0f));
			setSeriesStroke (3, new BasicStroke (1.0f, 
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, 
					new float[]{10.0f, 4.0f, 10.0f}, 0.0f));
			setSeriesStroke (4, new BasicStroke (1.0f, 
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, 
					new float[]{7.0f}, 0.0f));
			setPaint (Color.BLACK);
			setBaseShapesVisible (true);
		}
		
		@Override
		public void drawSecondaryPass (java.awt.Graphics2D g2, XYPlot plot,
				XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis,
				java.awt.geom.Rectangle2D dataArea, ValueAxis rangeAxis,
				CrosshairState crosshairState, EntityCollection entities) {

			if (item == 0) {
				counter = 0;
			} else {
				counter++;
			}

			// Paint the symbols in a distance of 'repetition_width' data values
			// with an offset that is computed from the current series' number.
			// (To offset every series by a different percentage of 'repetition_width')
			final int repetition_width = 8;
			int offset = 0;
			if (dataset instanceof XYSeriesCollection) {
				int numOfSeries = ((XYSeriesCollection)dataset).getSeriesCount ();
				offset = (int)( ((float)series) / ((float)numOfSeries) * ((float)repetition_width) );
			}
			if ((offset + counter) % repetition_width == 0) {
				super.drawSecondaryPass (g2, plot, dataset, pass, series, item,
						domainAxis, dataArea, rangeAxis, crosshairState, entities);
			}
		}
	}
}