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
import net.sf.zekr.common.config.ZekrConfigNaming;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;

import org.w3c.dom.Node;

/**
 * This class consists of detail of the quran text file located at
 * <code>ApplicationPath.QURAN_TEXT</code>.<br>
 * <code>QuranTextConfigNaming</code> is not really a base class, but it is extended so
 * that final <code>String</code>s in that class can be easily used.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @see TODO
 * @version 0.1
 */
final public class QuranTextProperties extends QuranTextConfigNaming {

	private static QuranTextProperties thisInstance = null;
	private ApplicationConfig appConfig = ApplicationConfig.getInsatnce();
	private XmlReader reader = null;

	private QuranTextProperties() {
		reader = new XmlReader(appConfig.getConfigFile(ZekrConfigNaming.QURAN_CONFIG_ID));
	}

	public static QuranTextProperties getInstance() {
		if (thisInstance == null)
			thisInstance = new QuranTextProperties();
		return thisInstance;
	}

	public String getSooraStartSign() {
		return XmlUtils.getAttr(reader.getNode(SOORA_TITLE), START_STRING_ATTR);
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

	public String getSooraNumberLeftString() {
		return XmlUtils.getAttr(reader.getNode(SOORA_SIGN), LEFT_STRING_ATTR);
	}

	/**
	 * @return the left aya delimiter string
	 */
	public String getAyaSignLeftString() {
		return XmlUtils.getAttr(reader.getNode(AYA_SIGN), LEFT_STRING_ATTR);
	}

	/**
	 * @return the right aya delimiter string
	 */
	public String getAyaSignRightString() {
		return XmlUtils.getAttr(reader.getNode(AYA_SIGN), RIGHT_STRING_ATTR);
	}

	public String getLineBreakString() {
		String style = XmlUtils.getAttr(reader.getNode(TEXT_FILE), LINE_BREAK_ATTR);
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
		return XmlUtils.getAttr(reader.getNode(TEXT_FILE), CHARSET_ATTR);
	}

	public String getMinorSujdaSign() {
		return getSujdaSign(false);
	}

	public String getMajorSujdaSign() {
		return getSujdaSign(true);
	}

	public String getJozRightString() {
		return XmlUtils.getAttr(reader.getNode(JOZ_SIGN), RIGHT_STRING_ATTR);
	}

	public String getJozLeftString() {
		return XmlUtils.getAttr(reader.getNode(JOZ_SIGN), LEFT_STRING_ATTR);
	}

	public String getJozValue() {
		return XmlUtils.getAttr(reader.getNode(JOZ_SIGN), VALUE_ATTR);
	}

	/**
	 * @return the regular expression corresponding to the Quran joz sign
	 */
	public String getJozRegex() {
		String value = getJozValue().equalsIgnoreCase(DIGIT_VALUE) ? "\\d+" : getJozValue();
		return "\\" + getJozRightString() + value + "\\" + getJozLeftString();
	}

	/**
	 * @param type
	 *            <code>true</code> is considered as <code>MAJOR_SUJDA</code> and
	 *            <code>false</code> is considered as <code>MINOR_SUJDA</code>
	 * @return Vajib or Mustahab Sujda sign
	 */
	private String getSujdaSign(boolean type) {
		Node node = XmlUtils.getNodeByNamedAttr(reader.getNodes(SUJDA_SIGN), SUJDA_TAG, TYPE_ATTR,
			type ? MAJOR_SUJDA : MINOR_SUJDA);
		return XmlUtils.getAttr(node, RIGHT_STRING_ATTR) + XmlUtils.getAttr(node, VALUE_ATTR)
				+ XmlUtils.getAttr(node, LEFT_STRING_ATTR);
	}

}