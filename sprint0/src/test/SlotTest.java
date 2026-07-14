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
	
	@Test
	public void testGetId() {
		slot = new Slot(ID);
	    assertTrue(slot.getID() == ID);
	}
	
	@Test
	public void testIsSetOccupied() {
	    slot = new Slot(ID);
		slot.setOccupied(true);
	    assertTrue(slot.isOccupied());
	}
}
