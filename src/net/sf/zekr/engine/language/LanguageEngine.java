/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 7, 2004
 */

package net.sf.zekr.engine.language;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.NodeList;
import net.sf.zekr.engine.xml.XmlReadException;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.w3c.dom.Node;

/**
 * LanguageEngine is a <i>singleton</i> class, designed to perform language related works. This class is
 * responsible for loading language packs, validating them and translating words into supported languages.
 * 
 * @author Mohsen Saboorian
 */
public class LanguageEngine extends LanguageEngineNaming {
	/**
	 * <code>engine</code> will be instantiated the first time <code>getInstance</code> is called.
	 */
	private static LanguageEngine engine = null;

	private LanguagePack languagePack = null;

	private XmlReader reader = null;

	private Map commonWords = null; // word.common
	private Map specialWords = null; // word.special
	private Map informMessages = null; // message.inform
	private Map confirmMessages = null; // message.confirm
	private Map errorMessages = null; // message.error
	private Map hintMessages = null; // message.tooltip
	private Map forms = null; // forms.frame
	private Map globals = null; // forms.global

	private final Logger logger = Logger.getLogger(LanguageEngine.class);

	private Language language;
	private File packFile;

	/**
	 * Creates a language engine instance using the given language pack. If the pack does not exists It will
	 * use DEAFULT_PACK.
	 */
	private LanguageEngine() {
		logger.info("Initializing language engine...");
		language = Language.getInstance();
		packFile = new File(language.getPackPath());
		if (!packFile.exists()) {
			logger.warn("Can not find language pack " + language.getActiveLanguagePack());
			logger.warn("Will load the default (en_US) language pack");
			language.setActiveLanguagePack("en_US");
		}
		init();
	}

	private void init() {
		languagePack = language.getActiveLanguagePack();
		packFile = new File(language.getActiveLanguagePack().getPath());
		if (!packFile.exists())
			throw new RuntimeException("Can not find language pack " + language.getActiveLanguagePack());
		logger.info("Parsing language pack " + language.getActiveLanguagePack());
		try {
			reader = new XmlReader(packFile);
		} catch (XmlReadException e) {
			logger.log(e);
		}
		commonWords = makeDictionary(reader.getNode(COMMON_WORDS).getChildNodes());
		specialWords = makeDictionary(reader.getNode(SPECIAL_WORDS).getChildNodes());
		informMessages = makeDictionary(reader.getNode(INFORM_MSG).getChildNodes());
		confirmMessages = makeDictionary(reader.getNode(CONFIRM_MSG).getChildNodes());
		errorMessages = makeDictionary(reader.getNode(ERROR_MSG).getChildNodes());
		hintMessages = makeDictionary(reader.getNode(HINT_MSG).getChildNodes());

		forms = makeMultipleDictionaries(reader.getNodes(FORM));
		globals = makeDictionary(reader.getNode(GLOBAL).getChildNodes());
	}

	/**
	 * @return language engine instance with default language settings.
	 */
	public static LanguageEngine getInstance() {
		if (engine == null)
			engine = new LanguageEngine();
		return engine;
	}

	/**
	 * This method is used to generate a <b>2D dictionary </b>. A map with <code>key</code>s equal to
	 * <code>ID_ATTR</code> of each node of <code>nodeList</code>. Each <code>key</code> is mapped then
	 * to a second map. This second map is returned from <code>makeDictionary()</code> using
	 * <code>nodeList.item(i).getChildNodes()</code> as it's parameter.
	 * 
	 * @param nodeList
	 * @return
	 */
	private Map makeMultipleDictionaries(NodeList nodeList) {
		Map retMap = new HashMap();
		Node node = null;
		String mapName;
		for (int i = 0; i < nodeList.size(); i++) {
			node = nodeList.item(i);
			mapName = XmlUtils.getAttr(node, ID_ATTR);
			retMap.put(mapName, makeDictionary(node.getChildNodes()));
		}
		return retMap;
	}

	/**
	 * Generates dictionaries from <code>node</code> mapping.
	 * 
	 * @param list a list of <code>&lttext&gt</code> nodes
	 * @return dictionary map
	 */
	private Map makeDictionary(org.w3c.dom.NodeList list) {
		Map resultMap = new HashMap();
		Node node = null;
		for (int i = 0; i < list.getLength(); i++) {
			node = list.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			resultMap.put(XmlUtils.getAttr(node, ID_ATTR), XmlUtils.getAttr(node, VALUE_ATTR));
		}
		return resultMap;
	}

