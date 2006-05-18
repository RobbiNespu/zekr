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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.zekr.ui.QuranFormMenuFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MiscMain {
	public static void main(String[] args) {
//			fixNoorQuran();
//			extractAll();
			GuiTest();
}
	
	private static void GuiTest() {
		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("salam");
		shell.setLocation(300, 400);
		shell.setSize(300, 300);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private static void fixNoorQuran() throws IOException {
		Writer writer = new OutputStreamWriter(new FileOutputStream(
		"res/text/noor-1256.txt"), "Cp1256");
		File inFile = new File("res/text/noor-quran.txt");
		
		char[] buf = new char[(int) inFile.length()];
		InputStreamReader isr = new InputStreamReader(new FileInputStream(inFile), "Cp1256");
		isr.read(buf);
		String str = new String(buf);
		str.replaceAll("\n\n", "\n");
//		StringTokenizer st = new StringTokenizer(str, "\n");
//		while(st.hasMoreTokens()) {
//			writer.write(st.nextToken());
//		}
		writer.write(str);
		writer.close();
	}

	private static void extractAll()  throws UnsupportedEncodingException {
		try {
			Writer writer = new OutputStreamWriter(new FileOutputStream(
					"F:\\Quran\\Text\\makarem-1256.txt"), "Cp1256");
			for (int i = 1; i <= 114; i++) {
				String name = "MAKA" + to3Dig(i) + ".htm";
				File inFile = new File("F:\\Quran\\Translations\\Farsi\\Quran-Makarem\\" + name);
				FileInputStream fis = new FileInputStream(inFile);
				extractTrans(new InputStreamReader(fis, "Cp1256"), writer, (int) inFile.length());
			}
//			File inFile = new File("F:\\Quran\\Web\\ArabEyes\\Trans.xml\\quran.fr.xml");
//			FileInputStream fis = new FileInputStream(inFile);
			// }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String to3Dig(int i) {
		 return i < 10 ? "00" + i : i < 100 ? "0" + i : "" + i;
//		return i + "";
	}

	public static void extractTranslation(File inf, RandomAccessFile outFile) {
		try {
			RandomAccessFile inFile = new RandomAccessFile(inf, "r");
			byte[] buf = new byte[(int) inFile.length()];
			inFile.readFully(buf);
			String str = new String(buf);
			Pattern p = Pattern.compile("<p align=\"right\">\\s*([^<]+)\\s*", Pattern.DOTALL);
			Matcher m = p.matcher(str);
			int c = 0;
			while (m.find()) {
				c++;
				// System.out.println("I found the text \"" + m.group(1) + "\" starting at
				// index "
				// + m.start() + " and ending at index " + m.end() + ".");
				outFile.writeBytes(m.group(1).replaceAll("\t", "").replaceAll("\n", "").replaceAll(
						"\r", "").replaceAll("\\s{1,}", " ").replaceAll("Ü", "").trim()
						+ "\n");

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
//			Pattern p = Pattern.compile("<p align=\"right\">\\s*([^<]+)\\s*", Pattern.DOTALL);
//			<p align="right">\s*\([^<]+\)\s*\<
//			<p>\([^<]*\)</p>
			Pattern p = Pattern.compile("<a name=\"aye[0-9]*\"></a><p>\\s*([^<]*)</p>", Pattern.DOTALL);
			Matcher m = p.matcher(str);
			int c = 0;
			while (m.find()) {
				c++;
				writer.write(m.group(1));
//				writer.write(m.group(1).replaceAll("\t", "").replaceAll("\n", "").replaceAll(
//						"\r", "").replaceAll("\\s{1,}", " ").replaceAll("Ü", "").trim()
//						+ "\n");

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
