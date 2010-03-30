/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2009
 */
package net.sf.zekr.ui;

import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.engine.audio.AudioCacheManager;
import net.sf.zekr.engine.audio.AudioData;
import net.sf.zekr.engine.audio.PlayableObject;
import net.sf.zekr.engine.audio.PlayerController;
import net.sf.zekr.engine.audio.PlayerException;
import net.sf.zekr.engine.audio.ZekrPlayerListener;
import net.sf.zekr.engine.audio.PlayerController.PlayingItem;
import net.sf.zekr.engine.audio.ui.AudioPlayerForm;
import net.sf.zekr.engine.log.Logger;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;

/**
 * @author Mohsen Saboorian
 */
public class AudioPlayerUiController {
	private static Logger logger = Logger.getLogger(AudioPlayerUiController.class);
	private QuranFormMenuFactory menu;
	private ApplicationConfig config = ApplicationConfig.getInstance();
	private QuranForm quranForm;
	private PlayerController playerController;
	private boolean firstTimeInThisLaunch = true;
	private AudioPlayerForm audioControllerForm;
	private ZekrPlayerListener zekrPlayerListener;
	private IUserView uvc;

	AudioPlayerUiController(QuranForm quranForm, ZekrPlayerListener zekrPlayerListener, PlayerController playerController) {
		this.quranForm = quranForm;
		this.zekrPlayerListener = zekrPlayerListener;
		this.playerController = playerController;
		uvc = config.getUserViewController();
	}

	public void playerStop(boolean fromUser) {
		logger.debug("Player stop status.");
		if (fromUser) {
			zekrPlayerListener.userPressedSomeButton();
			playerController.setPlayingItem(null);
		}

		playerController.stop();

		if (isAudioControllerFormOpen()) {
			audioControllerForm.playerTogglePlayPause(false);
			audioControllerForm.stop();
		}
		quranForm.qmf.playerTogglePlayPause(false);
	}

	public void playerContinue(boolean gotoNext) {
		try {
			playerStop(false);

			String playScope = playerController.getPlayScope();
			boolean lastInMode = isLastItemInMode(playScope);
			if (gotoNext && lastInMode) {
				if (!playerPlaySpecialItemIfNeeded(lastInMode)) {
					logger.info("Last location of this playing mode reached: " + playScope);
					playerSlightlyStop();
					return;
				}
			} else if (gotoNext) {
				PlayingItem playingItem = playerController.getPlayingItem();
				if (playingItem == PlayingItem.AYA) {
					quranForm.quranFormController.gotoNextAya();
				}
				boolean hasSpecial = playerPlaySpecialItemIfNeeded(false);
				if (!hasSpecial) {
					playerController.setPlayingItem(PlayingItem.AYA);
				}
			}
			playerTogglePlayPause(true, false);
		} catch (Exception e) {
			logger.error("Error while playing audio file", e);
			playerSlightlyStop();
		}
	}

	private boolean isLastItemInMode(String playScope) {
		IQuranLocation loc = uvc.getLocation();
		if (PlayerController.PS_AYA.equals(playScope)) {
			return true;
		} else if (PlayerController.PS_PAGE.equals(playScope)) {
			IQuranPage page = config.getQuranPaging().getDefault().getQuranPage(uvc.getPage());
			return loc.equals(page.getTo());
		} else if (PlayerController.PS_SURA.equals(playScope)) {
			return loc.isLastAya();
		} else if (PlayerController.PS_HIZB_QUARTER.equals(playScope)) {
			JuzProperties juz = QuranPropertiesUtils.getJuzOf(loc);
			int hqi = QuranPropertiesUtils.getHizbQuadIndex(juz, loc);
			if (hqi == 7) {
				if (juz.getIndex() < 30) {
					return loc.getNext().equals(QuranPropertiesUtils.getJuz(juz.getIndex() + 1).getLocation());
				} else {
					return loc.isLastAya() && loc.isLastSura();
				}
			} else {
				return loc.getNext().equals(juz.getHizbQuarters()[hqi + 1]);
			}
		} else if (PlayerController.PS_JUZ.equals(playScope)) {
			JuzProperties juz = QuranPropertiesUtils.getJuzOf(loc);
			if (juz.getIndex() < 30) {
				return loc.getNext().equals(QuranPropertiesUtils.getJuz(juz.getIndex() + 1).getLocation());
			} else {
				return loc.isLastAya() && loc.isLastSura();
			}
		} else {
			return loc.isLastAya() && loc.isLastSura();
		}
	}

