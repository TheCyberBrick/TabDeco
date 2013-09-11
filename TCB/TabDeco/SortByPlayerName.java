package TCB.TabDeco;

import java.util.Comparator;

import org.bukkit.entity.Player;

public class SortByPlayerName implements Comparator<Player> {
	@Override
	public int compare(Player o1, Player o2) {
		return o1.getDisplayName().compareTo(o2.getDisplayName());
	}
}
