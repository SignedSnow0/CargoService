package model;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.*;


public class HoldTest {
	private IHold hold = null; 
	private int width;
	private int length;

	@Before
	public void setup() {
		System.out.println("HoldTest | setup");	
		hold = new Hold();
		//lettura parametri da file esterno .properties per maggiore modularità
		width = Config.getInt("width"); 
		length = Config.getInt("length");
	}

	@After
	public void down() {
		System.out.println("HoldTest | down");
	}

	@Test
    public void TestEmptyHold() {
        assertFalse(hold.getSlots().get(0).component2().isOccupied());
        assertFalse(hold.getSlots().get(1).component2().isOccupied());
        assertFalse(hold.getSlots().get(2).component2().isOccupied());
        assertFalse(hold.getSlots().get(3).component2().isOccupied());
    }

	@Test
	public void TestFullHold() {
        for (int i = 0; i < 4; i++) {
        	hold.getSlots().get(i).component2().setOccupied(true);
        }

        assertTrue(hold.getSlots().get(0).component2().isOccupied());
        assertTrue(hold.getSlots().get(1).component2().isOccupied());
        assertTrue(hold.getSlots().get(2).component2().isOccupied());
        assertTrue(hold.getSlots().get(3).component2().isOccupied());
    }	
	
	@Test
	public void TestSlotsCoordinates() {
		int currentX = 0, currentY = 0;
        
		for (int i = 0; i < 4; i++) {
        	currentX = hold.getSlots().get(i).component1().getX();
            assertTrue(currentX < width);
        	currentY = hold.getSlots().get(i).component1().getY();
        	assertTrue(currentY < length);
        }
    }
	
	@Test
	public void TestIOCoordinates() {
		int currentX = 0, currentY = 0;
        
    	currentX = hold.getIOPortPosition().getX();
        assertTrue(currentX < width);
    	currentY = hold.getIOPortPosition().getY();
    	assertTrue(currentY < length);
    }
	
	@Test
	public void TestHomeCoordinates() {
		int currentX = 0, currentY = 0;

    	currentX = hold.getIOPortPosition().getX();
        assertTrue(currentX < width);
    	currentY = hold.getIOPortPosition().getY();
    	assertTrue(currentY < length);
    }
}
