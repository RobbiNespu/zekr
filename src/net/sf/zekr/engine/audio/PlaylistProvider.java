/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

import net.sf.zekr.common.resource.QuranPropertiesUtils;

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

	/**
	 * Default getItem behavior is implemented in this class, and can be overriden for special playlists.<br>
	 * If audio data is in sura mode, this method does <code>return aya - 1;</code>, regardless of sura/aya number,
	 * since in sura mode every sura has a corresponding playlist in which each playling item (counted from 0) is mapped
	 * exactly to its corresponding aya (counted from 1).<br>
	 * For audio data in collection mode assumption is that sura items are located regularly one sura after the other in
	 * natural Quran order. So exact number of an item for sura <i>i</i> is equal to aggregative sum of aya count from
	 * sura 1 to sura i - 1 plus aya parameter - 1:<br>
	 * (for sura: 1 to sura parameter) &#8721;(<i>aya count</i>) + <i>aya parameter</i> - 1</i>
	 * 
	 * @param sura
	 *           sura number in which corresponding aya should be returned as a playlist item. This parameter is 1-based.
	 * @param aya
	 *           aya number to be resolved to an item. This parameter is 1-based.
	 * @return playlist item to be played, corresponding to the given sura/aya number. This number is 0-based.
	 */
	public int getItem(int sura, int aya) {
		if (audioData.getPlaylistMode().equals(AudioData.SURA_PLAYLIST)) {
			return aya - 1;
		} else {
			return QuranPropertiesUtils.getAggregativeAyaCount(suraNum) + aya - 1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.engine.audio.IPlaylistProvider#getAudioData()
	 */
	public AudioData getAudioData() {
		return audioData;
	}

	/**
	 * Default implementation for special audio item index lookup. This implementation assumes that special items are
	 * located at the end of playlist (if any): <code>SPECIAL_PRESTART</code>, <code>SPECIAL_START</code>, and
	 * <code>SPECIAL_END</code> respectively.
	 * 
	 * @param name
	 *           the name of special audio item
	 * @return special audio item index, or -1 if there is no such item for the playlist
	 */
	public int getSpecialItem(String name) {
		int index = 0;
		if (audioData.getPlaylistMode().equals(AudioData.COLLECTION_PLAYLIST)) {
			index = QuranPropertiesUtils.QURAN_AYA_COUNT;
		} else {
			index = QuranPropertiesUtils.getSura(suraNum).getAyaCount();
		}
		if (SPECIAL_PRESTART.equals(name)) {
			if (audioData.getPrestartFileName() != null)
				return index;
			else
				return -1;
		} else if (SPECIAL_START.equals(name)) {
			if (audioData.getStartFileName() != null)
				return index + 1;
			else
				return -1;
		} else if (SPECIAL_END.equals(name)) {
			if (audioData.getEndFileName() != null)
				return index + 2;
			else
				return -1;
		}
		return -1;
	}
}
