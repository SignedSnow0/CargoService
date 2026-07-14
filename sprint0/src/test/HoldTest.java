package test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import unibo.basicomm23.utils.CommUtils;
import utils.*;
import model.Hold;
import model.IHold;


public class HoldTest {
	private IHold hold = null; 
	private static final int width = 8;
	private static final int length = 8;

	@Test
    public void TestEmptyHold() {
        var hold = new Hold();
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
        assertTrue(hold.getSlots().get(3).component2().isOccupied());
    }	
	
	@Test
	public void TestSlotsCoordinates() {
		var hold = new Hold();
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
		var hold = new Hold();
		int currentX = 0, currentY = 0;
        
    	currentX = hold.getIOPortPosition().getX();
        assertTrue(currentX < width);
    	currentY = hold.getIOPortPosition().getY();
    	assertTrue(currentY < length);
    }
	
	@Test
	public void TestHomeCoordinates() {
		var hold = new Hold();
		int currentX = 0, currentY = 0;

    	currentX = hold.getIOPortPosition().getX();
        assertTrue(currentX < width);
    	currentY = hold.getIOPortPosition().getY();
    	assertTrue(currentY < length);
    }
}
