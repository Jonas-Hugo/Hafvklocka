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
import com.hugo.hafvklocka.drivers.Controller;
import com.hugo.hafvklocka.drivers.Controller.GameMode;
import com.hugo.hafvklocka.hafvklocka.Game;
import com.hugo.hafvklocka.hafvklocka.Player;

public class TimeActivity extends Activity {
	private TextView time1, time2, player1, player2;
	private Clock clock1, clock2;
	private int type;
	private Controller controller;

	private Handler done = new Handler() {
		public void handleMessage(Message m) {
			if (Game.isHeatsLeft()) {
				selectType();
			} else {
				selectWinner(0);
			}
		}
	};

	final Handler timeHandler1 = new Handler() {
		@Override
		public void handleMessage(Message m) {
			time1.setText(Clock.getTimeString(m.what));
		}
	};

	final Handler timeHandler2 = new Handler() {
		@Override
		public void handleMessage(Message m) {
			time2.setText(Clock.getTimeString(m.what));
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
		if (Game.getGameMode() == GameMode.TIME_MODE) {
			clock1 = new Clock(timeHandler1);
		} else {
			clock1 = new Clock(timeHandler1);
			clock2 = new Clock(timeHandler2);
			player1.setText(Game.getNextHeats().getPlayer1().getName());
			player2.setText(Game.getNextHeats().getPlayer2().getName());
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (Game.getGameMode() == GameMode.TIME_MODE) {
			controller = new Controller(this.getApplicationContext(), Game.getGameMode(), clock1);
		} else {
			controller = new Controller(this.getApplicationContext(), Game.getGameMode(), clock1, clock2);
		}
		controller.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		controller.kill();
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
		controller.reset();
		clock1.sendMessage(Message.obtain(clock1, Konstants.RESET));
	}

	public void btnClear(View btn) {
		stop(btn);
		time1.setText("00:00:00");
		time2.setText("00:00:00");
	}

}
