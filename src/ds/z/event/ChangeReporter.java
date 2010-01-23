///* zet evacuation tool copyright (c) 2007-09 zet evacuation team
// *
// * This program is free software; you can redistribute it and/or
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
// */
///*
// * ChangeReporter.java
// * Created on 3. Dezember 2007, 15:02
// */
//package ds.z.event;
//
///**
// * An interface that defines the methods necessary for classes who intend to
// * report changes that are imposed on them to some Listeners. Those must previously
// * have been registered at the <code>ChangeReporter</code>.
// *
// * @author Timon Kelter
// */
//public interface ChangeReporter {
//	/** Calls <code>stateChanged(e)</code> at all registered listeners and sends
//	 * a specified event.
//	 * @param e the event
//	 */
//	void throwChangeEvent( ChangeEvent e );
//
//	/**
//	 * Adds a {@link ChangeListener} to this reporter. The Listener is only
//	 * added, if he hasn't been registered previously as no Listener sall have the
//	 * possibility to register twice at the same <code>ChangeReporter</change>.
//	 * @param c the added listener
//	 */
//	void addChangeListener( ChangeListener c );
//
//	/**
//	 * Removes a specified {@link ChangeListener} (de-registers).
//	 * @param c the change listener
//	 */
//	void removeChangeListener( ChangeListener c );
//}
