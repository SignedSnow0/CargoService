package model;

import java.util.List;
import kotlin.Pair;

public interface IHold {
    public IPosition getIOPortPosition();
    public IPosition getHomePosition();
    public IPosition getSlot5Position();
    public List<Pair<IPosition, ISlot>> getSlots();
}