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
package zet.gui.main.tabs.editor;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import java.awt.Color;
import java.util.LinkedList;
import de.tu_berlin.math.coga.common.localization.Localization;
import gui.GUIOptionManager;
import zet.gui.GUILocalization;

/**
 * An enumeration of all possible edit modes of the editor.
 * @author Jan-Philipp Kappmeier, Timon Kelter
 */
public enum EditMode {
	Selection( "Selection", Type.Selection, null, false ),
	RoomCreation( "RoomCreation", Type.CreationRectangled, GUIOptionManager.getRoomEdgeColor(), false ),
	InaccessibleAreaCreation( "InaccessibleAreaCreation", Type.CreationRectangled, GUIOptionManager.getInaccessibleAreaColor(), true ),
	AssignmentAreaCreation( "AssignmentAreaCreation", Type.CreationRectangled, GUIOptionManager.getAssignmentAreaColor(), true ),
	DelayAreaCreation( "DelayAreaCreation", Type.CreationRectangled, GUIOptionManager.getDelayAreaColor(), true ),
	StairAreaCreation( "CreateStairArea", Type.CreationRectangled, GUIOptionManager.getStairAreaColor(), true ),
	StairAreaMarkLowerLevel( "StairAreaMarkLowerLevel", Type.EditExisting, GUIOptionManager.getStairAreaColor(), true ),
	StairAreaMarkUpperLevel( "StairAreaMarkUpperLevel", Type.EditExisting, GUIOptionManager.getStairAreaColor(), true ),
	SaveAreaCreation( "SaveAreaCreation", Type.CreationRectangled, GUIOptionManager.getSaveAreaColor(), true ),
	EvacuationAreaCreation( "EvacuationAreaAreaCreation", Type.CreationRectangled, GUIOptionManager.getEvacuationAreaColor(), true ),
	RoomCreationPointwise( "RoomCreationPointwise", Type.CreationPointwise, GUIOptionManager.getRoomEdgeColor(), false ),
	BarrierCreationPointwise( "BarrierCreationPointwise", Type.CreationPointwise, GUIOptionManager.getRoomEdgeColor(), true ),
	InaccessibleAreaCreationPointwise( "InaccessibleAreaCreationPointwise", Type.CreationPointwise, GUIOptionManager.getInaccessibleAreaColor(), true ),
	AssignmentAreaCreationPointwise( "AssignmentAreaCreationPointwise", Type.CreationPointwise, GUIOptionManager.getAssignmentAreaColor(), true ),
	DelayAreaCreationPointwise( "DelayAreaCreationPointwise", Type.CreationPointwise, GUIOptionManager.getDelayAreaColor(), true ),
	StairAreaCreationPointwise( "CreateStairAreaPointwise", Type.CreationPointwise, GUIOptionManager.getStairAreaColor(), true ),
	SaveAreaCreationPointwise( "SaveAreaCreationPointwise", Type.CreationPointwise, GUIOptionManager.getSaveAreaColor(), true ),
	EvacuationAreaCreationPointwise( "EvacuationAreaCreationPointwise", Type.CreationPointwise, GUIOptionManager.getEvacuationAreaColor(), true ),
	TeleportEdgeCreation( "TeleportEdgeCreation", Type.EditExisting, GUIOptionManager.getRoomEdgeColor(), false ),
	PassableRoomCreation( "CreatePassageRoom", Type.EditExisting, GUIOptionManager.getRoomEdgeColor(), false ),
	TeleportAreaCreationPointwise( "CreateTeleportArea", Type.CreationPointwise, GUIOptionManager.getTeleportAreaColor(), true ),
	TeleportAreaCreation( "CreateTeleportAreaPointwise", Type.CreationRectangled, GUIOptionManager.getTeleportAreaColor(), true );

