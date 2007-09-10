/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 9, 2007
 */
package net.sf.zekr.engine.audio;

import net.sf.zekr.engine.server.HttpServer;

public class AudioUtils {

	public static String getAudioUrl(AudioData audioData, String fileName) {
		String serverUrl = audioData.getAudioServerUrl();
		if (serverUrl == null)
			serverUrl = HttpServer.getServer().getUrl();
		else
			serverUrl += "/";

		return serverUrl + audioData.getRelativeAudioUrl(fileName);
	}
}
