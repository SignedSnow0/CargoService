package model;

import org.junit.Test;

import utils.Config;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
 

public class SlotTest {
	private int id = 0;
	private ISlot slot;

	@Before
	public void setup() {
		System.out.println("SlotTest | setup");
		this.id = Config.getInt("id");
		slot = new Slot(this.id);
	}
	
	@After
	public void down() {
		System.out.println("SlotTest | down");
	}
	
	@Test
	public void testGetId() {
	    assertTrue(slot.getID() == this.id);
	}
	
	@Test
	public void testIsSetOccupied() {
	    slot.setOccupied(true);
	    assertTrue(slot.isOccupied());
	}
}
