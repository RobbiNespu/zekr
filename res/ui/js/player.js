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

function sendEvent(typ, p) { lookupMovie("myid").sendEvent(typ, p); };

// call back function to be called from action script. The name should be exactly 'getUpdate'.
function getUpdate(tp, p1, p2, pid) {
	if (tp == "time") {  }
	else if(tp == "volume") { alert("volume: " + p1); }
	else if(tp == "item") {  }
};

Recitation = function() {
	this.playFile = function(fileName, volume) {
		volume = volume != null ? (volume + 1) * 10 : 30;
	
		fileName = 'res/ui/player/sample.mp3';

		var fo = { movie:'res/ui/player/mp3player.swf', width:'220', height:'20', majorversion:'7',
				build:'0', bgcolor:'#ffffff', id: 'myid', flashvars:'file=' + fileName + '&volume=' + volume +
			'&enablejs=true&javascriptid=myid&id=myid&backcolor=0xeee0e0&frontcolor=0x0011cc&height=20&repeat=true&showdigits=true&autostart=true' };
		UFO.create(fo, 'reciterBar');
	};
	
	
	// stop sound 
	this.stopSound = function() {
		azanMisc.playFile('', 0);
	}
};