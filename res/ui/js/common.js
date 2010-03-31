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
function gotoSuraAya(sura, aya, page) { // use it when sura changed
    if (javaFunction) {
        javaFunction('ZEKR::GOTO', sura, aya, page);
    }
}
function redirect(sura, aya) { // used for search results
    if (javaFunction) {
        javaFunction('ZEKR::REDIRECT', sura, aya);
    }
}
function translate(sura, aya) {
    if (javaFunction) {
        javaFunction('ZEKR::TRANS', sura, aya);
    }
}
function play(loc, isPlay) {
    if (javaFunction) {
        javaFunction('ZEKR::PLAY', loc, isPlay);
    }
}

function zoom(z) {
    if (javaFunction) {
        javaFunction('ZEKR::ZOOM', z);
    }
}

function error(e) {
	alert("An unexpected error occurred:\n" + "[" + e.name + ":" + e.number + "] " + e.message + "\n");
}
