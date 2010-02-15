/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 13, 2010
 */
package net.sf.zekr.common.util;

import net.sf.zekr.engine.audio.AudioData;

/**
 * @author Mohsen Saboorian
 */
public class ProgressListener {
	public void start(long totalSize) {
	}

	public boolean progress(long itemSize) {
		return true;
	}

	public void finish(AudioData audioData) {
	}
}
