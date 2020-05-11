/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
package gui;

import de.zet_evakuierung.model.AreaType;
import ds.PropertyContainer;
import java.awt.Color;
import java.awt.Font;
import java.util.EnumSet;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Stores options concerning the graphical representation of projects
 * in the editor. In the future these options could also be user-editable
 * and be made persistent by storing them in a separate options file.
 *
 * @author Jan-Philipp Kappmeier
 * @author Timon Kelter
 */
public abstract class GUIOptionManager {
    /** The instance of the PropertyContainer. */
    private static final PropertyContainer propertyContainer = PropertyContainer.getGlobal();
    /** Global initialization of the GUIOptionManager. */

    static {
        // stores the needed values to the property container, as saving is not supported yet
        propertyContainer.define( "gui.optionManager.RoomNameFont", Font.class, Font.getFont( "Tahoma" ) );
        propertyContainer.define( "gui.optionManager.RoomEdgeColor", Color.class, Color.BLACK );
        propertyContainer.define( "gui.optionManager.TeleportEdgeColor", Color.class, Color.BLUE );
        propertyContainer.define( "gui.optionManager.SaveAreaColor", Color.class, Color.YELLOW );
        propertyContainer.define( "gui.optionManager.EvacuationAreaColor", Color.class, Color.GREEN );
        propertyContainer.define( "gui.optionManager.AssignmentAreaColor", Color.class, Color.decode( "#0066FF" ) );
        // #0066FF = Light Blue, from HTML-Internet colors (orange is hard to distinguish from
        // yellow and blue is an neutral color that suits well with assignments)
        propertyContainer.define( "gui.optionManager.InaccessibleAreaColor", Color.class, Color.BLACK );
        propertyContainer.define( "gui.optionManager.DelayAreaColor", Color.class, Color.RED );
        propertyContainer.define( "gui.optionManager.StairAreaColor", Color.class, Color.ORANGE );
        propertyContainer.define( "gui.optionManager.DragNodeColor", Color.class, Color.BLACK );
        propertyContainer.define( "gui.optionManager.EditorBackgroundColor", Color.class, Color.WHITE );
        propertyContainer.define( "gui.optionManager.EditorRasterColor", Color.class, Color.LIGHT_GRAY );
        propertyContainer.define( "gui.optionManager.AssignmentAreaVisibility", Boolean.class, true );
        propertyContainer.define( "gui.optionManager.DelayAreaVisibility", Boolean.class, true );
        propertyContainer.define( "gui.optionManager.StairAreaVisibility", Boolean.class, true );
        propertyContainer.define( "gui.optionManager.EvacuationAreaVisibility", Boolean.class, true );
        propertyContainer.define( "gui.optionManager.InaccessibleAreaVisibility", Boolean.class, true );
        propertyContainer.define( "gui.optionManager.SaveAreaVisibility", Boolean.class, true );
        propertyContainer.define( "gui.optionManager.SavePath", String.class, "" );

    // Added for FlowVisualizationTool
    //propertyContainer.define( "options.visualization.appeareance.colors.backgroundColor", Color.class, Color.WHITE );
    //propertyContainer.define( "options.visualization.appearance.floorDistance", Double.class, 100. );
    }

    /** Not instantiable class.  */
    private GUIOptionManager() { }

    public static Font getRoomNameFont() {
        return propertyContainer.getAsFont( "gui.optionManager.RoomNameFont" );
    }

    public static Color getRoomEdgeColor() {
        return propertyContainer.getAsColor( "gui.optionManager.RoomEdgeColor" );
    }

    public static Color getTeleportEdgeColor() {
        return propertyContainer.getAsColor( "gui.optionManager.TeleportEdgeColor" );
    }

    public static Color getSaveAreaColor() {
        return propertyContainer.getAsColor( "gui.optionManager.SaveAreaColor" );
    }

    public static Color getEvacuationAreaColor() {
        return propertyContainer.getAsColor( "gui.optionManager.EvacuationAreaColor" );
    }

    public static Color getAssignmentAreaColor() {
        return propertyContainer.getAsColor( "gui.optionManager.AssignmentAreaColor" );
    }

    public static Color getInaccessibleAreaColor() {
        return propertyContainer.getAsColor( "gui.optionManager.InaccessibleAreaColor" );
    }

    public static Color getDelayAreaColor() {
        return propertyContainer.getAsColor( "gui.optionManager.DelayAreaColor" );
    }

    public static Color getStairAreaColor() {
        return propertyContainer.getAsColor( "gui.optionManager.StairAreaColor" );
    }

