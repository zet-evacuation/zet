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
/*
 * ProcessUpdateMessage.java
 * Created on 27.01.2008, 02:36:44
 */

package batch.tasks;

/**
 * 
 * @author Jan-Philipp Kappmeier
 */
public class ProcessUpdateMessage {
		public int progress = 0;
		public String taskName = "";
		public String taskProgressInformation = "";
		public String taskDetailedProgressInformation = "";
		
		public ProcessUpdateMessage( int progress, String taskName, String taskProgressInformation, String taskDetailedProgressInformation ) {
			this.progress = progress;
			this.taskName = taskName;
			this.taskProgressInformation = taskProgressInformation;
			this.taskDetailedProgressInformation = taskDetailedProgressInformation;
		}
}