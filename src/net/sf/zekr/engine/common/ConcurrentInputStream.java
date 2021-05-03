/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 13, 2009
 */
package net.sf.zekr.engine.common;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class ConcurrentInputStream extends FilterInputStream {
	private static final long THREAD_SLEEP = 10;
	private static final int MARK_LIMIT = 1024 * 64; // 64kb seems a reasonably large enough value
	boolean streamClosed = false;
	PipedOutputStream pos;
	PipedInputStream pis;

	// BufferedInputStream bais;

	protected ConcurrentInputStream(InputStream in) throws IOException {
		// force support mark() and reset()
		super(new BufferedInputStream(in));
		pos = new PipedOutputStream();
		pis = new PipedInputStream(pos);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return pis.read(b, off, len);
	}

	/**
	 * This method should only be called by a caching thread, separate from application main thread.
	 * 
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public int readForCache(byte[] b, int off, int len) throws IOException {
		int ret = in.read(b, off, len);
		pos.write(b, off, len);
		return ret;
	}
}
