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
 * Test1.java
 * Created on 27. November 2007, 22:18
 */
package ds.z;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * Tests the distribution classes.
 * @author Jan-Philipp Kappmeier
 */
public class PolygonTest extends TestCase {

	/** Creates a new instance of Test1 */
	public PolygonTest () {
		super ();
	}

	@Override
	protected void setUp () throws Exception {
	}

	@Override
	protected void tearDown () throws Exception {
	}

	private PlanPolygon<Edge> getDefaultInstance (int number) {
		// nur eine
		PlanPoint pp1 = new PlanPoint (10, 10);
		PlanPoint pp2 = new PlanPoint (20, 10);
		PlanPoint pp3 = new PlanPoint (20, 20);
		PlanPoint pp4 = new PlanPoint (10, 20);

		PlanPolygon poly = new PlanPolygon<Edge> (Edge.class);
		new Edge (pp1, pp2, poly);
		new Edge (pp2, pp3, poly);
		new Edge (pp4, pp1, poly);

		return poly;
	}

	public void testPoint () throws Exception {
		PlanPoint a = new PlanPoint (3.65, 2.778, true);
		PlanPoint b = new PlanPoint (3.65, 2.778, true);
		PlanPoint c = new PlanPoint (2.778, 3.65, true);
		PlanPoint d = new PlanPoint (3.65, 2.779, true);
		assertEquals (a.equals (b), true);
		assertEquals (a.equals (c), false);
		assertEquals (a.equals (d), false);
	}

	public void testPolygonEasy () throws Exception {
		PlanPoint a = new PlanPoint (3.65, 2.778);
		PlanPoint c = new PlanPoint (2.778, 3.65);
		PlanPolygon t1 = new PlanPolygon<Edge> (Edge.class);
		PlanPolygon t2 = new PlanPolygon<Edge> (Edge.class);

		Edge e1 = new Edge (a, c, t1);
		Edge e2 = new Edge (c, a, t2);
		assertEquals (e1.equals (e2), true);
		assertEquals (e1.getOther (a).equals (c), true);
		assertEquals (e1.getOther (c).equals (a), true);
		assertEquals (e2.getOther (a).equals (c), true);
		assertEquals (e2.getOther (c).equals (a), true);
	}

	public void testPolygonAdvanced1 () throws Exception {
		PlanPoint pp1 = new PlanPoint (10, 10);
		PlanPoint pp2 = new PlanPoint (20, 10);
		PlanPoint pp3 = new PlanPoint (20, 20);
		PlanPoint pp4 = new PlanPoint (10, 20);

		PlanPolygon p1 = new PlanPolygon<Edge> (Edge.class); // Zielpolygon

		Edge pe1 = new Edge (pp1, pp2, p1);
		assertEquals (p1.getStart (), pp1);
		assertEquals (p1.getEnd (), pp2);
		assertEquals (p1.isClosed (), false);

		Edge pe2 = new Edge (pp1, pp4, p1);
		assertEquals (ds.z.PlanPolygon.fitsTogether (pe2, p1), false);
		assertEquals (p1.getStart (), pp4);
		assertEquals (p1.getEnd (), pp2);
		assertEquals (p1.isClosed (), false);

		Edge pe3 = new Edge (pp2, pp3, p1);
		assertEquals (p1.getStart (), pp4);
		assertEquals (p1.getEnd (), pp3);
		assertEquals (p1.isClosed (), false);

		Edge pe4 = new Edge (pp3, pp4, p1);
		assertEquals (p1.isClosed (), true);

		// Teste die addFirst() und addLast()-Methoden
		PlanPolygon newPolygon = new PlanPolygon<Edge> (Edge.class);
		newPolygon.addEdge (pe1); // von 1 nach 2
		newPolygon.addPointFirst (pp4);
		newPolygon.addPointFirst (pp3);
		assertEquals (newPolygon.getStart (), pp3);
		assertEquals (pp2, newPolygon.getEnd ());
		assertEquals (newPolygon.isClosed (), false);
		newPolygon.addPointLast (pp3);
		assertEquals (newPolygon.isClosed (), true);
	}

