/* zet evacuation tool copyright © 2007-15 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA	02110-1301, USA.
 */
package de.tu_berlin.math.coga.batch.input.reader;

import java.util.HashMap;


/**
 * A general reader for Dimacs type files that start with a character indicating line type.
 * @author Jan-Philipp Kappmeier
 */
public abstract class DimacsReader<T> extends LineBasedReader<T> {
  private final HashMap<Character,DimacsLineOperation> characterOperationMap;

  public final static DimacsLineOperation commentLine = new DimacsLineOperation() {
    @Override
    public void parseLine( String[] line ) {
      // do nothing
    }
  };

  public DimacsReader() {
    characterOperationMap = new HashMap<>();
    characterOperationMap.put( 'c', commentLine );
  }

  @Override
  protected void parseLine( String line ) {
    if( characterOperationMap.containsKey( line.charAt( 0 ) ) ) {
      String[] tokens = line.substring( 2 ).trim().split("\\s+");
      characterOperationMap.get( line.charAt( 0 ) ).parseLine( tokens );
    }
  }

  @Override
  protected void phaseComplete() {
    characterOperationMap.clear();
    characterOperationMap.put( 'c', commentLine );
  }

  protected void registerLineOperation( char c, DimacsLineOperation op ) {
    characterOperationMap.put( c, op );
  }

  protected void unregisterLineOperation( char c ) {
    characterOperationMap.remove( c );
  }
}
