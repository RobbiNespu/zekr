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
public abstract class PlaylistProvider implements IPlaylistProvider {
	protected AudioData audioData;
	protected int suraNum;

	public PlaylistProvider(AudioData audioData, int suraNum) {
		this.audioData = audioData;
		this.suraNum = suraNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.engine.audio.IPlaylistProvider#providePlaylist()
	 */
	public abstract String providePlaylist() throws PlaylistProvisionException;

}
