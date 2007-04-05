/**
 * Interface Elements for jQuery
 * FX
 * 
 * http://interface.eyecon.ro
 * 
 * Copyright (c) 2006 Stefan Petre
 * Dual licensed under the MIT (MIT-LICENSE.txt) 
 * and GPL (GPL-LICENSE.txt) licenses.
 *   
 * $Revision$
 * $Log$
 * Revision 1.3  2007/04/05 14:25:07  mohsen_s
 * Update before first 0.6.0 milestone
 *
 * Revision 1.2  2007/01/03 12:14:28  Mohsen Saboorian
 * *** empty log message ***
 *
 * Revision 1.1  2006/09/23 07:51:00  Mohsen Saboorian
 * *** empty log message ***
 *
 * Revision 1.10  2006/09/01 21:23:51  Stef
 * Fixed getting the sizes of the element to wrap
 *
 * Revision 1.9  2006/08/31 16:08:15  Stef
 * Added dual licensing
 *
 * Revision 1.8  2006/08/29 18:46:32  Stef
 * Changed the way fx wrapper is destroyed. first apply old style, then move element
 *
 * Revision 1.7  2006/08/27 19:05:55  Stef
 * *** empty log message ***
 *
 * Revision 1.6  2006/08/26 09:34:47  Stef
 * *** empty log message ***
 *
 * Revision 1.5  2006/08/26 09:31:11  Stef
 * *** empty log message ***
 *
 * Revision 1.4  2006/08/26 09:02:56  Stef
 * Added modified fx function from jQuery 1.0
 *
 * Revision 1.3  2006/08/03 21:38:41  Stef
 * *** empty log message ***
 *
 * Revision 1.2  2006/08/02 17:59:24  Stef
 * *** empty log message ***
 *
 */

//overwrite jQuery's default fx function with the new one to support diferent transitions
jQuery.fx = function( elem, options, prop, transition ){

	var z = this;

	z.transition = /easein|easeout|easeboth|bouncein|bounceout|bounceboth|elasticin|elasticout|elasticboth/.test(transition) ? transition : 'original';
	// The users options
	z.o = {
		duration: options.duration || 400,
		complete: options.complete,
		step: options.step
	};

	// The element
	z.el = elem;

	// The styles
	var y = z.el.style;

	// Simple function for setting a style value
	z.a = function(){
		if ( options.step )
			options.step.apply( elem, [ z.now ] );

		if ( prop == "opacity" ) {
			if (z.now == 1) z.now = 0.9999;
			if (window.ActiveXObject)
				y.filter = "alpha(opacity=" + z.now*100 + ")";
			else
				y.opacity = z.now;

		// My hate for IE will never die
		} else if ( parseInt(z.now) )
			y[prop] = parseInt(z.now) + "px";
			
		y.display = "block";
	};

	// Figure out the maximum number to run to
	z.max = function(){
		return parseFloat( jQuery.css(z.el,prop) );
	};

	// Get the current size
	z.cur = function(){
		var r = parseFloat( jQuery.curCSS(z.el, prop) );
		return r && r > -10000 ? r : z.max();
	};

	// Start an animation from one number to another
	z.custom = function(from,to){
		z.startTime = (new Date()).getTime();
		z.now = from;
		z.a();

		z.timer = setInterval(function(){
			z.step(from, to);
		}, 13);
	};

	// Simple 'show' function
	z.show = function( p ){
		if ( !z.el.orig ) z.el.orig = {};

		// Remember where we started, so that we can go back to it later
		z.el.orig[prop] = this.cur();

		z.custom( 0, z.el.orig[prop] );

		// Stupid IE, look what you made me do
		if ( prop != "opacity" )
			y[prop] = "1px";
	};

	// Simple 'hide' function
	z.hide = function(){
		if ( !z.el.orig ) z.el.orig = {};

		// Remember where we started, so that we can go back to it later
		z.el.orig[prop] = this.cur();

		z.o.hide = true;

		// Begin the animation
		z.custom(z.el.orig[prop], 0);
	};

	// IE has trouble with opacity if it does not have layout
	if ( jQuery.browser.msie && !z.el.currentStyle.hasLayout )
		y.zoom = "1";

	// Remember  the overflow of the element
	if ( !z.el.oldOverlay )
		z.el.oldOverflow = jQuery.css( z.el, "overflow" );

	// Make sure that nothing sneaks out
	y.overflow = "hidden";

	// Each step of an animation
	z.step = function(firstNum, lastNum){
		var t = (new Date()).getTime();

		if (t > z.o.duration + z.startTime) {
			// Stop the timer
			clearInterval(z.timer);
			z.timer = null;

			z.now = lastNum;
			z.a();
			if (z.el.curAnim)
				z.el.curAnim[ prop ] = true;
			
			var done = true;
			for ( var i in z.el.curAnim ) {
				if ( z.el.curAnim[i] !== true )
					done = false;
			}
					
			if ( done ) {
				// Reset the overflow
				y.overflow = z.el.oldOverflow;
			
				// Hide the element if the "hide" operation was done
				if ( z.o.hide ) 
					y.display = 'none';
				
				// Reset the property, if the item has been hidden
				if ( z.o.hide ) {
					for ( var p in z.el.curAnim ) {
						y[ p ] = z.el.orig[p] + ( p == "opacity" ? "" : "px" );

						// set its height and/or width to auto
						if ( p == 'height' || p == 'width' )
							jQuery.setAuto( z.el, p );
					}
				}
			}
			
			// If a callback was provided, execute it
			if( done && z.o.complete && z.o.complete.constructor == Function )
				// Execute the complete function
				z.o.complete.apply( z.el );
		} else {
			var n = t - this.startTime;
			// Figure out where in the animation we are and set the number
			//var p = (t - this.startTime) / z.o.duration;
			var p = n / z.o.duration;
			//z.now = ((-Math.cos(p*Math.PI)/2) + 0.5) * (lastNum-firstNum) + firstNum;
			z.now = jQuery.fx.transitions(p, n, firstNum, (lastNum-firstNum), z.o.duration, z.transition);

			// Perform the next step of the animation
			z.a();
		}
	};	
};

