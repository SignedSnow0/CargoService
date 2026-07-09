package model;

import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;

public class Hold implements IHold {
	private static final int LENGHT = 8;
	private static final int WIDTH = 8;
	private static final int DELTA = 2;
	
	private IPosition ioPosition;
	private IPosition homePosition;
	
	private List<Pair<IPosition, ISlot>> slotList = new ArrayList<Pair<IPosition, ISlot>>();
	
	public Hold() {
		ioPosition = new Position(0, WIDTH - 1);
		homePosition = new Position(0, 0);
		
		slotList.add(new Pair<IPosition, ISlot>(new Position(WIDTH, DELTA), new Slot(0)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(WIDTH - DELTA - 1, DELTA), new Slot(1)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(DELTA, LENGHT - DELTA - 1), new Slot(2)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(WIDTH - DELTA - 1, DELTA), new Slot(3)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(WIDTH - DELTA, LENGHT / 2), new Slot(4)));
	}

	@Override
	public IPosition getIOPortPosition() {
		return ioPosition;
	}

	@Override
	public IPosition getHomePosition() {
		return homePosition;
	}

	@Override
	public List<Pair<IPosition, ISlot>> getSlots() {
		return slotList;
	}
}
