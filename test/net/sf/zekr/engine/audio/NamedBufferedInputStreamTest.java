/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 11, 2010
 */
package net.sf.zekr.engine.audio;

import junit.framework.TestCase;

/**
 * @author Mohsen Saboorian
 */
public class NamedBufferedInputStreamTest extends TestCase {

	/**
	 * Test method for {@link net.sf.zekr.engine.audio.NamedBufferedInputStream#hashCode()}.
	 */
	public void testHashCode() {
		NamedBufferedInputStream nbis1 = new NamedBufferedInputStream(new String("some string"), new TestInputStream(), 1);
		NamedBufferedInputStream nbis2 = new NamedBufferedInputStream(new String("some string"), new TestInputStream(), 1);
		assertEquals(nbis1.hashCode(), nbis2.hashCode());

	}

	/**
	 * Test method for
	 * {@link net.sf.zekr.engine.audio.NamedBufferedInputStream#NamedBufferedInputStream(java.lang.String, java.io.InputStream, int)}
	 * .
	 */
	public void testNamedBufferedInputStream() {
		NamedBufferedInputStream nbis = new NamedBufferedInputStream("somename", new TestInputStream(), 1);
		assertNotNull(nbis);

		try {
			nbis = new NamedBufferedInputStream(null, new TestInputStream(), 1);
			fail("Non-reachable code.");
		} catch (NullPointerException e) {
			// this is ok
		}
	}

	/**
	 * Test method for {@link net.sf.zekr.engine.audio.NamedBufferedInputStream#toString()}.
	 */
	public void testToString() {
		NamedBufferedInputStream nbis = new NamedBufferedInputStream("somename", new TestInputStream(), 1);
		assertEquals(nbis.getName(), "somename");
	}

	/**
	 * Test method for {@link net.sf.zekr.engine.audio.NamedBufferedInputStream#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() {
		NamedBufferedInputStream nbis1 = new NamedBufferedInputStream("somename", new TestInputStream(), 1);
		NamedBufferedInputStream nbis2 = new NamedBufferedInputStream("some" + "name", new TestInputStream(), 1);
		assertEquals(nbis1, nbis2);
	}

}
