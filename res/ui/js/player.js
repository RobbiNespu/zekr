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
	this.contSura = ($('#hiddenContinuousSuraPlay').val() == 'true'); // continuous sura playing
	this.contAya = ($('#hiddenContinuousAyaPlay').val() == 'true'); // continuous aya playing
	this.setup = function(playlist, volume, items, index, contSura, contAya) {
		this.playlist = playlist;
		this.volume = volume;
		this.items = items;
		this.index = index;
		if (contAya != undefined) this.contAya = contAya;
		if (contSura != undefined) this.contSura = contSura;
		var fo = { movie:'res/audio/mp3player.swf', width:'220', height:'135', majorversion:'7',
				build:'0', bgcolor:'#ffffff', id: 'quranPlayer', flashvars:'file=' + playlist + '&volume=' + volume +
				'&repeat=' + (this.contAya ? 'list' : 'false') +
			'&displayheight=20&height=130&enablejs=true&shuffle=false&javascriptid=quranPlayer' + 
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

var playerOnLoad = function(jq, contSura, contAya) {
	player = new Player()
	var playlist = $('#hiddenPlaylistUrl').val().trim();
	if (playlist != "") {
		var playlistItems = $('#hiddenPlaylistItemArray').val();
		player.setup(playlist, $('#hiddenVolume').val(), eval(playlistItems), 0, contSura, contAya);
//		if ($('#hiddenContinuousSuraPlay').val() == 'true') {
		setTimeout(function() { // make sure that flash object is created (bug fix for IE)
			ayaFocusHooks.push(function(o) {
				// o[0,1] are focused sura and aya num
				player.setVolume(o[2]);
				player.contSura = o[3];
				player.contAya = o[4];
				$('#hiddenContinuousSuraPlay').val(player.contSura)
				$('#hiddenContinuousAyaPlay').val(player.contAya)
				$('#hiddenVolum').val(player.volume)
/*
				if ($('#hiddenContinuousSuraPlay').val() != $('#contSuraButton').attr('__state'))
					toggleSuraButton();
				if ($('#hiddenContinuousAyaPlay').val() != $('#contAyaButton').attr('__state'))
					toggleAyaButton();
*/
//				player.goto(o.aya - 1);
//				if ($('#hiddenContinuousSuraPlay').val() != 'true')
//					player.stop();
			});
		}, 100);
//		}
	}
}

$(playerOnLoad);

// call back function to be called from action script. The name should be exactly 'getUpdate'.
function getUpdate(tp, p1, p2, pid) {
	log(tp + ' - ' + p1 + ' - ' + p2);
	if (player.locked) return;
	if (tp == "load") {
		player.load = p1;
	} if (tp == "time") {
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
			if (player.load == 0) {
				// kill flash player, to prevent 100% CPU usage!
				player.locked = true;
				stopPlayer();
				playerOnLoad();
			} else {
//				gotoAya($('#hiddenSuraNum').val() + '-' + (parseInt(currItem) + 1));
			}
		}
//		if ((player.state == 0 || player.state == 3) && player.index + 1 >= $('#hiddenAyaCount').val()) { // end of list
		if (player.state == 3 && player.index + 1 >= $('#hiddenAyaCount').val()) { // end of list
			var s;
			if (!player.contSura) {
				player.stop();
				s = $('#hiddenSuraNum').val();
				alert(s);
			} else {
				// kill flash player, and goto the next sura (if any)
				player.locked = true;
				playerOnLoad();
				s = (parseInt($('#hiddenSuraNum').val()) + 1);
			}
			gotoSuraAya(s + '-' + 1);
		}
	} else if(tp == "item") {
		// player.index = p1;
		if (player.index + 1 < $('#hiddenAyaCount').val()) { // end of list
			// kill flash player, and goto next sura (if needed)
			// sendEvent('stop');
		}
	/*
		if (fnf) setTimeout(sendEvent('stop'), 10);
		if (currItem + 1 < $('#hiddenAyaCount').val() && !fnf) { // end of list
			gotoAya($('#hiddenSuraNum').val() + '-' + (p1 + 1));
		}
	*/
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
	var i = $('#hiddenAyaNum').val() - 1
	if (player.index != i)
		player.goto(i);
	else
		player.playPause();
}
function togglePlayPause() { swtTogglePlayPause(); setMessage('ZEKR::PLAYER_PLAYPAUSE'); }

function swtStopPlayer() {
	if (player.playing) _toggleButton($('#playButton'));
	player.stop();
}
function stopPlayer() { swtStopPlayer(); setMessage('ZEKR::PLAYER_STOP'); }

function toggleAyaButton() {
	$('#contSuraButton').attr('disabled', player.contAya);
	if (player.contAya) {
		if (player.contSura) toggleSuraButton();
	}
	_toggleButton($('#contAyaButton'));
	player.contAya = !player.contAya;
	$('#hiddenContinuousAyaPlay', player.contAya);
	if (player.playing) togglePlayPause();

	setMessage('ZEKR::PLAYER_CONT ' + player.contSura + '-' + player.contAya + ';');
	playerOnLoad(null, player.contSura, player.contAya);
}

function toggleSuraButton() {
	player.contSura = !player.contSura;
	$('#hiddenContinuousSuraPlay', player.contSura);

	_toggleButton($('#contSuraButton'));
	setMessage('ZEKR::PLAYER_CONT ' + player.contSura + '-' + player.contAya + ';');
//	playerOnLoad();
}

