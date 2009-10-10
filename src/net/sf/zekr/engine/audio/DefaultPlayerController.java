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
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

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
	private PlayMode playMode = PlayMode.CONTINUOUS;
	private Map<String, Object> props;
	private int volume = 50;
	private boolean multiAya = true;

	public DefaultPlayerController() {
		player = new BasicPlayer();
	}

	public void open(PlayableObject po) throws PlayerException {
		try {
			if (po.getUrl() != null) {
				player.open(po.getUrl());
			} else if (po.getFile() != null) {
				player.open(po.getFile());
			} else {
				player.open(po.getInputStream());
			}
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}
	}

	public void pause() throws PlayerException {
		try {
			player.pause();
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}
	}

	public void play() throws PlayerException {
		try {
			player.play();
			setGain(volume / 100.0);
		} catch (BasicPlayerException e) {
			throw new PlayerException(e);
		}
	}

	public void resume() throws PlayerException {
		try {
			player.resume();
			setGain(volume / 100.0);
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

	public PlayMode getPlayMode() {
		return playMode;
	}

	public void setPlayMode(PlayMode playMode) {
		this.playMode = playMode;
	}

	/**
	 * Sets volume. It is applied to the player thread only if player status is
	 * {@link PlayerController#PLAYING} or {@link PlayerController#PAUSED}
	 * 
	 * @param volume a number between 0 to 100
	 */
	public void setVolume(int volume) {
		this.volume = volume;
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
	}
}
