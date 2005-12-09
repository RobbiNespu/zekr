/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 7, 2005
 */
package net.sf.zekr.common.util;


public interface IQuranLocation {
	public int getAya();
	public void setAya(int aya);
	public int getSoora();
	public void setSoora(int soora);
	public String getSooraName();
}
