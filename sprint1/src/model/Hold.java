package model;

import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;
import utils.*;

public class Hold implements IHold {		
	private IPosition ioPosition;
	private IPosition homePosition;
	
	private List<Pair<IPosition, ISlot>> slotList = new ArrayList<Pair<IPosition, ISlot>>();
	
	public Hold() {
		//lettura parametri da file esterno .properties per maggiore modularità
		int width = Config.getInt("width"); 
		int length = Config.getInt("length");
		int delta = Config.getInt("delta");
		
		ioPosition = new Position(0, width - 1);
		homePosition = new Position(0, 0);
		
		slotList.add(new Pair<IPosition, ISlot>(new Position(delta, delta), new Slot(1)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(width - delta - 1, delta), new Slot(2)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(delta, length - delta - 1), new Slot(3)));
		slotList.add(new Pair<IPosition, ISlot>(new Position(width - delta - 1, delta), new Slot(4)));
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
