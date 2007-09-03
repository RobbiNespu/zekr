/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.template.BaseViewTemplate;
import net.sf.zekr.engine.template.TemplateTransformationException;

/**
 * @author Mohsen Saboorian
 */
public class OfflinePlaylistProvider extends PlaylistProvider {
	public OfflinePlaylistProvider(AudioData audioData, int suraNum) {
		super(audioData, suraNum);
	}

	public String providePlaylist() {
		String fileName = audioData.getPlaylistFileName();
		if (audioData.getPlaylistMode().equals(AudioData.SURA_PLAYLIST)) {
			fileName = audioData.getPlaylistFileName().replaceAll("\\{SURA\\}", String.valueOf(suraNum));
		} else { // a playlist for the whole Quran
			// do nothing
		}
		String playlistUrl = audioData.getPlaylistUrl() + fileName;

		return playlistUrl;
	}

	private class OfflinePlaylistTempalate extends BaseViewTemplate {
		String playlistFileName;

		public OfflinePlaylistTempalate(String playlistFileName, String[] audioNameList) {
			// super();
			this.playlistFileName = playlistFileName;
			engine.put("AUDIO_NAME_LIST", audioNameList);
			engine.put("AUDIO_DATA", audioData);
		}

		public String transform() throws TemplateTransformationException {
			String retStr = "";
			try {
				retStr = engine.getUpdated(resource.getString("playlist.template"));
				String destFile = Naming.getAudioDir() + "/" + playlistFileName;
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
