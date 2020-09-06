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
package batch;

import com.thoughtworks.xstream.XStream;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import ds.GraphVisualizationResults;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import ds.CompareVisualizationResults;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import io.visualization.BuildingResults;
import io.visualization.EvacuationSimulationResults;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.zet.cellularautomaton.statistic.CAStatistic;
import org.zet.cellularautomaton.statistic.MultipleCycleCAStatistic;

/** A set of results that were computed for a single BatchEntry. For every CA
 * cycle a new CA datastructure, a new CA statistic and a new CA visual result is
 * stored. For teh graph there is only one cycle, and so there is only one dynamic
 * flow that is stored. The GraphVisualizationResults can be generated with the
 * flow and the networkmodel, but we store it too to boost the performance when loading
 * the results.
 *
 * @author Timon Kelter
 */
public class BatchResultEntry {
	// Identifying information
	protected String name;
	private BuildingResults buildingResults;
	protected EvacuationCellularAutomaton[] cas;
	protected NetworkFlowModel networkFlowModel;
	
	// Computed solutions
	protected PathBasedFlowOverTime flow;
	protected CAStatistic[] caStatistics;
	protected MultipleCycleCAStatistic mccaStatistic;
	protected EvacuationSimulationResults[] caVis;
	protected GraphVisualizationResults graphVis;
  protected CompareVisualizationResults compVisRes;
	private double averageCAStepsPerSecond;
	
	/** The index of the CA cycle with the median evacuation time. */
	protected int medianIndex;
		
	public BatchResultEntry (String name, BuildingResults buildingResults) {
		this.name = name;
		this.buildingResults = buildingResults;
		this.medianIndex = -1;
    
    cas = new EvacuationCellularAutomaton[1];
    caStatistics = new CAStatistic[1];
    caVis = new EvacuationSimulationResults[1];
	}

	public String getName () { return name; }
	public BuildingResults getBuildingResults () { return buildingResults; }
	public EvacuationCellularAutomaton[] getCa () { return cas; }
	public NetworkFlowModel getGraph () { return networkFlowModel; }

	public PathBasedFlowOverTime getFlow () { return flow; }
	public MultipleCycleCAStatistic getMultipleCycleCAStatistics () { return mccaStatistic; }
	public EvacuationSimulationResults[] getCaVis () { return caVis; }
	public CAStatistic[] getCaStatistics() { return caStatistics; }
	public GraphVisualizationResults getGraphVis () { return graphVis; }
        public CompareVisualizationResults getCompRes() {return compVisRes; }

	/** @return The index of the CA cycle with the median evacuation time. */
	public int getMedianIndex () { return medianIndex; }
	
	@Override
	public String toString () {
		return name;
	}
		
	/**
	 * Sets the statistic for a given cycle.
	 * @param runNumber the cycle number
	 * @param statistic the statistic
	 */
	public void setCellularAutomatonStatistic( int runNumber, CAStatistic statistic ) {
		caStatistics[runNumber] = statistic;
	}
	
	/**
	 * Sets the visualization results for a given cycle.
	 * @param runNumber the cycle number
	 * @param visResults the statistic
	 */
	public void setCellularAutomatonVisualization( int runNumber, EvacuationSimulationResults visResults ) {
		caVis[runNumber] = visResults;
	}
	
	/**
	 * Sets the cellular automaton for a given cycle.
	 * @param runNumber the cycle number
	 * @param ca the cellular automaton
	 */
	public void setCellularAutomaton( int runNumber, EvacuationCellularAutomaton ca ) {
		cas[runNumber] = ca;
	}
	
	/**
	 * Returns the cellular automaton for a given cycle.
	 * @param runNumber the cycle number
	 * @return the cellular automaton
	 */
	public EvacuationCellularAutomaton getCellularAutomaton( int runNumber ) {
		return cas[runNumber];
	}
	
	/**
	 * Sets the index of a median run of some cellular automaton results.
	 * @param index the index
	 */
	public void setMedianIndex( int index ) {
		medianIndex = index;
	}
		
	/**
	 * Returns the {@link ds.NetworkFlowModel}
	 * @return the network flow model
	 */
	public NetworkFlowModel getNetworkFlowModel() {
		return networkFlowModel;
	}

	/**
	 * Sets the {@link ds.NetworkFlowModel}
	 * @param networkFlowModel the network flow model
	 */
	public void setNetworkFlowModel( NetworkFlowModel networkFlowModel ) {
		this.networkFlowModel = networkFlowModel;
	}

	/**
	 * Sets a flow for this result.
	 * @param flow the flow.
	 */
	public void setFlow( PathBasedFlowOverTime flow ) {
		this.flow = flow;
	}

	/**
	 * Sets the graph visualization results for the result entry.
	 * @param graphVis the visualization results
	 */
	public void setGraphVis( GraphVisualizationResults graphVis ) {
		this.graphVis = graphVis;
	}
        
        public void setCompVis (CompareVisualizationResults VisRes)
        {
            this.compVisRes = VisRes;
        }
	
	/* Saves BatchResultEntries in GZIPped XML format. */
	public void save (File selectedFile) throws IOException {
		GZIPOutputStream output = new GZIPOutputStream (
				new BufferedOutputStream (new FileOutputStream (selectedFile)));
		XStream xml_convert = new XStream ();
		xml_convert.setMode (XStream.ID_REFERENCES);
		xml_convert.toXML( this, output );
		output.flush ();
		output.close ();
		System.gc ();
	}
	
	/* Loads BatchResultEntries from GZIPped XML format. */
	public static BatchResultEntry load (File selectedFile) throws IOException {
		GZIPInputStream input = new GZIPInputStream (new BufferedInputStream (
				new FileInputStream (selectedFile)));
		XStream xml_convert = new XStream ();
		xml_convert.setMode (XStream.ID_REFERENCES);
		BatchResultEntry res = (BatchResultEntry)xml_convert.fromXML( input );
		input.close ();
		System.gc ();
		return res;
	}

	/** How many steps the underlying ca's perform (per second). */
	public double getAverageCAStepsPerSecond () {
		return averageCAStepsPerSecond;
	}

	/** @param averageCAStepsPerSecond How many steps the underlying ca's perform (per second). */
	public void setAverageCAStepsPerSecond (double averageCAStepsPerSecond) {
		this.averageCAStepsPerSecond = averageCAStepsPerSecond;
	}

  public void setMultipleCycleCAStatistic( MultipleCycleCAStatistic mcc ) {
    this.mccaStatistic = mcc;
  }
}
