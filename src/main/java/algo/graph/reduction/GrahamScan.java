/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
package algo.graph.reduction;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

/**
 *
 * @author Marlen Schwengfelder
 */
public class GrahamScan {

    public static ArrayList<Point> grahamScan(ArrayList<Point> polygon) {
        int min = 0;
        int length = polygon.size();
        for (int i = 1; i < length; i++) {
            Point get = polygon.get(i);
            if (get.y < polygon.get(min).y) {
                min = i;
            } else {
                if (get.y == polygon.get(min).y) {
                    if (get.x < polygon.get(min).x) {
                        min = i;
                    }
                }
            }
        }
        final Point pivot = polygon.get(min);
        ArrayList<Point> sorted = (ArrayList<Point>) polygon.clone();
        Collections.sort(sorted, new Comparator<Point>() {

            public int compare(Point o1, Point o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                if (angle_cmp(pivot, o1, o2)) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        sorted.add(0, pivot);
        Stack<Point> stack = new Stack<Point>();
        stack.push(sorted.get(length - 1));
        stack.push(pivot);
        int i = 1;
        while (i < length) {
            Point pt1 = stack.pop();
            Point pt2 = stack.peek();
            stack.push(pt1);
            if (isLeftTurn(pt1, pt2, sorted.get(i))) {
                stack.push(sorted.get(i));
                i++;
            } else {
                stack.pop();
            }
        }
        ArrayList<Point> convex = new ArrayList<Point>();
        while (!stack.isEmpty()) {
            convex.add(stack.pop());
        }
        convex.remove(convex.size() - 1);
        return convex;
    }

    private static int distance(Point a, Point b) {
        int dx = a.x - b.x, dy = a.y - b.y;
        return dx * dx + dy * dy;
    }

    private static int area(Point a, Point b, Point c) {
        return a.x * b.y - a.y * b.x + b.x * c.y - b.y * c.x + c.x * a.y - c.y * a.y;
    }

    private static boolean angle_cmp(Point pivot, Point a, Point b) {
        if (area(pivot, a, b) == 0) {
            return distance(pivot, a) < distance(pivot, b);
        }
        int d1x = a.x - pivot.x, d1y = a.y - pivot.y;
        int d2x = b.x - pivot.x, d2y = b.y - pivot.y;
        return (Math.atan2((double) d1y, (double) d1x) - Math.atan2((double) d2y, (double) d2x)) < 0;
    }

    private static int turnTest(Point p, Point q, Point r) {
        int result = (r.x - q.x) * (p.y - q.y) - (r.y - q.y) * (p.x - q.x);
        if (result < 0) {
            return -1;
        }
        if (result > 0) {
            return 1;
        }
        return 0;
    }

    private static boolean isLeftTurn(Point p, Point q, Point r) {
        return turnTest(p, q, r) > 0;
    }
}

