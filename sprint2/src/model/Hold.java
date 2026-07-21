package model;

import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;
import utils.*;

public class Hold implements IHold {		
	private IPosition ioPosition;
	private IPosition homePosition;
	private IPosition slot5Position;
	
	private List<Pair<IPosition, ISlot>> slotList = new ArrayList<Pair<IPosition, ISlot>>();
	
	public Hold() {
		//lettura parametri da file esterno .properties per maggiore modularità
		int width = Config.getInt("width"); 
		int length = Config.getInt("length");
		
		ioPosition = new Position(4, 0);
		homePosition = new Position(0, 0);
		slot5Position = new Position(2, 5);
		slotList.add(new Pair<IPosition, ISlot>(new Position(1, 1), new Slot(1)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(1, 4), new Slot(2)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(3, 1), new Slot(3)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(3, 4), new Slot(4)));
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
	public IPosition getSlot5Position() {
		return slot5Position;
	}

	@Override
	public List<Pair<IPosition, ISlot>> getSlots() {
		return slotList;
	}
}
