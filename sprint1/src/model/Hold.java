package model;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;

public class Hold implements IHold {		
	private IPosition ioPosition;
	private IPosition homePosition;
	
	private List<Pair<IPosition, ISlot>> slotList = new ArrayList<Pair<IPosition, ISlot>>();
	
	public Hold() {
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
		
		int width = Integer.parseInt(values.split(";")[0]); 
		int length = Integer.parseInt(values.split(";")[1]);
		int delta = Integer.parseInt(values.split(";")[2]);
		
		ioPosition = new Position(0, width - 1);
		homePosition = new Position(0, 0);
		
		slotList.add(new Pair<IPosition, ISlot>(new Position(width, delta), new Slot(1)));
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
