/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * @author: Mohsen Saboorian <mohsen@zekr.org>
 */

Recitation = function() {
	this.playFile = function(fileName, volume) {
	//	var doc = document.getElementById('soundFrame').contentWindow;
		volume = volume != null ? (volume + 1) * 10 : 30;
	
		fileName = 'res/ui/player/sample.mp3';
	
		var FO = { movie:'res/ui/player/mp3player.swf', width:'50%', height:'20', majorversion:'7', build:'0', bgcolor:'#ffffff',
			flashvars:'file='+ fileName+ '&autostart=true&volume='+ volume };
	//	doc.UFO.create(FO, 'reciterBar');
		UFO.create(FO, 'reciterBar');
	};
	
	
	// stop sound 
	this.stopSound = function() {
		azanMisc.playFile('', 0);
	}
};