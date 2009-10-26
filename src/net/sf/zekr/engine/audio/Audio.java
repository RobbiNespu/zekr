/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mohsen Saboorian
 */
public class Audio {
	private AudioData current;
	private Map<String, AudioData> audioList = new LinkedHashMap<String, AudioData>();

	public void add(AudioData ad) {
		audioList.put(ad.id, ad);
	}

	public AudioData get(String audioId) {
		return (AudioData) audioList.get(audioId);
	}

	public Collection<AudioData> getAllAudio() {
		return audioList.values();
	}

	public void setCurrent(AudioData currentAudio) {
		current = currentAudio;
	}

	public AudioData getCurrent() {
		return current;
	}
}
