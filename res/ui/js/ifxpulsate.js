/**
 * Interface Elements for jQuery
 * FX - pulsate
 * 
 * http://interface.eyecon.ro
 * 
 * Copyright (c) 2006 Stefan Petre
 * Dual licensed under the MIT (MIT-LICENSE.txt) 
 * and GPL (GPL-LICENSE.txt) licenses.
 *   
 * $Revision$
 * $Log$
 * Revision 1.1  2006/12/17 10:32:35  mohsen_s
 * Get ready for 0.5.0 first beta release
 *
 * Revision 1.4  2006/08/31 16:08:15  Stef
 * Added dual licencing
 *
 * Revision 1.3  2006/08/26 09:57:29  Stef
 * *** empty log message ***
 *
 * Revision 1.2  2006/08/02 17:59:24  Stef
 * *** empty log message ***
 *
 */

jQuery.fn.Pulsate = function(speed, times, callback) {
	return this.queue('interfaceFX',function(){
		if (!jQuery.fxCheckTag(this)) {
			jQuery.dequeue(this, 'interfaceFX');
			return false;
		}
		var fx = new jQuery.fx.Pulsate(this, speed, times, callback);
		fx.pulse();
	});
};

jQuery.fx.Pulsate = function (el, speed, times, callback)
{	
	var z = this;
	z.times = times;
	z.cnt = 1;
	z.el = el;
	z.speed = speed;
	z.callback = callback;
	jQuery(z.el).show();
	z.pulse = function()
	{
		z.cnt ++;
		z.e = new jQuery.fx(
			z.el, 
			jQuery.speed(
				z.speed, 
				function(){
					z.ef = new jQuery.fx(
						z.el, 
						jQuery.speed(
							z.speed,
							function()
							{
								if (z.cnt <= z.times)
									z.pulse();
								else {
									jQuery.dequeue(z.el, 'interfaceFX');
									if (z.callback && z.callback.constructor == Function) {
										z.callback.apply(z.el);
									}
								}
							}
						), 
						'opacity'
					);
					z.ef.custom(0,1);
				}
			), 
			'opacity'
		);
		z.e.custom(1,0);
	};
};
