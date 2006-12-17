/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 28, 2006
 */
package net.sf.zekr.common.config;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.XmlReadException;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlWriter;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class _Bookmark {
	private final static Logger logger = Logger.getLogger(_Bookmark.class);

	private static _Bookmark thisInstance;
	private File bookmarkFile;
	private XmlReader bookmarkXml;

	public _Bookmark(File bookmarkFile) {
		this.bookmarkFile = bookmarkFile;
		try {
			bookmarkXml = new XmlReader(bookmarkFile);
		} catch (XmlReadException e) {
			logger.error("Error loading bookmark XML file.");
			logger.log(e);
		}
	}

	public static _Bookmark load(File bookmarkFile) {
		thisInstance = new _Bookmark(bookmarkFile);
		return thisInstance;
	}

	public void save() {
		try {
			XmlWriter.writeXML(bookmarkXml.getDocument(), bookmarkFile);
		} catch (TransformerException e) {
			logger.error("Error saving bookmark XML file.");
			logger.log(e);
		}
	}

	public XmlReader getBookmarkXml() {
		return bookmarkXml;
	}

}
