/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 28, 2009
 */
package net.sf.zekr.engine.audio;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * @author Mohsen Saboorian
 */
public class NamedBufferedInputStream extends BufferedInputStream {
	String name;

	public NamedBufferedInputStream(String name, InputStream in, int size) {
		super(in, size);
		this.name = name;
	}

	public NamedBufferedInputStream(InputStream in, int size) {
		super(in, size);
	}

	public NamedBufferedInputStream(InputStream in) {
		super(in);
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", super.toString(), name);
	}
}
