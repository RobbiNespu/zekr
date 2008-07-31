/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

import org.apache.commons.lang.StringUtils;

/**
 * URL provider for online playlists.
 * 
 * @author Mohsen Saboorian
 */
public class OnlinePlaylistProvider extends PlaylistProvider {
	/**
	 * @param audioData
	 * @param pageNum
	 *           1-base page number
	 */
	public OnlinePlaylistProvider(AudioData audioData, int pageNum) {
		super(audioData, pageNum);
	}

	public String providePlaylist() {
		String fileName = audioData.getPlaylistFileName();
		if (audioData.getPlaylistMode().equals(AudioData.SURA_PLAYLIST)) {
			String playlistSuraPad = audioData.getPlaylistSuraPad();
			String s = StringUtils.leftPad(String.valueOf(pageNum), playlistSuraPad.length() + 1, playlistSuraPad);
			fileName = StringUtils.replace(fileName, "{SURA}", s);
		} else { // a playlist for the whole Quran
			// do nothing
		}
		String url = audioData.getPlaylistBaseUrl() + fileName;
		return url;
	}
}
