package de.tu_berlin.math.coga.zet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZETLocalizationTest {
	
	public ZETLocalizationTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Checks if instantiation of the singleton works, e.g. if the resource bundle
	 * is loaded correctly.
	 */
	@Test
	public void testGetSingleton() {
		ZETLocalization result = ZETLocalization.getSingleton();
	}
}