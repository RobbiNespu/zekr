/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * @author Mohsen Saboorian
 */
var _zekr_log_enabled_ = false;

function getPlayer(id) {
	if($.browser.msie) return window[id || 'quranPlayer'];
	return document[id || 'quranPlayer'];
}

function sendEvent(typ, p) {
	var player = getPlayer();
	if (player && player.sendEvent) player.sendEvent(typ, p);
}

function log(msg) {
	if (_zekr_log_enabled_)
		$('#messageArea').val($('#messageArea').val() + '\r\n' + msg);
}

Player = function() {
	this.ready = false;
	this.client;
	this.items = [];
	this.index = 0;
	this.locked = false;
	this.playing = false;
	this.state = 0;
	this.load = 0;
	this.volume = $('#hiddenVolume').val();
	this.playlist = '';
	this.elapsed = 0;
	this.repeatTime = $('#hiddenRepeatTime').val();
	this.repeatElapsed = 0;
	this.contAya = ($('#hiddenContinuousAyaPlay').val() == 'true'); // continuous aya playing
	this.butPressed = false; // is true only when html play/pause button is pressed (to distinct it from JWPlayer button)
	this.setup = function(playlist, volume, items, index, contAya) {
		this.playlist = playlist;
		this.volume = volume;
		this.items = items;
		this.index = index;
		if (contAya != undefined) this.contAya = contAya;
		var fo = {
			movie:'res/audio/player.swf', width:'220', height:'20',
			menu: 'false', allowscriptaccess: 'always',
			id: 'quranPlayer', name: 'quranPlayer', bgcolor: '#ffffff',
			majorversion: '8', build: '0',
			flashvars: 'file=' + playlist + '&volume=' + volume +
				'&repeat=' + (this.contAya ? 'list' : 'false') +
				'&height=20&enablejs=true&javascriptid=quranPlayer' +
				'&width=220&height=20' +
				'&backcolor=0xeee0e0&frontcolor=0x0011cc' + 
				'&shuffle=false&showdigits=true&autostart=false' };
		UFO.create(fo, 'reciterBar');
	}

	this.setContMode = function(continuous) {
		this.contAya = continuous;
	}

	this.setVolume = function(v) { sendEvent('volume', v); this.volume = v; }
	this.stop = function() { sendEvent('stop'); this.playing = false; }
	this.playPause = function() { sendEvent('playpause'); this.playing = !this.playing; }
	this.next = function() { sendEvent('next'); this.playing = true; }
	this.prev = function() { sendEvent('prev'); this.playing = true; }
	this.goto = function(index) {
		this.index = index;
		sendEvent('playitem', this.index);
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
					// player.goto(eval($('#hiddenSpecialItemArray').val())[0]);
				} else if (obj.autoPlay) {
					// player.goto(eval($('#hiddenSpecialItemArray').val())[1]);
				}
			});
		}, 100);
	}
}

$(function() {
	setTimeout(playerOnLoad, 100);
	$('#repeatAya').bind('change', function(e) {
		player.repeatTime = parseInt($(this).find(':selected').text().trim());
		setMessage('ZEKR::PLAYER_REPEAT ' + player.repeatTime + ';');
	});
});

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
		if (p1 == 3) {
			if (!player.contAya) stopPlayer();
			/*if (player.load == 0) {
				// kill flash player, to prevent 100% CPU usage!
				player.locked = true;
				stopPlayer();
				playerOnLoad();
			}*/
		}
		if (player.state == 3 && player.index + 1 == $('#hiddenAyaCount').val()) { // end of list
			player.locked = true;
			player.stop();
			togglePlayPause($('#playButton'));
			playerOnLoad();
		} else if (player.state == 3 && player.index + 1 > $('#hiddenAyaCount').val()) { // playing a special item
			s = (parseInt($('#hiddenSuraNum').val()) - 1);
			a = eval($('#hiddenSpecialItemArray').val());
			if (s == 0 && player.index == a[0]) // first time, goto bismillah
				player.goto(a[1]);
			else
				player.goto(s);
		}
	} else if (tp == "item") {
		if (player.elapsed != 0) {
			var suraAya = $('#hiddenSuraNum').val() + '-' + $('#hiddenAyaNum').val();
			var i = $(player.items).index(suraAya);
			var ayaCount = $('#hiddenAyaCount').val();
			if (i < ayaCount && i + 1 != p1) { player.goto(i); p1 = i; }
			// else { stopPlayer(); return; }
			player.index = p1;
			gotoSuraAya(player.items[p1])
		}
	}
};

function _toggleButton(but) {
	var b = $(but);
	b.attr('zekrstate', !b.attr('zekrstate'));
	var img = b.find('img');
	var i = img.attr('zekricon');
	var t = b.attr('zekrtitle');
	img.attr('zekricon', img.attr('src'));
	b.attr('zekrtitle', b.attr('title'));
	img.attr('src', i);
	b.attr('title', t);
}

function swtTogglePlayPause() {
	_toggleButton($('#playButton'));
	var i = $(player.items).index($('#hiddenSuraNum').val() + 
			'-' + $('#hiddenAyaNum').val());
	if (player.index != i && !player.playing) {
		player.goto(i);
	} else {
		player.playPause();
	}
}
function togglePlayPause() {
	player.butPressed = true;
	setMessage('ZEKR::PLAYER_PLAYPAUSE;');
	swtTogglePlayPause();
}

function swtStopPlayer() {
	player.stop();
}
function stopPlayer() {
	player.butPressed = true;
	if (player.playing)
		swtTogglePlayPause();
	setMessage('ZEKR::PLAYER_STOP');
	swtStopPlayer();
}

function toggleAyaButton() {
	_toggleButton($('#contAyaButton'));
	player.setContMode(!player.contAya);
	$('#hiddenContinuousAyaPlay', player.contAya);
	if (player.playing) togglePlayPause();
	setMessage('ZEKR::PLAYER_CONT ' + player.contAya + ';');
	playerOnLoad(null, player.contAya);
}
