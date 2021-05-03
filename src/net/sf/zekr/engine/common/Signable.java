/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 24, 2008
 */
package net.sf.zekr.engine.common;

/**
 * Implementations of this class have digital signature for integrity check.
 * 
 * @author Mohsen Saboorian
 */
public interface Signable {
	/** Resource not verified yet. */
	public static final int UNKNOWN = 0;
	/** Resource is authentic. */
	public static final int AUTHENTIC = 1;
	/** Resource not not authentic. */
	public static final int NOT_AUTHENTIC = 2;

	/**
	 * @return signature
	 */
	public byte[] getSignature();

	/**
	 * @return {@link Signable#UNKNOWN} if verification is not done yet, <code>AUTHENTIC</code> if verification
	 *         is done and document is authentic and <code>NOT_AUTHENTIC</code> if document is not authentic.
	 */
	public int getVerificationResult();
}
