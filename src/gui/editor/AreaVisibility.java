/*
 * AreaVisibility.java
 *
 * Created on 11. Dezember 2007, 21:56
 */

package gui.editor;

/**
 * This enumeration is used to mark what kinds of areas shall be displayed 
 * on the screen. The user may want some kinds of areas to be hidden if he is
 * not interested in them for some reason.
 *
 * @author Timon Kelter
 */
public enum AreaVisibility { 
	DELAY, STAIR, INACCESSIBLE, SAVE, EVACUATION, ASSIGNMENT
}