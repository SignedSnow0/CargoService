package test;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import model.IPosition;
import model.Position;


public class PositionTest {
	public static final int X = 4;
	public static final int Y = 5;
	
	@Test
	public void testCoordinates() throws Exception {
		int x = 0, y = 0;
		IPosition pos = new Position(X, Y);
		
		x = pos.getX();		
		assertTrue(x == X);
		
		y = pos.getY();
		assertTrue(y == Y);
	}
}
