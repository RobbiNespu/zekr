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
import net.sf.zekr.engine.server.HttpServer;
import net.sf.zekr.engine.server.HttpServerException;
import net.sf.zekr.engine.template.BaseViewTemplate;
import net.sf.zekr.engine.template.TemplateTransformationException;

import org.apache.commons.lang.StringUtils;

/**
 * @author Mohsen Saboorian
 */
public class OfflinePlaylistProvider extends PlaylistProvider {
	/**
	 * @param audioData
	 * @param suraNum
	 *           1-base sura number; only used when playlist mode is "sura" (not "all")
	 */
	public OfflinePlaylistProvider(AudioData audioData, int suraNum) {
		super(audioData, suraNum);
	}

	public String providePlaylist() throws PlaylistProvisionException {
		String fileName = audioData.getPlaylistFileName();
		if (audioData.getPlaylistMode().equals(AudioData.SURA_PLAYLIST)) {
			String playlistSuraPad = audioData.getPlaylistSuraPad();
			String s = StringUtils.leftPad(String.valueOf(suraNum), playlistSuraPad.length() + 1, playlistSuraPad);
			fileName = StringUtils.replace(fileName, "{SURA}", s);
		} else { // a playlist for the whole Quran
			// do nothing
		}
		String playlistUrl = Naming.getAudioCacheDir() + "/" + fileName;

		if (new File(playlistUrl).exists()) { // do nothing
			return playlistUrl;
		}

		List trackList = new ArrayList();
		String filePattern = audioData.getAudioFileName();
		String suraPad = audioData.getAudioFileSuraPad();
		String ayaPad = audioData.getAudioFileAyaPad();

		final String[] ayaParts = new String[290];
		for (int ap = 0; ap < ayaParts.length; ap++) {
			ayaParts[ap] = StringUtils.leftPad(String.valueOf(ap + 1), ayaPad.length() + 1, ayaPad);
		}

		final String[] suraParts = new String[120];
		for (int sp = 0; sp < suraParts.length; sp++) {
			suraParts[sp] = StringUtils.leftPad(String.valueOf(sp + 1), suraPad.length() + 1, suraPad);
		}
		QuranProperties quranProps = QuranProperties.getInstance();
		String serverUrl;
		try {
			serverUrl = HttpServer.getServer().getUrl();
		} catch (HttpServerException e) {
			throw new PlaylistProvisionException(e);
		}
		if (audioData.getPlaylistMode().equals(AudioData.SURA_PLAYLIST)) {
			SuraProperties suraProps = quranProps.getSura(suraNum);
			for (int aya = 0; aya < suraProps.getAyaCount(); aya++) {
				String s = StringUtils.replace(filePattern, "{SURA}", suraParts[suraNum - 1]);
				s = StringUtils.replace(s, "{AYA}", ayaParts[aya]);
				String url = serverUrl + audioData.getAudioBaseUrl() + "/" + s;
				trackList.add(new Track(suraProps, aya + 1, url));
			}
		} else {
			for (int sura = 0; sura < 114; sura++) {
				SuraProperties suraProps = quranProps.getSura(sura + 1);
				for (int aya = 0; aya < suraProps.getAyaCount(); aya++) {
					String s = StringUtils.replace(filePattern, "{SURA}", suraParts[sura]);
					s = StringUtils.replace(s, "{AYA}", ayaParts[aya]);
					String url = serverUrl + audioData.getAudioBaseUrl() + "/" + s;
					trackList.add(new Track(suraProps, aya + 1, url));
				}
			}
		}
		OfflinePlaylistTempalate olpt = new OfflinePlaylistTempalate(playlistUrl, trackList);
		try {
			olpt.transform();
		} catch (TemplateTransformationException e) {
			throw new PlaylistProvisionException(e);
		}
		return playlistUrl;
	}

	private class OfflinePlaylistTempalate extends BaseViewTemplate {
		String playlistFilePath;

		public OfflinePlaylistTempalate(String playlistFilePath, List trackList) {
			// super();
			this.playlistFilePath = playlistFilePath;
			engine.put("TRACK_LIST", trackList);
			engine.put("AUDIO_DATA", audioData);
		}

		public String transform() throws TemplateTransformationException {
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
