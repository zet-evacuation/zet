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
 * Created on 01.05.2008
 *
 */
package ds.ca.results;

/**
 * @author Daniel Pluempe
 *
 */
public class InconsistentPlaybackStateException extends Exception {

    public InconsistentPlaybackStateException(){
        this("The recorded state is inconsistent.");
    }
    
    public InconsistentPlaybackStateException(String message){
        super(message);
    }
    
    public InconsistentPlaybackStateException(int timestamp, Action action, String message){
        super("There was an error replaying the following action in timestep " 
                + timestamp +
                ": " 
                + action + ": " + message);
    }
}
