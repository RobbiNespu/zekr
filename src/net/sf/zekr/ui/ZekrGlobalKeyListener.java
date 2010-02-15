/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 6, 2009
 */
package net.sf.zekr.ui;

import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.KeyboardAction;
import net.sf.zekr.common.config.KeyboardShortcut;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

class ZekrGlobalKeyListener implements Listener {
	private QuranForm quranForm;
	ApplicationConfig config = ApplicationConfig.getInstance();
	private static final int MODALITY = SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;

	ZekrGlobalKeyListener(QuranForm quranForm) {
		this.quranForm = quranForm;
	}

	public void handleEvent(Event event) {
		//		boolean mac = GlobalConfig.isMac;
		//		if ((!mac && event.stateMask == SWT.CTRL) || (mac && event.stateMask == SWT.COMMAND)) {
		//			if (event.keyCode == 'f') { // find
		//				this.quranForm.focusOnSearchBox();
		//			} else if (event.keyCode == 'd') { // bookmark
		//				this.quranForm.quranFormController.bookmarkThis();
		//			} else if (event.keyCode == 'q') { // quit
		//				this.quranForm.quit();
		//			}
		//		} else if (event.stateMask == SWT.ALT) {
		//		} else if ((event.keyCode & SWT.KEYCODE_BIT) != 0) {
		//			if (event.keyCode == SWT.F1) {
		//			} else if (event.keyCode == SWT.F4) {
		//				boolean state = !this.quranForm.playerUiController.isAudioControllerFormOpen();
		//				this.quranForm.qmf.toggleAudioPanelState(state);
		//				this.quranForm.playerUiController.toggleAudioControllerForm(state);
		//			}
		//		}

		int keyCode = extractSwtBitKeyCode(event);
		KeyboardShortcut shortcut = config.getShortcut();
		if (shortcut != null) {
			List<KeyboardAction> actionList = shortcut.getKeyActionList(keyCode);
			if (actionList != null) {
				String formId = FormUtils.getCurrentFormId();
				for (KeyboardAction keyboardAction : actionList) {
					// check modality
					Shell activeShell = quranForm.getDisplay().getActiveShell();
					if (activeShell != null) {
						if (keyboardAction.suppressOnModal && isModal(activeShell.getStyle())) {
							continue;
						}
					}

					if (StringUtils.isNotBlank(keyboardAction.window)) {
						String[] winList = keyboardAction.window.split(",");
						if (ArrayUtils.contains(winList, formId) || keyboardAction.global) {
							quranForm.quranFormController.executeAction(keyboardAction.action);
							break;
						}
					} else if (keyboardAction.global) {
						quranForm.quranFormController.executeAction(keyboardAction.action);
						break;
					} else if (StringUtils.equals(formId, ZekrForm.FORM_ID)) { // act only when QuranForm is active
						quranForm.quranFormController.executeAction(keyboardAction.action);
						break;
					}
				}
			}
		}
	}

	private boolean isModal(int style) {
		return (SWT.PRIMARY_MODAL & style) == SWT.PRIMARY_MODAL || (SWT.SYSTEM_MODAL & style) == SWT.SYSTEM_MODAL
				|| (SWT.APPLICATION_MODAL & style) == SWT.APPLICATION_MODAL;
	}

	private int extractSwtBitKeyCode(Event event) {
		int keyCode = 0;
		if ((event.stateMask & SWT.CTRL) == SWT.CTRL) {
			keyCode = SWT.CTRL;
		}
		if ((event.stateMask & SWT.COMMAND) == SWT.COMMAND) {
			keyCode = SWT.COMMAND;
		}
		if ((event.stateMask & SWT.ALT) == SWT.ALT) {
			keyCode |= SWT.ALT;
		}
		if ((event.stateMask & SWT.SHIFT) == SWT.SHIFT) {
			keyCode |= SWT.SHIFT;
		}
		if ((event.keyCode & SWT.KEYCODE_BIT) == 0) {
			keyCode |= Character.toUpperCase(event.keyCode);
		} else {
			keyCode |= event.keyCode;
		}
		if (keyCode == 0) {
			keyCode = event.character;
		}
		return keyCode;
	}
}
