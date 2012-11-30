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

import net.sf.zekr.engine.common.LocalizedResource;

public class CandidateResource implements Resource {
   Class resourceType;
   LocalizedResource localizedResource;
   String id;
   File installationFile;
   Boolean isSharedInstallation;
   Resource installedResource;

   public CandidateResource(Class resourceType, File installationFile) {
      super();
      this.resourceType = resourceType;
      this.installationFile = installationFile;
   }

   public void setLocalizedResource(LocalizedResource localizedResource) {
      this.localizedResource = localizedResource;
   }

   public LocalizedResource getLocalizedResource() {
      return this.localizedResource;
   }

   public String getId() {
      return id;
   }

   public String getDescription() {
      return AddOnManagerUtils.getResourceDescription(this);
   }

   public void setType(Class c) {
      this.resourceType = c;
   }

   public Class getType() {
      return resourceType;
   }

   public boolean isCurrent() {
      return false;
   }

   public boolean isLoaded() {
      return AddOnManagerUtils.isLoaded(this);
   }

   public File getFile() {
      return installationFile;
   }

   public String getInstallationFolder() {
      return AddOnManagerUtils.getInstallationFolder(this);
   }

   public Boolean isShared() {
      return isSharedInstallation;
   }

   public void setIsShared(Boolean isShared) {
      isSharedInstallation = isShared;
   }

   public Resource getInstalledResource() {
      return installedResource;
   }

   public void setInstalledResource(Resource installedResource) {
      this.installedResource = installedResource;
   }
}
