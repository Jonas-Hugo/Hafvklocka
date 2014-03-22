package com.hugo.hafvklocka.activities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.hugo.hafvklocka.Konstants;
import com.hugo.hafvklocka.R;
import com.hugo.hafvklocka.Settings;
import com.hugo.hafvklocka.R.id;
import com.hugo.hafvklocka.R.layout;
import com.hugo.hafvklocka.R.menu;
import com.hugo.hafvklocka.drivers.Mic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import GraphView.*;
import GraphView.GraphView.GraphViewData;

public class SettingActivity extends Activity {
	
	private SeekBar micSens;
	private SeekBar maxHeats;
	private LineGraphView graphView;
	private GraphViewSeries series;
	private GraphViewSeries sens;
	private GraphViewData[] data;
	private TextView nrHeats;
	private Mic mic;
	private long time = 0;
	private final int xAxis = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        graphView = (LineGraphView)findViewById(R.id.graphLayout);
        micSens = (SeekBar)findViewById(R.id.sbMicSens);
        nrHeats = (TextView)findViewById(R.id.nrHeatsText);
        maxHeats = (SeekBar)findViewById(R.id.npMaxHeats);
        micSens.setProgress(Short.MAX_VALUE - Settings.MIC_SENSITIVITY);
        maxHeats.setActivated(true);
        maxHeats.setEnabled(true);
        maxHeats.setProgress(Settings.MAX_NUMBER_OF_SERIEROUNDS);
        nrHeats.setText("Antal: "+ Settings.MAX_NUMBER_OF_SERIEROUNDS);
        
        micSens.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            	Log.d("MicConfig","Aj det där kände jag!!!");
            	Settings.MIC_SENSITIVITY = Short.MAX_VALUE - micSens.getProgress();
            }

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {				
			}
        });
        maxHeats.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            	Log.d("maxHeats","Aj det där kände jag!!!");
            	Settings.MAX_NUMBER_OF_SERIEROUNDS = maxHeats.getProgress();
                nrHeats.setText("Antal: "+ Settings.MAX_NUMBER_OF_SERIEROUNDS);
            }

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
        });
        series = new GraphViewSeries(new GraphViewData[] {new GraphViewData(0, 0.0d), new GraphViewData(1, 1.0d)});
        data = new GraphViewData[] {new GraphViewData(0, (double)Settings.MIC_SENSITIVITY),
					new GraphViewData(1, (double)Settings.MIC_SENSITIVITY)};
        sens = new GraphViewSeries(data);
        initGraphView();
        
    }

    @Override
    public void onResume(){
    	super.onResume();
    	mic = new Mic(sound, Konstants.MIC_TEST);
    }
    @Override
    public void onPause(){
    	super.onPause();
    	mic.kill();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_setting, menu);
        return true;
    }
    private void initGraphView(){
    	graphView.setViewPort(0, (double)xAxis);
    	//graphView.setActivated(true);
    	graphView.setManualYAxis(true);
    	graphView.setManualYAxisBounds(32800, 0);
    	graphView.setScrollable(true); 
    	graphView.addSeries(series);
    	graphView.addSeries(sens);
    }
    
    public Handler sound = new Handler(){
    	public void handleMessage(Message m){
    		Log.d("MicConfig","the recorded sound has amplitude: " + m.what + ", mic sens is: " + Settings.MIC_SENSITIVITY);
    		sens.appendData(new GraphViewData((double)time, (double)Settings.MIC_SENSITIVITY), true);
    		series.appendData(new GraphViewData((double)time, (double)m.what), true);
    		time++;
    	}
    };    
    
    public void btnSave(View v){
    	FileOutputStream fos;
		try {
			fos = openFileOutput(Konstants.FILENAME, Context.MODE_PRIVATE);
        	fos.write(Settings.getOutput().getBytes());
        	fos.close();
		}catch(FileNotFoundException e1) {
			Log.d("Settings","Could not create file.");
		}catch(IOException e2){
			Log.d("Settings","Could not write to file.");
		}
    	
    }
}
