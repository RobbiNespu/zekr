/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * version 1
 */

var oldAyaId = null;

String.prototype.trim = function() {
    return this.replace(/(\s+$)|(^\s+)/g, '');
};

$(document).ready(function() {
	// todo backspace enable
	if (!$.browser.msie) {
		$(document).keyup(function(e) {
			if (e.keyCode == 9)
				history.go(-1);
		});
	}
	$("input#ayaNav").keyup(function(e) {
		if (e.keyCode == 13 || e.keyCode == 10)
			navtoSuraAya();
	});
});

navtoSuraAya = function(){
	var sura = $("input#suraNav").val();
	var aya = $("input#ayaNav").val();
	var origSuraNum = $("input#hiddenSuraNum").val();
	if (!isNaN(parseInt(sura.trim())) && !isNaN(parseInt(aya.trim())))
		if (origSuraNum != sura)
			gotoSuraAya(sura + "-" + aya);
		else
			gotoAya(aya);
};

function focusOnAya(suraNum, ayaNum) {
	var ayaId = suraNum + "_" + ayaNum;	
	var aya = document.getElementById(ayaId);
	if (!aya) return;

	$("input#suraNav").val(suraNum);
	$("input#ayaNav").val(ayaNum);

	if (oldAyaId != null)
		unHighlightAya(oldAyaId);
	highlightAya(ayaId);

	$(aya).ScrollTo(400, 'original', getBrowserHeight() > getObjectHeight(aya) ? 
					getBrowserHeight()/6 : 0);
	oldAyaId = ayaId;
}

function getObjectHeight(obj) { return obj.offsetHeight; }

function getBrowserHeight() { return document.body.clientHeight; }

function highlightAya(id) {
	$('#sign_' + id).attr('className', 'selectedAyaSign');
	$('#' + id).attr('className', 'selectedAya');
}

function unHighlightAya(id) {
	$('#sign_' + id).attr('className', 'sign');
	$('#' + id).attr('className', 'aya');
}

// Browser <-> SWT communication
function gotoSuraAya(suraAya) { setMessage('ZEKR::GOTO ' + suraAya + ';'); } // use it when sura changed
function gotoAya(aya) { setMessage('ZEKR::NAVTO ' + aya + ';'); } // only use it when sura is not changed
function translate(location) { setMessage('ZEKR::TRANS ' + location + ';'); }
function setMessage(msg) { window.status = msg; }


SearchResult = function() {
	var cnt;
	var num = 0;
	var oldNum = 0;
	var list;
	$(document).ready(function() {
		try{
			cnt = $("div.searchResult/div").size();
			list = $("div.searchResult/div");
		} catch(e) {return;}
		focus();
	});

	this.next = function() {
		if (num < cnt - 1) { oldNum = num; num++; focus(); }
	};

	this.prev = function() {
		if (num > 0) { oldNum = num; num--; focus(); }
	};

	function focus() {
		var h = list.eq(num).height();

		var bh = getBrowserHeight();
		$("#result_" + (1+oldNum)).children("div").attr("className", "item");
		$("#result_" + (1+num)).children("div").attr("className", "selectedAya").ScrollTo(500, 'original', bh > h  ? bh/6 : 0);

		var suraAya = $("#itemNum_" + (1+num)).attr("title").split('-');
		$("#suraNum").val(suraAya[0]);
		$("#ayaNum").val(suraAya[1]);
	};
};
