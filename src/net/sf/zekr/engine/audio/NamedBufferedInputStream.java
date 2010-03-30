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
 * Two {@link NamedBufferedInputStream}s are equal if they have the same name, regardless of their underlying
 * {@link InputStream}.
 * 
 * @author Mohsen Saboorian
 */
public class NamedBufferedInputStream extends BufferedInputStream {
	String name;

	/**
	 * Creates an instance of this class. Parameter <code>name</code> cannot be <code>null</code>.
	 * 
	 * @param name
	 * @param in
	 * @param size
	 */
	public NamedBufferedInputStream(String name, InputStream in, int size) {
		super(in, size);
		if (name == null) {
			throw new NullPointerException("Parameter name cannot be null");
		}
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof NamedBufferedInputStream) {
			NamedBufferedInputStream nbis = (NamedBufferedInputStream) obj;
			return name.equals(nbis.getName());
		}
		return false;
	}
}
