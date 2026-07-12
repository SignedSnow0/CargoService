package test;

import org.junit.Test;

import model.ISlot;
import model.Slot;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
 

public class SlotTest {
	public static final int ID = 3;
	private ISlot slot;

	@Before
	public void setup() {
		System.out.println("SlotTest | setup");	
		slot = new Slot(ID);
	}
	
	@After
	public void down() {
		System.out.println("SlotTest | down");
	}
	
	@Test
	public void testGetId() {
	    assertTrue(slot.getID() == ID);
	}
	
	@Test
	public void testIsSetOccupied() {
	    slot.setOccupied(true);
	    assertTrue(slot.isOccupied());
	}
}
