/*
 * QualityPreset.java
 * Created 11.09.2009, 19:06:37
 */

package gui.visualization;

import localization.Localization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public enum QualityPreset {
		HighQuality( 0.6d, Localization.getInstance ().getString ("ds.z.DelayArea.DelayType.OBSTACLE.description") ),
		MediumQuality( 0.6d, Localization.getInstance ().getString ("ds.z.DelayArea.DelayType.OBSTACLE.description") ),
		LowQuality( 1.0d, Localization.getInstance ().getString ("ds.z.DelayArea.DelayType.OTHER.description") );

		QualityPreset( double defaultSpeedFactor, String description ) {
			this.defaultSpeedFactor = defaultSpeedFactor;
			this.description = description;
		}

		public final double defaultSpeedFactor;
		public final String description;
}