jQuery.fxCheckTag = function(e)
{
	if (/tr|td|tbody|caption|thead|tfoot|col|colgroup|th|body|header|script|frame|frameset|option|optgroup|meta/i.test(e.nodeName) )
		return false;
	else 
		return true;
};
jQuery.fx.destroyWrapper = function(e, old)
{
	c = e.firstChild;
	cs = c.style;
	cs.position = old.position;
	cs.marginTop = old.margins.t;
	cs.marginLeft = old.margins.l;
	cs.marginBottom = old.margins.b;
	cs.marginRight = old.margins.r;
	cs.top = old.top + 'px';
	cs.left = old.left + 'px';
	e.parentNode.insertBefore(c, e);
	e.parentNode.removeChild(e);
};
jQuery.fx.buildWrapper = function(e)
{
	if (!jQuery.fxCheckTag(e))
		return false;
	var t = jQuery(e);
	var es = e.style;
	var restoreStyle = false;
	oldStyle = {};
	oldStyle.position = t.css('position');
	oldStyle.sizes = jQuery.iUtil.getSize(e);
	oldStyle.margins = jQuery.iUtil.getMargins(e);
	if (t.css('display') == 'none') {
		oldVisibility = t.css('visibility');
		t.show();
		restoreStyle = true;
	}
	oldStyle.top = parseInt(t.css('top'))||0;
	oldStyle.left = parseInt(t.css('left'))||0;
	if (restoreStyle) {
		t.hide();
		es.visibility = oldVisibility;
	}
	var wid = 'w_' + parseInt(Math.random() * 10000);
	var wr = document.createElement(/img|br|input|hr|select|textarea|object|iframe|button|form|table|ul|dl|ol/i.test(e.nodeName) ? 'div' : e.nodeName);
	jQuery.attr(wr,'id', wid);
	jQuery(wr).addClass('fxWrapper');
	var wrs = wr.style;
	var top = 0;
	var left = 0;
	if (oldStyle.position == 'relative' || oldStyle.position == 'absolute'){
		top = oldStyle.top;
		left = oldStyle.left;
	}
	
	wrs.top = top + 'px';
	wrs.left = left + 'px';
	wrs.position = oldStyle.position != 'relative' && oldStyle.position != 'absolute' ? 'relative' : oldStyle.position;
	wrs.height = oldStyle.sizes.hb + 'px';
	wrs.width = oldStyle.sizes.wb + 'px';
	wrs.marginTop = oldStyle.margins.t;
	wrs.marginRight = oldStyle.margins.r;
	wrs.marginBottom = oldStyle.margins.b;
	wrs.marginLeft = oldStyle.margins.l;
	wrs.overflow = 'hidden';
	if (jQuery.browser == "msie") {
		es.filter = "alpha(opacity=" + 0.999*100 + ")";
	}
	es.opacity = 0.999;
	//t.wrap(wr);
	e.parentNode.insertBefore(wr, e);
	wr.appendChild(e);
	es.marginTop = '0px';
	es.marginRight = '0px';
	es.marginBottom = '0px';
	es.marginLeft = '0px';
	es.position = 'absolute';
	es.listStyle = 'none';
	es.top = '0px';
	es.left = '0px';
	return {oldStyle:oldStyle, wrapper:jQuery(wr)};
};
jQuery.fx.transitions = function(p, n, firstNum, delta, duration, type)
{
	if (type == 'original') {
		return ((-Math.cos(p*Math.PI)/2) + 0.5) * delta + firstNum;
	}
	if (type == 'easein') {
		return delta*(n/=duration)*n*n + firstNum;
	}
	if (type == 'easeout') {
		return -delta * ((n=n/duration-1)*n*n*n - 1) + firstNum;
	}
	if (type == 'easeboth') {
		if ((n/=duration/2) < 1)
			return delta/2*n*n*n*n + firstNum;
			return -delta/2 * ((n-=2)*n*n*n - 2) + firstNum;
	}
	if (type == 'bounceout') {
		if ((n/=duration) < (1/2.75)) {
			return delta*(7.5625*n*n) + firstNum;
		} else if (n < (2/2.75)) {
			return delta*(7.5625*(n-=(1.5/2.75))*n + .75) + firstNum;
		} else if (n < (2.5/2.75)) {
			return delta*(7.5625*(n-=(2.25/2.75))*n + .9375) + firstNum;
		} else {
			return delta*(7.5625*(n-=(2.625/2.75))*n + .984375) + firstNum;
		}
	}
	if (type == 'bouncein') {
		nm = duration - n;
		if ((nm/=duration) < (1/2.75)) {
			m = delta*(7.5625*nm*nm);
		} else if (nm < (2/2.75)) {
			m = delta*(7.5625*(nm-=(1.5/2.75))*nm + .75);
		} else if (nm < (2.5/2.75)) {
			m = delta*(7.5625*(nm-=(2.25/2.75))*nm + .9375);
		} else {
			m = delta*(7.5625*(nm-=(2.625/2.75))*nm + .984375);
		}
		return delta - m + firstNum;
	}
	
	if (type == 'bounceboth') {
		if (n < duration/2) {
			nm = n * 2;
			if ((nm/=duration) < (1/2.75)) {
				m = delta*(7.5625*nm*nm);
			} else if (nm < (2/2.75)) {
				m = delta*(7.5625*(nm-=(1.5/2.75))*nm + .75);
			} else if (nm < (2.5/2.75)) {
				m = delta*(7.5625*(nm-=(2.25/2.75))*nm + .9375);
			} else {
				m = delta*(7.5625*(nm-=(2.625/2.75))*nm + .984375);
			}
			return (delta - m + firstNum) * .5 + firstNum;
		} else {
			n = n * 2 - duration;
			if ((n/=duration) < (1/2.75)) {
				m = delta*(7.5625*n*n) + firstNum;
			} else if (n < (2/2.75)) {
				m = delta*(7.5625*(n-=(1.5/2.75))*n + .75) + firstNum;
			} else if (n < (2.5/2.75)) {
				m = delta*(7.5625*(n-=(2.25/2.75))*n + .9375) + firstNum;
			} else {
				m = delta*(7.5625*(n-=(2.625/2.75))*n + .984375) + firstNum;
			}
			return m * .5 + delta*.5 + firstNum;
		}
	}
	if (type == 'elasticout') {
		if ((n/=duration)==1)
			return firstNum+delta;
		return delta*Math.pow(2,-10*n) * Math.sin( (n*duration-(duration*.3)/4)*(2*Math.PI)/(duration*.3) ) + delta + firstNum;
	}
	if (type == 'elasticin') {
		if (n==0)
			return b;  
		if ((n/=duration)==1) 
			return firstNum+delta;
		return -(delta*Math.pow(2,10*(n-=1)) * Math.sin( (n*duration-(duration*.3)/4)*(2*Math.PI)/(duration*.3) )) + firstNum;
	}
	if (type == 'elasticboth') {
		if (n==0)
			return firstNum;
		if ((n/=duration)==1) 
			return firstNum+delta;
		$('#test').html(p +'<br />'+n);
		if (p < 1)
			return -.5*(delta*Math.pow(2,10*(n-=1)) * Math.sin( (n*duration-(duration*.45)/4)*(2*Math.PI)/(duration*.45) )) + firstNum;
		return delta*Math.pow(2,-10*(n-=1)) * Math.sin( (n*duration-(duration*.45)/4)*(2*Math.PI)/(duration*.45) )*.5 + delta + firstNum;
	}
			
};