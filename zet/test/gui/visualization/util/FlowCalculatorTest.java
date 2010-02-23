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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package gui.visualization.util;
package gui.visualization.util;

import java.util.*;
import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import gui.visualization.util.*;
import gui.visualization.control.FlowHistroryTriple;
import java.util.ArrayList;

/**
 *
 * @author mouk
 */
public class FlowCalculatorTest
{

    FlowCalculator instance = new FlowCalculator();
    private final int[][] TEST_DATA = new int[][]{
        new int[]{0, 20, 3},
        new int[]{2, 40, 2},
        new int[]{2, 10, 7},
        new int[]{7, 32, 2},
        new int[]{19, 32, 1},
        new int[]{15, 17, 5},
        new int[]{8, 32, 14}};
    private final int MAX = 20;

    @Before
    public void setUp()
    {
        instance = new FlowCalculator();
    }

    @Test
    public void canCallWithoutThrowingExceptions()
    {
        _populateWithTestData();

    }

    @Test
    public void canCanculateFlowtWithoutThrowingExceptions()
    {
        _populateWithTestData();
        _outputFlow(TEST_DATA);
        System.out.println();
        ArrayList<FlowHistroryTriple> list = instance.getCalculatedFlow();
        _outputList(list);
    }

    @Test
    public void cantCallWithBadData()
    {
        try
        {
            instance.add(-2, 20d, 10);
            fail("It should not be possible to pass negative time");
        } catch (Exception e)
        {
        }
        try
        {
            instance.add(3, -2d, 10);
            fail("It should not be possible to pass negative duration");
        } catch (Exception e)
        {
        }

        try
        {
            instance.add(3, 20d, -2);
            fail("It should not be possible to pass time \\leq 0");
        } catch (Exception e)
        {
        }
    }

    @Test
    public void cantCalculateRightDataWithSingleCall()
    {
        ArrayList<Double> list = instance.getList();

        assertTrue(list.size() == 0);
        instance.add(1, 20, 2);


        assertTrue("The first element should be null",
                list.get(0).doubleValue() == 0d);

        assertTrue(list.get(1).doubleValue() == 20d);
        assertTrue(list.get(2).doubleValue() == 20d);
        if (list.size() >= 4)
            assertTrue("The third element should be zero ",
                    list.get(3).doubleValue() == 0d);

    }

    @Test
    public void cantCalculateRightDataWithTwoeSeperatCalls()
    {
        ArrayList<Double> list = instance.getList();

        assertTrue(list.size() == 0);
        instance.add(1, 20, 2);
        instance.add(5, 50, 2);


        assertTrue("The first element should be null",
                list.get(0).doubleValue() == 0d);

        assertTrue(list.get(1).doubleValue() == 20d);
        assertTrue(list.get(2).doubleValue() == 20d);
        assertTrue(list.get(3).doubleValue() == 0d);
        assertTrue(list.get(4).doubleValue() == 0d);
        assertTrue(list.get(5).doubleValue() == 50d);
        assertTrue(list.get(6).doubleValue() == 50d);
        assertEquals(7, list.size());


    }

    private void _populateWithTestData()
    {
        for (int[] trpl : TEST_DATA)
            instance.add(trpl[0], trpl[1], trpl[2]);

    }

    private void _outputList(ArrayList<FlowHistroryTriple> list)
    {
        for (int i = 0; i <= MAX; i++)
            System.out.print("-----");
        System.out.println();
        int time = 0;
        for (FlowHistroryTriple ts : list)
        {
            //output the empty flow
            while (time < ts.getTime() && time <= MAX)
            {
                System.out.print("  " + " | ");
                time++;
            }
            if (time > MAX)
                return;
            for (int i = ts.getTime(); i <= ts.getTime() + ts.getDuration() - 1 
                    && time<=MAX; i++)
            {
                System.out.print(_formatInt((int) ts.getFlow()) + " | ");
                time++;
            }
        }
        while (time++ <= MAX)
            System.out.print("  " + " | ");
        System.out.println();
    }

    private void _outputFlow(int[][] list)
    {


        for (int i = 0; i <= 20; i++)
            System.out.print(_formatInt(i, false) + " | ");
        System.out.println();
        for (int i = 0; i <= 20; i++)
            System.out.print("-----");

        for (int[] trpl : list)
        {
            System.out.println();
            for (int i = 0; i <= MAX; i++)
                if (i < trpl[0] || i > trpl[0] + trpl[2] - 1)
                    System.out.print("  " + " | ");
                else
                    System.out.print(_formatInt(trpl[1]) + " | ");

        }

    }

    private String _formatInt(int i)
    {
        return _formatInt(i, true);
    }

    private String _formatInt(int i, boolean removeZero)
    {
        if (i == 0 && removeZero)
            return "  ";
        String ret = Integer.toString(i);

        return ret.length() < 2 ? "0" + ret : ret;
    }

    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(FlowCalculatorTest.class);
    }
}
