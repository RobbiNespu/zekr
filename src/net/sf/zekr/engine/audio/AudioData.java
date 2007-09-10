/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 2, 2007
 */
package net.sf.zekr.engine.audio;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public class AudioData {
	/**
	 * Single playlist for each sura.
	 */
	public static final String SURA_PLAYLIST = "sura";

	/**
	 * A playlist for the whole Quran.
	 */
	public static final String COLLECTION_PLAYLIST = "all";

	private String id;
	private String name;
	private String reciter;
	private String license;
	private Locale locale;

	private String playlistMode;
	private String playlistBaseUrl;
	private String playlistFileName;
	private String playlistSuraPad;

	private String audioServerUrl;
	private String audioBaseUrl;
	private String audioFileName;
	private String audioFileAyaPad;
	private String audioFileSuraPad;

	private String prestartFileName;
	private String startFileName;
	private String endFileName;

	private String playlistProvider;

	public AudioData() {
	}

	public String getReciter() {
		return reciter;
	}

	public void setReciter(String reciter) {
		this.reciter = reciter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLicense() {
		return license;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getAudioServerUrl() {
		return audioServerUrl;
	}

	public void setAudioServerUrl(String audioServerUrl) {
		this.audioServerUrl = audioServerUrl;
	}

	public String getAudioBaseUrl() {
		return audioBaseUrl;
	}

	public void setAudioBaseUrl(String audioBaseUrl) {
		this.audioBaseUrl = audioBaseUrl;
	}

	public String getAudioFileAyaPad() {
		return audioFileAyaPad;
	}

	public void setAudioFileAyaPad(String audioFileAyaPad) {
		this.audioFileAyaPad = audioFileAyaPad;
	}

	public String getAudioFileName() {
		return audioFileName;
	}

	public void setAudioFileName(String audioFileName) {
		this.audioFileName = audioFileName;
	}

	public String getAudioFileSuraPad() {
		return audioFileSuraPad;
	}

	public void setAudioFileSuraPad(String audioFileSuraPad) {
		this.audioFileSuraPad = audioFileSuraPad;
	}

	/**
	 * Should always have a trailing slash, so that it can be concatenated with playlist file name. This should be
	 * ignored for offline playlist providers, because offline playlists have always a specific path on workspace (audio
	 * cache).
	 * 
	 * @return playlist base URL
	 */
	public String getPlaylistBaseUrl() {
		return playlistBaseUrl;
	}

	public void setPlaylistBaseUrl(String playlistBaseUrl) {
		this.playlistBaseUrl = playlistBaseUrl;
	}

	public String getPlaylistFileName() {
		return playlistFileName;
	}

	public void setPlaylistFileName(String playlistFileName) {
		this.playlistFileName = playlistFileName;
	}

	public String getPlaylistMode() {
		return playlistMode;
	}

	public void setPlaylistMode(String playlistMode) {
		this.playlistMode = playlistMode;
	}

	public String getEndFileName() {
		return endFileName;
	}

	public void setEndFileName(String endFileName) {
		this.endFileName = endFileName;
	}

	public String getPrestartFileName() {
		return prestartFileName;
	}

	public void setPrestartFileName(String prestartFileName) {
		this.prestartFileName = prestartFileName;
	}

	public String getStartFileName() {
		return startFileName;
	}

	public void setStartFileName(String startFileName) {
		this.startFileName = startFileName;
	}

	public String getPlaylistProvider() {
		return playlistProvider;
	}

	public void setPlaylistProvider(String playlistProvider) {
		this.playlistProvider = playlistProvider;
	}

	public String getPlaylistSuraPad() {
		return playlistSuraPad;
	}

	public void setPlaylistSuraPad(String playlistSuraPad) {
		this.playlistSuraPad = playlistSuraPad;
	}

	public PlaylistProvider newPlaylistProvider(int suraNum) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return (PlaylistProvider) Class.forName(getPlaylistProvider()).getConstructor(
				new Class[] { AudioData.class, int.class }).newInstance(new Object[] { this, new Integer(suraNum) });
	}

	public String toString() {
		return "AUDIO: " + getReciter();
	}

	/**
	 * @param fileName
	 * @return relative URL for the audio file name
	 */
	public String getRelativeAudioUrl(String fileName) {
		return getAudioBaseUrl() + "/" + fileName;
	}
}
