package com.hugo.hafvklocka.activities;

import com.hugo.hafvklocka.R;
import com.hugo.hafvklocka.R.id;
import com.hugo.hafvklocka.R.layout;
import com.hugo.hafvklocka.R.menu;
import com.hugo.hafvklocka.hafvklocka.Game;
import com.hugo.hafvklocka.hafvklocka.Player;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import android.support.v4.app.NavUtils;

public class AddPlayerActivity extends Activity {
	
	LinearLayout players; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        players = (LinearLayout)findViewById(R.id.addPlayerTable);
        EditText p = (EditText)findViewById(R.id.addPlayer1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_player, menu);
        return true;
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
    
    public void newPlayer(View btn){
    	EditText newPlayer = new EditText(this);
    	players.addView(newPlayer);
    }
    
    public void btnNext(View btn){
    	Game.removeAllPlayers();
    	for(int i = 0; i < players.getChildCount(); i++){
    		String name = ((EditText)players.getChildAt(i)).getText().toString();
    		Game.addPlayer(new Player(name));
    	}
        Intent myIntent = new Intent(btn.getContext(), GroupViewActivity.class);
        startActivityForResult(myIntent, 0);
    }

}
