///*
// *               In the name of Allah
// * This file is part of The Zekr Project. Use is subject to
// * license terms.
// *
// * Author:         Mohsen Saboorian
// * Start Date:     Sep 3, 2007
// */
//package net.sf.zekr.engine.audio;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStreamWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//import net.sf.zekr.common.config.ApplicationConfig;
//import net.sf.zekr.common.resource.IQuranLocation;
//import net.sf.zekr.common.resource.IQuranPage;
//import net.sf.zekr.common.runtime.Naming;
//import net.sf.zekr.engine.log.Logger;
//import net.sf.zekr.engine.template.BaseViewTemplate;
//import net.sf.zekr.engine.template.TemplateTransformationException;
//
//import org.apache.commons.lang.StringUtils;
//
///**
// * Offline path provider for playlist files.
// * 
// * @author Mohsen Saboorian
// */
//public class OfflinePlaylistProvider extends PlaylistProvider {
//	/**
//	 * @param audioData
//	 * @param pageNum 1-base page number
//	 */
//	public OfflinePlaylistProvider(AudioData audioData, int pageNum) {
//		super(audioData, pageNum);
//	}
//
//	public String providePlaylist() throws PlaylistProvisionException {
//		try {
//			String fileName = audioData.getPlaylistFileName();
//			if (audioData.getPlaylistMode().equals(AudioData.SURA_PLAYLIST)) {
//				String playlistSuraPad = audioData.getPlaylistSuraPad();
//				String s = StringUtils.leftPad(String.valueOf(pageNum), playlistSuraPad.length() + 1, playlistSuraPad);
//				fileName = StringUtils.replace(fileName, "{SURA}", s);
//			} else { // a playlist for the whole Quran
//				fileName = StringUtils.replace(fileName, "{SURA}", "all");
//			}
//			String playlistUrl = Naming.getAudioCacheDir() + "/" + fileName;
//
//			if (new File(playlistUrl).exists()) { // do nothing
//				return playlistUrl;
//			}
//
//			List trackList = new ArrayList();
//			String filePattern = audioData.getAudioFileName();
//			String suraPad = audioData.getAudioFileSuraPad();
//			String ayaPad = audioData.getAudioFileAyaPad();
//
//			// caching...
//			final String[] ayaParts = new String[290];
//			for (int ap = 0; ap < ayaParts.length; ap++) {
//				ayaParts[ap] = StringUtils.leftPad(String.valueOf(ap), ayaPad.length() + 1, ayaPad);
//			}
//			final String[] suraParts = new String[120];
//			for (int sp = 0; sp < suraParts.length; sp++) {
//				suraParts[sp] = StringUtils.leftPad(String.valueOf(sp), suraPad.length() + 1, suraPad);
//			}
//
//			IQuranPage quranPage = ApplicationConfig.getInstance().getQuranPaging().getDefault().getQuranPage(pageNum);
//			IQuranLocation from = quranPage.getFrom();
//			IQuranLocation to = quranPage.getTo();
//			while (from != null && from.compareTo(to) <= 0) {
//				String s = StringUtils.replace(filePattern, "{SURA}", suraParts[from.getSura()]);
//				s = StringUtils.replace(s, "{AYA}", ayaParts[from.getAya()]);
//				String url = AudioUtils.getAudioUrl(audioData, s);
//				trackList.add(new Track(from, url));
//				from = from.getNext();
//			}
//
//			OfflinePlaylistTempalate olpt = new OfflinePlaylistTempalate(playlistUrl, trackList);
//			olpt.transform();
//			return playlistUrl;
//		} catch (Exception e) {
//			throw new PlaylistProvisionException(e);
//		}
//	}
//
//	private class OfflinePlaylistTempalate extends BaseViewTemplate {
//		String playlistFilePath;
//
//		public OfflinePlaylistTempalate(String playlistFilePath, List trackList) {
//			// super();
//			this.playlistFilePath = playlistFilePath;
//			engine.put("TRACK_LIST", trackList);
//			engine.put("AUDIO_DATA", audioData);
//
//			ArrayList specialItems = new ArrayList();
//			if (audioData.getPrestartFileName() != null)
//				specialItems.add(AudioUtils.getAudioUrl(audioData, audioData.getPrestartFileName()));
//			else
//				specialItems.add("");
//			if (audioData.getStartFileName() != null)
//				specialItems.add(AudioUtils.getAudioUrl(audioData, audioData.getStartFileName()));
//			else
//				specialItems.add("");
//			if (audioData.getEndFileName() != null)
//				specialItems.add(AudioUtils.getAudioUrl(audioData, audioData.getEndFileName()));
//			else
//				specialItems.add("");
//			engine.put("SPECIAL_ITEM_LIST", specialItems);
//		}
//
//		public String doTransform() throws TemplateTransformationException {
//			String retStr = "";
//			try {
//				retStr = engine.getUpdated(resource.getString("audio.playlist.template"));
//				String destFile = playlistFilePath;
//				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
//				osw.write(retStr);
//				osw.close();
//			} catch (Exception e) {
//				Logger.getLogger(this.getClass()).log(e);
//			}
//			return retStr;
//		}
//	}
//}
