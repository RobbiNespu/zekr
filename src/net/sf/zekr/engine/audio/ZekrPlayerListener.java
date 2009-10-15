/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 9, 2009
 */
package net.sf.zekr.engine.audio;

import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import net.sf.zekr.ui.QuranForm;

import org.eclipse.swt.widgets.Display;

/**
 * @author Mohsen Saboorian
 */
@SuppressWarnings("unchecked")
public class ZekrPlayerListener implements BasicPlayerListener {
	private PlayerController playerController;
	private QuranForm quranForm;
	private int repeatTimer;
	private Display display;
	private int origRepeatTimer;

	public ZekrPlayerListener(PlayerController playerController, QuranForm quranForm) {
		this.playerController = playerController;
		this.quranForm = quranForm;
		repeatTimer = playerController.getRepeatTime();
	}

	public void opened(Object stream, Map properties) {
	}

	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
	}

	public void setController(BasicController controller) {
	}

	public void stateUpdated(BasicPlayerEvent event) {
		final int code = event.getCode();
		quranForm.getDisplay().syncExec(new Runnable() {
			public void run() {
				quranForm.playerUpdateAudioFormStatus(code);
			}
		});
		if (code == BasicPlayerEvent.OPENING || code == BasicPlayerEvent.STOPPED) {
			origRepeatTimer = playerController.getRepeatTime();
		}
		if (code == BasicPlayerEvent.EOM) {
			repeatTimer--;
			if (playerController.isMultiAya()) {
				int wait = playerController.getLapse();
				if (playerController.getRepeatTime() != origRepeatTimer) {
					System.out.println(String.format("Diff (%s): %s, %s", repeatTimer, playerController.getRepeatTime(), origRepeatTimer));
					repeatTimer += playerController.getRepeatTime() - origRepeatTimer;
					origRepeatTimer = playerController.getRepeatTime();
				}
				if (repeatTimer > 0) {
					quranForm.getDisplay().asyncExec(getAyaPlayerRunnable(false, wait));
				} else {
					repeatTimer = origRepeatTimer;
					quranForm.getDisplay().asyncExec(getAyaPlayerRunnable(true, wait));
				}
			}
		}
	}

	private Runnable getAyaPlayerRunnable(final boolean gotoNext, final int wait) {
		if (wait <= 0) {
			return new Runnable() {
				public void run() {
					quranForm.playerContinue(gotoNext);
				}
			};
		} else {
			Thread t = new Thread() {
				public void run() {
					try {
						sleep(wait);
					} catch (InterruptedException e) {
					}
					quranForm.playerContinue(gotoNext);
				}
			};
			t.setDaemon(true);
			return t;
		}
	}
}
