package com.hugo.hafvklocka.activities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hugo.hafvklocka.Konstants;
import com.hugo.hafvklocka.R;
import com.hugo.hafvklocka.Settings;
import com.hugo.hafvklocka.drivers.Controller.GameMode;
import com.hugo.hafvklocka.hafvklocka.Game;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ArrayList<String> attr = new ArrayList<String>();

		try {
			FileInputStream ofi = openFileInput(Konstants.FILENAME);
			DataInputStream dis = new DataInputStream(ofi);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				attr.add(strLine);
			}
			dis.close();
			new Settings(attr);
		} catch (Exception e) {
			new Settings();
			FileOutputStream fos;
			try {
				fos = openFileOutput(Konstants.FILENAME, Context.MODE_PRIVATE);
				fos.write(Settings.getOutput().getBytes());
				fos.close();
			} catch (FileNotFoundException e1) {
				Log.d("Settings", "Could not create file.");
			} catch (IOException e2) {
				Log.d("Settings", "Could not write to file.");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);

		MenuItem item = menu.findItem(R.id.settings);

		if (item == null)
			return true;

		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				return (btnSettings(item));
			}
		});

		return true;
	}

	public void startTimeMode(View btn) {
		Toast.makeText(this, "du tryckte på klockan", Toast.LENGTH_SHORT).show();
		new Game(GameMode.TIME_MODE);
		Intent myIntent = new Intent(btn.getContext(), TimeActivity.class);
		startActivityForResult(myIntent, 0);
	}

	public void startBracketMode(View btn) {
		Toast.makeText(this, "du tryckte på prispallen", Toast.LENGTH_SHORT).show();

		AlertDialog numberBeers = new AlertDialog.Builder(this).create();
		numberBeers.setCancelable(true);
		numberBeers.setTitle("Hur många öl har ni?");
		final EditText bottles = new EditText(this);
		bottles.setInputType(InputType.TYPE_CLASS_NUMBER);
		numberBeers.setView(bottles);
		numberBeers.setButton(DialogInterface.BUTTON_POSITIVE, "Okej", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				/**
				 * serierundor = antal öl - 16 (för finalspelet!)
				 */
				Settings.MAX_NUMBER_OF_SERIEROUNDS = (Integer.parseInt(bottles.getText().toString()) - 16) / 2;
				startBracketGame();
			}

		});
		numberBeers.show();

	}

	private void startBracketGame() {

		new Game(GameMode.BRACKET_MODE);
		Intent myIntent = new Intent(this, AddPlayerActivity.class);
		startActivityForResult(myIntent, 0);
	}

	public boolean btnSettings(MenuItem v) {
		Intent myIntent = new Intent(this, SettingActivity.class);
		startActivityForResult(myIntent, 0);
		return true;
	}
}
