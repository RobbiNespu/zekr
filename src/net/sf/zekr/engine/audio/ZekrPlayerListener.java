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
import net.sf.zekr.common.util.CommonUtils;
import net.sf.zekr.engine.audio.PlayerController.PlayingItem;
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
	private boolean userActionPerformed = false;

	public ZekrPlayerListener(PlayerController playerController, QuranForm quranForm) {
		this.playerController = playerController;
		this.quranForm = quranForm;
		repeatTimer = playerController.getRepeatTime();
		display = quranForm.getDisplay();
	}

	public void opened(Object stream, Map properties) {
	}

	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
	}

	public void setController(BasicController controller) {
	}

	public void stateUpdated(BasicPlayerEvent event) {
		final int code = event.getCode();
		display.syncExec(new Runnable() {
			public void run() {
				if (!quranForm.isDisposed()) {
					if (code == BasicPlayerEvent.PLAYING) {
						if (quranForm.playerUiController.isAudioControllerFormOpen()) {
							quranForm.playerUiController.togglePlayPauseState(true);
						}
					} else if (code == BasicPlayerEvent.PAUSED || code == BasicPlayerEvent.STOPPED) {
						if (quranForm.playerUiController.isAudioControllerFormOpen()) {
							quranForm.playerUiController.togglePlayPauseState(false);
						}
					}
					quranForm.playerUiController.playerUpdateAudioFormStatus();
				}
			}
		});
		if (code == BasicPlayerEvent.OPENING || code == BasicPlayerEvent.STOPPED) {
			origRepeatTimer = playerController.getRepeatTime();
			userActionPerformed = false;
		}
		if (code == BasicPlayerEvent.EOM) {
			repeatTimer--;
			if (playerController.isMultiAya()) {
				int wait = playerController.getInterval();
				if (playerController.getRepeatTime() != origRepeatTimer) {
					repeatTimer += playerController.getRepeatTime() - origRepeatTimer;
					origRepeatTimer = playerController.getRepeatTime();
				}
				if (repeatTimer > 0 && playerController.getPlayingItem() == PlayingItem.AYA) {
					if (!quranForm.isDisposed()) {
						display.asyncExec(getAyaPlayerRunnable(false, wait));
					}
				} else {
					repeatTimer = origRepeatTimer;
					if (!quranForm.isDisposed()) {
						display.asyncExec(getAyaPlayerRunnable(true, wait));
					}
				}
			} else {
				// stop
				if (!quranForm.isDisposed()) {
					display.asyncExec(new Runnable() {
						public void run() {
							quranForm.playerUiController.playerStop(false);
						}
					});
				}
			}
		}
	}

	private Runnable getAyaPlayerRunnable(final boolean gotoNext, final int wait) {
		if (wait > 0 && playerController.getPlayingItem() == PlayingItem.AYA) {
			try {
				Thread.sleep(wait);
				if (userActionPerformed) {
					userActionPerformed = false;
					return CommonUtils.EMPTY_RUNNABLE;
				}
			} catch (InterruptedException e) {
			}
		}
		return new Runnable() {
			public void run() {
				quranForm.playerUiController.playerContinue(gotoNext);
			}
		};
	}

	public void userPressedPlayButton() {
		userPressedSomeButton();
		if (playerController.getRepeatTime() != origRepeatTimer) {
			origRepeatTimer = playerController.getRepeatTime();
			repeatTimer = origRepeatTimer;
		}
	}

	public void userPressedSomeButton() {
		userActionPerformed = true;
	}
}
