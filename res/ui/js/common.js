/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * @author Mohsen Saboorian
 */

String.prototype.trim = function() {
    return this.replace(/(\s+$)|(^\s+)/g, '');
};

var ayaFocusHooks = [];

// Browser <-> SWT communication
function gotoSuraAya(suraAya) { setMessage('ZEKR::GOTO ' + suraAya + ';'); } // use it when sura changed
function gotoAya(suraAya) { setMessage('ZEKR::NAVTO ' + suraAya + ';'); } // only use it when sura is not changed
function translate(location) { setMessage('ZEKR::TRANS ' + location + ';'); }
function setMessage(msg) { window.status = msg; }

function error(e) {
	alert("An unexpected error occurred:\n" + "[" + e.name + ":" + e.number + "] " + e.message + "\n");
}
