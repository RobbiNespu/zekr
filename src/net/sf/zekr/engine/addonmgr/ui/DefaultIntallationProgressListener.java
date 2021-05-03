/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 13, 2010
 */
package net.sf.zekr.engine.addonmgr.ui;

import net.sf.zekr.common.util.IntallationProgressListener;
import net.sf.zekr.engine.addonmgr.Resource;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;

import org.eclipse.swt.widgets.Display;

/**
 * @author Mohsen Saboorian
 */
public class DefaultIntallationProgressListener implements IntallationProgressListener{
	
	
	long totalSize = 0;
	long sizeToNow = 0;
	Display display;
	private static final Logger logger = Logger.getLogger(DefaultIntallationProgressListener.class);
	
	public DefaultIntallationProgressListener(Display display){
		this.display=display;
	}
	
	public void start(long totalSize){
		this.totalSize = totalSize;
	}


	public boolean progress(long itemSize) {
		sizeToNow += itemSize;
		final int p = Math.min((int) (100.0 * sizeToNow / totalSize), 99);

		display.asyncExec(new Runnable() {
			public void run() {
				EventUtils.sendEvent(EventProtocol.IMPORT_PROGRESS, p);
			}
		});
		return true;
	}

	public void finish(Resource installedResource) {
	}
}