    public static Color getTeleportAreaColor() {
        return propertyContainer.getAsColor( "gui.optionManager.SaveAreaColor" );
    }

    public static Color getDragNodeColor() {
        return propertyContainer.getAsColor( "gui.optionManager.DragNodeColor" );
    }

    public static Color getEditorBackgroundColor() {
        return propertyContainer.getAsColor( "gui.optionManager.EditorBackgroundColor" );
    }

    public static Color getEditorRasterColor() {
        return propertyContainer.getAsColor( "gui.optionManager.EditorRasterColor" );
    }

    public static void changeLookAndFeel( String lookAndFeelClass ) {
        try {
            UIManager.setLookAndFeel( lookAndFeelClass );
        } catch( Exception e ) {
            JOptionPane.showMessageDialog( null, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    public static EnumSet<AreaType> getAreaVisibility() {
        /** The currently selected visible areas. */
        EnumSet<AreaType> areaVisibility = EnumSet.noneOf( AreaType.class );
        if( propertyContainer.getAsBoolean( "gui.optionManager.AssignmentAreaVisibility" ) )
            areaVisibility.add( AreaType.Assignment );
        if( propertyContainer.getAsBoolean( "gui.optionManager.DelayAreaVisibility" ) )
            areaVisibility.add( AreaType.Delay );
        if( propertyContainer.getAsBoolean( "gui.optionManager.EvacuationAreaVisibility" ) )
            areaVisibility.add( AreaType.Evacuation );
        if( propertyContainer.getAsBoolean( "gui.optionManager.InaccessibleAreaVisibility" ) )
            areaVisibility.add( AreaType.Inaccessible );
        if( propertyContainer.getAsBoolean( "gui.optionManager.SaveAreaVisibility" ) )
            areaVisibility.add( AreaType.Save );
        if( propertyContainer.getAsBoolean( "gui.optionManager.StairAreaVisibility" ) )
            areaVisibility.add( AreaType.Stair );
        areaVisibility.add( AreaType.Teleport);
        return areaVisibility;
    }

    public static void setAreaVisibility( EnumSet<AreaType> av ) {
        propertyContainer.set( "gui.optionManager.AssignmentAreaVisibility", av.contains( AreaType.Assignment ) );
        propertyContainer.set( "gui.optionManager.DelayAreaVisibility", av.contains( AreaType.Delay ) );
        propertyContainer.set( "gui.optionManager.EvacuationAreaVisibility", av.contains( AreaType.Evacuation ) );
        propertyContainer.set( "gui.optionManager.InaccessibleAreaVisibility", av.contains( AreaType.Inaccessible ) );
        propertyContainer.set( "gui.optionManager.SaveAreaVisibility", av.contains( AreaType.Save ) );
        propertyContainer.set( "gui.optionManager.StairAreaVisibility", av.contains( AreaType.Stair ) );
    }

    public static String getSavePath() {
        return PropertyContainer.getGlobal().getAsString( "information.directory.lastProject" );
    }

    public static void setSavePath( String path ) {
        PropertyContainer.getGlobal().set( "information.directory.lastProject", path );
    }

    public static String getImportPath() {
        return PropertyContainer.getGlobal().getAsString( "information.directory.importProject" );
    }

    public static void setImportPath( String importPath ) {
        PropertyContainer.getGlobal().set( "information.directory.importProject", importPath );
    }

    public static String getSavePathResults() {
        return PropertyContainer.getGlobal().getAsString( "information.directory.lastResult" );
    }

    public static void setSavePathResults( String resultsPath ) {
        PropertyContainer.getGlobal().set( "information.directory.lastResult", resultsPath );
    }

    public static String getBuildingPlanPath() {
        return PropertyContainer.getGlobal().getAsString( "information.directory.lastPlan" );
    }

    public static void setBuildingPlanPath( String planPath ) {
        PropertyContainer.getGlobal().set( "information.directory.lastPlan", planPath );
    }

    public static void setLastFile( int i, String filename ) {
        PropertyContainer.getGlobal().set( "information.file.lastFile" + Integer.toString( i ), filename );
    }

    public static String getLastFile( int i ) {
        return PropertyContainer.getGlobal().getAsString( "information.file.lastFile" + Integer.toString( i ) );
    }

    public static String getDoorTemplateFile() {
        return "./templates/door_default.xml";
    }
    public static String getExitDoorTemplateFile() {
        return "./templates/exitdoor_default.xml";
    }
}