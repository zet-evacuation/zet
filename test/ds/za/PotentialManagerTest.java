/*
 * DynamicPotentialTest.java
 * JUnit based test
 *
 * Created on 3. Dezember 2007, 21:48
 */


package ds.za;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import ds.ca.*;

/**
 *
 * @author Sophia
 */
public class PotentialManagerTest extends TestCase {
	

    
 public PotentialManagerTest(String testName) {
        super(testName);
    }

    
    @Before
    protected void setUp() throws Exception {   

    }

    
    @Test
    public void testsetDynamicPotential () {
        System.out.println("setDynamicPotential");  
        DynamicPotential dp1;
        dp1 = new DynamicPotential();
        
        PotentialManager pM1 = new PotentialManager();
        
        pM1.setDynamicPotential(dp1);
        
        assertEquals(pM1.getDynamicPotential(),dp1);     
    } 
   
    @Test
    public void testaddStaticPotential () {
        System.out.println("addStaticPotential");  
        StaticPotential sp1;        
        StaticPotential sp2;        
        StaticPotential sp3;
        sp1 = new StaticPotential();        
        sp2 = new StaticPotential();        
        sp3 = new StaticPotential();
        
        PotentialManager pM2 = new PotentialManager();
        
        pM2.addStaticPotential(sp1);
        pM2.addStaticPotential(sp2);
        pM2.addStaticPotential(sp3);       
        
        assertEquals(sp1,pM2.getStaticPotential(sp1.getID()));
        assertEquals(sp2,pM2.getStaticPotential(sp2.getID()));  
        assertEquals(sp3,pM2.getStaticPotential(sp3.getID()));
   
    }
    
    
}