	static {
		// Here the partnerModes set. This cannot be done in the constructor because it would need
		// illegal forward references
		setPartners( RoomCreation, RoomCreationPointwise );
		setPartners( InaccessibleAreaCreation, InaccessibleAreaCreationPointwise );
		setPartners( AssignmentAreaCreation, AssignmentAreaCreationPointwise );
		setPartners( DelayAreaCreation, DelayAreaCreationPointwise );
		setPartners( StairAreaCreation, StairAreaCreationPointwise );
		setPartners( SaveAreaCreation, SaveAreaCreationPointwise );
		setPartners( EvacuationAreaCreation, EvacuationAreaCreationPointwise );
		setPartners( TeleportAreaCreation, TeleportAreaCreationPointwise );
	}

	private static void setPartners( EditMode e1, EditMode e2 ) {
		e1.partnerMode = e2;
		e2.partnerMode = e1;
	}
	//private String name;
//	private int ID;
	private String key;
	private Type editType;
	private Color editorColor;
	private boolean createSubpolygons;
	private LinkedList payload;
	private EditMode partnerMode;

	EditMode( String key, Type editType, Color editorColor, boolean createSubpolygons ) {
		this.key = "gui.editor.EditMode." + key;
//		this.ID = ID;
		this.editType = editType;
		this.editorColor = editorColor;
		this.createSubpolygons = createSubpolygons;
		this.payload = new LinkedList();
		this.partnerMode = null;
	}

	/**
	 * Returns all EditModes in which new polygons can be created
	 * @return list of edit modes
	 */
	public static LinkedList<EditMode> getCreationModes() {
		LinkedList<EditMode> trueEditModes = new LinkedList<EditMode>();

		for( EditMode e : values() )
			if( e.getType().equals( Type.CreationPointwise ) || e.getType().equals( Type.CreationRectangled ) )
				trueEditModes.add( e );
		return trueEditModes;
	}

	/**
	 * Returns all EditModes of a specified type
	 * @param editType the creation type
	 * @return list of edit modes
	 */
	public static LinkedList<EditMode> getCreationModes( Type editType ) {
		LinkedList<EditMode> trueEditModes = new LinkedList<EditMode>();

		for( EditMode e : values() )
			if( e.getType().equals( editType ) )
				trueEditModes.add( e );
		return trueEditModes;
	}

	@Override
	/** @returns a reasonable description of the edit mode. */
	public String toString() {
		return getName();
	}

	/** 
	 * @return a reasonable description of the edit mode. */
	public String getName() {
		return GUILocalization.getSingleton().getString( key );
	}

	/** 
	 * @return The type of edits actions that are performed in this edit mode. */
	public Type getType() {
		return editType;
	}

	/** @return The color that the editor shall use to paint helper edges when using this edit mode. */
	public Color getEditorColor() {
		return editorColor;
	}

	/** @return The partner mode of a creation mode (PointWise Creation <-> Rectangled Creation)
	 *  or null if the current mode is not a polygon creation mode.
	 */
	public EditMode getPartnerMode() {
		return partnerMode;
	}

	/** @return whether this edit mode creates polygons that are part of 
	 * other polygons (f.e. Areas). This field is always false for edit modes
	 * that are not of type CreationPointwise or CreationRectangled */
	public boolean doesCreateSubpolygons() {
		return createSubpolygons;
	}

	/**
	 * Returns a list of further objects that are associated with the edit mode.
	 * The returned list is fully editable. For example in TeleportEdgeCreation 
	 * mode, this list is used to store the first edge which must be connected
	 * to the second one.
	 * @return the objects
	 */
	public LinkedList getPayload() {
		return payload;
	}

	/**
	 * The types of EditModes.
	 */
	public enum Type {
		/** Any edit mode that is focused on selection of polygons on screen. */
		Selection,
		/** Any edit mode that is used to create polygons on screen in rectangle form. */
		CreationRectangled,
		/** Any edit mode that is used to create polygons on screen in free form. */
		CreationPointwise,
		/** Any edit mode that is used to modify existing polygons. */
		EditExisting;
	}
}
