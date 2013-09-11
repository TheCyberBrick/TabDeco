package TCB.TabDeco;

import java.util.Comparator;

import org.bukkit.entity.Player;

public class SortByWorld implements Comparator<Player> {
	@Override
	public int compare(Player o1, Player o2) {
		return o1.getWorld().getName().compareTo(o2.getWorld().getName());
	}
}
