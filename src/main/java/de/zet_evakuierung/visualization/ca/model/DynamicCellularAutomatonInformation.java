/* zet evacuation tool copyright © 2007-21 zet evacuation team
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
package de.zet_evakuierung.visualization.ca.model;

/**
 * Collection of enumerations for various information that can be displayed in the cellular automaton visualization.
 *
 * @author Jan-Philipp Kappmeier
 */
public class DynamicCellularAutomatonInformation {

    /**
     * Different types of information which can be illustrated by different colors of the heads of the individuals.
     */
    public enum HeadInformation {
        /**
         * Shows default individual.
         */
        NOTHING,
        /**
         * Shows panic at the head.
         */
        PANIC,
        /**
         * Shows speed at the head.
         */
        SPEED,
        /**
         * Shows exhaustion at the head.
         */
        EXHAUSTION,
        /**
         * Shows the alarm-status at the head.
         */
        ALARMED,
        /**
         * Shows the chosen exit at the head.
         */
        CHOSEN_EXIT,
        /**
         * The rest of the reaction time.
         */
        REACTION_TIME;
    }

    /**
     * Describes the different types of information which can be illustrated by different colors of the cells of the
     * cellular automaton.
     */
    public enum CellInformationDisplay {

        /**
         * Disables displaying any potential on the floor of cells.
         */
        NO_POTENTIAL,
        /**
         * Enables displaying of static potential on the floor of cells.
         */
        STATIC_POTENTIAL,
        /**
         * Enables displaying of the dynamic potential on the floor of cells.
         */
        DYNAMIC_POTENTIAL,
        /**
         * Enables displaying usage statistic on the cells.
         */
        UTILIZATION,
        /**
         * Shows waiting times on cells.
         */
        WAITING;
    }
}
