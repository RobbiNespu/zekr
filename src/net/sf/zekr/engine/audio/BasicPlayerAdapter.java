/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 11, 2010
 */
package net.sf.zekr.engine.audio;

import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * @author Mohsen Saboorian
 */
public class BasicPlayerAdapter implements BasicPlayerListener {
	protected ZekrBasicPlayer zekrBasicPlayer;

	public BasicPlayerAdapter(ZekrBasicPlayer zekrBasicPlayer) {
		this.zekrBasicPlayer = zekrBasicPlayer;
	}

	public BasicPlayerAdapter() {
	}

	@SuppressWarnings("unchecked")
	public void opened(Object stream, Map properties) {
	}

	@SuppressWarnings("unchecked")
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
	}

	public void setController(BasicController controller) {
	}

	public void stateUpdated(BasicPlayerEvent event) {
	}
}
