/**
 * Class CullingLocation
 * Erstellt 02.05.2008, 17:56:36
 */

package opengl.helper;

/**
 * This enumeration describes the possible types of relation between a
 * {@link Frustum} and an object.
 * @author Kapman
 */
public enum CullingLocation {
    outside,
		inside,
		intersect;
}
