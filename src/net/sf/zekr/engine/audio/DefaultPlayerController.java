/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2009
 */
package net.sf.zekr.engine.audio;

import java.util.Collection;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Default Zekr audio player which utilizes {@link BasicPlayer} for playing audio. This player is capable of
 * playing mp3, ogg or other media formats while proper SPI implementations of
 * {@link javax.sound.sampled.spi.AudioFileReader} and
 * {@link javax.sound.sampled.spi.FormatConversionProvider} are provided at runtime.
 * <p/>
 * We need to just create a single instance of this class.
 * 
 * @author Mohsen Saboorian
 */
public class DefaultPlayerController implements PlayerController {
	BasicPlayer player;
	private int volume;
	private boolean multiAya;
	private int interval; // wait between two ayas (in milliseconds)
	private int repeatTime;
	private PropertiesConfiguration props;
	private PlayingItem playingItem = PlayingItem.AYA;
	private PlayableObject currentPlayableObject;

	public DefaultPlayerController(PropertiesConfiguration props) {
		this.props = props;
		player = new BasicPlayer();
		volume = props.getInt("audio.volume", 50);
		repeatTime = props.getInt("audio.repeatTime", 1);
		interval = props.getInt("audio.interval", 0);
		multiAya = props.getBoolean("audio.continuousAya", true);
	}

	public void open(PlayableObject playableObject) throws PlayerException {
		this.currentPlayableObject = playableObject;
		try {
			if (playableObject.getUrl() != null) {
				player.open(playableObject.getUrl());
			} else if (playableObject.getFile() != null) {
				if (!playableObject.getFile().exists()) {
					throw new PlayerException("File not found: " + playableObject.getFile());
				}
				player.open(playableObject.getFile());
			} else {
				player.open(playableObject.getInputStream());
			}
		} catch (BasicPlayerException e) {
			throw new PlayerException("Error opening playable object: " + playableObject, e);
		}
	}

	public void pause() throws PlayerException {
		try {
			player.pause();
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}
	}

	private void updateVolumeSilently() {
		try {
			setGain(volume / 100.0);
		} catch (PlayerException e) {
		}
	}

	public void play() throws PlayerException {
		try {
			player.play();
			updateVolumeSilently();
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}
	}

	public void resume() throws PlayerException {
		try {
			player.resume();
			updateVolumeSilently();
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}
	}

	public long seek(long bytes) throws PlayerException {
		try {
			return player.seek(bytes);
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}
	}

	public void setGain(double gain) throws PlayerException {
		try {
			player.setGain(gain);
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}

	}

	public void setPan(double pan) throws PlayerException {
		try {
			player.setPan(pan);
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}

	}

	public void stop() throws PlayerException {
		try {
			player.stop();
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}
	}

	public int getStatus() {
		return player.getStatus();
	}

	public void addBasicPlayerListener(BasicPlayerListener bpl) {
		player.addBasicPlayerListener(bpl);

	}

	@SuppressWarnings("unchecked")
	public Collection<BasicPlayerListener> getListeners() {
		return player.getListeners();
	}

	public void removeBasicPlayerListener(BasicPlayerListener bpl) {
		player.removeBasicPlayerListener(bpl);
	}

	/**
	 * Sets volume. It is applied to the player thread only if player status is
	 * {@link PlayerController#PLAYING} or {@link PlayerController#PAUSED}
	 * 
	 * @param volume a number between 0 to 100
	 */
	public void setVolume(int volume) {
		this.volume = volume;
		props.setProperty("audio.volume", volume);

		int stat = getStatus();
		if (stat == PLAYING || stat == PAUSED) {
			setGain(volume / 100.0);
		}
	}

	/**
	 * @return volume: a number between 0 to 100
	 */
	public int getVolume() {
		return volume;
	}

	public boolean isMultiAya() {
		return multiAya;
	}

	public void setMultiAya(boolean multiAya) {
		this.multiAya = multiAya;
		props.setProperty("audio.continuousAya", multiAya);

	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
		props.setProperty("audio.interval", interval);
	}

	public int getRepeatTime() {
		return repeatTime;
	}

	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
		props.setProperty("audio.repeatTime", repeatTime);
	}

	public PlayingItem getPlayingItem() {
		return this.playingItem;
	}

	public void setPlayingItem(PlayingItem playingItem) {
		this.playingItem = playingItem;
	}

	public PlayableObject getCurrentPlayableObject() {
		return currentPlayableObject;
	}
}
