package com.hugo.hafvklocka.drivers;

import com.hugo.hafvklocka.drivers.Clock.Actions;
import android.os.Message;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.*;

public class ClockTest extends AndroidTestCase{

	private Clock testee;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		testee = new Clock();
	}

	@MediumTest
	public void testClock_Doesnt_Differ_In_Time_Over_Normal_Length(){
		String expetedTime = runClockFor(445);
		assertEquals(expetedTime, testee.getText());
	}
	
	@MediumTest
    public void testClock_Doesnt_Differ_In_Time_Ober_Small_Length(){
		String expetedTime = runClockFor(34);
		assertEquals(expetedTime, testee.getText());
    }
	
	private String runClockFor(int milliSecunds){
		long startTime = System.currentTimeMillis();
		testee.handleMessage(Message.obtain(testee, Actions.START.getNumber()));
		try {
			Thread.sleep(milliSecunds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long stopTime = System.currentTimeMillis();
		testee.handleMessage(Message.obtain(testee, Actions.STOP.getNumber()));
		return Clock.getTimeString(stopTime - startTime);
	}

}
