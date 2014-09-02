/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * QualityPreset.java
 * Created 11.09.2009, 19:06:37
 */

package gui.visualization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public enum QualityPreset {
		VeryHighQuality( 48, 48, IndividualStyle.Cone, 48, 48 ),
		HighQuality( 24, 24, IndividualStyle.Cone, 24, 24 ),
		MediumQuality( 8, 8, IndividualStyle.Cone, 12, 12 ),
		LowQuality( 4, 4, IndividualStyle.Cone, 4, 4 );

		QualityPreset( int individualSlices, int individualStacks, IndividualStyle individualStyle, int nodeSlices, int edgeSlices ) {
			this.individualBodySlices = individualSlices;
			this.individualHeadSlices = individualSlices;
			this.individualBodyStacks = individualStacks/2;
			this.individualHeadStacks = individualStacks;
			this.individualStyle = individualStyle;
			this.nodeSlices = nodeSlices;
			this.nodeStacks = nodeSlices;
			this.edgeSlices = edgeSlices;
		}

		public final IndividualStyle individualStyle;

		public final int individualBodySlices;
		public final int individualBodyStacks;
		public final int individualHeadSlices;
		public final int individualHeadStacks;

		public final int nodeSlices;
		public final int nodeStacks;
		public final int edgeSlices;



	/**
	 * An enumeration of all possible styles of individuals during visualization.
	 * The default style is {@code Cone}.
	 */
	public enum IndividualStyle {
		Cylinder,
		Cone,
		Square,
		Circle
	}
}
