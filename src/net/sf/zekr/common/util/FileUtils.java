/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 11, 2005
 */
package net.sf.zekr.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class FileUtils {
	/**
	 * A more enhanced implementation of File.delete() with also deletes directories
	 * recursively.
	 * 
	 * @param file
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public static boolean delete(File file) {
		if (file.isDirectory())
			if (file.list().length == 0)
				return file.delete(); // an empty directory
			else
				return deltree(file);
		else
			return file.delete();
	}

	private static boolean deltree(File file) {
		if (file == null)
			return true;
		if (file.isFile() || file.list() == null)
			return file.delete();
		if (file.list().length == 0) // is an empty directory
			return file.delete();

		int fileNum = file.list().length;
		String[] names = file.list();
		for (int i = 0; i < fileNum; i++) {
			if (file.isFile())
				file.delete();
			else
				deltree(new File(file.getPath() + File.separatorChar + names[i]));
		}
		return file.delete();
	}

	public static String readFully(InputStream is, int size, String encoding) throws IOException {
		InputStreamReader isr = new InputStreamReader(is, encoding);
		char[] cbuf = new char[(int) size];
		isr.read(cbuf);
		String ret = new String(cbuf);
		return ret;
	}

	/**
	 * <code>FileUtils.readFully(is, size, "UTF-8")</code>
	 */
	public static String readFully(InputStream is, int size) throws IOException {
		return readFully(is, size, "UTF-8");
	}

	public static void recreateDirectory(File dir) throws IOException {
		if (dir.exists())
			if (!FileUtils.delete(dir))
				throw new IOException("Can not delete directory \"" + dir
						+ "\".");
		dir.mkdir();
	}
	
	public static void recreateDirectory(String dir) throws IOException {
		recreateDirectory(new File(dir));
	}
}
