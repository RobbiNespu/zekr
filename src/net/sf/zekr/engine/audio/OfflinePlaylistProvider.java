/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.SuraProperties;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.template.BaseViewTemplate;
import net.sf.zekr.engine.template.TemplateTransformationException;

import org.apache.commons.lang.StringUtils;

/**
 * Offline path provider for playlist files.
 * 
 * @author Mohsen Saboorian
 */
public class OfflinePlaylistProvider extends PlaylistProvider {
	/**
	 * @param audioData
	 * @param suraNum
	 *           1-base sura number; only used when playlist mode is <em>sura<em> (not <em>collection</em>)
	 */
	public OfflinePlaylistProvider(AudioData audioData, int suraNum) {
		super(audioData, suraNum);
	}

	public String providePlaylist() throws PlaylistProvisionException {
		try {
			String fileName = audioData.getPlaylistFileName();
			if (audioData.getPlaylistMode().equals(AudioData.SURA_PLAYLIST)) {
				String playlistSuraPad = audioData.getPlaylistSuraPad();
				String s = StringUtils.leftPad(String.valueOf(suraNum), playlistSuraPad.length() + 1, playlistSuraPad);
				fileName = StringUtils.replace(fileName, "{SURA}", s);
			} else { // a playlist for the whole Quran
				fileName = StringUtils.replace(fileName, "{SURA}", "all");
			}
			String playlistUrl = Naming.getAudioCacheDir() + "/" + fileName;

			if (new File(playlistUrl).exists()) { // do nothing
				return playlistUrl;
			}

			List trackList = new ArrayList();
			String filePattern = audioData.getAudioFileName();
			String suraPad = audioData.getAudioFileSuraPad();
			String ayaPad = audioData.getAudioFileAyaPad();

			// caching...
			final String[] ayaParts = new String[290];
			for (int ap = 0; ap < ayaParts.length; ap++) {
				ayaParts[ap] = StringUtils.leftPad(String.valueOf(ap + 1), ayaPad.length() + 1, ayaPad);
			}
			final String[] suraParts = new String[120];
			for (int sp = 0; sp < suraParts.length; sp++) {
				suraParts[sp] = StringUtils.leftPad(String.valueOf(sp + 1), suraPad.length() + 1, suraPad);
			}

			QuranProperties quranProps = QuranProperties.getInstance();

			if (audioData.getPlaylistMode().equals(AudioData.SURA_PLAYLIST)) {
				SuraProperties suraProps = quranProps.getSura(suraNum);
				for (int aya = 0; aya < suraProps.getAyaCount(); aya++) {
					String s = StringUtils.replace(filePattern, "{SURA}", suraParts[suraNum - 1]);
					s = StringUtils.replace(s, "{AYA}", ayaParts[aya]);
					String url = AudioUtils.getAudioUrl(audioData, s);
					trackList.add(new Track(suraProps, aya + 1, url));
				}
			} else {
				for (int sura = 0; sura < 114; sura++) {
					SuraProperties suraProps = quranProps.getSura(sura + 1);
					for (int aya = 0; aya < suraProps.getAyaCount(); aya++) {
						String s = StringUtils.replace(filePattern, "{SURA}", suraParts[sura]);
						s = StringUtils.replace(s, "{AYA}", ayaParts[aya]);
						String url = AudioUtils.getAudioUrl(audioData, s);
						trackList.add(new Track(suraProps, aya + 1, url));
					}
				}
			}
			OfflinePlaylistTempalate olpt = new OfflinePlaylistTempalate(playlistUrl, trackList);
			olpt.transform();
			return playlistUrl;
		} catch (Exception e) {
			throw new PlaylistProvisionException(e);
		}
	}

	private class OfflinePlaylistTempalate extends BaseViewTemplate {
		String playlistFilePath;

		public OfflinePlaylistTempalate(String playlistFilePath, List trackList) {
			// super();
			this.playlistFilePath = playlistFilePath;
			engine.put("TRACK_LIST", trackList);
			engine.put("AUDIO_DATA", audioData);

			ArrayList specialItems = new ArrayList();
			if (audioData.getPrestartFileName() != null)
				specialItems.add(AudioUtils.getAudioUrl(audioData, audioData.getPrestartFileName()));
			else
				specialItems.add("");
			if (audioData.getStartFileName() != null)
				specialItems.add(AudioUtils.getAudioUrl(audioData, audioData.getStartFileName()));
			else
				specialItems.add("");
			if (audioData.getEndFileName() != null)
				specialItems.add(AudioUtils.getAudioUrl(audioData, audioData.getEndFileName()));
			else
				specialItems.add("");
			engine.put("SPECIAL_ITEM_LIST", specialItems);
		}

		public String doTransform() throws TemplateTransformationException {
			String retStr = "";
			try {
				retStr = engine.getUpdated(resource.getString("audio.playlist.template"));
				String destFile = playlistFilePath;
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
				osw.write(retStr);
				osw.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).log(e);
			}
			return retStr;
		}
	}
}
