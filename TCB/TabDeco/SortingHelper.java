package TCB.TabDeco;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

public class SortingHelper {
	private TabDeco plugin;

	public SortingHelper(TabDeco plugin) {
		this.plugin = plugin;
	}

	public ArrayList<Player> sortListByPlayerName() {
		ArrayList<Player> playerList = plugin.playerList();
		Collections.sort(playerList, new SortByPlayerName());
		return playerList;
	}

	public ArrayList<Player> sortListByWorld() {
		ArrayList<Player> playerList = plugin.playerList();
		Collections.sort(playerList, new SortByPlayerName());
		return playerList;
	}

	public ArrayList<Player> sortListByLocalKD() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double bestKD = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerKD = plugin.getPlayerKD(plugin.getLocalPlayerKills(player1), plugin.getLocalPlayerDeaths(player1));
					if(bestKD < playerKD || bestKD == -1) {
						bestKD = playerKD;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerKD = plugin.getPlayerKD(plugin.getLocalPlayerKills(player2), plugin.getLocalPlayerDeaths(player2));
				if(playerKD == bestKD) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}

	public ArrayList<Player> sortListByGlobalKD() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double bestKD = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerKD = plugin.getPlayerKD(plugin.getGlobalPlayerKills(player1), plugin.getGlobalPlayerDeaths(player1));
					if(bestKD < playerKD || bestKD == -1) {
						bestKD = playerKD;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerKD = plugin.getPlayerKD(plugin.getGlobalPlayerKills(player2), plugin.getGlobalPlayerDeaths(player2));
				if(playerKD == bestKD) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}

	public ArrayList<Player> sortListByHunger() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double bestKD = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerKD = player1.getFoodLevel();
					if(bestKD < playerKD || bestKD == -1) {
						bestKD = playerKD;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerKD = player2.getFoodLevel();
				if(playerKD == bestKD) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}

	public ArrayList<Player> sortListByLocalDeath() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = plugin.getLocalPlayerDeaths(player1);
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = plugin.getLocalPlayerDeaths(player2);
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}	

	public ArrayList<Player> sortListByGlobalDeath() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = plugin.getGlobalPlayerDeaths(player1);
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = plugin.getGlobalPlayerDeaths(player2);
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}

	public ArrayList<Player> sortListByLocalKills() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = plugin.getLocalPlayerKills(player1);
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = plugin.getLocalPlayerKills(player2);
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}
	
	public ArrayList<Player> sortListByGlobalKills() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = plugin.getGlobalPlayerKills(player1);
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = plugin.getGlobalPlayerKills(player2);
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}
	public ArrayList<Player> sortListByID() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = player1.getEntityId();
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = player2.getEntityId();
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}

	public ArrayList<Player> sortListByEXP() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = player1.getExp();
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = player2.getExp();
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}

	public ArrayList<Player> sortListByLevel() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = player1.getLevel();
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = player2.getLevel();
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}

	public ArrayList<Player> sortListByAir() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = player1.getRemainingAir();
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = player2.getRemainingAir();
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}

	public ArrayList<Player> sortListByLifeTime() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = player1.getTicksLived();
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = player2.getTicksLived();
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}

	public ArrayList<Player> sortListByHealth() {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for(int i = 0; i < plugin.playerList().size(); i++) {
			double best = -1;
			for(Player player1 : plugin.playerList()) {
				if(!playerList.contains(player1)) {
					double playerValue = ((Damageable)player1).getHealth();
					if(best < playerValue || best == -1) {
						best = playerValue;
					}
				} else {
					continue;
				}
			}

			for(Player player2 : plugin.playerList()) {
				double playerValue = ((Damageable)player2).getHealth();
				if(playerValue == best) {
					playerList.add(player2);
				}
			}
		}
		return playerList;
	}
}
