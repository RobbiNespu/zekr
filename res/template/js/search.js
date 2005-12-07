/*
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

function arabicSimplify(str) {
	// diacritics removal
	var arr = [SUKUN, SHADDA, KASRA, DAMMA, FATHA, KASRATAN, DAMMATAN, FATHATAN, SUPERSCRIPT_ALEF];
	for (var i = 0; i < arr.length; i++) {
		str = replaceAll(str, arr[i], "");
	}

	// YEH replacement
	str = replaceAll(str, ALEF_MAKSURA, ARABIC_YEH);
	str = replaceAll(str, FARSI_YEH, ARABIC_YEH);
	str = replaceAll(str, FARSI_KEHEH, ARABIC_KAF);
	str = replaceAll(str, ALEF_HAMZA_ABOVE, ALEF);
	str = replaceAll(str, ALEF_HAMZA_BELOW, ALEF);
	return str;
}

function isDiac(ch) {
	return (ch == SUKUN) || (ch == SHADDA) || (ch == KASRA) || (ch == DAMMA) || 
	       (ch == FATHA) || (ch == KASRATAN) || (ch == DAMMATAN) || (ch == FATHATAN) || 
	       (ch == SUPERSCRIPT_ALEF);
}

function highlightWordInNode(aWord, aNode, matchDiac) {
	if (aNode.nodeType == 1){
		var children = aNode.childNodes;
		for(var i = 0; i < children.length; i++) {
			highlightWordInNode(aWord, children[i], matchDiac);
		}
    }
	else if (aNode.nodeType == 3){
		highlightWordInText(aWord, aNode, matchDiac);
	}
}

function indexOfIgnoreDiacritic(src, key) {
	key = arabicSimplify(key);
	var i = 0, k = 0, s = 0, start = -1;
	if (key.length == 0)
		return -1;
	while(s < src.length) {
		if (k == key.length)
			break;

		if (src.charAt(s) == key.charAt(k)) {
			if (start == -1)
				start = s;
			s++; k++;
		} else if(key.charAt(k) == ALEF &&
		          (src.charAt(s) == ALEF_HAMZA_ABOVE || 
		           src.charAt(s) == ALEF_HAMZA_BELOW)) {
			if (start == -1)
				start = s;
			s++;
			k++;
		} else if (isDiac(src.charAt(s))) {
			s++
		}else {
			s++;
			k = 0;
			start = -1;
		}
	}
	if (k == key.length) { // fully matched
		spaceAfter = src.indexOf(" ", start);
		spaceBefore = src.substring(0, start + 1).lastIndexOf(" ");
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

	spaceAfter = src.indexOf(" ", start);
	spaceBefore = src.substring(0, start + 1).lastIndexOf(" ");
	if (spaceBefore == -1) start = 0;
	else start = spaceBefore + 1;
	if (spaceAfter == -1) end = src.length;
	else end = spaceAfter;
	return {startIndex: start, endIndex: end};
}

function highlightWordInText(aWord, textNode, matchDiac){
	allText = new String(textNode.data);
	lower = allText.toLowerCase();
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
		spanNode.style.color = "red";
		spanNode.style.fontWeight = "bold";
		replacementNode.appendChild(spanNode);
		boldText = document.createTextNode(allText.substring(sIndex, eIndex));
		spanNode.appendChild(boldText);
		allText = allText.substring(eIndex);
		lower = allText.toLowerCase();
		loc = myIndexOf(lower, aWord);
	}
	newAfter = document.createTextNode(allText);
	replacementNode.appendChild(newAfter);
	textNode.parentNode.removeChild(textNode);
}

function find(str, matchDiac) {
	if (str == "") return;
//	if (match) {
	highlightWordInNode(str, document.body, matchDiac);
/*	} else {
		var strFound = null;
		var tRange = null;
		if (tRange == null || strFound == 0) { // first time
			tRange = self.document.body.createTextRange();
			strFound = tRange.findText(str);
			if (strFound) {
				tRange.expand("word");
			}
		}
		while(strFound) {
			if (tRange != null) {
				tRange.collapse(false);
				strFound = tRange.findText(str);
				if (strFound) {
					tRange.expand("word");
					tRange.select();
				}
					
			}
		}
	}
*/
}

