/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 9, 2009
 */
package net.sf.zekr.ui;

import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import net.sf.zekr.engine.audio.PlayerController;
import net.sf.zekr.engine.audio.PlayerController.PlayMode;

/**
 * @author Mohsen Saboorian
 */
@SuppressWarnings("unchecked")
public class ZekrPlayerListener implements BasicPlayerListener {
	private PlayerController playerController;
	private QuranForm form;

	public ZekrPlayerListener(PlayerController playerController, QuranForm quranForm) {
		this.playerController = playerController;
		this.form = quranForm;
	}

	public void opened(Object stream, Map properties) {
	}

	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
	}

	public void setController(BasicController controller) {
	}

	public void stateUpdated(BasicPlayerEvent event) {
		int code = event.getCode();
		if (code == BasicPlayerEvent.EOM) {
			if (playerController.getPlayMode() == PlayMode.CONTINUOUS) {
				form.getDisplay().asyncExec(new Runnable() {
					public void run() {
						form.playerContinue();
					}
				});
			}
		} else {
		}
	}
}
