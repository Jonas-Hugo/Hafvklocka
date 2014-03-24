package com.hugo.hafvklocka.hafvklocka;

import java.util.ArrayList;
import java.util.Collections;

import com.hugo.hafvklocka.Konstants;
import com.hugo.hafvklocka.Settings;
import com.hugo.hafvklocka.drivers.Controller.GameMode;

public class Game {

	private static GameMode gameMode;
	private static ArrayList<Player> players;
	private static Player tempW, tempL, firstPlace, secondPlace, thirdPlace;
	private static ArrayList<Heat> heats;
	private static Heat next;
	private static ArrayList<Heat> kvartFinaler;
	private static ArrayList<Heat> semiFinaler;
	private static Heat finalen;
	private static Heat bronsFinal;
	public static int nrFinals;
	public static boolean started = false;

	public Game(GameMode gameMode) {
		this.gameMode = gameMode;
		switch (gameMode) {
		case TIME_MODE:
			break;
		case BRACKET_MODE:
			players = new ArrayList<Player>();
			break;
		}
	}

	public static GameMode getGameMode() {
		return gameMode;
	}

	public static void addPlayer(Player p) {
		players.add(p);
	}

	public static void removePlayer(Player s) {
		players.remove(s);
	}

	public static ArrayList<Player> getPlayers() {
		return players;
	}

	public static boolean newPlayer(String n) {

		for (Player p : players) {
			if (p.getName().equals(n)) {
				return false;
			}
		}
		return true;

	}

	public static void removeAllPlayers() {
		players.clear();
	}

	public static void buildSerieGame() {
		heats = new ArrayList<Heat>();
		ArrayList<Heat> h = new ArrayList<Heat>();
		int numberHeats = Settings.MAX_NUMBER_OF_SERIEROUNDS;
		int count = 0;
		for (int i = 0; i < players.size() - 1; i++) {
			for (int j = i + 1; j < players.size(); j++) {
				count += 1;
				h.add(new Heat(players.get(i), players.get(j)));
				if (count > numberHeats) {
					Collections.shuffle(h);
					heats.addAll(h);
					return;
				}
			}
		}
		int one = count;
		do {
			Collections.shuffle(h);
			heats.addAll(h);
			count += one;
		} while (count < numberHeats);

	}

	public static int buildFinals() {
		int size = players.size();
		kvartFinaler = new ArrayList<Heat>();
		semiFinaler = new ArrayList<Heat>();
		if (size >= 8) {
			for (int i = 0; i < 4; i++) {
				kvartFinaler.add(new Heat(players.get(i), players.get(7 - i)));
			}
			nrFinals = 8;
			return Konstants.KVARTFINAL;
		} else if (size >= 4) {
			semiFinaler.add(new Heat(players.get(0), players.get(3)));
			semiFinaler.add(new Heat(players.get(1), players.get(2)));
			nrFinals = 4;
			return Konstants.SEMIFINAL;
		} else if (size >= 2) {
			finalen = new Heat(players.get(0), players.get(1));
			nrFinals = 1;
			return Konstants.FINAL;
		} else {
			return -1;
		}
	}

	public static void setResultFinal(int type, Player winner, Player loser) {
		if (tempW == null && tempL == null) {
			tempW = winner;
			tempL = loser;
		} else {
			switch (type) {
			case Konstants.KVARTFINAL:
				semiFinaler.add(new Heat(tempW, winner));
				break;
			case Konstants.SEMIFINAL:
				finalen = new Heat(tempW, winner);
				bronsFinal = new Heat(tempL, loser);
				break;
			case Konstants.BRONSFINAL:
				thirdPlace = winner;
				break;
			case Konstants.FINAL:
				secondPlace = loser;
				firstPlace = winner;
			}
			tempL = null;
			tempW = null;
		}
	}

	public static void getNextFinal() {
		if (kvartFinaler.size() > 0) {
			next = kvartFinaler.get(kvartFinaler.size() - 1);
		} else if (semiFinaler.size() > 0) {
			next = semiFinaler.get(semiFinaler.size() - 1);
		} else if (bronsFinal != null) {
			next = bronsFinal;
		} else if (finalen != null) {
			next = finalen;
		}
	}

	public static int removeUsedFinal() {
		if (kvartFinaler.size() > 0) {
			kvartFinaler.remove(kvartFinaler.size() - 1);
			return Konstants.KVARTFINAL;
		} else if (semiFinaler.size() > 0) {
			semiFinaler.remove(semiFinaler.size() - 1);
			return Konstants.SEMIFINAL;
		} else if (bronsFinal != null) {
			bronsFinal = null;
			return Konstants.BRONSFINAL;
		} else if (finalen != null) {
			finalen = null;
			return Konstants.FINAL;
		}
		return -1;
	}

	public static boolean isHeatsLeft() {
		return heats.size() > 0;
	}

	public static void setNextHeats() {
		next = heats.get(heats.size() - 1);
	}

	public static void removeUsedHeat() {
		heats.remove(heats.size() - 1);
	}

	public static void newNextHeat() {
		heats.remove(heats.size() - 1);
		heats.add((int) (Math.random() * (heats.size() - 1)), next);
		setNextHeats();
	}

	public static Heat getNextHeats() {
		return next;
	}

	public static int numberHeatsLeft() {
		return heats.size();
	}

	public static void sortPlayers() {
		for (int i = 0; i < players.size() - 1; i++) {
			for (int j = i + 1; j < players.size(); j++) {
				if (players.get(i).getPoints() < players.get(j).getPoints()) {
					Player temp = players.get(i);
					players.set(i, players.get(j));
					players.set(j, temp);
				} else if (players.get(i).getPoints() == players.get(j).getPoints()) {
					if (players.get(i).getBestTimeInt() < players.get(j).getBestTimeInt()) {
						Player temp = players.get(i);
						players.set(i, players.get(j));
						players.set(j, temp);
					}
				}
			}
		}
	}

}
