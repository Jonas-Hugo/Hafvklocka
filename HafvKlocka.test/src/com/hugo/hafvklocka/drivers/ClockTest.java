package com.hugo.hafvklocka.drivers;

import com.hugo.hafvklocka.drivers.Clock.Actions;

import android.os.Handler;
import android.os.Message;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.*;
import android.util.Log;

public class ClockTest extends AndroidTestCase{

	private Clock testee;
	private String text = "";
	final private Handler textHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			System.out.println("msg= " + msg.what);
		    text = Clock.getTimeString(msg.what);
			System.out.println("text= " + text);
		}
	};

	@Override
	public void setUp() throws Exception {
		super.setUp();
		testee = new Clock(textHandler);
	}

	@MediumTest
	public void testClock_Doesnt_Differ_In_Time_Over_Normal_Length(){
		String expetedTime = runClockFor(445);
		assertEquals(expetedTime, text);
	}
	
	@MediumTest
    public void testClock_Doesnt_Differ_In_Time_Ober_Small_Length(){
		String expetedTime = runClockFor(34);
		assertEquals(expetedTime, text);
    }
	
	@MediumTest
    public void testReset_Clock_Will_Set_Time_To_Zero() throws Exception {
		testee.start();
		try {
			Thread.sleep(120);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(Clock.getTimeString(0).equals(text));
		testee.reset();
		assertEquals(Clock.getTimeString(0), text);
    }
	
	private String runClockFor(int milliSecunds){
		long startTime = System.currentTimeMillis();
		testee.start();
		try {
			Thread.sleep(milliSecunds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long stopTime = System.currentTimeMillis();
		testee.stop();
		return Clock.getTimeString(stopTime - startTime);
	}

}
