/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2009
 */
package net.sf.zekr.engine.audio;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import net.sf.zekr.common.util.ExceptionsUtils;

/**
 * A playable can be either a {@link URL}, a {@link File} or an {@link InputStream}.
 * 
 * @author Mohsen Saboorian
 */
public class PlayableObject {
	private URL url;
	private File file;
	private InputStream inputStream;

	public PlayableObject(URL url) {
		ExceptionsUtils.preventNullParameter(url, "url");
		this.url = url;
	}

	public PlayableObject(File file) {
		ExceptionsUtils.preventNullParameter(file, "file");
		this.file = file;
	}

	public PlayableObject(InputStream inputStream) {
		ExceptionsUtils.preventNullParameter(inputStream, "inputStream");
		this.inputStream = inputStream;
	}

	public URL getUrl() {
		return url;
	}

	public File getFile() {
		return file;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public String toString() {
		return String.valueOf(file != null ? file : url != null ? url : inputStream);
	}

	@Override
	public int hashCode() {
		return file != null ? file.hashCode() : url != null ? url.hashCode() : inputStream.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof PlayableObject) {
			PlayableObject po = (PlayableObject) obj;
			return file != null ? file.equals(po.file) : url != null ? url.equals(po.url) : inputStream
					.equals(po.inputStream);
		}
		return false;
	}
}
