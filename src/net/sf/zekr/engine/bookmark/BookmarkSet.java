/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 1, 2006
 */
package net.sf.zekr.engine.bookmark;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import net.sf.zekr.common.ZekrBaseException;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.XmlReadException;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BookmarkSet {
	private final static Logger logger = Logger.getLogger(BookmarkSet.class);
	private final static LanguageEngine lang = LanguageEngine.getInstance();

	private File file;
	private String name;
	private String author;
	private String language = "English";
	private String dir = LanguageEngine.LEFT_TO_RIGHT;
	private String desc;
	private Date modifyDate;
	private Date createDate;
	private int _idCounter = 1;

	/** A dummy parent item to hold all the bookmark set. */
	private BookmarkItem parentItem;

	/**
	 * Although items are stored as a tree DS in <code>parentItem</code>, a reference to each item is stored in
	 * a map of id-items.
	 */
	private Map<String, BookmarkItem> itemMap = new HashMap<String, BookmarkItem>();

	private Document xmlDocument;

	private boolean loaded = false;

	private String id;

	/**
	 * This constructor will not load all the bookmark set file. A call to <code>load()</code> is needed first.
	 * Hence, this class is in fact lazy-load.
	 * 
	 * @param filePath
	 */
	public BookmarkSet(String filePath) {
		file = new File(filePath);
		id = FilenameUtils.getBaseName(filePath);
	}

	/**
	 * This constructor is used for creating new bookmark sets. ID of the bookmark in addition to a boolean
	 * value is provided in order to differ it from the other c'tor. However, <code>isNew</code> parameter is
	 * ignored.<br>
	 * This method will load blank bookmark, and the bookmark will not be saved until a call to
	 * <code>save()</code> be performed.<br>
	 * Note that after a call to this, bookmark would be loaded as well as initialized, hence there is no need
	 * more to call <code>load()</code>.
	 * 
	 * @param id bookmark id
	 * @param isNew a dummy parameter
	 */
	public BookmarkSet(String id, boolean isNew) {
		ResourceManager res = ResourceManager.getInstance();
		String bbPath = res.getString("bookmark.blank");
		file = new File(Naming.getBookmarkDir() + "/" + id + ".xml");
		this.id = id;
		Date d = new Date();
		load(bbPath);
		setCreateDate(d);
		setModifyDate(d);
	}

	/**
	 * Should be called only once. Nothing happens if this method be called more.
	 */
	public void load() {
		load(file.getPath());
	}

	/**
	 * Should be called only once. Nothing happens if this method be called more.
	 */
	private void load(String filePath) {
		if (!loaded) {
			try {
				XmlReader bookmarkXmlReader = new XmlReader(filePath);
				xmlDocument = bookmarkXmlReader.getDocument();
				loadXml(parentItem = new BookmarkItem(), xmlDocument.getFirstChild());
				loaded = true;
			} catch (XmlReadException e) {
				logger.error("Error reading/parsing bookmark set XML file.");
				logger.log(e);
			}
		}
	}

	public void save() throws BookmarkSaveException {
		updateXml();
		try {
			XmlUtils.writeXml(xmlDocument, file);
		} catch (TransformerException e) {
			logger.error("Error saving bookmark XML file: " + e);
			throw new BookmarkSaveException("Error saving bookmark: " + e.getMessage());
		}
	}

	public void save(Document target) throws BookmarkSaveException {
		updateXml();
		try {
			XmlUtils.writeXml(xmlDocument, file);
		} catch (TransformerException e) {
			logger.error("Error saving bookmark XML file: " + e);
			throw new BookmarkSaveException("Error saving bookmark: " + e.getMessage());
		}
	}

	private void updateXml() {
		// remove all items first
		Element root = xmlDocument.getDocumentElement();
		NodeList nodes = root.getChildNodes();
		int childNodeCount = nodes.getLength();
		// it should carefully remove from the end of the list (because nodes.getLenth() is decreased as nodes
		// are removed)
		for (int i = childNodeCount - 1; i >= 0; i--) {
			Node node = nodes.item(i);
			root.removeChild(node);
		}

		Element infoElem = xmlDocument.createElement("info");
		root.appendChild(infoElem);

		Element nameElem = xmlDocument.createElement("name");
		nameElem.appendChild(xmlDocument.createTextNode(getName()));
		infoElem.appendChild(nameElem);
		Element authorElem = xmlDocument.createElement("author");
		authorElem.appendChild(xmlDocument.createTextNode(getAuthor()));
		infoElem.appendChild(authorElem);
		Element language = xmlDocument.createElement("language");
		language.appendChild(xmlDocument.createTextNode(getLanguage()));
		infoElem.appendChild(language);
		Element dirElem = xmlDocument.createElement("dir");
		dirElem.appendChild(xmlDocument.createTextNode(getDirection()));
		infoElem.appendChild(dirElem);
		Element descElem = xmlDocument.createElement("desc");
		descElem.appendChild(xmlDocument.createTextNode(getDescription()));
		infoElem.appendChild(descElem);
		Element modifyDataElem = xmlDocument.createElement("modifyDate");
		setModifyDate(new Date());
		modifyDataElem.appendChild(xmlDocument.createTextNode(dateToString(getModifyDate())));
		infoElem.appendChild(modifyDataElem);
		Element createDateElem = xmlDocument.createElement("createDate");
		createDateElem.appendChild(xmlDocument.createTextNode(getCreateDate() == null ? dateToString(getModifyDate())
				: dateToString(getCreateDate())));
		infoElem.appendChild(createDateElem);

		_updateXml(parentItem, root);
	}

	private void _updateXml(BookmarkItem item, Node node) {
		List<BookmarkItem> list = item.getChildren();
		for (BookmarkItem childItem : list) {
			if (childItem.isFolder()) {
				Element fe = xmlDocument.createElement("folder");
				fe.setAttribute("name", childItem.getName());
				fe.setAttribute("desc", childItem.getDescription());
				_updateXml(childItem, fe);
				node.appendChild(fe);
			} else {
				Element ie = xmlDocument.createElement("item");
				ie.setAttribute("name", childItem.getName());
				ie.setAttribute("desc", childItem.getDescription());
				ie.setAttribute("data", CollectionUtils.toString(childItem.getLocations(), ","));
				node.appendChild(ie);
			}
		}
	}

	private void loadXml(BookmarkItem item, Node node) {
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals("info")) {
				NodeList infoChildren = n.getChildNodes();
				for (int j = 0; j < infoChildren.getLength(); j++) {
					Node nd = infoChildren.item(j);
					if (nd.getNodeType() != Node.ELEMENT_NODE)
						continue;
					String name = nd.getNodeName();
					String value = "";
					if (nd.getFirstChild() != null)
						value = nd.getFirstChild().getNodeValue();
					if (name.equals("name")) {
						setName(value);
					} else if (name.equals("desc")) {
						setDescription(value);
					} else if (name.equals("author")) {
						setAuthor(value);
					} else if (name.equals("language")) {
						setLanguage(value);
					} else if (name.equals("dir")) {
						setDirection(value);
					} else if (name.equals("modifyDate")) {
						setCreateDate(toDateFormat(value));
					} else if (name.equals("createDate")) {
						setCreateDate(toDateFormat(value));
					}
				}
				break;
			}
		}
		_loadXml(item, node);
	}

	@SuppressWarnings("unchecked")
	private void _loadXml(BookmarkItem item, Node node) {
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				BookmarkItem bi = new BookmarkItem();
				if (e.getTagName().equals("folder")) {
					bi.setFolder(true);
					bi.setName(e.getAttribute("name"));
					bi.setDescription(e.getAttribute("desc"));

					String id = nextItemId();
					bi.setId(id);
					itemMap.put(id, bi);
					item.addChild(bi);
					_loadXml(bi, e);
				} else if (e.getTagName().equals("item")) {
					bi.setFolder(false);
					bi.setName(e.getAttribute("name"));
					try {
						bi.setLocations(CollectionUtils.fromString(e.getAttribute("data"), ",", QuranLocation.class));
					} catch (Exception exc) {
						// logger.implicitLog(exc);
						logger.log(exc);
					}
					bi.setDescription(e.getAttribute("desc"));

					String id = nextItemId();
					bi.setId(id);
					itemMap.put(id, bi);
					item.addChild(bi);
				}
			}
		}
	}

	private Date toDateFormat(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.warn("Data parse error: " + e);
		}
		return null;
	}

	private String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		return sdf.format(date);
	}

	public List<BookmarkItem> getBookmarksItems() {
		return parentItem.getChildren();
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return desc;
	}

	public void setDescription(String description) {
		this.desc = description;
	}

	public String getDirection() {
		return dir;
	}

	public void setDirection(String direction) {
		this.dir = direction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String toString() {
		String[] idn = getIdAndName();
		return idn[0] + " <" + idn[1] + ">";
	}

	public String getId() {
		return id;
	}

	public String nextItemId() {
		return String.valueOf(_idCounter++);
	}

	public void remove() throws ZekrBaseException {
		if (!file.delete()) {
			throw new ZekrBaseException("Error removing bookmark: " + id);
		}
	}

	public void changeIdIfPossible(String newId) throws ZekrBaseException {
		File newFile = new File(Naming.getBookmarkDir() + "/" + newId + ".xml");
		if (newFile.exists())
			throw new ZekrBaseException("A bookmark with the ID \"" + newId + "\" already exists.");
		try {
			FileUtils.copyFile(file, newFile);
		} catch (IOException e) {
			throw new ZekrBaseException("IO Error during ID change: " + e.getMessage());
		}
		if (!file.delete()) {
			newFile.delete();
			throw new ZekrBaseException("Could not delete the old bookmark: " + id);
		}
		file = newFile;
		id = newId;
	}

	public String[] getIdAndName() {
		if (loaded)
			return new String[] { id, name };
		else
			return new String[] { id, "[" + lang.getMeaning("NOT_LOADED") + "]" };
	}

	public boolean isLoaded() {
		return loaded;
	}

	public File getFile() {
		return file;
	}

	public Document getXmlDocument() {
		return xmlDocument;
	}
}
