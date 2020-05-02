/**
 * Enum.java
 * Created: 28.07.2011, 15:34:22
 */
package de.tu_berlin.math.coga.zet.converter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
enum RoomRasterProperty {
	/* RasterSquare is accessible */
	ACCESSIBLE,
	/* RasterSquare is a stair */
	STAIR,
	/* RasterSquare belongs to a save area*/
	SAVE,
	/* RasterSquare belongs to an evacuation area*/
	EXIT,
	/* RasterSquare is a teleport cell. */
	TELEPORT;
}
