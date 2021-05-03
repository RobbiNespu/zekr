/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 13, 2010
 */
package net.sf.zekr.ui;

/**
 * @author Mohsen Saboorian
 */
public interface QuestionListener {
	void start(int result);

	void done();

	void cancel();
}
