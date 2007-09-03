/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 2, 2007
 */
package net.sf.zekr.engine.audio;

public class AudioData {
	/**
	 * Single playlist for each sura.
	 */
	public static final String SURA_PLAYLIST = "sura";

	/**
	 * A playlist for the whole Quran.
	 */
	public static final String WHOLE_QURAN_PLAYLIST = "all";

	public AudioData() {
	}

	public String getPlaylistMode() {
		return null;
	}

	public String getPlaylistFileName() {
		return null;
	}

	public String getAudioUrl() {
		return null;
	}

	/**
	 * Should always have a trailing slash, so that it can be concatenated with playlist file name.
	 * 
	 * @return
	 */
	public String getPlaylistUrl() {
		return null;
	}
}