	public String getMeaning(String scope, String word) {
		String meaning;
		if (scope.equalsIgnoreCase(COMMON_WORDS))
			meaning = (String) commonWords.get(word);
		else if (scope.equalsIgnoreCase(SPECIAL_WORDS))
			meaning = (String) specialWords.get(word);
		else if (scope.equalsIgnoreCase(INFORM_MSG))
			meaning = (String) informMessages.get(word);
		else if (scope.equalsIgnoreCase(CONFIRM_MSG))
			meaning = (String) confirmMessages.get(word);
		else if (scope.equalsIgnoreCase(ERROR_MSG))
			meaning = (String) errorMessages.get(word);
		else if (scope.equalsIgnoreCase(HINT_MSG))
			meaning = (String) hintMessages.get(word);
		else
			meaning = word; // return the original word
		return meaning;
	}

	public String getMeaning(String word) {
		String meaning;
		if ((meaning = (String) commonWords.get(word)) != null)
			;
		else if ((meaning = (String) specialWords.get(word)) != null)
			;
		else if ((meaning = (String) informMessages.get(word)) != null)
			;
		else if ((meaning = (String) confirmMessages.get(word)) != null)
			;
		else if ((meaning = (String) errorMessages.get(word)) != null)
			;
		else if ((meaning = (String) errorMessages.get(word)) != null)
			;
		else if ((meaning = (String) hintMessages.get(word)) != null)
			;
		else if ((meaning = (String) globals.get(word)) != null)
			;
		else
			meaning = word; // preventing null value
		return meaning;
	}

	/**
	 * Will replace any pattern of {x} (when x is an integer number between 1 and <code>strArray.length</code>)
	 * in <code>word</code> with corresponding item of strArray (here <code>strArray[x]</code>).
	 * 
	 * @param word source
	 * @param strArray replacement array of strings
	 */
	public String getDynamicMeaning(String word, String[] strArray) {
		String meaning = getMeaning(word);
		for (int i = 0; i < strArray.length; i++) {
			// TODO: bug with strings with "\" character
			meaning = meaning.replaceAll("\\{" + (i + 1) + "\\}", escape(strArray[i]));
		}
		return meaning;
	}

	/**
	 * @param id
	 * @param word
	 * @return meaning of the word, or <b>empty string</b> if there is either no <code>id</code> nor no
	 *         <code>word</code> within that <code>id</code> available.
	 */
	public String getMeaningById(String id, String word) {
		if (!forms.containsKey(id))
			return word; // prevent null value
		Map formMap = (Map) forms.get(id);
		if (!formMap.containsKey(word))
			return word; // prevent null value
		return (String) formMap.get(word);
	}

	/**
	 * Will replace any pattern of {x} (when x is an integer number between 1 and <code>strArray.length</code>)
	 * in <code>word</code> with corresponding item of strArray (here <code>strArray[x]</code>).
	 * 
	 * @param id
	 * @param word
	 * @param strArray replacement array of strings
	 */
	public String getDynamicMeaningById(String id, String word, String[] strArray) {
		if (!forms.containsKey(id))
			return "";
		String meaning = (String) ((Map) forms.get(id)).get(word);
		for (int i = 0; i < strArray.length; i++) {
			meaning = meaning.replaceAll("\\{" + (i + 1) + "\\}", escape(strArray[i]));
		}
		return meaning;
	}

	private String escape(String str) {
		return StringUtils.replace(str, "\\", "\\\\");
	}

	/**
	 * @return the language direction:
	 *         <ul>
	 *         <li><code>rtl</code> if it is right to left</li>
	 *         <li><code>ltr</code> otherwise (even if there is no indication)</li>
	 *         </ul>
	 */
	public String getDirection() {
		return RIGHT_TO_LEFT.equals(languagePack.direction) ? RIGHT_TO_LEFT : LEFT_TO_RIGHT;
	}

	/**
	 * @return current language pack locale.
	 */
	public Locale getLocale() {
		String[] l = languagePack.id.split("_");
		if (l.length != 2)
			throw new LanguagePackException(
					"Illegal language pack id. ID should be of the form: xx_YY, where xx is a 2-character language ID"
							+ " and YY is a 2-character country ID.");
		return new Locale(l[0], l[1]);
	}

	/**
	 * Call it when the active language is changed
	 */
	public void reload() {
		init();
	}

	public int getSWTDirection() {
		return RIGHT_TO_LEFT.equals(getDirection()) ? SWT.RIGHT_TO_LEFT : SWT.LEFT_TO_RIGHT;
	}

	public static int getSWTDirection(String dir) {
		return RIGHT_TO_LEFT.equals(dir) ? SWT.RIGHT_TO_LEFT : SWT.LEFT_TO_RIGHT;
	}

	/**
	 * @return An ascending sorted <code>List</code> of available <code>LanguagePack</code>s.
	 */
	public List getLangPacks() {
		List list = new ArrayList(language.getLanguageMap().values());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((LanguagePack) o1).name.compareTo(((LanguagePack) o2).name);
			}
		});
		return list;
	}
}
