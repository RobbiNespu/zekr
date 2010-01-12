/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 2, 2007
 */
package net.sf.zekr.engine.audio;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.zekr.engine.common.LocalizedResource;

/**
 * @author Mohsen Saboorian
 */
public class AudioData extends LocalizedResource implements Comparable<AudioData> {
	private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy", new Locale("en", "US"));
	/**
	 * Smallest version which is still valid for this build. For example It may be Zekr 0.8.0, but if this
	 * constant is 0.7.5, then 0.7.5 is also an acceptable recitation pack.
	 */
	public static final String BASE_VALID_VERSION = "0.7.5";
	public String id;
	/** Recitation file format version */
	public String version;
	public String lastUpdate;
	public String quality;

	public String license;
	public String type;

	public Locale locale;

	public String onlineAudhubillah;
	public String onlineBismillah;
	public String onlineSadaghallah;

	public String offlineAudhubillah;
	public String offlineBismillah;
	public String offlineSadaghallah;

	public String onlineUrl;
	public String offlineUrl;

	public String getOnlineUrl(int sura, int aya) {
		return String.format(onlineUrl, sura, aya);
	}

	public String getOfflineUrl(int sura, int aya) {
		return String.format(offlineUrl, sura, aya);
	}

	public String toString() {
		return String.format("%s", name);
	}

	public int compareTo(AudioData o) {
		try {
			return DATE_FORMATTER.parse(this.lastUpdate).compareTo(DATE_FORMATTER.parse(o.lastUpdate));
		} catch (ParseException e) {
			return 0;
		}
	}

	/**
	 * Writes properties to writer parameter. It doesn't close writer.
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void save(Writer writer) throws IOException {
		write(writer, "audio.id", id);
		write(writer, "audio.version", version);
		write(writer, "audio.lastUpdate", lastUpdate);
		write(writer, "audio.quality", quality);
		writer.write("\r\n");

		// write(writer, "audio.name", name);
		write(writer, "audio.license", license);
		write(writer, "audio.language", locale.getLanguage());
		write(writer, "audio.country", locale.getCountry());
		write(writer, "audio.type", type);
		writer.write("\r\n");

		write(writer, "audio.reciter", name);
		for (Entry<String, String> entry : localizedNameMap.entrySet()) {
			write(writer, "audio.reciter" + entry.getKey(), entry.getValue());
		}
		writer.write("\r\n");

		write(writer, "audio.onlineUrl", onlineUrl);
		write(writer, "audio.offlineUrl", offlineUrl);
		writer.write("\r\n");

		write(writer, "audio.onlineAudhubillah", onlineAudhubillah);
		write(writer, "audio.onlineBismillah", onlineBismillah);
		write(writer, "audio.onlineSadaghallah", onlineSadaghallah);
		writer.write("\r\n");

		write(writer, "audio.offlineAudhubillah", offlineAudhubillah);
		write(writer, "audio.offlineBismillah", offlineBismillah);
		write(writer, "audio.offlineSadaghallah", offlineSadaghallah);
		writer.write("\r\n");
	}

	private void write(Writer writer, String key, String value) throws IOException {
		writer.write(String.format("%s = %s\r\n", key, value == null ? "" : value));
	}
}
