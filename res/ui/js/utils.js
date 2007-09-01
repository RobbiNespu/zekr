/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * @author: Mohsen Saboorian <mohsen@zekr.org>
 */

var oldAyaId = null;

String.prototype.trim = function() {
    return this.replace(/(\s+$)|(^\s+)/g, '');
};

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
		refocus();
	});
	
	$("#suraNav").focus();
});

refocus = function() {
	var suraNum = $("input#hiddenSuraNum").val();
	var ayaNum = $("input#hiddenAyaNum").val();

	var ayaId = suraNum + "_" + ayaNum;	
	var aya = document.getElementById(ayaId);
	if (!aya) return;

	$(aya).ScrollTo(1, 'original', getBrowserHeight() > getObjectHeight(aya) ? 
					getBrowserHeight()/5 : 0);
};

navtoSuraAya = function() {
	var sura = $("input#suraNav").val();
	var aya = $("input#ayaNav").val();
	var origSuraNum = $("input#hiddenSuraNum").val();
	if (!isNaN(parseInt(sura.trim())) && !isNaN(parseInt(aya.trim())))
		if (origSuraNum != sura)
			gotoSuraAya(sura + "-" + aya);
		else
			gotoAya(sura + "-" + aya); // sura should also be passed (may user change the sura combo box)
};

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
	$(aya).ScrollTo(400, 'original', bh > (oh + bh/5) ? bh/5 : 35);
	oldAyaId = ayaId;
}

function getObjectHeight(obj) { return obj.offsetHeight; }

function getBrowserHeight() { return document.body.clientHeight; }

function highlightAya(id) {
	$('#sign_' + id).attr('className', 'selectedAyaSign');
	$('#' + id).attr('className', 'selectedAya');
}

function unHighlightAya(id) {
	$('#sign_' + id).attr('className', 'ayaSign');
	$('#' + id).attr('className', 'aya');
}

// Browser <-> SWT communication
function gotoSuraAya(suraAya) { setMessage('ZEKR::GOTO ' + suraAya + ';'); } // use it when sura changed
function gotoAya(suraAya) { setMessage('ZEKR::NAVTO ' + suraAya + ';'); } // only use it when sura is not changed
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
		} catch(e) {error(e); return;}
		focus();
	});

	this.next = function() {
		if (num < cnt - 1) { oldNum = num; num++; focus(); }
	};

	this.prev = function() {
		if (num > 0) { oldNum = num; num--; focus(); }
	};

	function focus() {
		if (cnt <= 0)
			return;
		var h = list.eq(num).height();

		var bh = getBrowserHeight();
		$("#result_" + (1+oldNum)).children("div").attr("className", "item");
		$("#result_" + (1+num)).children("div").attr("className", "selectedAya").ScrollTo(500, 'original', bh > h  ? bh/5 : 0);

		var suraAya = $("#itemNum_" + (1+num)).attr("title").split('-');
		$("#suraNum").val(suraAya[0]);
		$("#ayaNum").val(suraAya[1]);
		
	};
};


CurrentPageSearchResult = function() {
	var cnt;
	var num = 0;
	var oldNum = 0;
	var list;
	$(document).ready(function() {
		try{
			list = $("span.jsHighlight");
			cnt = list.size();
		} catch(e) {error(e); return;}
		if (cnt > 0)
			focus();
	});

	this.next = function() {
		if (cnt <= 0) return;
		oldNum = num;
		num < cnt - 1 ? num++ : num = 0;
		focus();
	};

	this.prev = function() {
		if (cnt <= 0) return;
		oldNum = num;
		num > 0 ? num-- : num = cnt - 1; 
		focus(); 
	};

	function focus() {
		var h = list.eq(num).height();
		var bh = getBrowserHeight();
		var item = list.get(num);
		$("#focusedWord").html("\"" + $(item).text() + "\"");
		$(list.get(oldNum)).attr("className", "jsHighlight");
		$(item).ScrollTo(500, 'original', bh > h  ? bh/5 : 0).attr("className", "jsHighlightFocused");
	};
};

function error(e) {
	alert("An unexpected error occurred:\n" + "[" + e.name + ":" + e.number + "] " + e.message + "\n");
}
