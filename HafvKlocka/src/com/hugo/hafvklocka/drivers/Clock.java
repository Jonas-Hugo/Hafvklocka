package com.hugo.hafvklocka.drivers;

import android.os.Handler;
import android.os.Message;
import android.widget.*;

import java.util.concurrent.TimeUnit;

import com.hugo.hafvklocka.Konstants;

public class Clock extends Handler{

	private long initTime = 0;
	private TextView time1, time2;
	private boolean firstStoped = false;
	private Handler done;

	
	public Clock(TextView tv){
		time1 = tv;
	}
	
	public Clock(TextView tv1, TextView tv2, Handler h){
		time1 = tv1;
		time2 = tv2;
		done = h;
	}
	

    public void handleMessage(Message m) {
    	switch(m.what){
    	case Konstants.START_ONE:
	    	initTime = System.currentTimeMillis();
        	sendMessageDelayed(Message.obtain(this, Konstants.TICK_ONE), 10);
        	break;
    	case Konstants.START_TWO:
	    	initTime = System.currentTimeMillis();
        	sendMessageDelayed(Message.obtain(this, Konstants.TICK_TWO), 10);
        	firstStoped = false;
        	break;
    	case Konstants.TICK_ONE:
        	sendMessageDelayed(Message.obtain(this, Konstants.TICK_ONE), 10);
        	writeToTextView(time1);
    		break;
    	case Konstants.TICK_TWO:
        	sendMessageDelayed(Message.obtain(this, Konstants.TICK_TWO), 10);
        	if(firstStoped) writeToTextView(time2);
        	else writeToTextView(time1, time2);
    		break;
    	case Konstants.STOP_FIRST:
        	writeToTextView(time1);
        	firstStoped = true;
        	this.removeMessages(Konstants.TICK_ONE);
    		break;
    	case Konstants.STOP_LAST:
        	writeToTextView(time2);
        	this.removeMessages(Konstants.TICK_TWO);
        	done.sendMessage(Message.obtain(this, 0));
    		break;
		case Konstants.RESET:
        	this.removeMessages(Konstants.TICK_ONE);
			this.removeMessages(Konstants.TICK_TWO);
			break;
    	}
	}
    
    private void writeToTextView(TextView tv){
        long now = System.currentTimeMillis();
        long delta = now - initTime;
    	tv.setText(getTimeString(delta));
    }
    private void writeToTextView(TextView tv1, TextView tv2){
        long now = System.currentTimeMillis();
        long delta = now - initTime;
        String output = getTimeString(delta);
    	tv1.setText(output);
    	tv2.setText(output);
    }

    public static String getTimeString(long delta){
    	String time = "";
        long min = TimeUnit.MILLISECONDS.toMinutes(delta);
        long sec = TimeUnit.MILLISECONDS.toSeconds(delta - TimeUnit.MINUTES.toMillis(min));
        long hs = Math.round(TimeUnit.MILLISECONDS.toMillis(delta - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec))/10.0);
        time = (min < 10 ? "0"+min : min) + ":" + (sec < 10 ? "0"+sec : sec) + ":" + (hs < 10 ? "0"+hs : hs);
    	return time;
    }
	
}
