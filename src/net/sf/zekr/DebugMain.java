/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 10, 2004
 */
package net.sf.zekr;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @see TODO
 * @version 0.1
 */

import java.io.File;
import java.io.IOException;

import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.runtime.InitRuntime;
import net.sf.zekr.common.util.QuranPropertiesUtils;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;
import net.sf.zekr.engine.xml.XmlWriter;
import net.sf.zekr.ui.QuranForm;
import net.sf.zekr.ui.SplashScreen;

import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;

public class DebugMain {

	public static void main(String[] args) {
//		testXmlReader();
//		testLanguagePack();
//		testQuranForm();
//		testHTML1();
//		testQuranPropertiesUtils();
		testXmlWriter();
	}

	public static void testXmlWriter() {
		try {
			XmlReader reader = new XmlReader("src/net/sf/zekr/test1.xml");
			Document d = reader.getDocument();
			XmlUtils.setAttr(reader.getNode("node12"), "attr1", "abs");
			XmlWriter.writeXML(d, new File("src/net/sf/zekr/test1.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testXmlReader() {
		XmlReader reader = new XmlReader(ApplicationPath.LANGUAGE_DIR + "farsi.xml");
		System.out.println(reader.getNodes("language"));
	}

	public static void testQuranForm() {
		InitRuntime runtime = new InitRuntime();
		try {
			runtime.configureDirectories();
			Display display = new Display();
			QuranForm quraForm = new QuranForm(display);
			quraForm.show(700, 500);
		} catch (IOException e) {
			Logger.getLogger().log(e);
		}
	}

	public static void testSplashScreen() {
		Display display = new Display();
		SplashScreen s = new SplashScreen(display);
		s.show();
	}

	public static void testLanguagePack() {
//		ApplicationConfig config = ApplicationConfig.getInsatnce();
//		LanguageEngine langEngine = null;
//		try {
//			langEngine = LanguageEngine.getInstance(new Language("FARS"));
//		} catch (LanguagePackException e) {
//			e.printStackTrace();
//		}
//		finally {
//			langEngine = LanguageEngine.getInstance(new Language());
//		}
//		try {
//			System.out.println(new String(langEngine.getMeaning("CANCEL").getBytes("cp1256"), "UTF-8"));
//			System.out.println(langEngine.getMeaning("GENERAL_ERROR"));
//			System.out.println(langEngine.getMeaningById("APPLICATION", "APP_NAME"));
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
	}
	
	public static void testQuranPropertiesUtils() {
		int[] joz = QuranPropertiesUtils.getJozInside(4);
		System.out.println(joz[0]);
		System.out.println("First joz of soora 2: " + QuranPropertiesUtils.getFirstJozOf(2));
		System.out.println("First joz of soora 112: " + QuranPropertiesUtils.getFirstJozOf(112));
		System.out.println("Joz inside 2: " + QuranPropertiesUtils.getJozInside(4)[1]);

		System.out.println("Sujda inside: " + QuranPropertiesUtils.getSujdaInsideList(11));
	}


}