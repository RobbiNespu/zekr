/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 20, 2007
 */

package net.sf.zekr;

import java.io.File;
import java.io.FileFilter;

import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.SuraProperties;

/**
 * @author Mohsen Saboorian
 */
public class MiscTests extends ZekrBaseTest {

	public MiscTests() throws Exception {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCeiling() throws Exception {
		assertEquals((int) Math.ceil((double) 15 / 10), 2);
		assertEquals((int) Math.ceil((double) 20 / 10), 2);
		assertEquals((int) Math.ceil((double) 21 / 10), 3);
		assertEquals((int) Math.ceil((double) 0 / 10), 0);
	}

	public void testStringReplaceAll() throws Exception {
		String s = "salam\nsalam\n\r salam\r\n salam\r\t";
		String s1 = s.replaceAll("\\r\\n|\\n\\r|\\n|\\r", " ");
		assertEquals(s1, "salam salam  salam  salam \t");
	}

	public void testLFCR() throws Exception {
		assertEquals('\n', 10);
		assertEquals('\r', 13);
		// WIN: CR+LF: \r\n: 13+10
		// UNIX: LF: \n: 10
		// MAC: CR: \r: 13
	}

	public void testFOrmatter() throws Exception {
		assertEquals(String.format("[absolute]f:/Karim Mansouri/%1$03d/%1$03d%2$03d.mp3", 1, 14),
				"[absolute]f:/Karim Mansouri/001/001014.mp3");
	}

	public void testStringReplace() throws Exception {
		final String str = "Salam; bar! to";
		assertEquals(str.replaceAll("([;!])", "<ss>$0</ss>"), "Salam<ss>;</ss> bar<ss>!</ss> to");
	}

	public void testMissingAudioItemDetector() {
		String[] paths = { "E:/recitation/muaiqly-48kbps-offline/%1$03d/%1$03d%2$03d.mp3",
				"E:/recitation/ghamdi-40kbps-offline/%1$03d%2$03d.mp3" };
		// ApplicationConfig.getInstance();
		for (int i = 0; i < paths.length; i++) {
			for (int sn = 1; sn <= 114; sn++) {
				SuraProperties sura = QuranPropertiesUtils.getSura(sn);
				int ayaCount = sura.getAyaCount();
				for (int an = 1; an <= ayaCount; an++) {
					String filePath = String.format(paths[i], sn, an);
					// String filePath = String.format("H:/recitation/mansouri/%1$03d/%1$03d%2$03d.mp3", sn, an);
					File file = new File(filePath);
					if (!file.exists()) {
						System.out.println(filePath + " is missing.");
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		String parentPath = "E:/recitation";
		//		String[] paths = { "ghamdi-40kbps-offline", "mansouri-48kbps-offline", "shatri-48kbps-offline",
		//				"afasy-40kbps-offline" };
		String[] paths = { "abdulbasit-32kbps-offline" };
		for (int i = 0; i < paths.length; i++) {
			System.out.println("Moving files for " + paths[i]);
			for (int sn = 1; sn <= 114; sn++) {
				final int sura = sn;
				File[] list = new File(parentPath, paths[i]).listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						return pathname.getName().startsWith(String.format("%1$03d", sura));
					}
				});
				String dir = String.format(paths[i] + "/%1$03d", sn);
				new File(parentPath, dir).mkdirs();
				for (File file : list) {
					String newFile = parentPath + "/" + dir + "/" + file.getName();
					if (!file.renameTo(new File(newFile))) {
						System.out.println(String.format("Failed to move %s to %s.", file, newFile));
					}

				}
				//				SuraProperties sura = QuranPropertiesUtils.getSura(sn);
				//				int ayaCount = sura.getAyaCount();
				//				for (int an = 1; an <= ayaCount; an++) {
				//					String filePath = String.format(paths[i], sn, an);
				//					// String filePath = String.format("H:/recitation/mansouri/%1$03d/%1$03d%2$03d.mp3", sn, an);
				//					File file = new File(filePath);
				//					if (!file.exists()) {
				//						System.out.println(filePath + " is missing.");
				//					}
				//				}
			}
		}
	}
}
