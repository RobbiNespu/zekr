function salam() {
	alert("salam");
}

function highlightWordInNode(aWord, aNode) {
	if (aNode.nodeType == 1){
		var children = aNode.childNodes;
		for(var i = 0; i < children.length; i++) {
			highlightWordInNode(aWord, children[i]);
		}
    }
	else if (aNode.nodeType == 3){
		highlightWordInText(aWord, aNode);
	}
}

function arabicSimplify(str) {
	var SUKUN = 0x652;
	var SHADDA = 0x651;
	var KASRA = 0x650;
	var DAMMA = 0x64f;
	var FATHA = 0x64e;

	var KASRATAN = 0x64d;
	var DAMMATAN = 0x64c;
	var FATHATAN = 0x64b;

	var SUPERSCRIPT_ALEF = 0x670;

	var HAMZA = 0x621;
	var ALEF = 0x627;
	var ALEF_MADDA = 0x622;
	var ALEF_HAMZA_ABOVE = 0x623;
	var ALEF_HAMZA_BELOW = 0x625;
	
	var YEH_HAMZA_ABOVE = 0x626;
	var WAW_HAMZA_ABOVE = 0x624;
	var WAW = 0x648;
	
	var ALEF_MAKSURA = 0x649;
	var FARSI_YEH = 0x6cc;
	var ARABIC_YEH = 0x64a;

	// diacritics removal
	var arr = [SUKUN, SHADDA, KASRA, DAMMA, FATHA, KASRATAN, DAMMATAN, FATHATAN, SUPERSCRIPT_ALEF];
	for (var i = 0; i < arr.length; i++) {
		str.replace(arr[i], "");
	}

	// YEH replacement
	str.replace(ALEF_MAKSURA, ARABIC_YEH);
	str.replace(FARSI_YEH, ARABIC_YEH);
}

function myIndexOf(src, key) {
	return src.indexOf(key);
//	s = arabicSimplify(src);
//	k = arabicSimplify(key);
}

function highlightWordInText(aWord, textNode){
	allText = new String(textNode.data);
	allTextLowerCase = allText.toLowerCase();
	index = myIndexOf(allTextLowerCase, aWord);
	if (index >= 0){
		// create a node to replace the textNode so we end up
		// not changing number of children of textNode.parent
		replacementNode = document.createElement("span");
		textNode.parentNode.insertBefore(replacementNode, textNode);
		while (index >= 0){
			before = allText.substring(0, index);
			newBefore = document.createTextNode(before);
			replacementNode.appendChild(newBefore);
			spanNode = document.createElement("span");
			spanNode.style.background = "Highlight";
			spanNode.style.color = "HighlightText";
			replacementNode.appendChild(spanNode);
			boldText = document.createTextNode(allText.substring(index, index + aWord.length));
			spanNode.appendChild(boldText);
			allText = allText.substring(index + aWord.length);
			allTextLowerCase = allText.toLowerCase();
			index = allTextLowerCase.indexOf(aWord);
		}
		newAfter=document.createTextNode(allText);
		replacementNode.appendChild(newAfter);
		textNode.parentNode.removeChild(textNode);
	}
}

var tRange = null;
function find(str, findAll) {
	if (str == "") return;
	var strFound;
	if (findAll) {
		tRange = self.document.body.createTextRange();
		/*
		do {
			strFound = tRange.findText(str);
			if (strFound) {
				tRange.select();
				tRange.collapse(false);
				alert("");
			}
		} while(strFound);
		*/
		highlightWordInNode(str, document.body);
	} else {
		if (tRange != null) {
			tRange.collapse(false);
			strFound = tRange.findText(str);
			if (strFound) tRange.select();
		}
		if (tRange == null || strFound == 0) {
			tRange = self.document.body.createTextRange();
			strFound = tRange.findText(str);
			if (strFound) tRange.select();
		}
	}
}

function findAll(str) {
	find(str, true);
}
