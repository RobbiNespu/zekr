/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 3, 2009
 */
package net.sf.zekr.engine.audio;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import net.sf.zekr.common.util.ConfigUtils;
import net.sf.zekr.engine.log.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 * @author Mohsen Saboorian
 */
public class RecitationPackConverter {
	static Logger logger = Logger.getLogger(RecitationPackConverter.class);

	public static AudioData convert(File oldRecitationPack) throws IOException, ConfigurationException {
		PropertiesConfiguration props = ConfigUtils.loadConfig(oldRecitationPack, "UTF-8");
		AudioData ad = new AudioData();
		ad.id = props.getString("audio.id");
		if (StringUtils.isBlank(ad.id)) {
			logger.debug("audio.id cannot be null or empty");
			return null;
		}
		ad.id += "-converted";

		ad.version = "0.7.5";
		ad.lastUpdate = props.getString("audio.lastUpdate");
		ad.quality = "?";

		ad.name = props.getString("audio.name");
		if (StringUtils.isBlank(ad.id)) {
			logger.debug("audio.name cannot be null or empty");
			return null;
		}
		ad.name += " (converted)";

		ad.license = props.getString("audio.license");
		ad.locale = new Locale(props.getString("audio.language"), props.getString("audio.country"));

		String fileName = props.getString("audio.fileName");
		fileName = StringUtils.replace(fileName, "{SURA}", "%1$03d");
		fileName = StringUtils.replace(fileName, "{AYA}", "%2$03d");

		String baseUrl = props.getString("audio.baseUrl");
		if (StringUtils.isBlank(baseUrl)) {
			logger.debug("audio.baseUrl cannot be null or empty");
			return null;
		}
		baseUrl = StringUtils.replace(baseUrl, "%", "%%");

		String path;
		if (props.containsKey("audio.serverUrl")) {
			ad.type = "online";
			String serverUrl = props.getString("audio.serverUrl");
			path = serverUrl + "/" + baseUrl + "/";
			ad.onlineUrl = path + fileName;

			if (props.containsKey("audio.prestartFileName")) {
				ad.onlineAudhubillah = path + props.getString("audio.prestartFileName");
			}
			if (props.containsKey("audio.startFileName")) {
				ad.onlineBismillah = path + props.getString("audio.startFileName");
			}
			if (props.containsKey("audio.endFileName")) {
				ad.onlineSadaghallah = path + props.getString("audio.endFileName");
			}
		} else {
			ad.type = "offline";

			baseUrl = StringUtils.replace(baseUrl, "[base]", "<base>");
			baseUrl = StringUtils.replace(baseUrl, "[workspace]", "<workspace>");
			baseUrl = StringUtils.replace(baseUrl, "[w_b]", "<workspace_base>");

			path = baseUrl + "/";
			ad.offlineUrl = path + fileName;

			if (props.containsKey("audio.prestartFileName")) {
				ad.offlineAudhubillah = path + props.getString("audio.prestartFileName");
			}
			if (props.containsKey("audio.startFileName")) {
				ad.offlineBismillah = path + props.getString("audio.startFileName");
			}
			if (props.containsKey("audio.endFileName")) {
				ad.offlineSadaghallah = path + props.getString("audio.endFileName");
			}
		}

		return ad;
	}
}
