package test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.IPosition;
import model.Position;
import utils.Config;


public class PositionTest {
	private int x = 0;
	private int y = 0;
	
	@Before
	public void setup() {
		System.out.println("PositionTest | setup");
		this.x = Config.getInt("x");
		this.y = Config.getInt("y");
	}
	
	@After
	public void down() {
		System.out.println("PositionTest | down");
	}
	
	@Test
	public void testCoordinates() throws Exception {
		int x_current = 0, y_current = 0;
		IPosition pos = new Position(x, y);
		
		x_current = pos.getX();		
		assertTrue(x_current == x);
		
		y_current = pos.getY();
		assertTrue(y_current == y);
	}
}
