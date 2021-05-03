/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 12, 2006
 */
package net.sf.zekr.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Some utilities for working with zip files.
 * 
 * @author Mohsen Saboorian
 */
public class ZipUtils {

	/**
	 * Extracts zip file <code>zipFile</code> info <code>destDir</code>
	 * 
	 * @param zipFile source zip file to be unzipped
	 * @param destDir destination directory to extract content of the zip file into it. Will be created first,
	 *           if does not exist.
	 * @param progressListener
	 * @throws IOException
	 * @return <code>true</code> if extraction done, and <code>false</code> otherwise
	 */
	public static boolean extract(File zipFile, String destDir, IntallationProgressListener progressListener) throws IOException {
		boolean interrupted = false;
		ZipFile zf = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> e = zf.entries();
		byte buffer[] = new byte[4096];
		int readSize = -1;

		if (progressListener != null) {
			progressListener.start(zipFile.length());
		}

		try {
			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();
				if (ze.isDirectory()) {
					File entry = new File(destDir + File.separator + ze.getName());
					entry.mkdirs();
					continue;
				}

				if (progressListener != null) {
					long size = ze.getCompressedSize();
					if (size < 0) {
						size = ze.getSize();
					}
					if (!progressListener.progress(size)) {
						interrupted = true;
						break;
					}
				}

				File f = new File(destDir + File.separator + ze.getName());
				File p = new File(f.getParent());
				if (!p.exists()) {
					p.mkdirs();
				}
				f.createNewFile();
				OutputStream os = new BufferedOutputStream(new FileOutputStream(f));

				InputStream inStream = zf.getInputStream(ze);
				while ((readSize = inStream.read(buffer)) != -1) { // read partially
					os.write(buffer, 0, readSize); // write partially
				}
				os.close();
			}
		} finally {
			zf.close();
		}
		return !interrupted;
	}

	/**
	 * Extracts zip file <code>zipFile</code> info <code>destDir</code>
	 * 
	 * @param zipFile source zip file to be unzipped
	 * @param destDir destination directory to extract content of the zip file into it. Will be created first,
	 *           if does not exist.
	 * @throws IOException
	 * @return <code>true</code> if extraction done, and <code>false</code> otherwise
	 */
	public static boolean extract(File zipFile, String destDir) throws IOException {
		return extract(zipFile, destDir, null);
	}

	public static void closeQuietly(ZipFile zipFile) {
		try {
			zipFile.close();
		} catch (Exception e) {
		}
	}
}
