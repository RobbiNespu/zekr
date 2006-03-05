/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 2, 2006
 */
package net.sf.zekr.common.config;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipTest {
	public static void main(String[] args) {
		try {
//			ZipInputStream zis = new ZipInputStream(null);
			ZipFile zf = new ZipFile(new File("res/text/trans/makarem.zip"));
			Enumeration entries = zf.entries();
			System.out.println(zf.getInputStream(new ZipEntry("translation.properties")));
//			while (entries.hasMoreElements()) {
//				ZipEntry ze = (ZipEntry) entries.nextElement();
//				System.out.println(ze.getName());
//				System.out.println(zf.getInputStream(ze));
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
