/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     25/07/2010
 */
package net.sf.zekr.engine.addonmgr;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

import net.sf.zekr.common.ZekrMessageException;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.IntallationProgressListener;
import net.sf.zekr.engine.audio.AudioData;
import net.sf.zekr.engine.common.LocalizedResource;
import net.sf.zekr.engine.translation.TranslationData;

import org.apache.commons.configuration.ConfigurationException;

public class AddOnManagerUtils {

   public static Resource install(CandidateResource r, IntallationProgressListener progressListener)
         throws ZekrMessageException {
      if (!isLoaded(r)) {
         return ApplicationConfig.getInstance().installResource(r, progressListener);
      } else
         throw new ZekrMessageException("Resource Already installed!" + r.getDescription());
   }

   public static void unistall(Resource r, IntallationProgressListener progressListener) throws ZekrMessageException {
      if (isCurrent(r))
         throw new ZekrMessageException("Resources is currenly in use cannot be removed," + r.getDescription());
      else if (isLoaded(r)) {
         ApplicationConfig.getInstance().unistallResource(r, progressListener);
      } else
         throw new ZekrMessageException("Resource Already un-installed!" + r.getDescription());
   }

   /*public static boolean isInstalled(Resource r){
   	return ApplicationConfig.getInstance().isCurrentlyInstalled(r);	
   }*/

   public static boolean isCurrent(Resource r) {
      return getResourceManager(r).getCurrentResource().equals(r);
   }

   public static void load(Resource r) throws ConfigurationException, IOException {
      ResourceManager resourceMgr = getResourceManager(r);
      if (!resourceMgr.getLoadedResources().contains(r))
         resourceMgr.loadResource(r);
   }

   public static void unload(Resource r) {
      ResourceManager resourceMgr = getResourceManager(r);
      if (resourceMgr.getLoadedResources().contains(r) && !isCurrent(r))
         resourceMgr.unloadResource(r);
   }

   public static String getResourceDescription(Resource r) {
      LocalizedResource localizedResource = r.getLocalizedResource();
      if (localizedResource != null) {
         String localizedName = localizedResource.getLocalizedName();
         String name = localizedResource.getName();
         String language = localizedResource.getLanguage();
         String id = r.getId();
         return id + " " + (localizedName) + " " + (localizedName.equals(name) ? "" : "(" + name + ") ") + language;
      } else
         return r.getId();
   }

   public static void setCurrent(Resource r) {
      ResourceManager resourceMgr = getResourceManager(r);
      resourceMgr.setCurrentResource(r);
   }

   public static boolean isLoaded(Resource r) {
      return getResourceManager(r).getLoadedResources().contains(r);
   }

   public static Resource getCurrentResource(Class resourceType) {
      return getResourceManager(resourceType).getCurrentResource();
   }

   public static List<Resource> getLoadedResources(Class resourceType) {
      return getResourceManager(resourceType).getLoadedResources();
   }

   public static CandidateResource getNewCandidateResource(Class resourceType, File file) {
      return getResourceManager(resourceType).getNewCandidateResource(file);
   }

   private static ResourceManager getResourceManager(Resource r) {
      return getResourceManager(r.getType());
   }

   @SuppressWarnings("rawtypes")
   private static ResourceManager getResourceManager(Class resourceType) {
      if (AudioData.class.equals(resourceType))
         return ApplicationConfig.getInstance().getAudio();
      else if (TranslationData.class.equals(resourceType))
         return ApplicationConfig.getInstance().getTranslation();
      else
         throw new InvalidParameterException("ResourceType not handled");
   }

   public static String getInstallationFolder(Resource resource) {
      if (resource.isShared() == null)
         throw new RuntimeException("Unable to determine if this" + " resouce is Shared or not");
      if (resource.isShared())
         return getSharedInstallationFolder(resource);
      else
         return getUserIntallationFolder(resource);
   }

   private static String getUserIntallationFolder(Resource resource) {
      if (AudioData.class.equals(resource.getType()))
         return ApplicationPath.AUDIO_DIR;
      else if (TranslationData.class.equals(resource.getType()))
         return ApplicationPath.TRANSLATION_DIR;
      else
         throw new InvalidParameterException("ResourceType not handled");
   }

   private static String getSharedInstallationFolder(Resource resource) {
      if (AudioData.class.equals(resource.getType()))
         return Naming.getAudioDir();
      else if (TranslationData.class.equals(resource.getType()))
         return Naming.getTransDir();
      else
         throw new InvalidParameterException("ResourceType not handled");
   }

   /**
    * Is the resource is installed in the shared directory
    * 
    * @param resource
    * @return true if installed in the shared directory
    */
   public static Boolean isResourceShared(Resource resource) {
      if (resource instanceof CandidateResource)
         throw new InvalidParameterException("CandidateResource is not a valid resource for this method");
      else {
         String installationPath = resource.getFile().getAbsolutePath();
         return installationPath.contains(getSharedInstallationFolder(resource));
      }
   }

   @SuppressWarnings("rawtypes")
   public static String getValidInstallationFileExtensions(Class resourceType) {
      if (AudioData.class.equals(resourceType))
         return "*-online.properties;*.recit.zip";
      else if (TranslationData.class.equals(resourceType))
         return "*.trans.zip";
      else
         throw new InvalidParameterException("ResourceType not handled");
   }
}
