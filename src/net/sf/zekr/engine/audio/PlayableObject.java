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
		this.url = url;
	}

	public PlayableObject(File file) {
		this.file = file;
	}

	public PlayableObject(InputStream inputStream) {
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
		return String.valueOf(file != null ? file.toString() : url != null ? url : inputStream);
	}
}
