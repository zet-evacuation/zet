/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.util.units.Quantity;
import org.zetool.common.util.units.TimeUnits;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.CompareVisualizationResults;
import ds.GraphVisualizationResults;
import ds.ca.evac.EvacuationCellularAutomaton;
import de.zet_evakuierung.model.Project;
import io.visualization.BuildingResults;
import io.visualization.EvacuationSimulationResults;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.RunnableFuture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kapman
 */
public class AlgorithmControlTest {

	public AlgorithmControlTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of isError method, of class AlgorithmControl.
	 */
	@Test
	public void testIsError() {
		System.out.println( "isError" );
		AlgorithmControl instance = null;
		boolean expResult = false;
		boolean result = instance.isError();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getError method, of class AlgorithmControl.
	 */
	@Test
	public void testGetError() {
		System.out.println( "getError" );
		AlgorithmControl instance = null;
		RuntimeException expResult = null;
		RuntimeException result = instance.getError();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of throwError method, of class AlgorithmControl.
	 */
	@Test
	public void testThrowError() {
		System.out.println( "throwError" );
		AlgorithmControl instance = null;
		instance.throwError();
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of setProject method, of class AlgorithmControl.
	 */
	@Test
	public void testSetProject() {
		System.out.println( "setProject" );
		Project project = null;
		AlgorithmControl instance = null;
		instance.setProject( project );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of convertBuildingPlan method, of class AlgorithmControl.
	 */
	@Test
	public void testConvertBuildingPlan_0args() {
		System.out.println( "convertBuildingPlan" );
		AlgorithmControl instance = null;
		instance.convertBuildingPlan();
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of convertBuildingPlan method, of class AlgorithmControl.
	 */
	@Test
	public void testConvertBuildingPlan_PropertyChangeListener() {
		System.out.println( "convertBuildingPlan" );
		PropertyChangeListener pcl = null;
		AlgorithmControl instance = null;
		instance.convertBuildingPlan( pcl );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getConversionRuntime method, of class AlgorithmControl.
	 */
	@Test
	public void testGetConversionRuntime() {
		System.out.println( "getConversionRuntime" );
		AlgorithmControl instance = null;
		long expResult = 0L;
		Quantity<TimeUnits> result = instance.getConversionRuntime();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getBuildingResults method, of class AlgorithmControl.
	 */
	@Test
	public void testGetBuildingResults() {
		System.out.println( "getBuildingResults" );
		AlgorithmControl instance = null;
		BuildingResults expResult = null;
		BuildingResults result = instance.getBuildingResults();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of convertCellularAutomaton method, of class AlgorithmControl.
	 */
	@Test
	public void testConvertCellularAutomaton_0args() {
		System.out.println( "convertCellularAutomaton" );
		AlgorithmControl instance = null;
		instance.convertCellularAutomaton(instance);
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of convertCellularAutomaton method, of class AlgorithmControl.
	 */
	@Test
	public void testConvertCellularAutomaton_PropertyChangeListener() {
		System.out.println( "convertCellularAutomaton" );
		PropertyChangeListener propertyChangeListener = null;
		AlgorithmControl instance = null;
		instance.convertCellularAutomaton( propertyChangeListener );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of invalidateConvertedCellularAutomaton method, of class AlgorithmControl.
	 */
	@Test
	public void testInvalidateConvertedCellularAutomaton() {
		System.out.println( "invalidateConvertedCellularAutomaton" );
		AlgorithmControl instance = null;
		//instance.invalidateConvertedCellularAutomaton;
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getCellularAutomaton method, of class AlgorithmControl.
	 */
	@Test
	public void testGetCellularAutomaton() {
		System.out.println( "getCellularAutomaton" );
		AlgorithmControl instance = null;
		EvacuationCellularAutomaton expResult = null;
		EvacuationCellularAutomaton result = instance.getCellularAutomaton();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getContainer method, of class AlgorithmControl.
	 */
	@Test
	public void testGetContainer() {
		System.out.println( "getContainer" );
		AlgorithmControl instance = null;
		ZToCARasterContainer expResult = null;
		ZToCARasterContainer result = instance.getContainer();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getMapping method, of class AlgorithmControl.
	 */
	@Test
	public void testGetMapping() {
		System.out.println( "getMapping" );
		AlgorithmControl instance = null;
		ZToCAMapping expResult = null;
		ZToCAMapping result = instance.getMapping();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of performSimulation method, of class AlgorithmControl.
	 */
	@Test
	public void testPerformSimulation_0args() {
		System.out.println( "performSimulation" );
		AlgorithmControl instance = null;
		instance.performSimulation(instance,null);
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of performSimulation method, of class AlgorithmControl.
	 */
	@Test
	public void testPerformSimulation_PropertyChangeListener_AlgorithmListener() {
		System.out.println( "performSimulation" );
		PropertyChangeListener propertyChangeListener = null;
		AlgorithmListener listener = null;
		AlgorithmControl instance = null;
		instance.performSimulation( propertyChangeListener, listener );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of performSimulationQuick method, of class AlgorithmControl.
	 */
	@Test
	public void testPerformSimulationQuick() {
		System.out.println( "performSimulationQuick" );
		PropertyChangeListener propertyChangeListener = null;
		AlgorithmListener listener = null;
		AlgorithmControl instance = null;
		instance.performSimulationQuick( listener );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getCaVisResults method, of class AlgorithmControl.
	 */
	@Test
	public void testGetCaVisResults() {
		System.out.println( "getCaVisResults" );
		AlgorithmControl instance = null;
		EvacuationSimulationResults expResult = null;
		EvacuationSimulationResults result = instance.getCaVisResults();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of createConcreteAssignment method, of class AlgorithmControl.
	 */
	@Test
	public void testCreateConcreteAssignment() throws Exception {
		System.out.println( "createConcreteAssignment" );
		AlgorithmControl instance = null;
		//instance.createConcreteAssignment();
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of setUpSimulationAlgorithm method, of class AlgorithmControl.
	 */
	@Test
	public void testSetUpSimulationAlgorithm() {
		System.out.println( "setUpSimulationAlgorithm" );
		AlgorithmControl instance = null;
//		instance.setUpSimulationAlgorithm();
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of pauseStepByStep method, of class AlgorithmControl.
	 */
	@Test
	public void testPauseStepByStep() {
		System.out.println( "pauseStepByStep" );
		AlgorithmControl instance = null;
		//instance.pauseStepByStep();
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of performOneStep method, of class AlgorithmControl.
	 */
	@Test
	public void testPerformOneStep() throws Exception {
		System.out.println( "performOneStep" );
		PropertyChangeListener propertyChangeListener = null;
		AlgorithmListener listener = null;
		AlgorithmControl instance = null;
		instance.performOneStep( listener );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of propertyChange method, of class AlgorithmControl.
	 */
	@Test
	public void testPropertyChange() {
		System.out.println( "propertyChange" );
		PropertyChangeEvent pce = null;
		AlgorithmControl instance = null;
		instance.propertyChange( pce );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of convertGraph method, of class AlgorithmControl.
	 */
	@Test
	public void testConvertGraph_0args() {
		System.out.println( "convertGraph" );
		AlgorithmControl instance = null;
		instance.convertGraph();
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of convertGraph method, of class AlgorithmControl.
	 */
	@Test
	public void testConvertGraph_PropertyChangeListener_GraphConverterAlgorithms() {
		System.out.println( "convertGraph" );
		PropertyChangeListener propertyChangeListener = null;
		GraphConverterAlgorithms Algo = null;
		AlgorithmControl instance = null;
		RunnableFuture expResult = null;
		RunnableFuture result = instance.convertGraph( propertyChangeListener, Algo );
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getNetworkFlowModel method, of class AlgorithmControl.
	 */
	@Test
	public void testGetNetworkFlowModel() {
		System.out.println( "getNetworkFlowModel" );
		AlgorithmControl instance = null;
		NetworkFlowModel expResult = null;
		NetworkFlowModel result = instance.getNetworkFlowModel();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of performOptimization method, of class AlgorithmControl.
	 */
	@Test
	public void testPerformOptimization_GUIControl() {
		System.out.println( "performOptimization" );
		GUIControl control = null;
		AlgorithmControl instance = null;
		instance.performOptimization( control );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of performOptimization method, of class AlgorithmControl.
	 */
	@Test
	public void testPerformOptimization_PropertyChangeListener_AlgorithmListener() {
		System.out.println( "performOptimization" );
		PropertyChangeListener propertyChangeListener = null;
		AlgorithmListener control = null;
		AlgorithmControl instance = null;
		instance.performOptimization( propertyChangeListener, control );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of performOptimizationCompare method, of class AlgorithmControl.
	 */
	@Test
	public void testPerformOptimizationCompare() {
		System.out.println( "performOptimizationCompare" );
		PropertyChangeListener propertyChangeListener = null;
		AlgorithmControl instance = null;
		instance.performOptimizationCompare( propertyChangeListener );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getGraphVisResults method, of class AlgorithmControl.
	 */
	@Test
	public void testGetGraphVisResults() {
		System.out.println( "getGraphVisResults" );
		AlgorithmControl instance = null;
		GraphVisualizationResults expResult = null;
		GraphVisualizationResults result = instance.getGraphVisResults();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}

	/**
	 * Test of getCompVisResults method, of class AlgorithmControl.
	 */
	@Test
	public void testGetCompVisResults() {
		System.out.println( "getCompVisResults" );
		AlgorithmControl instance = null;
		CompareVisualizationResults expResult = null;
		CompareVisualizationResults result = instance.getCompVisResults();
		assertEquals( expResult, result );
		// TODO review the generated test code and remove the default call to fail.
		fail( "The test case is a prototype." );
	}
}
