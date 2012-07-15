/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2009
 */
package net.sf.zekr.engine.audio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.log.Logger;

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
   private static Logger logger = Logger.getLogger(DefaultPlayerController.class);

   private ZekrBasicPlayer player;
   private int volume;
   private boolean multiAya;
   private int interval; // wait between two ayas (in milliseconds)
   private int repeatTime;
   private PropertiesConfiguration props;
   private PlayingItem playingItem;
   private String playScope;

   private PlayableObject currentPlayableObject;
   private int currentAudioIndex;

   @SuppressWarnings("rawtypes")
   private Map currentAudioInfo;
   private List<BasicPlayerListener> playerListenerList = new ArrayList<BasicPlayerListener>();

   /**
    * A fixed size cache for {@link ZekrBasicPlayer} objects whose <code>open</code> method is already called.
    */
   private Map<PlayableObject, ZekrBasicPlayer> playerCache;

   public DefaultPlayerController(PropertiesConfiguration props) {
      this.props = props;
      volume = props.getInt("audio.volume", 50);
      repeatTime = props.getInt("audio.repeatTime", 1);
      interval = props.getInt("audio.interval", 0);
      multiAya = props.getBoolean("audio.continuousAya", true);
      playScope = props.getString("audio.playScope", "continuous");

      final int prefetcher = props.getInt("audio.cache.prefetcher", 1);
      playerCache = new LinkedHashMap<PlayableObject, ZekrBasicPlayer>() {
         private static final long serialVersionUID = -9223127035368415046L;

         @Override
         protected boolean removeEldestEntry(java.util.Map.Entry<PlayableObject, ZekrBasicPlayer> eldest) {
            return size() > prefetcher;
         }
      };
   }

   @SuppressWarnings("unchecked")
   public void open(PlayableObject playableObject, boolean openForCaching) throws PlayerException {
      // check if exists in the cache
      ZekrBasicPlayer localPlayer = playerCache.get(playableObject);

      if (openForCaching) {
         if (localPlayer == null) {
            localPlayer = new ZekrBasicPlayer();
            localPlayer.addBasicPlayerListener(new BasicPlayerAdapter(localPlayer) {
               @SuppressWarnings("rawtypes")
               @Override
               public void opened(Object stream, Map properties) {
                  zekrBasicPlayer.setAudioInfo(properties);
                  // trying to remove this listener from here causes ConcurrentModificationException
               }
            });
            playerCache.put(playableObject, localPlayer);
         } else {
            logger.debug("Already exists in cache: " + playableObject);
            return; // already exists in the cache
         }
      } else {
         this.currentPlayableObject = playableObject;
         boolean isAlreadyOpened = true;
         if (localPlayer == null) {
            isAlreadyOpened = false;
            localPlayer = new ZekrBasicPlayer();
            localPlayer.addBasicPlayerListener(new BasicPlayerAdapter(localPlayer) {
               @SuppressWarnings("rawtypes")
               @Override
               public void opened(Object stream, Map properties) {
                  currentAudioInfo = properties;
                  // trying to remove this listener from here causes ConcurrentModificationException
               }
            });
         } else {
            currentAudioInfo = localPlayer.getAudioInfo();
            localPlayer.getListeners().clear();
         }
         this.player = localPlayer;
         player.getListeners().addAll(playerListenerList);
         if (isAlreadyOpened) {
            return;
         }
      }

      try {
         if (playableObject.getUrl() != null) {
            localPlayer.open(playableObject.getUrl());
         } else if (playableObject.getFile() != null) {
            if (!playableObject.getFile().exists()) {
               throw new PlayerException("File not found: " + playableObject.getFile());
            }
            localPlayer.open(playableObject.getFile());
         } else {
            localPlayer.open(playableObject.getInputStream());
         }
      } catch (BasicPlayerException e) {
         if (openForCaching) {
            logger.error("Error occurred while openning playable object for cache.", e);
         } else {
            throw new PlayerException("Error opening playable object: " + playableObject, e);
         }
      }
   }

   public void open(PlayableObject playableObject) throws PlayerException {
      open(playableObject, false);
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
         if (player != null) {
            player.stop();
         }
      } catch (BasicPlayerException e) {
         throw new PlayerException(e);
      }
   }

   public int getStatus() {
      if (player == null) {
         return ZekrBasicPlayer.UNKNOWN;
      }
      return player.getStatus();
   }

   public void addBasicPlayerListener(BasicPlayerListener bpl) {
      playerListenerList.add(bpl);
      // player.addBasicPlayerListener(bpl);
   }

   public Collection<BasicPlayerListener> getListeners() {
      return playerListenerList;
   }

   public void removeBasicPlayerListener(BasicPlayerListener bpl) {
      playerListenerList.remove(bpl);
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
      return !"aya".equals(playScope);
      // return multiAya;
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

   public String getPlayScope() {
      return playScope;
   }

   public void setPlayScope(String playScope) {
      this.playScope = playScope;
      props.setProperty("audio.playScope", playScope);
   }

   @SuppressWarnings({ "rawtypes" })
   public Map getCurrentAudioInfo() {
      return currentAudioInfo;
   }

   @Override
   public int getCurrentAudioIndex() {
      return currentAudioIndex;
   }

   @Override
   public void setCurrentAudioIndex(int currentAudioIndex) {
      this.currentAudioIndex = currentAudioIndex;
   }

   @Override
   public int getRecitationCount() {
      return ApplicationConfig.getInstance().getAudio().getCurrentList().size();
   }

   @Override
   public boolean isLastRecitation() {
      return getCurrentAudioIndex() + 1 >= getRecitationCount();
   }

   @Override
   public boolean gotoNextAudio() {
      if (getCurrentAudioIndex() + 1 >= getRecitationCount()) {
         // reset
         currentAudioIndex = 0;
         return true;
      } else {
         currentAudioIndex++;
         return false;
      }
   }
}
