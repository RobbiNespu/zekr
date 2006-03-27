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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscMain {
	public static void main(String[] args) throws UnsupportedEncodingException {
//		try {
//			Writer writer = new OutputStreamWriter(new FileOutputStream(
//					"F:\\Quran\\Web\\ArabEyes\\Trans.xml\\fr.txt"), "iso-8859-1");
			// for (int i = 1; i <= 114; i++) {
			// String name =
			// "F:\\Quran\\Web\\ArabEyes\\kuran-0.08\\quran\\text\\quran.id.xml" +
			// to3Dig(i)
			// + ".qmt.html";
//			File inFile = new File("F:\\Quran\\Web\\ArabEyes\\Trans.xml\\quran.fr.xml");
//			FileInputStream fis = new FileInputStream(inFile);
//			extractTrans(new InputStreamReader(fis, "iso-8859-1"), writer, (int) inFile.length());
			// }
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
			localeTest();
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
			Pattern p = Pattern.compile("<strong>SHAKIR:</strong>\\s*([^<]+)\\s*", Pattern.DOTALL);
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

	public static void extractTrans(Reader reader, Writer writer, int length) {
		try {
			char[] buf = new char[length];
			reader.read(buf);
			// inFile.readFully(buf);
			String str = new String(buf);
			Pattern p = Pattern.compile("<qurantext> \\s*([^<]+)\\s*", Pattern.DOTALL);
			Matcher m = p.matcher(str);
			int c = 0;
			while (m.find()) {
				c++;
				writer.write(m.group(1).replaceAll("\\s{1,}", " ").trim() + "\n");

			}
			System.out.println(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void localeTest() {
		Locale.setDefault(new Locale("fa", "IR"));
		System.out.println(System.getProperties());
	}
}
