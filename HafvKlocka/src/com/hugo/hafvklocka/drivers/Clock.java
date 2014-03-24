package com.hugo.hafvklocka.drivers;

import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Message;

public class Clock extends Handler {

	public enum Actions {
		START(0), TICK(1), STOP(2), RESET(3);

		private int number;

		private Actions(int number) {
			this.number = number;
		}

		public int getNumber() {
			return number;
		}

		public static Actions getType(int number) {
			for (Actions a : Actions.values()) {
				if (a.getNumber() == number) {
					return a;
				}
			}
			return RESET;
		}
	}

	private long initTime = 0;
	private String timeText;
	private Handler textHandler;

	public Clock(Handler textHandler) {
		this.textHandler = textHandler;
	}

	@Override
	public void handleMessage(Message m) {
		switch (Actions.getType(m.what)) {
		case START:
			initTime = System.currentTimeMillis();
			sendMessageDelayed(Message.obtain(this, Actions.TICK.getNumber()), 10);
			setText();
			break;
		case TICK:
			sendMessageDelayed(Message.obtain(this, Actions.TICK.getNumber()), 10);
			setText();
			break;
		case STOP:
			this.removeMessages(Actions.TICK.getNumber());
			setText();
			break;
		case RESET:
			this.removeMessages(Actions.TICK.getNumber());
			resetText();
			break;
		}
	}

	public void start() {
		handleMessage(Message.obtain(this, Actions.START.getNumber()));
	}

	public void stop() {
		handleMessage(Message.obtain(this, Actions.STOP.getNumber()));
	}

	public void reset() {
		handleMessage(Message.obtain(this, Actions.RESET.getNumber()));
	}

	private void setText() {
		long now = System.currentTimeMillis();
		long delta = now - initTime;
		textHandler.sendMessage(Message.obtain(textHandler, (int) delta));
	}

	private void resetText() {
		textHandler.sendMessage(Message.obtain(textHandler, 0));
	}

	public static String getTimeString(long delta) {
		String time = "";
		long min = TimeUnit.MILLISECONDS.toMinutes(delta);
		long sec = TimeUnit.MILLISECONDS.toSeconds(delta - TimeUnit.MINUTES.toMillis(min));
		long hs = Math.round(TimeUnit.MILLISECONDS.toMillis(delta - TimeUnit.MINUTES.toMillis(min)
		        - TimeUnit.SECONDS.toMillis(sec)) / 10.0);
		time = (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec) + ":" + (hs < 10 ? "0" + hs : hs);
		return time;
	}

}
