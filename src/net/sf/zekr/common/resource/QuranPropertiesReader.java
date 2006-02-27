/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 21, 2004
 */

package net.sf.zekr.common.resource;

import java.util.ArrayList;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ZekrConfigNaming;
import net.sf.zekr.common.util.JuzProperties;
import net.sf.zekr.common.util.QuranPropertiesUtils;
import net.sf.zekr.common.util.SajdaProperties;
import net.sf.zekr.common.util.SuraProperties;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.NodeList;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;

/**
 * A class used to read properties of the Quran suras from respective XML file. All
 * operations on this class are acted as zero-relative. This class is for internal use
 * only.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
class QuranPropertiesReader extends QuranBaseProperties {
	private XmlReader reader;
	private NodeList suraNodeList, juzNodeList, sajdaNodeList;
	private ApplicationConfig appConfig = ApplicationConfig.getInstance();

	ArrayList suraProp = new ArrayList();
	ArrayList juzProp = new ArrayList();
	ArrayList sajdaProp = new ArrayList();

	private Logger logger = Logger.getLogger(this.getClass());

	QuranPropertiesReader() {
		logger.info("Loading Quran properties from \""
				+ appConfig.getConfigFile(ZekrConfigNaming.QURAN_DETAIL_ID) + "\".");
		reader = new XmlReader(appConfig.getConfigFile(ZekrConfigNaming.QURAN_DETAIL_ID));
		suraNodeList = reader.getNodes(QuranPropertiesNaming.SURA_TAG);
		juzNodeList = reader.getNodes(QuranPropertiesNaming.JUZ_TAG);
		sajdaNodeList = reader.getNodes(QuranPropertiesNaming.SAJDA_TAG);

		int i;
		SuraProperties sura = new SuraProperties();
		JuzProperties juz = new JuzProperties();
		SajdaProperties sajda = new SajdaProperties();

		for (i = 0; i < suraNodeList.size(); i++) {
			sura = new SuraProperties();

			sura.setAyaCount(Integer.parseInt(XmlUtils.getAttr(suraNodeList.item(i),
					QuranPropertiesNaming.AYA_COUNT_ATTR)));
			sura.setMadani(QuranPropertiesUtils.isMadani(XmlUtils.getAttr(suraNodeList.item(i),
					QuranPropertiesNaming.DESCENT_ATTR)));
			sura.setName(XmlUtils.getAttr(suraNodeList.item(i), QuranPropertiesNaming.NAME_ATTR));
			sura.setIndex(Integer.parseInt(XmlUtils.getAttr(suraNodeList.item(i),
					QuranPropertiesNaming.INDEX_ATTR)));

			suraProp.add(sura);
		}

		for (i = 0; i < juzNodeList.size(); i++) {
			juz = new JuzProperties();

			juz.setIndex(Integer.parseInt(XmlUtils.getAttr(juzNodeList.item(i),
					QuranPropertiesNaming.INDEX_ATTR)));
			juz.setSuraNumber(Integer.parseInt(XmlUtils.getAttr(juzNodeList.item(i),
					QuranPropertiesNaming.SURA_NUM_ATTR)));
			juz.setAyaNumber(Integer.parseInt(XmlUtils.getAttr(juzNodeList.item(i),
					QuranPropertiesNaming.AYA_NUM_ATTR)));

			juzProp.add(juz);
		}

		for (i = 0; i < sajdaNodeList.size(); i++) {
			sajda = new SajdaProperties();

			sajda.setIndex(Integer.parseInt(XmlUtils.getAttr(sajdaNodeList.item(i),
					QuranPropertiesNaming.INDEX_ATTR)));
			sajda.setType(QuranPropertiesUtils.getSajdaType(XmlUtils.getAttr(sajdaNodeList.item(i),
					QuranPropertiesNaming.TYPE_ATTR)));
			sajda.setAyaNumber(Integer.parseInt(XmlUtils.getAttr(sajdaNodeList.item(i),
					QuranPropertiesNaming.AYA_NUM_ATTR)));
			sajda.setSuraNumber(Integer.parseInt(XmlUtils.getAttr(sajdaNodeList.item(i),
					QuranPropertiesNaming.SURA_NUM_ATTR)));

			sajdaProp.add(sajda);
		}

		logger.info("Quran properties loaded successfully.");
	}

}