	public void testPolygonAdvanced2 () throws Exception {
		// Erzeugen von Polynomen, testen auf Verbindbarkeit und merge

		// Beinhaltet:
		// Edge setAssociatedPolygon-Test

		// Punkte für quadrat
		PlanPoint pp1 = new PlanPoint (10, 10);
		PlanPoint pp2 = new PlanPoint (20, 10);
		PlanPoint pp3 = new PlanPoint (20, 20);
		PlanPoint pp4 = new PlanPoint (10, 20);

		Edge e14 = new Edge (pp1, pp4, new PlanPolygon<Edge> (Edge.class));
		Edge e12 = new Edge (pp1, pp2, new PlanPolygon<Edge> (Edge.class));
		Edge e23 = new Edge (pp2, pp3, new PlanPolygon<Edge> (Edge.class));

		assertEquals (e14.getAssociatedPolygon ().getStart (), pp1);
		assertEquals (e12.getAssociatedPolygon ().getStart (), pp1);
		assertEquals (e23.getAssociatedPolygon ().getStart (), pp2);
		assertEquals (e14.getAssociatedPolygon ().getEnd (), pp4);
		assertEquals (e12.getAssociatedPolygon ().getEnd (), pp2);
		assertEquals (e23.getAssociatedPolygon ().getEnd (), pp3);

		PlanPolygon pe14 = e14.getAssociatedPolygon ();
		PlanPolygon pe12 = e12.getAssociatedPolygon ();
		PlanPolygon pe23 = e23.getAssociatedPolygon ();

		assertEquals (pe14.getNumberOfEdges (), 1);
		assertEquals (pe12.getNumberOfEdges (), 1);
		assertEquals (pe23.getNumberOfEdges (), 1);

		e14.setAssociatedPolygon (pe12);
		assertEquals (pe12.getNumberOfEdges (), 2);
		assertEquals (pe14.getNumberOfEdges (), 0);
		assertEquals (e14.getAssociatedPolygon (), e12.getAssociatedPolygon ());
		assertEquals (e14.getAssociatedPolygon (), pe12);

		e23.setAssociatedPolygon (pe12);
		assertEquals (pe12.getNumberOfEdges (), 3);
		assertEquals (pe23.getNumberOfEdges (), 0);
		assertEquals (pe12.isClosed (), false);

		// erzeuge zweites polygon
		PlanPolygon zwei = new PlanPolygon<Edge> (Edge.class);
		Edge ne1 = new Edge (new PlanPoint (20, 20), new PlanPoint (20, 30), zwei);
		PlanPoint p = new PlanPoint (20, 30);
		assertEquals (zwei.getEnd (), p);

		Edge ne2 = new Edge (new PlanPoint (20, 30), new PlanPoint (10, 30), zwei);
		assertEquals (false, zwei.isClosed ());
		assertEquals (false, pe12.fitsTogether (zwei));
		Edge ne3 = new Edge (new PlanPoint (10, 30), new PlanPoint (10, 20), zwei);

		assertEquals (new PlanPoint (10, 20), zwei.getEnd ());
		assertEquals (new PlanPoint (20, 20), zwei.getStart ());

		assertEquals (new PlanPoint (20, 20), pe12.getEnd ());
		assertEquals (new PlanPoint (10, 20), pe12.getStart ());

		assertEquals (true, PlanPolygon.fitsTogether (zwei, pe12));
	}

