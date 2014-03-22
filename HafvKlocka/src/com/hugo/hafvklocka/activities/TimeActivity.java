package com.hugo.hafvklocka.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hugo.hafvklocka.Konstants;
import com.hugo.hafvklocka.R;
import com.hugo.hafvklocka.drivers.Clock;
import com.hugo.hafvklocka.drivers.Mic;
import com.hugo.hafvklocka.hafvklocka.Game;
import com.hugo.hafvklocka.hafvklocka.Player;

public class TimeActivity extends Activity {
	private TextView time1, time2, player1, player2;
	private Clock clock;
	private int type;
	private Mic mic;

	private Handler done = new Handler() {
		public void handleMessage(Message m) {
			if (Game.isHeatsLeft()) {
				selectType();
			} else {
				selectWinner(0);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time);
		time1 = (TextView) findViewById(R.id.clock1);
		time2 = (TextView) findViewById(R.id.clock2);
		player1 = (TextView) findViewById(R.id.name1);
		player2 = (TextView) findViewById(R.id.name2);
		if (Game.getGameMode() == Konstants.TIME_MODE) {
			clock = new Clock();
		} else {
			clock = new Clock();
			player1.setText(Game.getNextHeats().getPlayer1().getName());
			player2.setText(Game.getNextHeats().getPlayer2().getName());
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		mic = new Mic(clock, Game.getGameMode());
	}

	@Override
	public void onPause() {
		super.onPause();
		mic.kill();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_time, menu);
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

	private void selectWinner(int t) {
		type = t;
		AlertDialog selectWinner = new AlertDialog.Builder(this).create();
		selectWinner.setCancelable(true);
		selectWinner.setTitle("Vem vann?");
		selectWinner.setButton(DialogInterface.BUTTON_POSITIVE, Game.getNextHeats().getPlayer1().getName(),
		        new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
				        reportWinner(Game.getNextHeats().getPlayer1(), Game.getNextHeats().getPlayer2());
			        }

		        });
		selectWinner.setButton(DialogInterface.BUTTON_NEGATIVE, Game.getNextHeats().getPlayer2().getName(),
		        new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
				        reportWinner(Game.getNextHeats().getPlayer2(), Game.getNextHeats().getPlayer1());
			        }

		        });
		selectWinner.show();
	}

	private void selectType() {
		AlertDialog type = new AlertDialog.Builder(this).create();
		type.setCancelable(true);
		type.setTitle("Vad Häfvdes?");
		type.setButton(DialogInterface.BUTTON_POSITIVE, "Flaska", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				selectWinner(Konstants.BOTTLE);
			}

		});
		type.setButton(DialogInterface.BUTTON_NEUTRAL, "50cl Sejdel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				selectWinner(Konstants.MUG_50CL);
			}

		});
		type.setButton(DialogInterface.BUTTON_NEGATIVE, "100cl Sejdel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				selectWinner(Konstants.MUG_100CL);
			}

		});
		type.show();
	}

	private void reportWinner(Player winner, Player loser) {

		if (Game.isHeatsLeft()) {
			Game.removeUsedHeat();
			winner.addMatch(true, type, time1.getText().toString());
			loser.addMatch(false, type, time2.getText().toString());

			Game.sortPlayers();
			Intent myIntent = new Intent(this, GroupViewActivity.class);
			startActivityForResult(myIntent, 0);
		} else {
			int type = Game.removeUsedFinal();
			winner.addFinal(true, type, loser, time1.getText().toString());
			loser.addFinal(false, type, winner, time1.getText().toString());
			Intent myIntent = new Intent(this, FinalViewActivity.class);
			startActivityForResult(myIntent, 0);
		}

	}

	public void stop(View btn) {
		mic.reset();
		clock.sendMessage(Message.obtain(clock, Konstants.RESET));
	}

	public void btnClear(View btn) {
		stop(btn);
		time1.setText("00:00:00");
		time2.setText("00:00:00");
	}

}
