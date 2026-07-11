package test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import unibo.basicomm23.utils.CommUtils;
import model.Hold;
import model.IHold;


public class HoldTest {
	private IHold hold = null; 

	@Before
	public void setup() {
		System.out.println("HoldTest | setup");	
		hold = new Hold();
	}

	@After
	public void down() {
		System.out.println("HoldTest | down");
	}

	@Test
    public void TestEmptyHold() {
        IHold hold = new Hold();
        
        assertFalse(hold.getSlots().get(0).component2().isOccupied());
        assertFalse(hold.getSlots().get(1).component2().isOccupied());
        assertFalse(hold.getSlots().get(2).component2().isOccupied());
        assertFalse(hold.getSlots().get(3).component2().isOccupied());
    }

	@Test
	public void TestFullHold() {
        var hold = new Hold();
        for (int i = 0; i < 4; i++) {
        	hold.getSlots().get(i).component2().setOccupied(true);
        }

        assertTrue(hold.getSlots().get(0).component2().isOccupied());
        assertTrue(hold.getSlots().get(1).component2().isOccupied());
        assertTrue(hold.getSlots().get(2).component2().isOccupied());
        assertTrue(hold.getSlots().get(3).component2().isOccupied());;
    }	
}
