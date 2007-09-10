/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * @author Mohsen Saboorian
 */

function lookupMovie(movie) {
	if($.browser.msie) return window[movie];
	return document[movie];
}

function sendEvent(typ, p) { lookupMovie("quranPlayer").sendEvent(typ, p); }

function log(msg) {
	$('#messageArea').val($('#messageArea').val() + '\r\n' + msg);
}

Player = function() {
	this.items = [];
	this.index = 0;
	this.locked = false;
	this.playing = false;
	this.state = 0;
	this.load = 0;
	this.volume = $('#hiddenVolume').val();
	this.playlist = '';
	this.elapsed = 0;
	this.contAya = ($('#hiddenContinuousAyaPlay').val() == 'true'); // continuous aya playing
	this.butPressed = false; // is true only when html play/pause button is pressed (to distinct it from JWPlayer button)
	this.setup = function(playlist, volume, items, index, contAya) {
		this.playlist = playlist;
		this.volume = volume;
		this.items = items;
		this.index = index;
		if (contAya != undefined) this.contAya = contAya;
		var fo = { movie:'res/audio/player.swf', width:'220', height:'20', majorversion:'7',
				build:'0', bgcolor:'#ffffff', id: 'quranPlayer', flashvars:'file=' + playlist + '&volume=' + volume +
				'&repeat=' + (this.contAya ? 'list' : 'false') +
			'&height=20&enablejs=true&shuffle=false&javascriptid=quranPlayer' + 
			'&allowscriptaccess=always' +
			'&backcolor=0xeee0e0&frontcolor=0x0011cc' + 
			'&showdigits=true&autostart=false' };
		UFO.create(fo, 'reciterBar');
	}
	
	this.setVolume = function(v) { sendEvent('volume', v); this.volume = v; }
	this.stop = function() { sendEvent('stop'); this.playing = false; }
	this.playPause = function() { sendEvent('playpause'); this.playing = !this.playing; }
	this.next = function() { sendEvent('next'); this.playing = true; }
	this.prev = function() { sendEvent('prev'); this.playing = true; }
	this.goto = function(index) {
		this.index = index;
		sendEvent('playitem', this.items[this.index]);
		this.playing = true;
	}
};

var player;
var playerOnLoad = function(jq, contAya) {
	player = new Player();
	var playlist = $('#hiddenPlaylistUrl').val().trim();
	if (playlist != "") {
		var playlistItems = $('#hiddenPlaylistItemArray').val();
		player.setup(playlist, $('#hiddenVolume').val(), eval(playlistItems), 0, contAya);
		setTimeout(function() { // make sure that flash object is created (bug fix for IE)
			ayaFocusHooks = [];
			ayaFocusHooks.push(function(o) {
				// o[0,1] are focused sura and aya num
				obj = o[2]
				player.setVolume(obj.volume);
				player.contAya = obj.contAya;
				$('#hiddenContinuousAyaPlay').val(player.contAya)
				$('#hiddenVolum').val(player.volume)
				if (obj.firstTime) {
					player.goto(eval($('#hiddenSpecialItemArray').val())[0]);
				} else if (obj.autoPlay) {
					player.goto(eval($('#hiddenSpecialItemArray').val())[1]);
				}
			});
		}, 200);
	}
}

$(document).ready(playerOnLoad);

// call back function to be called from action script. The name should be exactly 'getUpdate'.
function getUpdate(tp, p1, p2, pid) {
	log(tp + ' - ' + p1 + ' - ' + p2);
	if (player.locked) return;
	if (tp == "load") {
		player.load = p1;
	} if (tp == "time") {
		player.butPressed = false; // reset state
		player.elapsed = p1;
		// p2 is remaining, p1 is elapsed
	} else if(tp == "volume") {
		player.volume = p1;
		setMessage('ZEKR::PLAYER_VOLUME ' + p1 + ';');
	} else if(tp == "state") {
		// 0=pause, 1=buffering, 2=playing, 3=completed
		player.state = p1;
		// if (p1 == 2 || p1 == 1) player.playing = true;
		if (p1 == 0) {
			//if (player.playing && !player.butPressed) { _toggleButton($('#playButton')); player.playing = false; player.butPressed = false; }
		} else if (p1 == 1 || p1 == 3) {
			//if (player.playing && !player.butPressed) { _toggleButton($('#playButton')); player.butPressed = false; }
		} else if (p1 == 3) {
			if (!player.contAya) stopPlayer();
			if (player.load == 0) {
				// kill flash player, to prevent 100% CPU usage!
				player.locked = true;
				stopPlayer();
				playerOnLoad();
			}
		}
		if (player.state == 3 && player.index + 1 == $('#hiddenAyaCount').val()) { // end of list
			var s;
			player.stop();
			var s = $('#hiddenSuraNum').val();
			gotoSuraAya(s + '-' + 1);
		} else if (player.state == 3 && player.index + 1 > $('#hiddenAyaCount').val()) { // playing a special item
			s = (parseInt($('#hiddenSuraNum').val()) - 1);
			a = eval($('#hiddenSpecialItemArray').val());
			if (s == 0 && player.index == a[0]) // first time, goto bismillah
				player.goto(a[1]);
			else
				player.goto(s);
		}
	} else if(tp == "item") {
		if (player.elapsed != 0) {
			gotoAya($('#hiddenSuraNum').val() + '-' + (parseInt(p1) + 1));
			player.index = p1;
		}
	}
};

function _toggleButton(but) {
	var b = $(but);
	b.attr('__state', !b.attr('__state'));
	var img = b.find('img');
	var i = img.attr('__icon');
	var t = b.attr('__title');
	img.attr('__icon', img.attr('src'));
	b.attr('__title', b.attr('title'));
	img.attr('src', i);
	b.attr('title', t);
}

function swtTogglePlayPause() {
	_toggleButton($('#playButton'));
	var i = $('#hiddenAyaNum').val() - 1;
	if (player.index != i && !player.playing) {
		player.goto(i);
	} else {
		player.playPause();
	}
}
function togglePlayPause() { player.butPressed = true; swtTogglePlayPause(); setMessage('ZEKR::PLAYER_PLAYPAUSE'); }

function swtStopPlayer() {
	// if (player.playing) _toggleButton($('#playButton'));
	player.stop();
}
function stopPlayer() {
	player.butPressed = true;
	if (player.playing)
//		_toggleButton($('#playButton'));
		swtTogglePlayPause();
	swtStopPlayer();
	setMessage('ZEKR::PLAYER_STOP');
}

function toggleAyaButton() {
	_toggleButton($('#contAyaButton'));
	player.contAya = !player.contAya;
	$('#hiddenContinuousAyaPlay', player.contAya);
	if (player.playing) togglePlayPause();

	setMessage('ZEKR::PLAYER_CONT ' + player.contAya + ';');
	playerOnLoad(null, player.contAya);
}
