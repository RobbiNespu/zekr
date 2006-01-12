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
import net.sf.zekr.common.util.JozProperties;
import net.sf.zekr.common.util.QuranPropertiesUtils;
import net.sf.zekr.common.util.SajdaProperties;
import net.sf.zekr.common.util.SooraProperties;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.NodeList;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;

/**
 * A class used to read properties of the Quran sooras from respective XML file. All
 * operations on this class are acted as zero-relative. This class is for internal use
 * only.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
class QuranPropertiesReader {
	private XmlReader reader;
	private NodeList sooraNodeList, jozNodeList, sajdaNodeList;
	private ApplicationConfig appConfig = ApplicationConfig.getInsatnce();

	ArrayList sooraProp = new ArrayList();
	ArrayList jozProp = new ArrayList();
	ArrayList sajdaProp = new ArrayList();

	private Logger logger = Logger.getLogger(this.getClass());

	QuranPropertiesReader() {
		logger.info("Loading Quran properties from \""
				+ appConfig.getConfigFile(ZekrConfigNaming.QURAN_DETAIL_ID) + "\".");
		reader = new XmlReader(appConfig.getConfigFile(ZekrConfigNaming.QURAN_DETAIL_ID));
		sooraNodeList = reader.getNodes(QuranPropertiesNaming.SOORA_TAG);
		jozNodeList = reader.getNodes(QuranPropertiesNaming.JOZ_TAG);
		sajdaNodeList = reader.getNodes(QuranPropertiesNaming.SAJDA_TAG);

		int i;
		SooraProperties soora = new SooraProperties();
		JozProperties joz = new JozProperties();
		SajdaProperties sajda = new SajdaProperties();

		for (i = 0; i < sooraNodeList.size(); i++) {
			soora = new SooraProperties();

			soora.setAyaCount(Integer.parseInt(XmlUtils.getAttr(sooraNodeList.item(i),
					QuranPropertiesNaming.AYA_COUNT_ATTR)));
			soora.setMadani(QuranPropertiesUtils.isMadani(XmlUtils.getAttr(sooraNodeList.item(i),
					QuranPropertiesNaming.DESCENT_ATTR)));
			soora.setName(XmlUtils.getAttr(sooraNodeList.item(i), QuranPropertiesNaming.NAME_ATTR));
			soora.setIndex(Integer.parseInt(XmlUtils.getAttr(sooraNodeList.item(i),
					QuranPropertiesNaming.INDEX_ATTR)));

			sooraProp.add(soora);
		}

		for (i = 0; i < jozNodeList.size(); i++) {
			joz = new JozProperties();

			joz.setIndex(Integer.parseInt(XmlUtils.getAttr(jozNodeList.item(i),
					QuranPropertiesNaming.INDEX_ATTR)));
			joz.setSooraNumber(Integer.parseInt(XmlUtils.getAttr(jozNodeList.item(i),
					QuranPropertiesNaming.SOORA_NUM_ATTR)));
			joz.setAyaNumber(Integer.parseInt(XmlUtils.getAttr(jozNodeList.item(i),
					QuranPropertiesNaming.AYA_NUM_ATTR)));

			jozProp.add(joz);
		}

		for (i = 0; i < sajdaNodeList.size(); i++) {
			sajda = new SajdaProperties();

			sajda.setIndex(Integer.parseInt(XmlUtils.getAttr(sajdaNodeList.item(i),
					QuranPropertiesNaming.INDEX_ATTR)));
			sajda.setType(QuranPropertiesUtils.getSajdaType(XmlUtils.getAttr(sajdaNodeList.item(i),
					QuranPropertiesNaming.TYPE_ATTR)));
			sajda.setAyaNumber(Integer.parseInt(XmlUtils.getAttr(sajdaNodeList.item(i),
					QuranPropertiesNaming.AYA_NUM_ATTR)));
			sajda.setSooraNumber(Integer.parseInt(XmlUtils.getAttr(sajdaNodeList.item(i),
					QuranPropertiesNaming.SOORA_NUM_ATTR)));

			sajdaProp.add(sajda);
		}

		logger.info("Quran properties loaded successfully.");
	}

}
