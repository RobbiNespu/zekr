/**
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * version 1
 */

var SUKUN = String.fromCharCode(0x652);
var SHADDA = String.fromCharCode(0x651);
var KASRA = String.fromCharCode(0x650);
var DAMMA = String.fromCharCode(0x64f);
var FATHA = String.fromCharCode(0x64e);

var KASRATAN = String.fromCharCode(0x64d);
var DAMMATAN = String.fromCharCode(0x64c);
var FATHATAN = String.fromCharCode(0x64b);

var SUPERSCRIPT_ALEF = String.fromCharCode(0x670);

var HAMZA = String.fromCharCode(0x621);
var ALEF = String.fromCharCode(0x627);
var ALEF_MADDA = String.fromCharCode(0x622);
var ALEF_HAMZA_ABOVE = String.fromCharCode(0x623);
var ALEF_HAMZA_BELOW = String.fromCharCode(0x625);

var YEH_HAMZA_ABOVE = String.fromCharCode(0x626);
var WAW_HAMZA_ABOVE = String.fromCharCode(0x624);
var WAW = String.fromCharCode(0x648);

var ALEF_MAKSURA = String.fromCharCode(0x649);
var FARSI_YEH = String.fromCharCode(0x6cc);
var ARABIC_YEH = String.fromCharCode(0x64a);

var ARABIC_KAF = String.fromCharCode(0x643);
var FARSI_KEHEH = String.fromCharCode(0x6a9);
	
function replaceAll(str, oldStr, newStr) {
	var i = str.indexOf(oldStr);
	var newLen = newStr.length;
	while (i > -1) {
		str = str.replace(oldStr, newStr);
		i = str.indexOf(oldStr, i + newLen);
	}
	return str;
}

function replaceSimilarArabic(str) {
	str = replaceAll(str, ALEF_MAKSURA, ARABIC_YEH);
	str = replaceAll(str, ALEF_HAMZA_ABOVE, ALEF);
	str = replaceAll(str, ALEF_HAMZA_BELOW, ALEF);
	str = replaceAll(str, ALEF_MADDA, ALEF);
	return str;
}

function arabicSimplify(str) {
	// diacritics removal
	var arr = [SUKUN, SHADDA, KASRA, DAMMA, FATHA, KASRATAN, DAMMATAN, FATHATAN, SUPERSCRIPT_ALEF];
	for (var i = 0; i < arr.length; i++) {
		str = replaceAll(str, arr[i], "");
	}

	// Some replacement
	str = replaceAll(str, ALEF_MAKSURA, ARABIC_YEH);
	str = replaceAll(str, FARSI_YEH, ARABIC_YEH);
	str = replaceAll(str, FARSI_KEHEH, ARABIC_KAF);
	str = replaceAll(str, ALEF_HAMZA_ABOVE, ALEF);
	str = replaceAll(str, ALEF_HAMZA_BELOW, ALEF);
	str = replaceAll(str, ALEF_MADDA, ALEF);
	return str;
}

function isDiac(ch) {
	return (ch == SUKUN) || (ch == SHADDA) || (ch == KASRA) || (ch == DAMMA) || 
	       (ch == FATHA) || (ch == KASRATAN) || (ch == DAMMATAN) || (ch == FATHATAN) || 
	       (ch == SUPERSCRIPT_ALEF);
}

function indexOfIgnoreDiacritic(src, key) {
	key = arabicSimplify(key);
	src = replaceSimilarArabic(src);
	var k = 0, s = 0, start = -1;
	if (key.length == 0)
		return -1;
	while(s < src.length) {
		if (k == key.length)
			break;

		if (src.charAt(s) == key.charAt(k)) {
			if (start == -1)
				start = s;
			s++; k++;
		} else {
			if (!isDiac(src.charAt(s))) {
				if (k != 0)
					s--;
				k = 0;
				start = -1;
			}
			s++;
		}
	}
	if (k == key.length) { // fully matched
		spaceBefore = (key.charAt(0) != ' ') ? src.substring(0, start).lastIndexOf(' ') : start;
		spaceAfter = (key.charAt(key.length - 1) != ' ') ? src.indexOf(' ', s) : s;
		if (spaceBefore == -1) start = 0;
		else start = spaceBefore + 1;
		if (spaceAfter == -1) s = src.length;
		else s = spaceAfter;
		return {startIndex: start, endIndex: s};
	}
	return -1;
}