	public void testPolygonRemove () throws Exception {
		// Testet, ob das Entfernen von Kanten aus dem Polygon funktioniert.
		// Testfälle:
		// - Nur eine Kante
		// - Mehrere Kanten, Erste und Letzte entfernen
		// - Verbunden, Erste und Letzte entfernen

		// ENTFERNEN VON KANTEN IMMER NUR PER EDGE.DELETE!

		// Punkte für quadrat
		PlanPoint pp1 = new PlanPoint (10, 10);
		PlanPoint pp2 = new PlanPoint (20, 10);
		PlanPoint pp3 = new PlanPoint (20, 20);
		PlanPoint pp4 = new PlanPoint (10, 20);

		// Eine Kante entfernen
		PlanPolygon p = new PlanPolygon<Edge> (Edge.class);
		Edge e14 = new Edge (pp1, pp4, p);

		assertEquals (1, p.getNumberOfEdges ());
		e14.delete ();
		assertEquals (0, p.getNumberOfEdges ());
		// _nicht_ testen, ob in der Ecke das assoziierte Polygon entfernt worden ist,
		// da removeEdge _immer_ von der Ecke aufgerufen wird.

		// Mehrere testen, nicht verbunden
		p = new PlanPolygon<Edge> (Edge.class);
		Edge e12 = new Edge (pp1, pp2, p);
		e14 = new Edge (pp1, pp4, p);
		Edge e23 = new Edge (pp2, pp3, p);

		assertEquals (new PlanPoint (10, 20), p.getStart ());
		assertEquals (new PlanPoint (20, 20), p.getEnd ());
		assertEquals (3, p.getNumberOfEdges ());

		e14.delete ();
		assertEquals (new PlanPoint (10, 10), p.getStart ());
		assertEquals (new PlanPoint (20, 20), p.getEnd ());
		assertEquals (2, p.getNumberOfEdges ());

		e23.delete ();
		assertEquals (new PlanPoint (10, 10), p.getStart ());
		assertEquals (new PlanPoint (20, 10), p.getEnd ());
		assertEquals (1, p.getNumberOfEdges ());

		e12.delete ();
		assertEquals (0, p.getNumberOfEdges ());

		// Mehrere testen
		// verbunden 1
		p = new PlanPolygon<Edge> (Edge.class);
		e12 = new Edge (pp1, pp2, p);
		e23 = new Edge (pp2, pp3, p);
		Edge e34 = new Edge (pp3, pp4, p);
		Edge e41 = new Edge (pp4, pp1, p);

		assertEquals (true, p.isClosed ());

		e34.delete ();
		assertEquals (new PlanPoint (10, 20), p.getStart ());
		assertEquals (new PlanPoint (20, 20), p.getEnd ());
		assertEquals (3, p.getNumberOfEdges ());
		assertEquals (false, p.isClosed ());

		// verbunden 2
		e34 = new Edge (pp3, pp4, p);
		assertEquals (true, p.isClosed ());

		e41.delete ();
		assertEquals (new PlanPoint (10, 10), p.getStart ());
		assertEquals (new PlanPoint (10, 20), p.getEnd ());
		assertEquals (3, p.getNumberOfEdges ());

		// Teste entfernen aus der mitte
		p = new PlanPolygon<Edge> (Edge.class);
		e12 = new Edge (pp1, pp2, p);
		e23 = new Edge (pp2, pp3, p);
		e41 = new Edge (pp4, pp1, p);

		try {
			e12.delete ();
			fail ("No exception!");
		} catch (Exception e) {
		// alles OK
		}

		// Create closed polygon and test removal
		p = new PlanPolygon<Edge> (Edge.class);
		e12 = new Edge (pp1, pp2, p);
		e23 = new Edge (pp2, pp3, p);
		e34 = new Edge (pp3, pp4, p);
		e41 = new Edge (pp4, pp1, p);

		e12.delete ();
		assertEquals (new PlanPoint (20, 10), p.getStart ());
		assertEquals (new PlanPoint (10, 10), p.getEnd ());
		assertEquals (3, p.getNumberOfEdges ());
		assertTrue (p.getEdges ().contains (e41));
		assertTrue (p.getEdges ().contains (e23));
		assertTrue (p.getEdges ().contains (e34));
		assertFalse (p.getEdges ().contains (e12));
	}

