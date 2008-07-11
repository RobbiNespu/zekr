/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 9, 2007
 */
package net.sf.zekr.engine.audio;

import net.sf.zekr.common.config.ApplicationConfig;

public class AudioUtils {
	public static String getAudioUrl(AudioData audioData, String fileName) {
		String serverUrl = audioData.getAudioServerUrl();
		if (serverUrl == null)
			serverUrl = ApplicationConfig.getInstance().getHttpServer().getUrl();
		else
			serverUrl += "/";

		return serverUrl + audioData.getRelativeAudioUrl(fileName);
	}
}
