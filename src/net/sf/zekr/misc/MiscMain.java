/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 24, 2006
 */
package net.sf.zekr.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscMain {
	public static void main(String[] args) {
		try {
			RandomAccessFile resultFile = new RandomAccessFile(
					"F:/Quran/Translations/English/3-translations/new/shakir.txt", "rw");
			for (int i = 1; i <= 114; i++) {
				String name = "F:/Quran/Translations/English/3-translations/" + to3Dig(i)
						+ ".qmt.html";
				extractTranslation(new File(name), resultFile);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String to3Dig(int i) {
		return i < 10 ? "00" + i : i < 100 ? "0" + i : "" + i;
	}

	public static void extractTranslation(File inf, RandomAccessFile outFile) {
		try {
			RandomAccessFile inFile = new RandomAccessFile(inf, "r");
			byte[] buf = new byte[(int) inFile.length()];
			inFile.readFully(buf);
			String str = new String(buf);
			Pattern p = Pattern
					.compile("<strong>SHAKIR:</strong>\\s*([^<]+)\\s*", Pattern.DOTALL);
			Matcher m = p.matcher(str);
			int c = 0;
			while (m.find()) {
				c++;
				// System.out.println("I found the text \"" + m.group(1) + "\" starting at
				// index "
				// + m.start() + " and ending at index " + m.end() + ".");
				outFile.writeBytes(m.group(1).replaceAll("\\s{1,}", " ").trim() + "\n");

			}
			System.out.println(c);
			// System.out.println(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
