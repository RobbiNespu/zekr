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
	/** A'oodho billah... */
	public final static String SPECIAL_PRESTART = "prestart";
	/** Bismillah... */
	public final static String SPECIAL_START = "start";
	/** SadaqAllah... */
	public final static String SPECIAL_END = "end";

	/**
	 * Provides playlist file path. If this is an online provider, it just returns the absolute URL to playlist. If
	 * offline, and playlist doesn't already exist, first creates the playlist and returns URL to that.
	 * 
	 * @return URL for provided playlist
	 */
	String providePlaylist() throws PlaylistProvisionException;

	/**
	 * Playlists can be in two modes: <em>`sura'</em> or <em>`collection'</em> mode. For the former (aya), provider
	 * should always <code>return aya - 1;</code>, regardless of sura/aya number, since in sura mode every sura has a
	 * corresponding playlist in which each playling item (counted from 0) is mapped exactly to its corresponding aya
	 * (counted from 1).<br>
	 * The latter (collection) mode, on the other hand, is a single playlist in which there are all playing items, so
	 * playlist provider should provide the exact item number for each aya in a sura.
	 * 
	 * @param sura
	 *           sura number in which corresponding aya should be returned as a playlist item. This parameter is 1-based.
	 * @param aya
	 *           aya number to be resolved to an item. This parameter is 1-based.
	 * @return playlist item to be played, corresponding to the given sura/aya number. This number is 0-based.
	 */
	int getItem(int sura, int aya);

	/**
	 * @return {@link AudioData} object for the playlist
	 */
	AudioData getAudioData();

	/**
	 * This method is to lookup some special audio file index in the playlist.
	 * 
	 * @param name
	 *           name of the special item (bismillah for example is {@link #SPECIAL_START})
	 * @return item index in the playlist
	 */
	int getSpecialItem(String name);
}
