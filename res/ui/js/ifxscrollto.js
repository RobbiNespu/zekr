/**
 * Interface Elements for jQuery
 * FX - scroll to
 * 
 * http://interface.eyecon.ro
 * 
 * Copyright (c) 2006 Stefan Petre
 * Dual licensed under the MIT (MIT-LICENSE.txt) 
 * and GPL (GPL-LICENSE.txt) licenses.
 *   
 * $Revision$
 * $Log$
 * Revision 1.2  2007/04/05 14:25:07  mohsen_s
 * Update before first 0.6.0 milestone
 *
 * Revision 1.1  2006/09/23 07:51:00  Mohsen Saboorian
 * *** empty log message ***
 *
 * Revision 1.4  2006/08/31 16:08:16  Stef
 * Added dual licencing
 *
 * Revision 1.3  2006/08/27 19:05:55  Stef
 * *** empty log message ***
 *
 * Revision 1.2  2006/08/02 17:59:24  Stef
 * *** empty log message ***
 *
 */

jQuery.fn.ScrollTo = function(s, transition, y) {
	o = jQuery.speed(s);
	return this.queue('interfaceFX',function(){
		new jQuery.fx.ScrollTo(this, o, transition, y||0);
	});
};

jQuery.fx.ScrollTo = function (e, o, transition, y)
{
	var z = this;
	z.o = o;
	z.e = e;
	z.transition = transition||'original';
	p = jQuery.iUtil.getPosition(e);
	s = jQuery.iUtil.getScroll();
	p.y -= y;
	z.clear = function(){clearInterval(z.timer);z.timer=null;jQuery.dequeue(z.e, 'interfaceFX');};
	z.t=(new Date).getTime();
	s.h = s.h > s.ih ? (s.h - s.ih) : s.h;
	s.w = s.w > s.iw ? (s.w - s.iw) : s.w;
	z.endTop = p.y > s.h ? s.h : p.y;
	z.endLeft = p.x > s.w ? s.w : p.x;
	z.startTop = s.t;
	z.startLeft = s.l;
	z.step = function(){
		var t = (new Date).getTime();
		var n = t - z.t;
		var p = n / z.o.duration;
		if (t >= z.o.duration+z.t) {
			z.clear();
			setTimeout(function(){z.scroll(z.endTop, z.endLeft)},13);
		} else {
			st = jQuery.fx.transitions(p, n, z.startTop, (z.endTop - z.startTop), z.o.duration, z.transition);//((-Math.cos(p*Math.PI)/2) + 0.5) * (z.p.y-z.s.t) + z.s.t;
			sl = jQuery.fx.transitions(p, n, z.startLeft, (z.endLeft - z.startLeft), z.o.duration, z.transition);//((-Math.cos(p*Math.PI)/2) + 0.5) * (z.p.x-z.s.l) + z.s.l;
			z.scroll(st, sl);
		}
	};
	z.scroll = function (t, l){window.scrollTo(l, t)};
	z.timer=setInterval(function(){z.step();},13);
};