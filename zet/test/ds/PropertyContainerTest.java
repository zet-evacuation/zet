/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ds;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Martin
 */
public class PropertyContainerTest {

    public PropertyContainerTest() {
    }

    @Test
    public void getInstance() {
        PropertyContainer properties = PropertyContainer.getInstance();
        properties.define("message", String.class, "Hello World");
        properties.define("value", Integer.class, 42);
        System.out.println(properties.get("message"));
        System.out.println(properties.get("value"));
        System.out.println(properties.getAsString("message"));
        System.out.println(properties.getAsInt("value"));
    }

}
