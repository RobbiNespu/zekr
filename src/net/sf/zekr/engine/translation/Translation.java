/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 17, 2006
 */
package net.sf.zekr.engine.translation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.addonmgr.CandidateResource;
import net.sf.zekr.engine.addonmgr.Resource;
import net.sf.zekr.engine.addonmgr.ResourceManager;
import net.sf.zekr.engine.log.Logger;

import org.apache.commons.configuration.ConfigurationException;

/**
 * A collection of all available translations as <code>{@link TranslationData}</code> objects.<br>
 * If this class had at least a single <code>TranslationData</code>, it should be set as default translation
 * as well. No default translation means, there is no translation at all.
 * 
 * @author Mohsen Saboorian
 */
public class Translation implements ResourceManager{
	private final static Logger logger = Logger.getLogger(Translation.class);

	TranslationData defaultTrans;

	private Map<String, TranslationData> translations = new LinkedHashMap<String, TranslationData>();
	private List<TranslationData> customGroup = new ArrayList<TranslationData>();

	private Comparator<TranslationData> localeComparator = new Comparator<TranslationData>() {
		public int compare(TranslationData td1, TranslationData td2) {
			int res = td1.locale.toString().compareTo(td2.locale.toString());
			return res > 0 ? 1 : -1;
		}
	};

	/**
	 * @return default translation, or <code>null</code> if there is translation at all.
	 */
	public TranslationData getDefault() {
		return defaultTrans;
	}

	public void setDefault(TranslationData defaultTrans) {
		this.defaultTrans = defaultTrans;
	}

	public TranslationData get(String transId) {
		return translations.get(transId);
	}

	public void add(TranslationData td) {
		translations.put(td.id, td);
	}

	/**
	 * @return a sorted collection representation of translations. Changing this list may not affect on the
	 *         underling translation list. Returned list is not empty (size = 0, not <code>null</code>) if
	 *         there is no translation data item.
	 */
	public List<TranslationData> getAllTranslation() {
		ArrayList<TranslationData> ret = new ArrayList<TranslationData>(translations.values());
		Collections.sort(ret, localeComparator);
		return ret;
	}

	/**
	 * @return a List of custom translations currently being used. Custom translations are a set of
	 *         translations all displayed side by side.
	 */
	public List<TranslationData> getCustomGroup() {
		return customGroup;
	}

	public void setCustomGroup(List<TranslationData> customGroup) {
		this.customGroup = customGroup;
	}

	public void loadResource(Resource r) throws ConfigurationException, IOException {
		//I think this method should be extracted out here.
		ApplicationConfig.getInstance().loadTranslationData(r.getFile());
		//here probably I should send an event to refresh the GUI.
	}

	public void unloadResource(Resource r) {
		if(!getDefault().getId().equals(r.getId()))
			translations.remove(r.getId());
			//here probably I should send an event to refresh the GUI.
	}

	public List<Resource> getLoadedResources() {
		List<Resource> resourceList=new ArrayList<Resource>();
		resourceList.addAll(getAllTranslation());
		return resourceList;
	}

	public CandidateResource getNewCandidateResource(File file) {
		return new CandidateResource(TranslationData.class,file);
	}

	public Resource getCurrentResource() {
		return getDefault();
	}

	public void setCurrentResource(Resource r) {
		this.setDefault((TranslationData)r);
	}

	
}
