package gui.editor;

import java.awt.Color;
import java.util.LinkedList;
import localization.Localization;

/**
 * An enumeration of all possible edit modes of the editor.
 * @author Jan-Philipp Kappmeier, Timon Kelter
 */
public enum EditMode {

	Selection( 1, Type.SELECTION, null, false ),
	RoomCreation( 2, Type.CREATION_RECTANGLED, GUIOptionManager.getRoomEdgeColor(), false ),
	InaccessibleAreaCreation( 3, Type.CREATION_RECTANGLED, GUIOptionManager.getInaccessibleAreaColor(), true ),
	AssignmentAreaCreation( 4, Type.CREATION_RECTANGLED, GUIOptionManager.getAssignmentAreaColor(), true ),
	DelayAreaCreation( 5, Type.CREATION_RECTANGLED, GUIOptionManager.getDelayAreaColor(), true ),
	StairAreaCreation( 17, Type.CREATION_RECTANGLED, GUIOptionManager.getStairAreaColor(), true ),
	StairAreaMarkLowerLevel( 19, Type.EDIT_EXISTING, GUIOptionManager.getStairAreaColor(), true ),
	StairAreaMarkUpperLevel( 20, Type.EDIT_EXISTING, GUIOptionManager.getStairAreaColor(), true ),
	SaveAreaCreation( 6, Type.CREATION_RECTANGLED, GUIOptionManager.getSaveAreaColor(), true ),
	EvacuationAreaCreation( 7, Type.CREATION_RECTANGLED, GUIOptionManager.getEvacuationAreaColor(), true ),
	RoomCreationPointwise( 8, Type.CREATION_POINTWISE, GUIOptionManager.getRoomEdgeColor(), false ),
	BarrierCreationPointwise( 9, Type.CREATION_POINTWISE, GUIOptionManager.getRoomEdgeColor(), true ),
	InaccessibleAreaCreationPointwise( 10, Type.CREATION_POINTWISE, GUIOptionManager.getInaccessibleAreaColor(), true ),
	AssignmentAreaCreationPointwise( 11, Type.CREATION_POINTWISE, GUIOptionManager.getAssignmentAreaColor(), true ),
	DelayAreaCreationPointwise( 12, Type.CREATION_POINTWISE, GUIOptionManager.getDelayAreaColor(), true ),
	StairAreaCreationPointwise( 18, Type.CREATION_POINTWISE, GUIOptionManager.getStairAreaColor(), true ),
	SaveAreaCreationPointwise( 13, Type.CREATION_POINTWISE, GUIOptionManager.getSaveAreaColor(), true ),
	EvacuationAreaCreationPointwise( 14, Type.CREATION_POINTWISE, GUIOptionManager.getEvacuationAreaColor(), true ),
	TeleportEdgeCreation( 15, Type.EDIT_EXISTING, GUIOptionManager.getRoomEdgeColor(), false ),
	PassableRoomCreation( 16, Type.EDIT_EXISTING, GUIOptionManager.getRoomEdgeColor(), false );
		/** The localization class. */
	Localization loc = Localization.getInstance();
	
	static {
		// Here the partnerModes set. This cannot be done in the constructor because it would need
		// illegal forward references
		setPartners (RoomCreation, RoomCreationPointwise);
		setPartners (InaccessibleAreaCreation, InaccessibleAreaCreationPointwise);
		setPartners (AssignmentAreaCreation, AssignmentAreaCreationPointwise);
		setPartners (DelayAreaCreation, DelayAreaCreationPointwise);
		setPartners (StairAreaCreation, StairAreaCreationPointwise);
		setPartners (SaveAreaCreation, SaveAreaCreationPointwise);
		setPartners (EvacuationAreaCreation, EvacuationAreaCreationPointwise);
	}
	private static void setPartners (EditMode e1, EditMode e2) {
		e1.partnerMode = e2;
		e2.partnerMode = e1;
	}
	
	//private String name;
	private int ID;
	private Type editType;
	private Color editorColor;
	private boolean createSubpolygons;
	private LinkedList payload;
	private EditMode partnerMode;
	
	EditMode( Integer ID, Type editType, Color editorColor, boolean createSubpolygons) {
		this.ID = ID;
		this.editType = editType;
		this.editorColor = editorColor;
		this.createSubpolygons = createSubpolygons;
		this.payload = new LinkedList ();
		this.partnerMode = null;
	}

