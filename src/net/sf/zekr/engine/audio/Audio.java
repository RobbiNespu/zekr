/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.addonmgr.CandidateResource;
import net.sf.zekr.engine.addonmgr.Resource;
import net.sf.zekr.engine.addonmgr.ResourceManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.ConfigurationException;

/**
 * @author Mohsen Saboorian
 */
public class Audio implements ResourceManager {
   private AudioData current;
   private List<AudioData> currentList = new ArrayList<AudioData>();

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

   public List<AudioData> getCurrentList() {
      return currentList;
   }

   public List<String> getCurrentIdList() {
      if (CollectionUtils.isEmpty(currentList)) {
         return Collections.emptyList();
      }
      List<String> ret = new ArrayList<String>();
      for (AudioData ad : currentList) {
         ret.add(ad.id);
      }
      return ret;

   }

   public void setCurrentList(List<AudioData> currentList) {
      this.currentList = currentList;
   }

   public AudioData getCurrent() {
      return current;
   }

   public AudioData getCurrent(int audioIndex) {
      return currentList.get(audioIndex);
   }

   public void loadResource(Resource r) throws ConfigurationException, IOException {
      //I think this method should be extracted out here.
      ApplicationConfig.getInstance().loadAudioData(r.getFile(), true);
      //here probably I should send an event to refresh the GUI.
   }

   public void unloadResource(Resource r) {
      if (!getCurrent().getId().equals(r.getId()))
         audioList.remove(r.getId());
      //here probably I should send an event to refresh the GUI.
   }

   public List<Resource> getLoadedResources() {
      List<Resource> resourceList = new ArrayList<Resource>();
      resourceList.addAll(getAllAudio());
      return resourceList;
   }

   public CandidateResource getNewCandidateResource(File file) {
      return new CandidateResource(AudioData.class, file);
   }

   public Resource getCurrentResource() {
      return getCurrent();
   }

   public void setCurrentResource(Resource r) {
      setCurrent((AudioData) r);
   }

}
