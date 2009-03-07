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
