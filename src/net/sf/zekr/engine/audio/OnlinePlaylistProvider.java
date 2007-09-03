/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

/**
 * @author Mohsen Saboorian
 */
public class OnlinePlaylistProvider extends PlaylistProvider {
	public OnlinePlaylistProvider(AudioData audioData, int suraNum) {
		super(audioData, suraNum);
	}

	public String providePlaylist() {
		String fileName = audioData.getPlaylistFileName();
		if (audioData.getPlaylistMode().equals(AudioData.SURA_PLAYLIST)) {
			fileName = audioData.getPlaylistFileName().replaceAll("\\{SURA\\}", String.valueOf(suraNum));
		} else { // a playlist for the whole Quran
			// do nothing
		}
		String url = audioData.getPlaylistUrl() + fileName;
		return url;
	}
}
