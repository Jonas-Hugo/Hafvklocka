package com.hugo.hafvklocka.hafvklocka;

import java.util.concurrent.TimeUnit;

import com.hugo.hafvklocka.Konstants;
import com.hugo.hafvklocka.drivers.Clock;

import GraphView.LineGraphView;
import GraphView.GraphView.GraphViewData;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.widget.TextView;

/**
 * Player is a TextView modified to show a players result.
 */
public class Player {
	
	private String name = "";
	private int points;
	private int bestTime;
	private int nrMatches;
	private int winings;
	
	public Player(String n){
		this.name = n;
		this.points = 0;
		this.bestTime = 0;
		this.nrMatches = 0;
		this.winings = 0;
	}
	
	/**
	 * Use this method to add a match to the player statistic.
	 * 
	 * @param win Did the player win the match
	 * @param type Bottle, mug 0,5l or mug 1l. Use {@link Konstants.BOTTLE}, {@link Konstants.MUG_50CL} or {@link Konstants.MUG_100CL}
	 * @param time The time the player got
	 */
	public void addMatch(boolean win, int type, String time){
		if(win){
			winings += 1;
			switch(type){
			case Konstants.BOTTLE:
				points += 2;
				break;
			case Konstants.MUG_50CL:
				points += 3;
				break;
			case Konstants.MUG_100CL:
				points += 5;
				break;
			}
		}
		int t = convert(time);
		if(t > bestTime) bestTime = t;
		nrMatches += 1;
	}
	/*
	 * TODO add saving result functionality for build brackets for post on facebook.
	 */
	public void addFinal(boolean win, int finalType, Player p, String time){
		switch(finalType){
		case Konstants.KVARTFINAL:
			break;
		case Konstants.SEMIFINAL:
			break;
		case Konstants.BRONSFINAL:
			break;
		case Konstants.FINAL:
			break;
		}
	}
	public int getPoints(){
		return this.points;
	}
	public int getNrMatches(){
		return this.nrMatches;
	}
	public int getWinings(){
		return this.winings;
	}
	public String getBestTime(){
		return Clock.getTimeString((long)bestTime);
	}
	public int getBestTimeInt(){
		return bestTime;
	}
	public String getName(){
		return this.name;
	}
	
	public boolean equals(Player p){
		return this.name.equals(p.getName());
	}
	
	private int convert(String t){
		int output = 0;
		int min = Integer.parseInt(t.split(":")[0]);
		int sec = Integer.parseInt(t.split(":")[1]);
		int hun = Integer.parseInt(t.split(":")[2]);
        output += min * 60 * 1000;
        output += sec * 1000;
        output += hun * 10;
		return output;
	}
	

}