	public void testSimpleSplitting () throws Exception {
		// Split a simple rectangular polygon
		PlanPoint p1 = new PlanPoint (0, 0);
		PlanPoint p2 = new PlanPoint (10, 0);
		PlanPoint p3 = new PlanPoint (20, 0);
		PlanPoint p4 = new PlanPoint (30, 0);
		PlanPoint p5 = new PlanPoint (30, 10);
		PlanPoint p6 = new PlanPoint (20, 10);
		PlanPoint p7 = new PlanPoint (10, 10);
		PlanPoint p8 = new PlanPoint (0, 10);

		PlanPolygon<Edge> toSplit = new PlanPolygon<Edge> (Edge.class);
		new Edge (p1, p2, toSplit);
		Edge e1 = new Edge (p2, p3, toSplit);
		new Edge (p3, p4, toSplit);
		new Edge (p4, p5, toSplit);
		new Edge (p5, p6, toSplit);
		Edge e2 = new Edge (p6, p7, toSplit);
		new Edge (p7, p8, toSplit);
		new Edge (p8, p1, toSplit);

		PlanPolygon<Edge> experiment_result = toSplit.splitClosedPolygon (e1, e2);

		PlanPolygon<Edge> nominal_result_1 = new PlanPolygon<Edge> (Edge.class);
		new Edge (p7, p8, nominal_result_1);
		new Edge (p8, p1, nominal_result_1);
		new Edge (p1, p2, nominal_result_1);
		new Edge (p2, p7, nominal_result_1);
		PlanPolygon<Edge> nominal_result_2 = new PlanPolygon<Edge> (Edge.class);
		new Edge (p3, p4, nominal_result_2);
		new Edge (p4, p5, nominal_result_2);
		new Edge (p5, p6, nominal_result_2);
		new Edge (p6, p3, nominal_result_2);

		assertEquals ("Error with simple split", toSplit, nominal_result_1);
		assertEquals ("Error with simple split", experiment_result, nominal_result_2);
	}

	public void testComplexSplitting () throws Exception {
		//Split a dumbell-shaped room into two at it's two equal edges
		PlanPoint p1 = new PlanPoint (0, 0);
		PlanPoint p2 = new PlanPoint (10, 0);
		PlanPoint p2_1 = new PlanPoint (10, 5);
		PlanPoint p2_2 = new PlanPoint (20, 5);
		PlanPoint p3 = new PlanPoint (20, 0);
		PlanPoint p4 = new PlanPoint (30, 0);
		PlanPoint p5 = new PlanPoint (30, 10);
		PlanPoint p6 = new PlanPoint (20, 10);
		PlanPoint p7 = new PlanPoint (10, 10);
		PlanPoint p8 = new PlanPoint (0, 10);

		PlanPolygon<Edge> toSplit = new PlanPolygon<Edge> (Edge.class);
		new Edge (p1, p2, toSplit);
		new Edge (p2, p2_1, toSplit);
		Edge e1 = new Edge (p2_1, p2_2, toSplit);
		new Edge (p2_2, p3, toSplit);
		new Edge (p3, p4, toSplit);
		new Edge (p4, p5, toSplit);
		new Edge (p5, p6, toSplit);
		new Edge (p6, p2_2, toSplit);
		Edge e2 = new Edge (p2_2, p2_1, toSplit);
		new Edge (p2_1, p7, toSplit);
		new Edge (p7, p8, toSplit);
		new Edge (p8, p1, toSplit);

		PlanPolygon<Edge> experiment_result = toSplit.splitClosedPolygon (e1, e2);

		PlanPolygon<Edge> nominal_result_1 = new PlanPolygon<Edge> (Edge.class);
		new Edge (p2_1, p7, nominal_result_1);
		new Edge (p7, p8, nominal_result_1);
		new Edge (p8, p1, nominal_result_1);
		new Edge (p1, p2, nominal_result_1);
		new Edge (p2, p2_1, nominal_result_1);
		PlanPolygon<Edge> nominal_result_2 = new PlanPolygon<Edge> (Edge.class);
		new Edge (p2_2, p3, nominal_result_2);
		new Edge (p3, p4, nominal_result_2);
		new Edge (p4, p5, nominal_result_2);
		new Edge (p5, p6, nominal_result_2);
		new Edge (p6, p2_2, nominal_result_2);

		assertEquals ("Error with edge-equal split", toSplit, nominal_result_1);
		assertEquals ("Error with edge-equal split", experiment_result, nominal_result_2);
	}