	boolean playerPlaySpecialItemIfNeeded(boolean isLastInMode) {
		AudioData audioData = config.getAudio().getCurrent();
		AudioCacheManager audioCacheManager = config.getAudioCacheManager();
		IQuranLocation loc = uvc.getLocation();
		PlayingItem playingItem = playerController.getPlayingItem();
		int aya = loc.getAya();
		int sura = loc.getSura();

		PropertiesConfiguration props = config.getProps();
		String audhubillahMode = props.getString("audio.playAudhubillah", "smart");
		String bismillahMode = props.getString("audio.playBismillah", "smart");
		String sadaghallahMode = props.getString("audio.playSadaghallah", "smart");

		if ((!"never".equals(audhubillahMode) && firstTimeInThisLaunch && playingItem == null)
				|| ("always".equals(audhubillahMode) && aya == 1 && playingItem != PlayingItem.AUDHUBILLAH && playingItem != PlayingItem.BISMILLAH)) {
			String ona = audioData.onlineAudhubillah;
			String ofa = audioData.offlineAudhubillah;
			PlayableObject po = audioCacheManager.getPlayableObject(audioData, ofa, ona);
			if (po != null) {
				playerController.setPlayingItem(PlayingItem.AUDHUBILLAH);
				playerOpenAyaAudio(po);
				return true;
			}
		} else if (("smart".equals(bismillahMode) && aya == 1 && sura != 9 && sura != 1 && (playingItem == PlayingItem.AYA || playingItem == PlayingItem.AUDHUBILLAH))
				|| ("always".equals(bismillahMode) && aya == 1 && sura != 9 && sura != 1 && playingItem != PlayingItem.BISMILLAH)) {
			String onb = audioData.onlineBismillah;
			String ofb = audioData.offlineBismillah;
			PlayableObject po = audioCacheManager.getPlayableObject(audioData, ofb, onb);
			if (po != null) {
				playerController.setPlayingItem(PlayingItem.BISMILLAH);
				playerOpenAyaAudio(po);
				return true;
			}
		} else if (("smart".equals(sadaghallahMode) && loc.isLastSura() && loc.isLastAya()
				&& playingItem == PlayingItem.AYA && isLastInMode)
				|| ("always".equals(sadaghallahMode) && isLastInMode && playingItem == PlayingItem.AYA)) {
			String ons = audioData.onlineSadaghallah;
			String ofs = audioData.offlineSadaghallah;
			PlayableObject po = audioCacheManager.getPlayableObject(audioData, ofs, ons);
			if (po != null) {
				playerController.setPlayingItem(PlayingItem.SADAGHALLAH);
				playerOpenAyaAudio(po);
				return true;
			}
		}
		return false;
	}

	public void playerUpdateAudioFormStatus() {
		if (isAudioControllerFormOpen()) {
			audioControllerForm.updatePlayerLabel();
			audioControllerForm.checkIfSeekIsSupported();
		}
	}

	public void playerTogglePlayPause(boolean play, boolean fromUser) {
		try {
			int status = playerController.getStatus();
			logger.debug(String.format("Play/pause status changed to %s. Current status is %s.", play, status));
			if (fromUser) {
				zekrPlayerListener.userPressedPlayButton();
			}
			if (play) {
				if (playerController.getPlayingItem() == PlayingItem.AYA && status != PlayerController.PLAYING
						&& status != PlayerController.PAUSED) {
					playerOpenAyaAudio();
				} else if (status != PlayerController.PAUSED && playerController.isMultiAya() && fromUser/* && firstTimeInThisLaunch*/) {
					if (!playerPlaySpecialItemIfNeeded(isLastItemInMode(playerController.getPlayScope()))) {
						playerController.setPlayingItem(PlayingItem.AYA);
						playerOpenAyaAudio();
					}
				} else if (status == PlayerController.UNKNOWN || playerController.getPlayingItem() == null) {
					playerController.setPlayingItem(PlayingItem.AYA);
					playerOpenAyaAudio();
				}
				if (status == PlayerController.PAUSED) {
					playerController.resume();
				} else {
					playerController.play();
				}
			} else {
				playerController.pause();
			}
			togglePlayPauseState(play);
		} catch (PlayerException e) {
			logger.error("Error occured in play-pause method.", e);
			playerSlightlyStop();
		} finally {
			firstTimeInThisLaunch = false;
		}
	}

	void playerOpenAyaAudio() {
		PlayableObject playableObject = config.getAudioCacheManager().getPlayableObject(uvc.getLocation());
		if (playableObject == null) {
			throw new PlayerException("Audio for this location cannot be loaded: " + uvc.getLocation());
		}
		playerOpenAyaAudio(playableObject);
	}

	void playerOpenAyaAudio(PlayableObject playableObject) {
		logger.debug(String.format("Open playable object: %s.", playableObject));
		playerController.open(playableObject);
	}

	/**
	 * When any exception happens during playing, call this method.
	 */
	public void playerSlightlyStop() {
		logger.debug("Player stop status.");
		try {
			playerController.stop();
		} catch (Exception ex) {
		}
		try {
			togglePlayPauseState(false);
		} catch (Exception e) {
		}
	}

