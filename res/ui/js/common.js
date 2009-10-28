/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * @author Mohsen Saboorian
 */

String.prototype.trim = function() {
    return this.replace(/([\s\xA0]+$)|(^[\s\xA0]+)/g, '');
};

var ayaFocusHooks = [];

// Browser <-> SWT communication
function gotoSuraAya(sura, aya, page) { javaFunction('ZEKR::GOTO', sura, aya, page); } // use it when sura changed
function redirect(sura, aya) { javaFunction('ZEKR::REDIRECT', sura, aya); } // used for search results
function translate(sura, aya) { javaFunction('ZEKR::TRANS', sura, aya); }
// function setMessage(msg) { window.title = msg; }

function error(e) {
	alert("An unexpected error occurred:\n" + "[" + e.name + ":" + e.number + "] " + e.message + "\n");
}
