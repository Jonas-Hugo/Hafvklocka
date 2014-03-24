package com.hugo.hafvklocka.drivers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Message;
import android.util.Log;

import com.hugo.hafvklocka.Settings;

public class Controller extends Thread {

	private boolean alive = true, clockStarted = false, timeStoped = false, alreadyStarted = false;
	private AudioRecord arec;
	private short[] buffer;
	private int buffersize, micSens, runningClockIndex = -1;
	private float triggerValue = -1000;
	private Clock[] clocks;
	private GameMode gameMode;
	private static SensorManager sensorService;
	private Sensor sensor;
	private Context context;

	public enum GameMode {
		TIME_MODE, BRACKET_MODE, MIC_TEST
	}

	public Controller(Context context, GameMode gameMode, Clock... clocks) {
		this.clocks = clocks;
		this.gameMode = gameMode;
		this.context = context;
		instanciateMic();
		instanciateAccelerometer();
	}

	private void instanciateAccelerometer() {
		sensorService = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (sensor != null) {
			sensorService.registerListener(accelerationSensorEventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
	}

	private void instanciateMic() {
		buffersize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		arec = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
		        AudioFormat.ENCODING_PCM_16BIT, buffersize);
		buffer = new short[buffersize];
		micSens = Settings.MIC_SENSITIVITY;
	}

	public void run() {
		arec.startRecording();
		while (alive) {
			switch (gameMode) {
			case TIME_MODE:
			case BRACKET_MODE:
				while ((!clockStarted || !timeStoped) && alive) {
					arec.read(buffer, 0, buffersize);
					for (short s : buffer) {
						if (Math.abs(s) > micSens && triggerValue > 10.0) {
							if (clockStarted)
								timeStoped = true;
							else {
								clockStarted = true;
							}
						}
					}
					if (clockStarted && !alreadyStarted) {
						startClocks();
						alreadyStarted = true;
					}
					if (timeStoped) {
						stopNextClock();
					}
					try {
						if (!timeStoped)
							Thread.sleep(600);
					} catch (InterruptedException e) {
						Log.e("MIC", "Faild at sleeping");
					}
				}
				break;
			case MIC_TEST:
				while (alive) {
					arec.read(buffer, 0, buffersize);
					int max = 0;
					for (short s : buffer) {
						if (max < Math.abs(s))
							max = Math.abs(s);
					}
					clocks[0].sendMessageDelayed(Message.obtain(clocks[0], max), 500);
				}
				break;
			}
		}
		stopRecord();
		this.interrupt();
	}

	private void stopNextClock() {
		if (runningClockIndex != -1) {
			clocks[runningClockIndex].stop();
			runningClockIndex += 1;
			if (runningClockIndex == clocks.length) {
				runningClockIndex = -1;
			}
		}
	}

	private void startClocks() {
		for (Clock clock : clocks) {
			clock.start();
		}
		if (clocks.length > 0) {
			runningClockIndex = 0;
		}
	}

	public void stopRecord() {
		arec.stop();
		arec.release();

		sensorService.unregisterListener(accelerationSensorEventListener);
	}

	public void kill() {
		alive = false;
	}

	public void reset() {
		clockStarted = false;
		timeStoped = false;
		alreadyStarted = false;
	}

	private SensorEventListener accelerationSensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			triggerValue = maxValue(event.values);
		}

		private float maxValue(float... accelerations) {
			triggerValue = -1000;
			for (float a : accelerations) {
				if (a > triggerValue) {
					triggerValue = a;
				}
			}
			return triggerValue;
		}
	};
}
