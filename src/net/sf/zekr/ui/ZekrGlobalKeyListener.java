/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 6, 2009
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.config.GlobalConfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

class ZekrGlobalKeyListener implements Listener {
	private QuranForm quranForm;

	ZekrGlobalKeyListener(QuranForm quranForm) {
		this.quranForm = quranForm;
	}

	public void handleEvent(Event event) {
		boolean mac = GlobalConfig.isMac;
		if ((!mac && event.stateMask == SWT.CTRL) || (mac && event.stateMask == SWT.COMMAND)) {
			if (event.keyCode == 'f') { // find
				this.quranForm.focusOnSearchBox();
			} else if (event.keyCode == 'd') { // bookmark
				this.quranForm.quranFormController.bookmarkThis();
			} else if (event.keyCode == 'q') { // quit
				this.quranForm.quit();
			}
		} else if (event.stateMask == SWT.ALT) {
		} else if ((event.keyCode & SWT.KEYCODE_BIT) != 0) {
			if (event.keyCode == SWT.F1) {
			} else if (event.keyCode == SWT.F4) {
				boolean state = !this.quranForm.playerUiController.isAudioControllerFormOpen();
				this.quranForm.qmf.toggleAudioPanelState(state);
				this.quranForm.playerUiController.toggleAudioControllerForm(state);
			}
		}
	}
}
