/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 21, 2004
 */

package net.sf.zekr.common.resource;

import java.io.File;
import java.util.ArrayList;

import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.NodeList;
import net.sf.zekr.engine.xml.XmlReadException;
import net.sf.zekr.engine.xml.XmlReader;

import org.w3c.dom.Element;

/**
 * A class used to read properties of the Quran suras from respective XML file. All operations on this class
 * are acted as zero-relative. This class is for internal use only.
 * 
 * @author Mohsen Saboorian
 */
class QuranPropertiesReader extends BaseQuranProperties {
	private NodeList suraNodeList, juzNodeList, sajdaNodeList;
	private ResourceManager resource = ResourceManager.getInstance();
	private NodeList suraNodeListL10N;

	ArrayList suraProp = new ArrayList();
	ArrayList juzProp = new ArrayList();
	ArrayList sajdaProp = new ArrayList();

	private static final Logger logger = Logger.getLogger(QuranPropertiesReader.class);

	void updateLocalizedSuraNames() {
		try {
			if (loadLocalizedProps()) {
				for (int i = 0; i < suraProp.size(); i++) {
					updateSuraLocalizedProps((SuraProperties) suraProp.get(i));
				}
			}
		} catch (XmlReadException e) {
			logger.doFatal(e);
		}
	}

	QuranPropertiesReader() {
		logger.info("Loading Quran properties...");
		try {
			logger.debug("Loading base Quran properties" + resource.getString("quran.props"));
			XmlReader reader = new XmlReader(resource.getString("quran.props"));
			suraNodeList = reader.getNodes(QuranPropertiesNaming.SURA_TAG);
			juzNodeList = reader.getNodes(QuranPropertiesNaming.JUZ_TAG);
			sajdaNodeList = reader.getNodes(QuranPropertiesNaming.SAJDA_TAG);
			loadLocalizedProps();
		} catch (XmlReadException e) {
			logger.doFatal(e);
		}

		int i;
		SuraProperties sura = new SuraProperties();
		JuzProperties juz = new JuzProperties();
		SajdaProperties sajda = new SajdaProperties();

		for (i = 0; i < suraNodeList.size(); i++) {
			sura = new SuraProperties();
			Element suraElem = (Element) suraNodeList.item(i);
			sura.setAyaCount(Integer.parseInt(suraElem.getAttribute(QuranPropertiesNaming.AYA_COUNT_ATTR)));
			sura.setMadani(QuranPropertiesUtils.isMadani(suraElem.getAttribute(QuranPropertiesNaming.DESCENT_ATTR)));
			sura.setName(suraElem.getAttribute(QuranPropertiesNaming.NAME_ATTR));
			sura.setEnglishName(suraElem.getAttribute(QuranPropertiesNaming.EN_NAME_ATTR));
			sura.setEnglishTransliteratedName(suraElem.getAttribute(QuranPropertiesNaming.NAME_TRANSLITERATED_ATTR));
			sura.setIndex(Integer.parseInt(suraElem.getAttribute(QuranPropertiesNaming.INDEX_ATTR)));

			updateSuraLocalizedProps(sura);
			suraProp.add(sura);
		}

		for (i = 0; i < juzNodeList.size(); i++) {
			Element juzElem = (Element) juzNodeList.item(i);
			juz = new JuzProperties();
			juz.setIndex(Integer.parseInt(juzElem.getAttribute(QuranPropertiesNaming.INDEX_ATTR)));
			juz.setSuraNumber(Integer.parseInt(juzElem.getAttribute(QuranPropertiesNaming.SURA_NUM_ATTR)));
			juz.setAyaNumber(Integer.parseInt(juzElem.getAttribute(QuranPropertiesNaming.AYA_NUM_ATTR)));
			for (int j = 0; j < 4; j++) {
				juz.setHizbQuarters(1, j + 1, new QuranLocation(hizbQuads[8 * i + j][0], hizbQuads[8 * i + j][1]));
				juz.setHizbQuarters(2, j + 1, new QuranLocation(hizbQuads[8 * i + j + 4][0], hizbQuads[8 * i + j + 4][1]));
			}

			juzProp.add(juz);
		}

		for (i = 0; i < sajdaNodeList.size(); i++) {
			Element sajdaElem = (Element) sajdaNodeList.item(i);
			sajda = new SajdaProperties();

			sajda.setIndex(Integer.parseInt(sajdaElem.getAttribute(QuranPropertiesNaming.INDEX_ATTR)));
			sajda.setType(QuranPropertiesUtils.getSajdaType(sajdaElem.getAttribute(QuranPropertiesNaming.TYPE_ATTR)));
			sajda.setAyaNumber(Integer.parseInt(sajdaElem.getAttribute(QuranPropertiesNaming.AYA_NUM_ATTR)));
			sajda.setSuraNumber(Integer.parseInt(sajdaElem.getAttribute(QuranPropertiesNaming.SURA_NUM_ATTR)));

			sajdaProp.add(sajda);
		}

		logger.info("Quran properties loaded successfully.");
	}

	private void updateSuraLocalizedProps(SuraProperties sura) {
		int i = sura.getIndex() - 1;
		if (suraNodeListL10N != null) {
			Element l10SuraElem = (Element) suraNodeListL10N.item(i);
			SuraProperties.l10nName[i] = l10SuraElem.getAttribute(QuranPropertiesNaming.NAME_TRANSLATED_ATTR);
			SuraProperties.l10nTransliterate[i] = l10SuraElem.getAttribute(QuranPropertiesNaming.NAME_TRANSLITERATED_ATTR);
		} else {
			SuraProperties.l10nName[i] = sura.getEnglishName();
			SuraProperties.l10nTransliterate[i] = sura.getEnglishTransliteratedName();
		}
	}

	private boolean loadLocalizedProps() throws XmlReadException {
		File localizedPropFile = new File(resource.getString("quran.props.l10n", new Object[] { LanguageEngine
				.getInstance().getLocale().getLanguage() }));
		if (localizedPropFile.exists()) {
			logger.debug("Loading localized Quran sura names from: " + localizedPropFile.getName());
			XmlReader reader = new XmlReader(localizedPropFile);
			suraNodeListL10N = reader.getNodes(QuranPropertiesNaming.SURA_TAG);
			return true;
		} else {
			logger.debug("No localized Quran metadata available: " + localizedPropFile);
			return false;
		}
	}
}
