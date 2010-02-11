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
import java.net.URISyntaxException;

import net.sf.zekr.common.config.ApplicationConfig;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class FileUtils {
	/**
	 * A more enhanced implementation of File.delete() with also deletes directories recursively.
	 * 
	 * @param file
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public static boolean delete(File file) {
		String[] lst;
		if (file.isDirectory()) {
			lst = file.list();
			if (lst == null)
				return false;
			if (lst.length == 0)
				return file.delete(); // an empty directory
			else
				return deltree(file);
		} else {
			return file.delete();
		}
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

	/**
	 * <b>This method doesn't work correctly on GCJ</b>
	 * 
	 * @param is
	 * @param size
	 * @param encoding
	 * @return the whole file being read
	 * @throws IOException
	 */
	public static String readFully(InputStream is, int size, String encoding) throws IOException {
		InputStreamReader isr = new InputStreamReader(is, encoding);
		char[] cbuf = new char[(int) size];
		isr.read(cbuf);
		String ret = new String(cbuf);
		return ret;
	}

	/**
	 * <code>FileUtils.readFully(is, size, "UTF-8")</code><br>
	 * <b>This method doesn't work correctly on GCJ</b>
	 * 
	 * @param is
	 * @param size
	 * @return the whole stream being read
	 * @throws IOException
	 */
	public static String readFully(InputStream is, int size) throws IOException {
		return readFully(is, size, "UTF-8");
	}

	public static void recreateDirectory(File dir) throws IOException {
		if (dir.exists())
			if (!FileUtils.delete(dir))
				throw new IOException("Can not delete directory \"" + dir + "\".");
		dir.mkdirs();
	}

	public static void recreateDirectory(String dir) throws IOException {
		recreateDirectory(new File(dir));
	}

	/**
	 * Open a connection to a remote/local URL.
	 * 
	 * @param uri
	 * @return an open stream to the URL
	 * @throws IOException
	 */
	public static InputStream getContent(String uri) throws IOException {
		try {
			return ApplicationConfig.getInstance().getNetworkController().openSteam(uri);
		} catch (URISyntaxException e) {
			throw new IOException(e.toString());
		}
	}
}
