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
package statistics.collectors;

import org.zetool.common.datastructure.Triple;
import org.zet.cellularautomaton.EvacCell;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SpecificFlowCollector {

	HashMap<Integer, ArrayList<PositionCountTriple>> timeMeasure = new HashMap<>();;

	ArrayList<Integer> personCount;


	int minx = Integer.MAX_VALUE;
	int maxx = Integer.MIN_VALUE;

	private class PositionCountTriple extends Triple<Point, Point, Integer> {
		public PositionCountTriple( Point start, Point end, int count ) {
			super( start, end, count );
		}

		public void inc() {
			w++;
		}

		public void add( int value ) {
			w = w + value;
		}
	}

	public SpecificFlowCollector() {
		personCount = new ArrayList<>();
		for( int i = 0; i < 77; ++i ) { // TODO: hier anstatt 77 die tatsächliche größe des raums einstellen
			personCount.add( 0 );
		}
	}

	public void collect( int timeStep, EvacCell start, EvacCell end ) {
    if( true )
      return;
		try {

			if( start.equals( end ) ) {
				return;	// do not count no-movements
			}

			if( start.getX() == end.getX() + 1 ) {
				personCount.set( start.getX(), personCount.get( start.getX() ) + 1 );
			}
			if( start.getX() == end.getX() - 1 ) {
				personCount.set( end.getX(), personCount.get( end.getX() ) - 1 );
			}

			// ignore moves by more than one cell (teleportations)
			if( Math.abs( start.getX() - end.getX() ) > 1 || Math.abs( start.getY() - end.getY() ) > 1 )  {
				//System.out.println( "Teleport ignored" );
				return;
			}

			int val = 0;
			if( start.getX() == end.getX() + 1 )
				val = 1;
			else if( start.getX() == end.getX() )
				return;
			else if( start.getX() == end.getX() - 1 )
				val = -1;

			// check if some data for the time step is collected
			Point sp = new Point( start.getX(), start.getY() );
			Point ep = new Point( end.getX(), end.getY() );
			minx = Math.min( minx, start.getX() );
			maxx = Math.max( maxx, start.getX() );
			if( timeMeasure.containsKey( timeStep ) ) {
				ArrayList<PositionCountTriple> values = timeMeasure.get( timeStep );

				// search for the given point in the list
				boolean added = false;
				for( PositionCountTriple tripel : values ) {
					if( tripel.u().equals( sp ) && tripel.v().equals( ep ) ) {
						tripel.inc();
						added = true;
						break;
					}
				}
				if( !added ) {
					PositionCountTriple newTripel = new PositionCountTriple( sp, ep, 0 );
					newTripel.add( val );
					values.add( newTripel );
	//				System.out.println( "Triple added. Minx = " + minx + " maxx = " + maxx );
				}
			} else {
				// Nothing for this time step is added yet
				ArrayList<PositionCountTriple> values = new ArrayList<>();
				PositionCountTriple newTripel = new PositionCountTriple( sp, ep, 0 );
				newTripel.add( val );
				values.add( newTripel );
	//				System.out.println( "Triple added. Minx = " + minx + " maxx = " + maxx );
				timeMeasure.put( timeStep, values );
			}

		} catch( Exception ex ) {
			System.err.println( "Error in SpecificFlowCollector" );
		}
	}

	public void execute() {
		//int startIndex = 39;
		int count = 0;
		double sum = 0;
		double sum2 = 0;
		for( int i = minx; i <= maxx; ++i ) {
			count++;
			sum += getSpecificFlow( i );
			sum2 += getSpecificFlow2( i );
		}
		System.out.println( "Counted: " + count );
		// count = maxx - minx + 1
		System.out.println( "Average specific flow: " + (sum/count) );
		System.out.println( "Average specific flow2: " + (sum2/count) );
	}

	private double getSpecificFlow2( int index ) {
		return personCount.get( index )/(wall*time);
	}

	double time = 600;
	double wall = 8;

	private double getSpecificFlow( int index ) {
		int count = 0;
		// count all individuals leaving a cell on x-index 39
		for( int timestep : timeMeasure.keySet() ) {
			for( PositionCountTriple value : timeMeasure.get( timestep ) ) {
				if( value.u().getX() == index && value.v().getX() == index-1 )
					count += value.w();
				if( value.u().getX() ==	index-1 && value.v().getX() == index ) {
					System.out.println( "RÜCKW'ÄRTS GEHENDE FIGUR AUFGETAUCHT!!!!!!!!!!!!!!" );
					//count -= value.w;
				}
			}
		}
		double specificFlow = (double)count/(wall*time);
		System.out.println( "Specific flow for index " + index + ": " + specificFlow );
		return specificFlow;

	}

}