	public void testMultiplePointsConstructur () throws Exception {
		PlanPoint p1 = new PlanPoint (10, 10);
		PlanPoint p2 = new PlanPoint (20, 10);
		PlanPoint p3 = new PlanPoint (20, 20);
		PlanPoint p4 = new PlanPoint (10, 20);

		ArrayList<PlanPoint> pointList = new ArrayList<PlanPoint> ();
		pointList.add (p1);
		pointList.add (p2);
		pointList.add (p3);
		pointList.add (p4);

		PlanPolygon p = new PlanPolygon<Edge> (Edge.class);
		p.defineByPoints (pointList);
		assertEquals (true, p.isClosed ());

		p.delete (); // danach ist alles null, kann man nicht testen ;)
		p = null;
	}

	public void testMultiplePoints () throws Exception {
		PlanPolygon p = getDefaultInstance (1);

		List<PlanPoint> points = p.getPolygonPoints ();

		java.awt.Polygon drawPolygon = p.getAWTPolygon ();
		PlanPoint testPoint1 = new PlanPoint (15, 15);
		PlanPoint testPoint2 = new PlanPoint (10, 10);
		// Problem hier: polygon wurde mit integer koordinaten aufgebaut, 10.001, ist _nicht_ drinnen (denn = 10001!!!!!!!)
		PlanPoint testPoint3 = new PlanPoint (10.001, 10.001);
		PlanPoint testPoint4 = new PlanPoint (10, 15);
		PlanPoint testPoint5 = new PlanPoint (5, 5);
		PlanPoint testPoint6 = new PlanPoint (15, 9.999);
		//assertEquals( true, drawPolygon.contains( testPoint1.getXInt(), testPoint1.getYInt() ) ); //??? warum geht das nicht?
		assertEquals (true, drawPolygon.contains (testPoint2.getXInt (), testPoint2.getYInt ()));
//    assertEquals( true, drawPolygon.contains( testPoint3.getXInt(), testPoint3.getYInt() ) );
		assertEquals (true, drawPolygon.contains (testPoint4.getXInt (), testPoint4.getYInt ()));
		assertEquals (false, drawPolygon.contains (testPoint5.getXInt (), testPoint5.getYInt ()));
		assertEquals (false, drawPolygon.contains (testPoint6.getXInt (), testPoint6.getYInt ()));

		// Test with the is inside-function
		// this function may use another algorithm than getAWTPolygon
		assertEquals (true, p.contains (testPoint1));
		assertEquals (true, p.contains (testPoint2));
		//assertEquals( true, p.contains( testPoint3 ) );
		assertEquals (true, p.contains (testPoint4));
		assertEquals (false, p.contains (testPoint5));
		assertEquals (false, p.contains (testPoint6));
	}

	public void testArea () throws Exception {
		PlanPolygon test = this.getDefaultInstance (1);
		int area = test.area ();

		assertEquals (100, area);
	}

	public void testReplace () throws Exception {
		// Punkte für quadrat
		PlanPoint pp1 = new PlanPoint (10, 10);
		PlanPoint pp2 = new PlanPoint (20, 10);
		PlanPoint pp3 = new PlanPoint (20, 20);
		PlanPoint pp4 = new PlanPoint (10, 20);

		PlanPolygon p = new PlanPolygon<Edge> (Edge.class);
		Edge e12 = new Edge (pp1, pp2, p);
		Edge e14 = new Edge (pp1, pp4, p);
		Edge e23 = new Edge (pp2, pp3, p);
		//Edge e34 = new Edge( pp3, pp4, p );

		PlanPoint newPoint = new PlanPoint (15, 5);
//    Edge add1 = new Edge( pp1, newPoint );
//    Edge add2 = new Edge( newPoint, pp2 );
	}

