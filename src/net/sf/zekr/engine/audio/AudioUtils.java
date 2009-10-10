/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 9, 2007
 */
package net.sf.zekr.engine.audio;

import java.io.File;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.PathUtils;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 */
public class AudioUtils {
	public static Logger logger = Logger.getLogger(AudioUtils.class);

	/**
	 * @param audioData
	 * @param sura sura number counted from 1
	 * @param aya aya number counted from 1
	 * @return audio file url
	 */
	public static String getAudioFileUrl(AudioData audioData, int sura, int aya) {
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			String lookupMode = config.getProps().getString("audio.lookupMode", "offline-online");
			if ("online-only".equals(lookupMode)) {
				return String.format(audioData.getOnlineUrl(), sura, aya);
			} else if ("offline-only".equals(lookupMode)) {
				return String.format(audioData.getOfflineUrl(), sura, aya);
			} else {
				String offlineUrl = String.format(audioData.getOfflineUrl(), sura, aya);
				if (PathUtils.resolve(offlineUrl).exists()) {
					return offlineUrl;
				} else {
					return String.format(audioData.getOnlineUrl(), sura, aya);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public static File getAudioFolder(String baseFolder, AudioData audioData) {
		return new File(baseFolder, audioData.getId());
	}

	public static File getUserOfflineAudioPath(AudioData audioData, String fileName) {
		return new File(getAudioFolder(ApplicationPath.AUDIO_DIR, audioData), fileName);
	}

	public static File getGlobalOfflineAudioPath(AudioData audioData, String fileName) {
		return new File(getAudioFolder(Naming.getAudioDir(), audioData), fileName);
	}

	public static String getAudioFileUrl(AudioData audioData, IQuranLocation location) {
		return getAudioFileUrl(audioData, location.getSura(), location.getAya());
	}
}
