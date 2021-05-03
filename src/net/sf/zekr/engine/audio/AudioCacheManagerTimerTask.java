/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2009
 */
package net.sf.zekr.engine.audio;

import java.util.TimerTask;

import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 */
public class AudioCacheManagerTimerTask extends TimerTask {
	Logger logger = Logger.getLogger(this.getClass());
	private AudioCacheManager audioCacheManager;

	public AudioCacheManagerTimerTask(AudioCacheManager audioCacheManager) {
		this.audioCacheManager = audioCacheManager;
	}

	public void run() {
		logger.info("Run audio cache manager task silently.");
		try {
			audioCacheManager.flushCache();
		} catch (Exception e) {
			logger.implicitLog(e);
		}
	}
}
