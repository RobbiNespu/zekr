/**
 * Interface Elements for jQuery
 * utility function
 * 
 * http://interface.eyecon.ro
 * 
 * Copyright (c) 2006 Stefan Petre
 * Dual licensed under the MIT (MIT-LICENSE.txt) 
 * and GPL (GPL-LICENSE.txt) licenses.
 *   
 * $Revision$
 * $Log$
 * Revision 1.1  2006/09/23 07:51:00  mohsen_s
 * *** empty log message ***
 *
 * Revision 1.13  2006/09/04 18:20:54  Stef
 * Fixed finding element position on screen in Opera 9.01
 *
 * Revision 1.12  2006/09/04 16:45:41  Stef
 * Fixed finding element's position in page. For Safari and Opera BODY tag position should not count.
 *
 * Revision 1.11  2006/09/02 06:46:03  Stef
 * *** empty log message ***
 *
 * Revision 1.10  2006/09/02 06:07:38  Stef
 * *** empty log message ***
 *
 * Revision 1.9  2006/09/01 21:25:07  Stef
 * Fixed  bug finding the elemnt position on screen when element has display: none;
 *
 * Revision 1.8  2006/08/31 16:08:16  Stef
 * Added dual licencing
 *
 * Revision 1.7  2006/08/31 15:45:54  Stef
 * Fixed element position on page not to include BODY scrollTop and scrollLeft
 *
 * Revision 1.6  2006/08/29 18:46:41  Stef
 * *** empty log message ***
 *
 * Revision 1.5  2006/08/27 19:05:55  Stef
 * *** empty log message ***
 *
 * Revision 1.4  2006/08/26 07:53:53  Stef
 * *** empty log message ***
 *
 * Revision 1.3  2006/08/02 17:59:25  Stef
 * *** empty log message ***
 *
 */

jQuery.iUtil = {
	getPos : function (e, s)
	{
		var l = 0;
		var t  = 0;
		var sl = 0;
		var st  = 0;
		var w = jQuery.css(e,'width');
		var h = jQuery.css(e,'height');
		var wb = e.offsetWidth;
		var hb = e.offsetHeight;
		while (e.offsetParent){
			l += e.offsetLeft + (e.currentStyle?parseInt(e.currentStyle.borderLeftWidth)||0:0);
			t += e.offsetTop  + (e.currentStyle?parseInt(e.currentStyle.borderTopWidth)||0:0);
			if (s) {
				sl += e.parentNode.scrollLeft||0;
				st += e.parentNode.scrollTop||0;
			}
			e = e.offsetParent;
		}
		l += e.offsetLeft + (e.currentStyle?parseInt(e.currentStyle.borderLeftWidth)||0:0);
		t += e.offsetTop  + (e.currentStyle?parseInt(e.currentStyle.borderTopWidth)||0:0);
		st = t - st;
		sl = l - sl;
		return {x:l, y:t, sx:sl, sy:st, w:w, h:h, wb:wb, hb:hb};
	},
	getPosition : function(e)
	{
		var x = 0;
		var y = 0;
		var restoreStyle = false;
		es = e.style;
		if (jQuery(e).css('display') == 'none') {
			oldVisibility = es.visibility;
			oldPosition = es.position;
			es.visibility = 'hidden';
			es.display = 'block';
			es.position = 'absolute';
			restoreStyle = true;
		}
		el = e;
		while (el && el.tagName != 'BODY'){
			x += el.offsetLeft + (el.currentStyle && !jQuery.browser.opera ?parseInt(el.currentStyle.borderLeftWidth)||0:0);
			y += el.offsetTop  + (el.currentStyle && !jQuery.browser.opera ?parseInt(el.currentStyle.borderTopWidth)||0:0);
			el = el.offsetParent;
		}
		el = e;
		while (el.tagName != 'BODY' && el.parentNode)
		{
			x -= el.scrollLeft||0;
			y -= el.scrollTop||0;
			el = el.parentNode;
		}
		if (restoreStyle) {
			es.display = 'none';
			es.position = oldPosition;
			es.visibility = oldVisibility;
		}
		return {x:x, y:y};
	},
	getSize : function(e)
	{
		var w = jQuery.css(e,'width');
		var h = jQuery.css(e,'height');
		var wb = 0;
		var hb = 0;
		es = e.style;
		if (jQuery(e).css('display') != 'none') {
			wb = e.offsetWidth;
			hb = e.offsetHeight;
		} else {
			oldVisibility = es.visibility;
			oldPosition = es.position;
			es.visibility = 'hidden';
			es.display = 'block';
			es.position = 'absolute';
			wb = e.offsetWidth;
			hb = e.offsetHeight;
			es.display = 'none';
			es.position = oldPosition;
			es.visibility = oldVisibility;
		}
		return {w:w, h:h, wb:wb, hb:hb};
	},
	getClient : function(e)
	{
		if (e) {
			w = e.clientWidth;
			h = e.clientHeight;
		} else {
			w = window.innerWidth || document.documentElement.clientWidth || document.body.offsetWidth;
			h = window.innerHeight || document.documentElement.clientHeight || document.body.offsetHeight;
		}
		return {w:w,h:h};
	},
	getScroll : function (e) 
	{
		if (e) {
			t = e.scrollTop;
			l = e.scrollLeft;
			w = e.scrollWidth;
			h = e.scrollHeight;
			iw = 0;
			ih = 0;
		} else  {
			if (document.documentElement && document.documentElement.scrollTop) {
				t = document.documentElement.scrollTop;
				l = document.documentElement.scrollLeft;
				w = document.documentElement.scrollWidth;
				h = document.documentElement.scrollHeight;
			} else if (document.body) {
				t = document.body.scrollTop;
				l = document.body.scrollLeft;
				w = document.body.scrollWidth;
				h = document.body.scrollHeight;
			}
			iw = self.innerWidth||document.documentElement.clientWidth||document.body.clientWidth||0;
			ih = self.innerHeight||document.documentElement.clientHeight||document.body.clientHeight||0;
		}
		return { t: t, l: l, w: w, h: h, iw: iw, ih: ih };
	},
	getMargins : function(e)
	{
		el = $(e);
		t = el.css('marginTop') || '';
		r = el.css('marginRight') || '';
		b = el.css('marginBottom') || '';
		l = el.css('marginLeft') || '';
		return {t: t, r: r,	b: b, l: l};
	},
	getPadding : function(e)
	{
		el = $(e);
		t = el.css('paddingTop') || '';
		r = el.css('paddingRight') || '';
		b = el.css('paddingBottom') || '';
		l = el.css('paddingLeft') || '';
		return {t: t, r: r, b: b, l: l};
	},
	getBorder : function(e)
	{
		el = $(e);
		t = el.css('borderTopWidth') || '';
		r = el.css('borderRightWidth') || '';
		b = el.css('borderBottomWidth') || '';
		l = el.css('borderLeftWidth') || '';
		return {t: t, r: r, b: b, l: l};
	}
};