	public void toggleAudioControllerForm(boolean open) {
		if (open) {
			audioControllerForm = new AudioPlayerForm(quranForm, quranForm.getShell());
			config.getProps().setProperty("audio.controller.show", open);
			audioControllerForm.getShell().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					if (!quranForm.isDisposed()) {
						Point location = audioControllerForm.getShell().getLocation();
						config.getProps().setProperty("audio.controller.location", new Object[] { location.x, location.y });
						quranForm.qmf.toggleAudioPanelState(false);
					}
				}
			});
			audioControllerForm.show();
		} else if (audioControllerForm != null) {
			audioControllerForm.close();
		}
	}

	public void changeRecitation(String audioId) {
		playerSlightlyStop();
		config.setCurrentAudio(audioId);
		if (audioControllerForm != null) {
			playerUpdateAudioFormStatus();
		}
		if (isAudioControllerFormOpen()) {
			audioControllerForm.getShell().pack();
		}
	}

	public AudioPlayerForm getAudioControllerForm() {
		return audioControllerForm;
	}

	public boolean isAudioControllerFormOpen() {
		return audioControllerForm != null && audioControllerForm.getShell() != null
				&& !audioControllerForm.getShell().isDisposed();
	}

	/**
	 * Navigates to next of previous aya if possible. This method keeps playing state, if it's paused or
	 * stopped remain pause or stopped and if playing, remain playing.
	 * 
	 * @param action either "prev" or "next"
	 */
	public void navigate(String action) {
		int st = playerController.getStatus();
		playerController.setPlayingItem(PlayingItem.AYA);
		playerStop(true);
		if ("prev".equals(action)) {
			if (uvc.getLocation().getPrev() == null) {
				return;
			}
			quranForm.quranFormController.gotoPrevAya();
		} else {
			if (uvc.getLocation().getNext() == null) {
				return;
			}
			quranForm.quranFormController.gotoNextAya();
		}
		if (st == PlayerController.PLAYING) {
			playerTogglePlayPause(true, true);
		}
	}

	public void togglePlayPauseState(boolean play) {
		if (isAudioControllerFormOpen()) {
			audioControllerForm.playerTogglePlayPause(play);
		}
		quranForm.qmf.playerTogglePlayPause(play);
	}

	public void progress(float progressPercent) {
		audioControllerForm.progress(10 * progressPercent);
	}

	/**
	 * Taken from <code>PlayerUI.processSeek(double)</code> of JavaZoom's jlgui.
	 * 
	 * @param seekPercent should be a float number between 0.0 and 1.0
	 */
	@SuppressWarnings("unchecked")
	public void seek(float seekPercent) {
		Map audioInfo = playerController.getCurrentAudioInfo();
		// boolean posValueJump;
		try {
			if (audioInfo != null && audioInfo.containsKey("audio.type")) {
				String type = (String) audioInfo.get("audio.type");
				// Seek support for MP3.
				Integer bytesLength = (Integer) audioInfo.get("audio.length.bytes");
				if (type.equalsIgnoreCase("mp3") && bytesLength != null) {
					long skipBytes = Math.round(bytesLength.intValue() * seekPercent);
					logger.debug(String.format("Seek bytes (MP3): %s, percent: %s.", skipBytes, seekPercent));
					playerController.seek(skipBytes);
					playerController.setVolume(playerController.getVolume());
				}
				// Seek support for WAV.
				else if (type.equalsIgnoreCase("wave") && bytesLength != null) {
					long skipBytes = Math.round(bytesLength.intValue() * seekPercent);
					logger.debug(String.format("Seek bytes (WAVE): %s, percent: %s.", skipBytes, seekPercent));
					playerController.seek(skipBytes);
					playerController.setVolume(playerController.getVolume());
				} else {
					// posValueJump = false;
				}
			} else {
				// posValueJump = false;
			}
		} catch (PlayerException pe) {
			logger.error("Cannot seek.", pe);
			// posValueJump = false;
		}
	}

	/**
	 * @param inc a value between -100 to +100 to be added to the old volume value
	 */
	public void addVolume(int inc) {
		int newVol = Math.min(playerController.getVolume() + inc, 100);
		newVol = Math.max(newVol, 0);
		playerController.setVolume(newVol);
		if (isAudioControllerFormOpen()) {
			audioControllerForm.updateVolume();
		}
	}

	/**
	 * @param percent a value between -100 to +100 to seek to. For example if current location is 67%, a -20
	 *           causes seeking to 47%. Overall seek location of lower than 0 or higher than 100 are truncated.
	 */
	public void seekForward(int percent) {
		int status = playerController.getStatus();
		if (status == PlayerController.PLAYING || status == PlayerController.PAUSED) {
			float currentProgress = (float) audioControllerForm.getProgress() / AudioPlayerForm.MAX_SEEK_VALUE;
			currentProgress = Math.max(Math.min(currentProgress + percent / 100.0f, 1f), 0f);
			seek(currentProgress);
		}
	}

	public void updateRecitationListMenu() {
		if (isAudioControllerFormOpen()) {
			audioControllerForm.updateRecitationPopupMenu();
		}
	}
}