	/** Returns all EditModes in which new polygons can be created
	 * @return list of edit modes
	 */
	public static LinkedList<EditMode> getCreationModes() {
		LinkedList<EditMode> trueEditModes = new LinkedList<EditMode>();

		for( EditMode e : values() ) {
			if( e.getType().equals( Type.CREATION_POINTWISE ) ||
							e.getType().equals( Type.CREATION_RECTANGLED ) ) {
				trueEditModes.add( e );
			}
		}
		return trueEditModes;
	}

	/** Returns all EditModes of a specified type
	 * @param editType the creation type
	 * @return list of edit modes
	 */
	public static LinkedList<EditMode> getCreationModes( Type editType ) {
		LinkedList<EditMode> trueEditModes = new LinkedList<EditMode>();

		for( EditMode e : values() ) {
			if( e.getType().equals( editType ) ) {
				trueEditModes.add( e );
			}
		}
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
		switch( this.ID ) {
			case 1:
				return loc.getString( "gui.editor.EditMode.Selection" );
			case 2:
				return loc.getString( "gui.editor.EditMode.RoomCreation" );
			case 3:
				return loc.getString( "gui.editor.EditMode.InaccessibleAreaCreation" );
			case 4:
				return loc.getString( "gui.editor.EditMode.AssignmentAreaCreation" );
			case 5:
				return loc.getString( "gui.editor.EditMode.DelayAreaCreation" );
			case 6:
				return loc.getString( "gui.editor.EditMode.SaveAreaCreation" );
			case 7:
				return loc.getString( "gui.editor.EditMode.EvacuationAreaAreaCreation" );
			case 8:
				return loc.getString( "gui.editor.EditMode.RoomCreationPointwise" );
			case 9:
				return loc.getString( "gui.editor.EditMode.BarrierCreationPointwise" );
			case 10:
				return loc.getString( "gui.editor.EditMode.InaccessibleAreaCreationPointwise" );
			case 11:
				return loc.getString( "gui.editor.EditMode.AssignmentAreaCreationPointwise" );
			case 12:
				return loc.getString( "gui.editor.EditMode.DelayAreaCreationPointwise" );
			case 13:			
				return loc.getString( "gui.editor.EditMode.SaveAreaCreationPointwise" );
			case 14:			
				return loc.getString( "gui.editor.EditMode.EvacuationAreaCreationPointwise" );
			case 15:			
				return loc.getString( "gui.editor.EditMode.TeleportEdgeCreation" );
			case 16:
				return loc.getString( "gui.editor.EditMode.CreatePassageRoom");
			case 17:
				return loc.getString( "gui.editor.EditMode.CreateStairArea");
			case 18:
				return loc.getString( "gui.editor.EditMode.CreateStairAreaPointwise");
			case 19:
				return loc.getString( "gui.editor.EditMode.StairAreaMarkLowerLevel");
			case 20:
				return loc.getString( "gui.editor.EditMode.StairAreaMarkUpperLevel");
		}
		return loc.getString( "gui.UnknownEditMode" );
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
	public EditMode getPartnerMode () {
		return partnerMode;
	}
	
	/** @return whether this edit mode creates polygons that are part of 
	 * other polygons (f.e. Areas). This field is always false for edit modes
	 * that are not of type CREATION_POINTWISE or CREATION_RECTANGLED */
	public boolean doesCreateSubpolygons() {
		return createSubpolygons;
	}

	/**
	 * Returns a list of further objects that are associated with the edit mode.
	 * The returned list is fully editable. For example in TeleportEdgeCreation 
	 * mode, this list ist used to store the first edge which must be connected
	 * to the second one.
	 * @return the objects
	 */
	public LinkedList getPayload() {
		return payload;
	}
	
	/** The types of EditModes. */
	public enum Type {
		/** Any edit mode that is focused on selection of polygons on screen. */
		SELECTION,
		/** Any edit mode that is used to create polygons on screen in rectangle form. */
		CREATION_RECTANGLED,
		/** Any edit mode that is used to create polygons on screen in free form. */
		CREATION_POINTWISE,
		/** Any edit mode that is used to modify existing polygons. */
		EDIT_EXISTING;
	}
}
