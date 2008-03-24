/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * @author Mohsen Saboorian
 */

var oldAyaId = null;

$(document).ready(function() {
	// backspace: history.back()
	if (!$.browser.msie) {
		$(document).keyup(function(e) {
			if (e.keyCode == 8) {
				var inp = e.target;
				if ("INPUT" == inp.nodeName.toUpperCase() && inp.type 
					&& "TEXT" == inp.type.toUpperCase())
					return; // by-pass this event
				history.go(-1);
			}
		});
	}

	$(window).resize(function(e) {
//		refocus();
	});
	
	$("#suraNav").focus();
});

function refocus() {
	var suraNum = $("input#hiddenSuraNum").val();
	var ayaNum = $("input#hiddenAyaNum").val();

	var ayaId = suraNum + "_" + ayaNum;	
	var aya = document.getElementById(ayaId);
	if (!aya) return;

	if (ayaNum != 1)
		$(aya).ScrollTo(1, 'original', getBrowserHeight() > getObjectHeight(aya) ? getBrowserHeight()/5 : 0);
	else
		$(aya).ScrollTo(1, 'original', 200);

}

function navtoSuraAya() {
	var sura = $("input#suraNav").val();
	var aya = $("input#ayaNav").val();
	var origSuraNum = $("input#hiddenSuraNum").val();
	if (!isNaN(parseInt(sura.trim())) && !isNaN(parseInt(aya.trim())))
		if (origSuraNum != sura)
			gotoSuraAya(sura + "-" + aya);
		else
			gotoAya(sura + "-" + aya); // sura should also be passed (may user change the sura combo box)
}

function getObjectHeight(obj) { return obj.offsetHeight; }
function getBrowserHeight() { return document.body.clientHeight; }

function highlightAya(id) {
	$('#sign_' + id).addClass('selectedAyaSign');
	$('#' + id).addClass('selectedAya');
}

function unHighlightAya(id) {
    $('#sign_' + id).removeClass('selectedAyaSign');
    $('#' + id).removeClass('selectedAya');
}

function focusOnAya(suraNum, ayaNum) {
	var ayaId = suraNum + "_" + ayaNum;	
	var aya = document.getElementById(ayaId);
	if (!aya) return;

	$("input#suraNav").val(suraNum);
	$("input#ayaNav").val(ayaNum);
	$("input#hiddenSuraNum").val(suraNum);
	$("input#hiddenAyaNum").val(ayaNum);

	if (oldAyaId != null)
		unHighlightAya(oldAyaId);
	highlightAya(ayaId);

	var oh = getObjectHeight(aya);
	var bh = getBrowserHeight();
	if (ayaNum != 1)
		$(aya).ScrollTo(400, 'original', bh > (oh + bh/5) ? bh/5 : 45);
	else
		$(aya).ScrollTo(400, 'original', 200);
	oldAyaId = ayaId;

	// fire post-focus hooks
	var args = arguments;
	$(ayaFocusHooks).each(function() {
		this.call(this, args);
	});
}
