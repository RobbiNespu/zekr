/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * version 1
 */

// initializations...
// window.status = '';

var oldAyaId = null;

function focusOnAya(ayaId) {
	var aya = document.getElementById(ayaId);
	if (!aya) return;
	if (oldAyaId != null)
		unHighlightAya(oldAyaId);
	highlightAya(ayaId)
	window.scrollTo(getX(aya), getY(aya) - 100);
	oldAyaId = ayaId;
}

function highlightAya(id) {
	if (elem = document.getElementById('sign_' + id)) {
		elem.className = 'selectedAyaSign';
		document.getElementById(id).className = 'selectedAya';
	}
}

function unHighlightAya(id) {
	if (elem = document.getElementById('sign_' + id)) {
		elem.className = 'sign';
		document.getElementById(id).className = 'aya';
	}
}

function getX(elem)
{
	var curleft = 0;
	if (elem.offsetParent)
		while (1) {
			curleft += elem.offsetLeft;
			if (!elem.offsetParent)
				break;
			elem = elem.offsetParent;
		}
	else if (elem.x)
		curleft += elem.x;
	return curleft;
}

function getY(elem)
{
	var curtop = 0;
	if (elem.offsetParent)
		while (1) {
			curtop += elem.offsetTop;
			if (!elem.offsetParent)
				break;
			elem = elem.offsetParent;
		}
	else if (elem.y)
		curtop += elem.y;
	return curtop;
}

// Browser->SWT communication
function gotoView(location) {
	setMessage('ZEKR::GOTO ' + location + ';');
}

function setMessage(msg) {
	window.status = msg;
}
