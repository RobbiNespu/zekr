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
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.zekr.engine.log.Logger;

/**
 * Some utilities for working with zip files.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ZipUtils {
	private final static Logger logger = Logger.getLogger(ZipUtils.class);

	/**
	 * Extracts zip file <code>zipFile</code> info <code>destDir</code>
	 * 
	 * @param zipFile source zip file to be unzipped
	 * @param destDir destination directory to extract content of the zip file into it.
	 *            Will be created first, if does not exist.
	 * @throws IOException
	 */
	public static void extract(File zipFile, String destDir) throws IOException {
		ZipFile zf = new ZipFile(zipFile);
		Enumeration e = zf.entries();
		while (e.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) e.nextElement();
			if (ze.isDirectory()) {
				File entry = new File(destDir + File.separator + ze.getName());
				entry.mkdirs();
				continue;
			}
			File f = new File(destDir + File.separator + ze.getName());
			File p = new File(f.getParent());
			if (!p.exists())
				p.mkdirs();
			f.createNewFile();
			OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
			byte[] buf = new byte[(int) ze.getSize()];
			zf.getInputStream(ze).read(buf); // read fully
			os.write(buf); // write fully
			os.close();
		}
		zf.close();
	}
}
