/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2009
 */
package net.sf.zekr.engine.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * This class works as a redirector input stream which reads from the input stream provides in the constructor
 * and writes as much data as it reads from input stream to output stream.
 * <p/>
 * It overrides {@link #read(byte[], int, int)}, and writes all the bytes read from input to output.
 * 
 * @author Mohsen Saboorian
 */
public class CachingInputStream extends FilterInputStream {
	private OutputStream out;
	private File destFile;
	private File tmpFile;
	private boolean outputClosed = false;

	public CachingInputStream(InputStream in, File destFile) throws FileNotFoundException {
		super(new BufferedInputStream(in, 1024 * 8));
		this.destFile = destFile;
		this.tmpFile = new File(System.getProperty("java.io.tmpdir"), destFile.getName());
		// out = new BufferedOutputStream(new FileOutputStream(tmpFile));
		out = new FileOutputStream(tmpFile);
	}

	public synchronized void mark(int readlimit) {
		super.mark(readlimit);
	}

	public long skip(long n) throws IOException {
		return super.skip(n);
	}

	public int available() throws IOException {
		return super.available();
	}

	public synchronized void reset() throws IOException {
		super.reset();
	}

	/**
	 * Reads from input stream, and then writes all the bytes read to output stream.
	 * 
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		int read = super.read(b, off, len);
		if (read > 0) {
			if (!outputClosed) {
				out.write(b, off, read);
			}
		} else {
			closeOutputCache();
		}
		return read;
	}

	private void closeOutputCache() {
		if (!outputClosed) {
			outputClosed = true;
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * Closes first input stream, and then output stream.
	 * 
	 * @see java.io.FilterInputStream#close()
	 */
	public void close() throws IOException {
		super.close();
		closeOutputCache();
		FileUtils.moveFile(tmpFile, destFile);
	}
}
