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
import model.Hold;
import model.IHold;


public class HoldTest {
	private IHold hold = null; 
	private int width;
	private int length;

	@Before
	public void setup() {
		System.out.println("HoldTest | setup");	
		hold = new Hold();
		BufferedReader rd = null;
		System.out.println(System.getProperty("user.dir"));
		
		try {
			rd = new BufferedReader(new FileReader("src/model/values.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("File read error!");
			e.printStackTrace();
		}
		String values = null;
		try {
			values = rd.readLine();
		} catch (IOException e) {
			System.out.println("String read error!");
			e.printStackTrace();
		}
		
		width = Integer.parseInt(values.split(";")[0]); 
		length = Integer.parseInt(values.split(";")[1]);
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
        assertTrue(hold.getSlots().get(3).component2().isOccupied());;
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
