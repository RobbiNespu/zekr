/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

public interface IPlaylistProvider {
	/**
	 * Provides playlist file path. If this is an online provider, it just returns the absolute URL to playlist. If
	 * offline, and playlist doesn't already exist, first creates the playlist and returns URL to that.
	 * 
	 * @return URL for provided playlist
	 */
	String providePlaylist() throws PlaylistProvisionException;
}
