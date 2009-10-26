/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2009
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.engine.audio.AudioCacheManager;
import net.sf.zekr.engine.audio.AudioData;
import net.sf.zekr.engine.audio.PlayableObject;
import net.sf.zekr.engine.audio.PlayerController;
import net.sf.zekr.engine.audio.PlayerException;
import net.sf.zekr.engine.audio.ZekrPlayerListener;
import net.sf.zekr.engine.audio.PlayerController.PlayingItem;
import net.sf.zekr.engine.audio.ui.AudioPlayerForm;
import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

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
	AudioPlayerForm audioControllerForm;
	private ZekrPlayerListener zekrPlayerListener;
	private IUserView uvc;

	AudioPlayerUiController(QuranForm quranForm, ZekrPlayerListener zekrPlayerListener, PlayerController playerController) {
		this.quranForm = quranForm;
		this.zekrPlayerListener = zekrPlayerListener;
		this.playerController = playerController;
		this.uvc = config.getUserViewController();
	}

	public void playerStop(boolean fromUser) {
		logger.debug("Player stop status.");
		if (fromUser) {
			zekrPlayerListener.userPressedSomeButton();
		}

		playerController.stop();
		if (audioControllerForm != null && !audioControllerForm.isDisposed()) {
			audioControllerForm.stop();
		}
		quranForm.qmf.playerStop();
	}

	public void playerContinue(boolean gotoNext) {
		try {
			// playerTogglePlayPause(false);
			playerStop(false);

			IQuranLocation location = uvc.getLocation();
			if (location.isLastSura() && location.isLastAya()) {
				logger.info("Last location reached.");
				playerSlightlyStop();
				return;
			}

			if (gotoNext) {
				if (playerController.getPlayingItem() == PlayingItem.AUDHUBILLAH) {
					playerController.setPlayingItem(PlayingItem.AYA);
				} else {
					boolean hasSpecial = playerPlaySpecialItemIfNeeded();
					if (hasSpecial) {
						if (playerController.getPlayingItem() == PlayingItem.BISMILLAH) {
							quranForm.gotoNextAya();
						}
					} else {
						if (playerController.getPlayingItem() == PlayingItem.AYA) {
							quranForm.gotoNextAya();
						}
						playerController.setPlayingItem(PlayingItem.AYA);
					}
				}
			}
			playerTogglePlayPause(true, false);
		} catch (Exception e) {
			logger.error("Error while playing audio file", e);
			playerSlightlyStop();
		}
	}

	boolean playerPlaySpecialItemIfNeeded() {
		AudioData audioData = config.getAudio().getCurrent();
		AudioCacheManager audioCacheManager = config.getAudioCacheManager();
		IQuranLocation loc = uvc.getLocation();
		PlayingItem playingItem = playerController.getPlayingItem();
		if (firstTimeInThisLaunch && playingItem != PlayingItem.AUDHUBILLAH) {
			firstTimeInThisLaunch = false;
			String ona = audioData.onlineAudhubillah;
			String ofa = audioData.offlineAudhubillah;
			PlayableObject po = audioCacheManager.getPlayableObject(audioData, ofa, ona);
			if (po != null) {
				playerController.setPlayingItem(PlayingItem.AUDHUBILLAH);
				playerOpenAyaAudio(po);
				return true;
			}
		} else if (loc.isLastAya() && !loc.isLastSura() && playingItem != PlayingItem.BISMILLAH) {
			String onb = audioData.onlineBismillah;
			String ofb = audioData.offlineBismillah;
			PlayableObject po = audioCacheManager.getPlayableObject(audioData, ofb, onb);
			if (po != null) {
				playerController.setPlayingItem(PlayingItem.BISMILLAH);
				playerOpenAyaAudio(po);
				return true;
			}
		} else if (loc.isLastAya() && loc.isLastSura() && playingItem != PlayingItem.SADAGHALLAH) {
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
		audioControllerForm.updatePlayerLabel();
	}

	public void playerTogglePlayPause(boolean play, boolean fromUser) {
		try {
			int status = playerController.getStatus();
			logger.debug(String.format("Play/pause status changed to %s for %s. Current status is %s", play,
					playerController.getCurrentPlayableObject(), status));
			if (fromUser) {
				zekrPlayerListener.userPressedPlayButton();
			}
			if (play) {
				if (playerController.isMultiAya() && firstTimeInThisLaunch) {
					playerPlaySpecialItemIfNeeded();
				} else if (playerController.getPlayingItem() == PlayingItem.AYA && status != PlayerController.PLAYING
						&& status != PlayerController.PAUSED) {
					playerOpenAyaAudio();
				}
				if (status == PlayerController.PAUSED) {
					playerController.resume();
				} else {
					playerController.play();
				}
				firstTimeInThisLaunch = false;
			} else {
				playerController.pause();
			}
			if (audioControllerForm != null && !audioControllerForm.isDisposed()) {
				audioControllerForm.playerTogglePlayPause(play);
			}
			quranForm.qmf.playerTogglePlayPause(play);
		} catch (PlayerException e) {
			logger.error("Error occured in play-pause method.", e);
		}
	}

	void playerOpenAyaAudio() {
		playerOpenAyaAudio(config.getAudioCacheManager().getPlayableObject(uvc.getLocation()));
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
			if (audioControllerForm != null && !audioControllerForm.isDisposed()) {
				audioControllerForm.stop();
			}
			quranForm.qmf.playerStop();
		} catch (Exception e) {
		}
	}

	public void toggleAudioControllerForm(boolean open) {
		if (open) {
			audioControllerForm = new AudioPlayerForm(quranForm, quranForm.getShell());
			audioControllerForm.getShell().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					if (!quranForm.isDisposed()) {
						quranForm.qmf.toggleAudioPanelState(false);
					}
				}
			});
			audioControllerForm.show();
		} else if (audioControllerForm != null) {
			audioControllerForm.close();
		}
	}
}
