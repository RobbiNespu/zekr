/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * @author: Mohsen Saboorian <mohsen@zekr.org>
 */

function lookupMovie(movie) {
    if($.browser.msie) {
		return window[movie];
	} else {
		return document[movie];
	}
};

function sendEvent(typ, p) { lookupMovie("quranPlayer").sendEvent(typ, p); };

// call back function to be called from action script. The name should be exactly 'getUpdate'.
function getUpdate(tp, p1, p2, pid) {
	if (tp == "time") {
		// p1 is time elapese, p2 is time remained
	} else if(tp == "volume") {
	} else if(tp == "item") {
	}
};

function nextAudio() {
}

Recitation = function() {
	this.playFile = function(playlist, volume) {
		var fo = { movie:'res/audio/mp3player.swf', width:'220', height:'140', majorversion:'7',
				build:'0', bgcolor:'#ffffff', id: 'quranPlayer', flashvars:'file=' + playlist + '&volume=' + volume +
			'&displayheight=20&height=90&enablejs=true&shuffle=false&javascriptid=quranPlayer' + 
			'&backcolor=0xeee0e0&frontcolor=0x0011cc&id=quranPlayer&repeat=list&showdigits=true&autostart=false' };

			//allowscriptaccess=always&
		UFO.create(fo, 'reciterBar');
	};
};