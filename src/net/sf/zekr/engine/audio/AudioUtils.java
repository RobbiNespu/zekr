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
	 * @return audio file URL
	 */
	public static String getAudioFileUrl(AudioData audioData, int sura, int aya) {
		try {
			if ("offline".equals(audioData.type)) {
				return String.format(audioData.offlineUrl, sura, aya);
			} else if ("offline-online".equals(audioData.type)) {
				String offlineUrl = String.format(audioData.offlineUrl, sura, aya);
				if (PathUtils.resolve(offlineUrl).exists()) {
					return offlineUrl;
				} else {
					return String.format(audioData.onlineUrl, sura, aya);
				}
			} else { // online
				return String.format(audioData.onlineUrl, sura, aya);
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public static String getAudioFileUrl(AudioData audioData, String offlineUrl, String onlineUrl) {
		try {
			if ("offline".equals(audioData.type)) {
				return offlineUrl;
			} else if ("offline-online".equals(audioData.type)) {
				if (PathUtils.resolve(offlineUrl).exists()) {
					return offlineUrl;
				} else {
					return onlineUrl;
				}
			} else { // online
				return onlineUrl;
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public static File getAudioFolder(String baseFolder, AudioData audioData) {
		return new File(baseFolder, audioData.id);
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
