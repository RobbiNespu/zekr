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
import java.util.HashMap;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.NodeList;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;

import org.eclipse.swt.SWT;
import org.w3c.dom.Node;

/**
 * LanguageEngine is a <i>singleton </i> class, designed to perform language related
 * works. In a nutshell, this class is responsible for loading language packs, validating
 * them and translating words into supported languages.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class LanguageEngine extends LanguageEngineNaming {
	/**
	 * <code>engine</code> will be instantiated the first time <code>getInstance</code>
	 * is called.
	 */
	private static LanguageEngine engine = null;
	
	private LanguagePack languagePack = null;

	private XmlReader reader = null;

	private Map commonWords = null; // word.common
	private Map specialWords = null; // word.special
	private Map informMessages = null; // message.inform
	private Map confirmMessages = null; // message.confirm
	private Map errorMessages = null; // message.error
	private Map tooltipMessages = null; // message.tooltip
	private Map forms = null; // forms.frame
	private Map globals = null; // forms.global

	private final static Logger logger = Logger.getLogger(LanguageEngine.class);
	private final ApplicationConfig config = ApplicationConfig.getInsatnce();

	private Language language;
	private File packFile;

	/**
	 * Creates a language engine instance using the given language pack. If the pack does
	 * not exists It will use DEAFULT_PACK.
	 */
	private LanguageEngine() {
		logger.info("Initializing language engine...");
		language = config.getLanguage();
		packFile = new File(config.getLanguage().getPackPath());
		if (!packFile.exists()) {
			logger.warn("Can not find language pack \"" + language.getActiveLanguagePack() + "\".");
			logger.warn("Will load the default language pack");
			language.setActiveLanguagePack(language.getDefaultLanguagePack());
		}
		init();
	}

	private void init() {
		packFile = new File(language.getActiveLanguagePack().getPath());
		if (!packFile.exists()) 
			throw new RuntimeException("Can not find default language pack " + language.getActiveLanguagePack());
		logger.info("Parsing language pack " + language.getActiveLanguagePack());
		reader = new XmlReader(packFile);
		commonWords = makeDictionary(reader.getNode(COMMON_WORDS).getChildNodes());
		specialWords = makeDictionary(reader.getNode(SPECIAL_WORDS).getChildNodes());
		informMessages = makeDictionary(reader.getNode(INFORM_MSG).getChildNodes());
		confirmMessages = makeDictionary(reader.getNode(CONFIRM_MSG).getChildNodes());
		errorMessages = makeDictionary(reader.getNode(ERROR_MSG).getChildNodes());
		tooltipMessages = makeDictionary(reader.getNode(TOOLTIP_MSG).getChildNodes());
		
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
	 * This method is used to generate a <b>2D dictionary </b>. A map with
	 * <code>key</code>s equal to <code>ID_ATTR</code> of each node of
	 * <code>nodeList</code>. Each <code>key</code> is mapped then to a second map.
	 * This second map is returned from <code>makeDictionary()</code> using
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
	 * @param list
	 *            a list of <code>&lttext&gt</code> nodes
	 * @return dictionary map
	 */
	private Map makeDictionary(org.w3c.dom.NodeList list) {
		Map resultMap = new HashMap();
		Node node = null;
		for (int i = 0; i < list.getLength(); i++) {
			node = list.item(i);
			if (node.getNodeName().equals(XmlUtils.TEXT_NODE))
				continue;
			resultMap.put(XmlUtils.getAttr(node, ID_ATTR), XmlUtils.getAttr(node,
					VALUE_ATTR));
		}
		return resultMap;
	}

	public String getMeaning(String scope, String word) {
		String meaning = "";
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
		else if (scope.equalsIgnoreCase(TOOLTIP_MSG))
			meaning = (String) tooltipMessages.get(word);
		else
			meaning = word; // return the original word
		return meaning;
	}

	public String getMeaning(String word) {
		String meaning = null;
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
		else if ((meaning = (String) tooltipMessages.get(word)) != null)
			;
		else if ((meaning = (String) globals.get(word)) != null)
			;
		else
			meaning = ""; // preventing null value
		return meaning;
	}

	/**
	 * Will replace any pattern of {x} (when x is an integer number between 1 and
	 * <code>strArray.length</code>) in <code>word</code> with corresponding item of
	 * strArray (here <code>strArray[x]</code>).
	 * 
	 * @param word source
	 * @param strArray replacement array of strings
	 * @return
	 */
	public String getDynamicMeaning(String word, String[] strArray) {
		String meaning = getMeaning(word);
		for (int i = 0; i < strArray.length; i++) {
			meaning = meaning.replaceAll("\\{" + (i + 1) + "\\}", strArray[i]);
		}
		return meaning;
	}


	/**
	 * @param id
	 * @param word
	 * @return meaning of the word, or <code>null</code> if there is either no
	 *         <code>id</code> nor no <code>word</code> within that <code>id</code>
	 *         available.
	 */
	public String getMeaningById(String id, String word) {
		if (!forms.containsKey(id))
			return null;
		return (String) ((Map) forms.get(id)).get(word);
	}

	/**
	 * @return the language direction:
	 *         <ul>
	 *         <li><code>rtl</code> if it is right to left</li>
	 *         <li><code>ltr</code> otherwise (even if there is no indication)</li>
	 *         </ul>
	 */
	public String getDirection() {
		return RIGHT_TO_LEFT.equals(XmlUtils.getAttr(reader.getParentNode(), DIRECTION_ATTR)) ? RIGHT_TO_LEFT
				: LEFT_TO_RIGHT;
	}

	/**
	 * @return Returns the current language pack.
	 */
	public LanguagePack getLanguagePack() {
		return languagePack;
	}
	
	/**
	 * Call it when the active language is changed
	 */
	public void reload() {
		init();
	}

	public int getSWTDirection() {
		return RIGHT_TO_LEFT.equals(XmlUtils.getAttr(reader.getParentNode(), DIRECTION_ATTR)) ? SWT.RIGHT_TO_LEFT
				: SWT.LEFT_TO_RIGHT;
	}

}
