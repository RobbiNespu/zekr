/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 5, 2004
 */

package net.sf.zekr.common.resource;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ConfigNaming;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;

import org.w3c.dom.Element;

/**
 * This class consists of detail of the quran text file located at
 * <code>ApplicationPath.QURAN_TEXT</code>.<br>
 * <code>QuranTextConfigNaming</code> is not really a base class, but it is extended so
 * that final <code>String</code>s in that class can be easily used.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 * @deprecated This class is depricated as there is no more need for Quran 
 * text properties. Text properties is fixed: Code page Windows 1256, and \n as
 * the line delimiter.
 */
final public class QuranTextProperties extends QuranTextConfigNaming {

	private static QuranTextProperties thisInstance = null;
	private ApplicationConfig appConfig = ApplicationConfig.getInstance();
	private XmlReader reader = null;
	private ResourceManager resource = ResourceManager.getInstance();

	private QuranTextProperties() {
//		reader = new XmlReader(appConfig.getConfigFile(ConfigNaming.QURAN_CONFIG_ID));
		reader = new XmlReader(resource.getString("text.quran.props"));
	}

	public static QuranTextProperties getInstance() {
		if (thisInstance == null)
			thisInstance = new QuranTextProperties();
		return thisInstance;
	}

	public String getSuraStartSign() {
		return XmlUtils.getAttr(reader.getNode(SURA_TITLE), START_STRING_ATTR);
	}

	public boolean hasBismillah() {
		return Boolean.getBoolean(XmlUtils.getAttr(reader.getNode(BIMILLAH), EXIST_ATTR));
	}

	/**
	 * @return a regular expression matches the aya delimiter signs and numbers (if any)
	 */
	public String getAyaDelimiter() {
		String pattern = "\\d+"; // a digit one or more times.

		String leftAya = getAyaSignLeftString();
		String rightAya = getAyaSignRightString();
		return rightAya + pattern + leftAya;
	}

	public String getSuraNumberLeftString() {
		return reader.getElement(SURA_SIGN).getAttribute(LEFT_STRING_ATTR);
	}

	/**
	 * @return the left aya delimiter string
	 */
	public String getAyaSignLeftString() {
		return reader.getElement(AYA_SIGN).getAttribute(LEFT_STRING_ATTR);
	}

	/**
	 * @return the right aya delimiter string
	 */
	public String getAyaSignRightString() {
		return reader.getElement(AYA_SIGN).getAttribute(RIGHT_STRING_ATTR);
	}

	public String getLineBreakString() {
		String style = reader.getElement(TEXT_FILE).getAttribute(LINE_BREAK_ATTR);
		if (style.equalsIgnoreCase("unix") || style.equalsIgnoreCase("linux"))
			return "\n";
		if (style.equalsIgnoreCase("pc") || style.equalsIgnoreCase("windows")
				|| style.equalsIgnoreCase("win32"))
			return "\r\n";
		if (style.equalsIgnoreCase("mac") || style.equalsIgnoreCase("macintosh"))
			return "\r";
		return null;
	}

	public String getCharset() {
		return reader.getElement(TEXT_FILE).getAttribute(CHARSET_ATTR);
	}

	public String getMinorSajdaSign() {
		return getSajdaSign(false);
	}

	public String getMajorSajdaSign() {
		return getSajdaSign(true);
	}

	public String getJuzRightString() {
		return reader.getElement(JUZ_SIGN).getAttribute(RIGHT_STRING_ATTR);
	}

	public String getJuzLeftString() {
		return reader.getElement(JUZ_SIGN).getAttribute(LEFT_STRING_ATTR);
	}

	public String getJuzValue() {
		return reader.getElement(JUZ_SIGN).getAttribute(VALUE_ATTR);
	}

	/**
	 * @return the regular expression corresponding to the Quran juz sign
	 */
	public String getJuzRegex() {
		String value = getJuzValue().equalsIgnoreCase(DIGIT_VALUE) ? "\\d+" : getJuzValue();
		return "\\" + getJuzRightString() + value + "\\" + getJuzLeftString();
	}

	/**
	 * @param type
	 *            <code>true</code> is considered as <code>MAJOR_SAJDA</code> and
	 *            <code>false</code> is considered as <code>MINOR_SAJDA</code>
	 * @return Vajib or Mustahab Sajda sign
	 */
	private String getSajdaSign(boolean type) {
		Element elem = XmlUtils.getElementByNamedAttr(reader.getNodes(SAJDA_SIGN), SAJDA_TAG, TYPE_ATTR,
			type ? MAJOR_SAJDA : MINOR_SAJDA);
		return elem.getAttribute(RIGHT_STRING_ATTR) + elem.getAttribute(VALUE_ATTR)
				+ elem.getAttribute(LEFT_STRING_ATTR);
	}

}