/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 9, 2009
 */
package net.sf.zekr.engine.audio;

import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.util.CommonUtils;
import net.sf.zekr.engine.audio.PlayerController.PlayingItem;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.QuranForm;

import org.eclipse.swt.widgets.Display;

/**
 * @author Mohsen Saboorian
 */
@SuppressWarnings("rawtypes")
public class ZekrPlayerListener implements BasicPlayerListener {
   private static Logger logger = Logger.getLogger(AudioCacheManager.class);
   private PlayerController playerController;
   private QuranForm quranForm;
   private int repeatTimer;
   private Display display;
   private int origRepeatTimer;
   private boolean userActionPerformed = false;
   private Map audioInfo;
   boolean seeking = false;

   public ZekrPlayerListener(PlayerController playerController, QuranForm quranForm) {
      this.playerController = playerController;
      this.quranForm = quranForm;
      repeatTimer = playerController.getRepeatTime();
      display = quranForm.getDisplay();
   }

   public void opened(Object stream, Map properties) {
      // open should be handled in DefaultPlayerController's open method, since this
      // call back is not called for caching audio files.
   }

   public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
      final float progressPercent = computeProgress(bytesread, microseconds, playerController.getCurrentAudioInfo(),
            properties);
      display.asyncExec(new Runnable() {
         public void run() {
            if (!quranForm.isDisposed()) {
               if (quranForm.playerUiController.isAudioControllerFormOpen()) {
                  quranForm.playerUiController.progress(progressPercent);
               }
            }
         }
      });
   }

   public void setController(BasicController controller) {
   }

   public void stateUpdated(BasicPlayerEvent event) {
      final int code = event.getCode();
      display.syncExec(new Runnable() {
         public void run() {
            if (!quranForm.isDisposed()) {
               if (code == BasicPlayerEvent.PLAYING) {
                  if (quranForm.playerUiController.isAudioControllerFormOpen()) {
                     quranForm.playerUiController.togglePlayPauseState(true);
                  }
               } else if (code == BasicPlayerEvent.PAUSED || code == BasicPlayerEvent.STOPPED) {
                  if (quranForm.playerUiController.isAudioControllerFormOpen()) {
                     quranForm.playerUiController.togglePlayPauseState(false);
                     if (code == BasicPlayerEvent.STOPPED) {
                        quranForm.playerUiController.progress(0);
                     }
                  }
               }
               quranForm.playerUiController.playerUpdateAudioFormStatus();
            }
         }
      });

      // pre-fetch
      if (code == BasicPlayerEvent.PLAYING && playerController.isMultiAya()) {
         ApplicationConfig config = ApplicationConfig.getInstance();
         IUserView uvc = config.getUserViewController();
         IQuranLocation loc = uvc.getLocation();
         int prefetcher = config.getProps().getInt("audio.cache.prefetcher", 1);
         logger.info(String.format("Pre-fetching next %s playable objects.", prefetcher));
         IQuranLocation ql = loc;

         if (playerController.getCurrentAudioIndex() == 0) { // pre fetch next audios for current location
            for (int i = 1; i < playerController.getRecitationCount(); i++) {
               PlayableObject po = config.getAudioCacheManager().getPlayableObject(ql, i);
               if (po == null) {
                  logger.error(String.format("Failed to pre-fetch %s.", ql));
               } else {
                  logger.info(String.format("Pre-fetch %s.", po));
                  playerController.open(po, true);
               }
            }
         }

         for (int i = 0; i < prefetcher; i++) {
            ql = ql.getNext();
            if (ql == null) {
               break;
            }
            PlayableObject po = config.getAudioCacheManager().getPlayableObject(ql,
                  playerController.getCurrentAudioIndex());
            if (po == null) {
               logger.error(String.format("Failed to pre-fetch %s.", ql));
            } else {
               logger.info(String.format("Pre-fetch %s.", po));
               playerController.open(po, true);
            }
         }
      } else if (code == BasicPlayerEvent.OPENING || code == BasicPlayerEvent.STOPPED) {
         origRepeatTimer = playerController.getRepeatTime();
         userActionPerformed = false;
      } else if (code == BasicPlayerEvent.EOM) {
         repeatTimer--;

         if (playerController.isMultiAya() || !playerController.isLastRecitation()) {
            int wait = playerController.getInterval();
            // update stats
            if (playerController.getRepeatTime() != origRepeatTimer) {
               repeatTimer += playerController.getRepeatTime() - origRepeatTimer;
               origRepeatTimer = playerController.getRepeatTime();
            }

            if (repeatTimer > 0 && playerController.getPlayingItem() == PlayingItem.AYA) {
               if (!quranForm.isDisposed()) {
                  display.asyncExec(getAyaPlayerRunnable(false, wait));
               }
            } else {
               repeatTimer = origRepeatTimer;
               if (!quranForm.isDisposed()) {
                  display.asyncExec(getAyaPlayerRunnable(true, wait));
               }
            }
         } else {
            // stop
            if (!quranForm.isDisposed()) {
               display.asyncExec(new Runnable() {
                  public void run() {
                     quranForm.playerUiController.playerStop(false);
                  }
               });
            }
         }
      } else if (code == BasicPlayerEvent.SEEKING) {
      } else if (code == BasicPlayerEvent.SEEKED) {
      }
   }

   private Runnable getAyaPlayerRunnable(final boolean gotoNext, final int wait) {
      if (wait > 0 && playerController.getPlayingItem() == PlayingItem.AYA) {
         try {
            Thread.sleep(wait);
            if (userActionPerformed) {
               userActionPerformed = false;
               return CommonUtils.EMPTY_RUNNABLE;
            }
         } catch (InterruptedException e) {
         }
      }
      return new Runnable() {
         public void run() {
            quranForm.playerUiController.playerContinue(gotoNext);
         }
      };
   }

   public void userPressedPlayButton() {
      userPressedSomeButton();
      if (playerController.getRepeatTime() != origRepeatTimer) {
         origRepeatTimer = playerController.getRepeatTime();
         repeatTimer = origRepeatTimer;
      }
   }

   public void userPressedSomeButton() {
      userActionPerformed = true;
   }

   /**
    * Taken from <code>PlayerUI.processProgress(int, long, byte, Map)</code> of JavaZoom's jlgui.
    * 
    * @param bytesread
    * @param microseconds
    * @param audioInfo
    * @param properties
    * @return a float value between 0.0 and 100.0 or a negative value if this method is not supported
    */
   public float computeProgress(int bytesread, long microseconds, Map audioInfo, Map properties) {
      int byteslength = -1;
      long totalMillis = -1;

      // if it fails then try again with JavaSound SPI.
      if (totalMillis <= 0) {
         totalMillis = AudioUtils.estimateAudioTime(audioInfo);
      }

      // if it fails again then it might be stream => Total = -1
      if (totalMillis <= 0) {
         totalMillis = -1;
      }

      if (audioInfo.containsKey("audio.length.bytes")) {
         byteslength = ((Integer) audioInfo.get("audio.length.bytes")).intValue();
      }
      long millis = 0;
      float progress = -1.0f;

      //		if (microseconds > 0) {
      if (true) {
         //			millis = microseconds / 1000;
         //		} else {
         if (bytesread > 0 && byteslength > 0) {
            progress = bytesread * 1.0f / byteslength * 1.0f;
         }
         if (audioInfo.containsKey("audio.type")) {
            String audioformat = (String) audioInfo.get("audio.type");
            if (audioformat.equalsIgnoreCase("mp3")) {
               if (totalMillis > 0) {
                  millis = (long) (totalMillis * progress);
               } else {
                  millis = -1;
               }
            } else if (audioformat.equalsIgnoreCase("wave")) {
               millis = (long) (totalMillis * progress);
            } else {
               millis = Math.round(microseconds / 1000);
            }
         } else {
            millis = Math.round(microseconds / 1000);
         }
         if (millis < 0) {
            millis = Math.round(microseconds / 1000);
         }
      }
      if (totalMillis != 0) {
         return millis * 100f / totalMillis;
      } else {
         return 0;
      }
   }
}
