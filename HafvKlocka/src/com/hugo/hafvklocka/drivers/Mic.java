package com.hugo.hafvklocka.drivers;

import com.hugo.hafvklocka.Konstants;
import com.hugo.hafvklocka.Settings;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Mic extends Thread{

	private boolean alive = true;
    private AudioRecord arec;
    private short[] buffer;
    private int buffersize;
    private Handler Clock;
    private int gameMode, micSens;
	private boolean clockStarted = false, firstStoped = false, timeStoped = false;
	private boolean alreadyStarted = false, alreadyStoped = false;
    
    public Mic(Handler c, int gm){
    	
    	this.Clock = c;
    	this.gameMode = gm;
        buffersize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        arec = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffersize);
        buffer = new short[buffersize];
        micSens = Settings.MIC_SENSITIVITY;
        
        this.start();
    	
    }
    

    public void run(){
    	arec.startRecording();
    	while(alive){
    		switch(gameMode){
    		case Konstants.TIME_MODE:
    			while((!clockStarted || !timeStoped) && alive){
    				arec.read(buffer, 0, buffersize);
    				boolean theOneAndOnly = false;
    				for(short s : buffer){
    					if(Math.abs(s) > micSens && !theOneAndOnly){
    						theOneAndOnly = true;
    						if(clockStarted ) timeStoped = true;
    						else{
    							clockStarted = true;
    						}
    					}
    				}
    				if(clockStarted && !alreadyStarted){
    					Clock.sendMessage(Message.obtain(Clock, Konstants.START_ONE));
    					alreadyStarted = true;
    				}
    				if(timeStoped){
    					Clock.sendMessage(Message.obtain(Clock, Konstants.STOP_FIRST));
    				}
    				try {
    					if(theOneAndOnly && !timeStoped) Thread.sleep(600);
    				} catch (InterruptedException e) {
    					Log.e("MIC","Faild at sleeping");
    				}
    			}
    			break;
    		case Konstants.BRACKET_MODE:
    			while((!clockStarted || !timeStoped || !firstStoped) && alive){
    				arec.read(buffer, 0, buffersize);
    				boolean theOneAndOnly = false;
    				for(short s : buffer){
    					if(Math.abs(s) > micSens && !theOneAndOnly){
    						theOneAndOnly = true;
    						if(clockStarted ){
    							if(firstStoped) timeStoped = true;
    							else firstStoped = true;
    						}
    						else{
    							clockStarted = true;
    						}
    					}
    				}
    				if(clockStarted && !alreadyStarted){
    					Clock.sendMessage(Message.obtain(Clock, Konstants.START_TWO));
    					alreadyStarted = true;
    				}
    				if(firstStoped && !alreadyStoped){
    					Clock.sendMessage(Message.obtain(Clock, Konstants.STOP_FIRST));
    					alreadyStoped = true;        			
    				}
    				if(timeStoped){
    					Clock.sendMessage(Message.obtain(Clock, Konstants.STOP_LAST));
    				}
    				try {
    					if(theOneAndOnly && !timeStoped) Thread.sleep(600);
    				} catch (InterruptedException e) {
    					Log.e("MIC","Faild at sleeping");
    				}
    			}
    			break;
    		case Konstants.MIC_TEST:
    			while(alive){
    				arec.read(buffer, 0, buffersize);
    				int max = 0;
    				for(short s : buffer){
    					if(max < Math.abs(s)) max = Math.abs(s);
    				}
    				Clock.sendMessageDelayed(Message.obtain(Clock, max), 500);
    			}
    			break;
    		}
    	}
    	stopRecord();
    	this.interrupt();
    }
    
    public void stopRecord(){
    	arec.stop();
    	arec.release();
    }
    public void kill(){
    	alive = false;
    }
    public void reset(){
    	clockStarted = false;
    	firstStoped = false;
    	timeStoped = false;
    	alreadyStarted = false;
    	alreadyStoped = false;
    }
}
