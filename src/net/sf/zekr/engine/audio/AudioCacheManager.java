/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2009
 */
package net.sf.zekr.engine.audio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.util.PathUtils;
import net.sf.zekr.engine.log.Logger;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.lang.StringUtils;

/**
 * This is immutable, hence thread-safe.
 * 
 * @author Mohsen Saboorian
 */
public class AudioCacheManager {
	private Logger logger = Logger.getLogger(this.getClass());
	private long capacitySize;
	private File userPath;
	private long flushSize;

	private static final String AUDIO_CACHE_ITEM_AYA = "%1$03d%2$03d";
	private static final String AUDIO_CACHE_ITEM = "%s_%s";

	public AudioCacheManager(PropertiesConfiguration props) {
		capacitySize = props.getLong("audio.cache.capacitySize", 200);
		flushSize = props.getLong("audio.cache.flushSize", 20);
		userPath = PathUtils.resolve(props.getString("audio.cache.userPath", "<workspace>/audio/cache"));
	}

	public PlayableObject getPlayableObject(IQuranLocation location) throws PlayerException {
		ApplicationConfig config = ApplicationConfig.getInstance();
		return getPlayableObject(config.getAudio().getCurrent(), location.getSura(), location.getAya());
	}

	public void prefetchAudioFileImplicitly(AudioData audioData, int sura, int aya) {
		try {
			String cacheItemName = getCacheItemName(audioData, sura, aya);
			File cachedFile = getCacheItem(cacheItemName);
			if (cachedFile == null || cachedFile.length() <= 0) {
				String fileUrl = String.format(audioData.getOnlineUrl(sura, aya));
				File tmpFile = new File(System.getProperty("java.io.tmpdir"), cacheItemName);
				OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile), 1024 * 8);
				IOUtils.copy(net.sf.zekr.common.util.FileUtils.getContent(fileUrl), outputStream);
				FileUtils.moveFile(tmpFile, cachedFile);
			}
		} catch (Exception e) {
			logger.implicitLog(e);
		}
	}

	private String getCacheItemName(AudioData audioData, int sura, int aya) {
		return getCacheItemName(audioData, String.format(AUDIO_CACHE_ITEM_AYA, sura, aya));
	}

	private String getCacheItemName(AudioData audioData, String fileName) {
		return String.format(AUDIO_CACHE_ITEM, audioData.id, fileName);
	}

	private File getCacheItem(String cacheItemName) {
		File file = new File(userPath, cacheItemName);
		if (file.exists()) {
			return file;
		}
		return null;
	}

	public PlayableObject getPlayableObject(AudioData audioData, int sura, int aya) {
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			String filePath = AudioUtils.getAudioFileUrl(audioData, sura, aya);
			if (filePath == null) {
				return null;
			}
			if (PathUtils.isOnlineContent(filePath)) {
				String cacheItemName = getCacheItemName(audioData, sura, aya);
				File cached = getCacheItem(cacheItemName);
				if (cached == null || cached.length() == 0) {
					InputStream stream = config.getNetworkController().openSteam(filePath, 5000);
					return new PlayableObject(new NamedBufferedInputStream(filePath, stream, 4 * 1024));
				} else {
					return new PlayableObject(cached);
				}
			} else {
				return new PlayableObject(PathUtils.resolve(filePath));
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public PlayableObject getPlayableObject(AudioData audioData, String offlineUrl, String onlineUrl) {
		try {
			ApplicationConfig config = ApplicationConfig.getInstance();
			String filePath = AudioUtils.getAudioFileUrl(audioData, offlineUrl, onlineUrl);
			if (filePath == null) {
				return null;
			}
			if (PathUtils.isOnlineContent(filePath)) {
				String fileName = FilenameUtils.getName(new URI(filePath).getPath());
				String cacheItemName = getCacheItemName(audioData, fileName);
				File cached = getCacheItem(cacheItemName);
				if (cached == null || cached.length() == 0) {
					InputStream stream = config.getNetworkController().openSteam(filePath, 5000);
					return new PlayableObject(new NamedBufferedInputStream(filePath, stream, 4 * 1024));
				} else {
					return new PlayableObject(cached);
				}
			} else {
				if (StringUtils.isBlank(filePath)) {
					return null;
				}
				return new PlayableObject(PathUtils.resolve(filePath));
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	/**
	 * Removes {@link #getCapacitySize()} megabytes of files from user's audio cache, so that cache size limit
	 * ({@link #getCapacitySize()}) is met. It simply ignores if audio cache size is not exceeded
	 * {@link #getCapacitySize()}.
	 */
	@SuppressWarnings("unchecked")
	public void flushCache() {
		logger.info("Flush audio cache.");
		long cacheSize = FileUtils.sizeOfDirectory(userPath);
		if (cacheSize > FileUtils.ONE_MB * capacitySize) {
			logger.info("Capacity size is " + capacitySize + " MB, of which "
					+ FileUtils.byteCountToDisplaySize(cacheSize) + " is used. Flush size is " + flushSize + " MB.");
			Collection<File> audioDirectoryList = FileUtils.listFiles(userPath, new AbstractFileFilter() {
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return true;
					} else {
						return false;
					}
				}
			}, null);

			List fileList = new ArrayList();
			for (File dir : audioDirectoryList) {
				fileList.addAll(Arrays.asList(dir.listFiles()));
			}

			// return older files first
			Collections.sort(fileList, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			long deleteSize = 0;
			for (int i = 0; i < fileList.size(); i++) {
				if (deleteSize > flushSize + FileUtils.ONE_MB) {
					logger.info("Cache flushing suffices. " + FileUtils.byteCountToDisplaySize(deleteSize)
							+ " were deleted.");
					break;
				}
				File file = (File) fileList.get(i);
				deleteSize += file.length();
				logger.debug("Delete: " + file);
				file.delete();
			}
		} else {
			logger.info("No flush is required.");
		}
	}

	public long getFlushSize() {
		return flushSize;
	}

	public long getCapacitySize() {
		return capacitySize;
	}
}