	public void testEdge () throws Exception {
		PlanPoint pp1 = new PlanPoint (10, 10);
		PlanPoint pp2 = new PlanPoint (20, 10);
		PlanPoint pp3 = new PlanPoint (20, 20);
		PlanPoint pp4 = new PlanPoint (10, 20);
		Edge e12 = new Edge (pp1, pp2);
		Edge e14 = new Edge (pp1, pp4);
		Edge e23 = new Edge (pp2, pp3);
		Edge e34 = new Edge (pp3, pp4);
		Edge e13 = new Edge (pp1, pp3);
		assertEquals (e12.length (), Edge.length (pp1, pp2));
		assertEquals (e14.length (), Edge.length (pp1, pp4));
		assertEquals (e23.length (), Edge.length (pp2, pp3));
		assertEquals (e34.length (), Edge.length (pp3, pp4));
		assertEquals (e13.length (), Edge.length (pp1, pp3));
		assertEquals (e12.length (), Edge.length (pp2, pp1));
		assertEquals (e14.length (), Edge.length (pp4, pp1));
		assertEquals (e23.length (), Edge.length (pp3, pp2));
		assertEquals (e34.length (), Edge.length (pp4, pp3));
		assertEquals (e13.length (), Edge.length (pp3, pp1));
	}

	public void testMethodsForRasterization () throws Exception {
		ArrayList<PlanPoint> points = new ArrayList<PlanPoint> ();
		points.add (new PlanPoint (00, 10));
		points.add (new PlanPoint (10, 10));
		points.add (new PlanPoint (20, 10));
		points.add (new PlanPoint (20, 00));
		points.add (new PlanPoint (10, 00));
		points.add (new PlanPoint (00, 00));
		PlanPoint p1 = new PlanPoint (00, 10);
		PlanPoint p2 = new PlanPoint (10, 10);
		PlanPoint p3 = new PlanPoint (20, 10);
		PlanPoint p4 = new PlanPoint (20, 00);
		PlanPoint p5 = new PlanPoint (10, 00);
		PlanPoint p6 = new PlanPoint (00, 00);

		PlanPolygon<Edge> p = new PlanPolygon<Edge> (Edge.class);
		p.defineByPoints (points);

		Edge e = p.getEdge (p2, p3);
		assertEquals (p5, p.getPointAfterTheNext (e, e.getPoint (p3)));
		assertEquals (p6, p.getPointAfterTheNext (e, e.getPoint (p2)));

		// Test replaceEdge()
		p = getDefaultInstance (1);
		p.close ();

		System.out.println ("Polygon initial: " + p.toString ());

		// Test normal combine
		points = new ArrayList<PlanPoint> ();
		p1 = new PlanPoint (10, 20);
		p2 = new PlanPoint (15, 25);
		p3 = new PlanPoint (20, 20);
		points.add (p1);
		points.add (p2);
		points.add (p3);
		p.replaceEdge (p.getEdge (p1, p3), points);
		assertTrue ("Polygon not closed after edge replace!", p.isClosed ());
		assertEquals ("Edge count is not correct after edge replace!", p.getNumberOfEdges (), 5);
		System.out.println ("Polygon after replace: " + p.toString ());

		// Combine two edges
		p1 = new PlanPoint (20, 10);
		p2 = new PlanPoint (20, 20);
		p3 = new PlanPoint (15, 25);
		p.combineEdges (p1, p2, p3, true);
		assertTrue ("Polygon not closed after combining!", p.isClosed ());
		assertEquals ("Edge count is not correct after combining!", p.getNumberOfEdges (), 4);
		System.out.println ("Polygon after first combine: " + p.toString ());

		// Test the combine of first and last edge case
		p.combineEdges (p.getStart ().getNextEdge (), p.getStart ().getPreviousEdge (), true);
		assertTrue ("Polygon not closed after combining!", p.isClosed ());
		assertEquals ("Edge count is not correct after combining!", p.getNumberOfEdges (), 3);
		System.out.println ("Polygon after second combine: " + p.toString ());

		// Test combining a thorn / zipfel
		p = getDefaultInstance (1);
		Edge e1 = new Edge (new PlanPoint (20, 20), new PlanPoint (20, 30), p);
		Edge e2 = new Edge (new PlanPoint (20, 30), new PlanPoint (20, 20), p);
		new Edge (new PlanPoint (20, 20), new PlanPoint (10, 20), p);
		System.out.println ("Polygon initial: " + p.toString ());
		p.combineEdges (e1, e2, true);
		assertTrue ("Polygon not closed after combining!", p.isClosed ());
		assertEquals ("Edge count is not correct after combining!", p.getNumberOfEdges (), 4);
		System.out.println ("Polygon after zipfel combine: " + p.toString ());
	}
}