function indexOfMatchDiacritic(src, key) {
	start = src.indexOf(key);
	if (start == -1)
		return -1;

	spaceBefore = (key.charAt(0) != ' ') ? src.substring(0, start).lastIndexOf(' ') : start;
	spaceAfter = (key.charAt(key.length - 1) != ' ') ? src.indexOf(' ', start + key.length) : start + key.length;
	if (spaceBefore == -1) start = 0;
	else start = spaceBefore + 1;
	if (spaceAfter == -1) end = src.length;
	else end = spaceAfter;
	return {startIndex: start, endIndex: end};
}

function highlightWordInNode(aWord, aNode, matchDiac, matchCase) {
	if (aNode.nodeType == 1){
		var children = aNode.childNodes;
		for(var i = 0; i < children.length; i++) {
			highlightWordInNode(aWord, children[i], matchDiac, matchCase);
		}
    }
	else if (aNode.nodeType == 3){
		highlightWordInText(aWord, aNode, matchDiac, matchCase);
	}
}

function highlightWordInText(aWord, textNode, matchDiac, matchCase){
	var allText = new String(textNode.data);

	var lower = ""
	if (!matchCase) { lower = allText.toLowerCase(); aWord = aWord.toLowerCase(); }
	else lower = allText;

	var myIndexOf;
	if (matchDiac)
		myIndexOf = indexOfMatchDiacritic;
	else
		myIndexOf = indexOfIgnoreDiacritic;

	loc = myIndexOf(lower, aWord);
	if (loc == -1) return;

	// create a node to replace the textNode so we end up
	// not changing number of children of textNode.parent
	replacementNode = document.createElement("span");
	textNode.parentNode.insertBefore(replacementNode, textNode);
	while (loc != -1){
		sIndex = loc.startIndex;
		eIndex = loc.endIndex;
		before = allText.substring(0, sIndex);
		newBefore = document.createTextNode(before);
		replacementNode.appendChild(newBefore);
		spanNode = document.createElement("span");
		spanNode.className = "jsHighlight";
		spanNode.title = aWord;
		replacementNode.appendChild(spanNode);
		boldText = document.createTextNode(allText.substring(sIndex, eIndex));
		spanNode.appendChild(boldText);
		allText = allText.substring(eIndex);
		lower = matchCase ? allText : allText.toLowerCase();
		loc = myIndexOf(lower, aWord);
	}
	newAfter = document.createTextNode(allText);
	replacementNode.appendChild(newAfter);
	textNode.parentNode.removeChild(textNode);
}

Finder = function(matchDiac, matchCase) {
	this.matchDiac = matchDiac;
	this.matchCase = matchCase;
}

Finder.prototype.find = function(str) {
	if (str == "") return;
	highlightWordInNode(str, document.getElementById("searchableSection"), this.matchDiac, this.matchCase);
}

find = function(str, matchDiac, matchCase) {
	if (matchCase === undefined) matchCase = false;

	$("span.jsHighlight").each(function() {
		var p = $(this).parent().eq(0);
		p.after(p.text()).remove();
	});
	$("span.jsHighlight").each(function() {
		$(this).after($(this).text()).remove();
	});
	$("span.jsHighlightFocused").each(function() {
		var p = $(this).parent().eq(0);
		p.after(p.text()).remove();
	});
	$("span.jsHighlightFocused").each(function() {
		$(this).after($(this).text()).remove();
	});

//	$("span.jsHighlightFocused").removeClass("jsHighlightFocused");
//	$("span.jsHighlight").removeClass("jsHighlight");

	var finder = new Finder(matchDiac, matchCase);
	finder.find(str);

	// reveal next-prev buttons (if possible: this is not possible in a whole-quran search view)
	$("#nextPrevButtons").show("", new function() {
		$("#nextPrevButtons")[0]._res = new CurrentPageSearchResult();
	});
};
