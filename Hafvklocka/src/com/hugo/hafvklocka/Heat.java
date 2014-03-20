package com.hugo.hafvklocka;

public class Heat {
	
	private Player player1, player2;
	
	public Heat(Player p1, Player p2){
		player1 = p1;
		player2 = p2;
	}
	
	public Player getPlayer1(){
		return player1;
	}
	public Player getPlayer2(){
		return player2;
	}
	
	public boolean contains(Heat h){
		return (h.getPlayer1().equals(player1) || h.getPlayer1().equals(player2)) || 
				(h.getPlayer2().equals(player1) || h.getPlayer2().equals(player2));
	}

}
