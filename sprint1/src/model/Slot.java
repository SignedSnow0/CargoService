package model;

public class Slot implements ISlot {
	private int id;
	private boolean occupied;
	
	public Slot(int id) {
		this.id = id;
		occupied = false;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public boolean isOccupied() {
		return this.occupied;
	}

	@Override
	public void setOccupied(boolean value) {
		this.occupied = value;
	}
}
