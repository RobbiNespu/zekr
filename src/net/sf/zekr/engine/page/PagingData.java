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
import java.util.List;

import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.XmlReader;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This file corresponds to a single <tt>.page.xml</tt> which is a Quran paging schema containing from-to pair
 * of informations for each page of the Quran.
 * 
 * @author Mohsen Saboorian
 */
public class PagingData extends AbstractQuranPagingData implements IPagingData {
	private final Logger logger = Logger.getLogger(this.getClass());

	public String id;
	public File file;

	private boolean loaded;

	/** List of {@link QuranPage} items. */
	private List pageData;

	public String name;

	public PagingData() {
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.engine.page.IPagingData#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.engine.page.IPagingData#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.engine.page.IPagingData#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.engine.page.IPagingData#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.engine.page.IPagingData#getQuranPage(int)
	 */
	public IQuranPage getQuranPage(int pageNum) {
		return (IQuranPage) pageData.get(pageNum - 1);
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.engine.page.IPagingData#getPageList()
	 */
	public List getPageList() {
		return pageData;
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.engine.page.IPagingData#size()
	 */
	public int size() {
		return pageData.size();
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.engine.page.IPagingData#load()
	 */
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
			logger.debug("Process current pagination data.");
			XmlReader xmlReader = new XmlReader(file);
			Element root = xmlReader.getDocumentElement();
			name = root.getAttribute("name");
			NodeList pageNodeList = root.getElementsByTagName("page");
			QuranPage prevPage = null;
			for (int i = 0; i < pageNodeList.getLength(); i++) {
				QuranPage page = new QuranPage();
				Element pageElem = (Element) pageNodeList.item(i);
				page.setIndex(Integer.parseInt(pageElem.getAttribute("index")));
				QuranLocation fromLoc = new QuranLocation(Integer.parseInt(pageElem.getAttribute("sura")), Integer
						.parseInt(pageElem.getAttribute("aya")));
				page.setFrom(fromLoc);
				if (prevPage != null) { // works from second element
					prevPage.setTo(fromLoc);
				}
				pageData.add(page);
				prevPage = page;
			}
			if (pageData.size() > 0) {
				pageData.remove(pageData.size() - 1); // last element is always fake
			}
		} catch (Exception e) {
			throw new PagingException(e);
		}
	}

	/**
	 * @param obj
	 * @return true if <code>obj.id</code> equals with <code>this.id</code>, or false if either of IDs are
	 *         null, or obj is not instance of {@link PagingData}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof PagingData) {
			PagingData pd = (PagingData) obj;
			if (pd.id != null && id != null)
				return pd.id.equals(this.id);
		}
		return false;
	}
}
