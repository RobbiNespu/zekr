/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2008
 */
package net.sf.zekr.engine.page;

import java.io.File;
import java.util.ArrayList;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.XmlReader;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Custom paging data loaded from a <tt>.page.xml</tt> file.<br>
 * <br>
 * This file corresponds to a single <tt>.page.xml</tt> which is a Quran paging schema containing from-to pair
 * of informations for each page of the Quran.
 * 
 * @author Mohsen Saboorian
 */
public class CustomPagingData extends AbstractQuranPagingData implements IPagingData {
	private final Logger logger = Logger.getLogger(this.getClass());
	public File file;

	private boolean loaded;

	public CustomPagingData() {
		pageList = new ArrayList();
	}

	public void setId(String id) {
		this.id = id;
	}

	public void load() throws PagingException {
		if (!loaded) {
			logger.debug("Loading paging data: " + file);
			loaded = true;
			_load();
		} else {
			logger.debug("Revelation pack already loaded: " + id);
		}
	}

	private void _load() throws PagingException {
		try {
			XmlReader xmlReader = new XmlReader(file);
			Element root = xmlReader.getDocumentElement();
			name = root.getAttribute("name");
			logger.debug("Process pagination data: " + name);
			NodeList pageNodeList = root.getElementsByTagName("page");
			QuranPage prevPage = null;
			for (int i = 0; i < pageNodeList.getLength(); i++) {
				QuranPage page = new QuranPage();
				Element pageElem = (Element) pageNodeList.item(i);
				page.setIndex(Integer.parseInt(pageElem.getAttribute("index")));
				IQuranLocation fromLoc = new QuranLocation(Integer.parseInt(pageElem.getAttribute("sura")), Integer
						.parseInt(pageElem.getAttribute("aya")));
				page.setFrom(fromLoc);
				if (prevPage != null) { // works from second element
					prevPage.setTo(fromLoc.getPrev());
				}
				pageList.add(page);
				prevPage = page;
			}
			if (pageList.size() > 0) {
				pageList.remove(pageList.size() - 1); // last element is always fake
			}
		} catch (Exception e) {
			throw new PagingException(e);
		}
	}

	/**
	 * @param obj
	 * @return true if <code>obj.id</code> equals with <code>this.id</code>, or false if either of IDs are
	 *         null, or obj is not instance of {@link CustomPagingData}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof CustomPagingData) {
			CustomPagingData pd = (CustomPagingData) obj;
			if (pd.id != null && id != null)
				return pd.id.equals(this.id);
		}
		return false;
	}

	public String toString() {
		return getId() + ": (" + (loaded ? getName() : lang.getMeaning("NOT_LOADED")) + ")";
	}
}
