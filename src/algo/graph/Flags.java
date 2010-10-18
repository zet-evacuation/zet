/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Flags.java
 *
 */
package algo.graph;

import java.util.logging.Logger;

/**
 *
 */
public class Flags {

    Logger log;
    public final static boolean ALGO_PROGRESS = true;

    public final static boolean MARTIN = false;
    public final static boolean MEL = false;
    public final static boolean MEL_LONG = MEL && false;
    public final static boolean GORDON = false;   // Switch some more informations as output for maxFlowOverTime on or off.
    public final static boolean TIMON = false;    //****************************************************************************
    // General Debug Flags
    public final static boolean TEMFOT = MARTIN && false;
    public final static boolean PP = MARTIN && true;    // The following two debug flags influence all classes having 'Transshipment' in their names.
    public final static boolean TRANSSHIPMENT_SHORT = false;
    public final static boolean TRANSSHIPMENT_LONG = !TRANSSHIPMENT_SHORT && false;
    public final static boolean TRANSSHIPMENT_RESULT_FLOW = false;
    public final static boolean FLOWWRONG = false;
    //public final static boolean FLOWWRONG_LONG = false;	// WARNING Outputs in GLEdge have been commented out!

    // Debug while calculating upper bound for time horizon in transshipment calculations
    public final static boolean BOUND_ESTIMATOR = false;
    public final static boolean BOUND_ESTIMATOR_LONG = false;
    public final static boolean BOUND_ESTIMATOR_STATIC_FLOW = false;
}
