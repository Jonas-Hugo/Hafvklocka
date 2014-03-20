package com.hugo.hafvklocka;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class GroupViewActivity extends Activity {

	private TableLayout rows;
	private TextView heatsLeft;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        rows = (TableLayout)findViewById(R.id.rows);
        heatsLeft = (TextView)findViewById(R.id.HeatsLeft);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_group_view, menu);
        return true;
    }
    @Override
    public void onResume(){
    	super.onResume();
    	if(Game.started){
    		heatsLeft.setText("Häfv kvar: " + Game.numberHeatsLeft());

            if(!Game.isHeatsLeft()){

            	Game.buildFinals();
            	
        		AlertDialog finals = new AlertDialog.Builder(this).create();
        		finals.setCancelable(true);
        		finals.setTitle("Nu börjar finalerna");
        		finals.setButton(DialogInterface.BUTTON_POSITIVE, "Okej", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        				startFinals();
        			}

        		});
        		finals.show();
            }
    	}
    	TableRow row = new TableRow(this);
    	TextView name = new TextView(this);
    	name.setTextSize(20.0f);
    	name.setText("Namn                ");
    	TextView matches = new TextView(this);
    	matches.setTextSize(20.0f);
    	matches.setText("M    ");
    	TextView win = new TextView(this);
    	win.setTextSize(20.0f);
    	win.setText("V    ");
    	TextView bestTime = new TextView(this);
    	bestTime.setTextSize(20.0f);
    	bestTime.setText("B             ");
    	TextView points = new TextView(this);
    	points.setTextSize(20.0f);
    	points.setText("P");
    	row.addView(name);
    	row.addView(matches);
    	row.addView(win);
    	row.addView(bestTime);
    	row.addView(points);
    	rows.addView(row);
        ArrayList<Player> players = Game.getPlayers();
        int count = 0;
        for(Player p : players){
        	row = new TableRow(this);
        	name = new TextView(this);
        	matches = new TextView(this);
        	win = new TextView(this);
        	bestTime = new TextView(this);
        	points = new TextView(this);
        	
        	name.setTextSize(18.0f);
        	name.setText(p.getName());
        	matches.setTextSize(18.0f);
        	matches.setText("" + p.getNrMatches());
        	win.setTextSize(18.0f);
        	win.setText("" + p.getWinings());
        	bestTime.setTextSize(18.0f);
        	bestTime.setText(p.getBestTime());
        	points.setTextSize(18.0f);
        	points.setText("" + p.getPoints());
        	if(Game.started){
        		if(count < 8){
        			name.setTextColor(Color.GREEN);
        			matches.setTextColor(Color.GREEN);
        			win.setTextColor(Color.GREEN);
        			bestTime.setTextColor(Color.GREEN);
        			points.setTextColor(Color.GREEN);
        		}else{
        			name.setTextColor(Color.RED);
        			matches.setTextColor(Color.RED);
        			win.setTextColor(Color.RED);
        			bestTime.setTextColor(Color.RED);
        			points.setTextColor(Color.RED);
        		}
        	}
        	row.addView(name);
        	row.addView(matches);
        	row.addView(win);
        	row.addView(bestTime);
        	row.addView(points);
        	rows.addView(row);
        	count += 1;
        }
    }
    @Override
    public void onPause(){
    	super.onPause();
    	rows.removeAllViews();
    }
    @Override
    public void onBackPressed() {
    }


    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void btnStartNext(View v){
    	if(!Game.started){
    		Game.buildSerieGame();
    		Game.started = true;
    	}
    	if(Game.isHeatsLeft()){
    		Game.setNextHeats();

    		AlertDialog showBattle = new AlertDialog.Builder(this).create();
    		showBattle.setCancelable(true);
    		showBattle.setTitle(Game.getNextHeats().getPlayer1().getName() + " vs " + Game.getNextHeats().getPlayer2().getName());
    		showBattle.setButton(DialogInterface.BUTTON_POSITIVE, "Okej", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				startBattle();
    			}

    		});
    		showBattle.setButton(DialogInterface.BUTTON_NEGATIVE, "Byt tävlande", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				Game.newNextHeat();
    				btnStartNext(null);
    			}

    		});
    		showBattle.show();
    	}else{
    		/*
    		 * TODO Move this code to FinalViewActivity!
    		 * 
    		 */
    		Game.getNextFinal();
    	}
    }
    private void startBattle(){

        Intent myIntent = new Intent(this, TimeActivity.class);
        startActivityForResult(myIntent, 0);
    }
    private void startFinals(){
		Intent myIntent = new Intent(this, FinalViewActivity.class);
		startActivityForResult(myIntent, 0);
    }

}
