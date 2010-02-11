/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 11, 2010
 */
package net.sf.zekr.engine.audio;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import net.sf.zekr.ZekrBaseTest;

public class PlayableObjectTest extends ZekrBaseTest {

	public PlayableObjectTest() throws Exception {
		super();
	}

	public void testHashCode() throws Exception {
		assertEquals(new PlayableObject(new URL("http://zekr.org")).hashCode(), new PlayableObject(new URL(
				"http://zekr.org")).hashCode());
		assertEquals(new PlayableObject(new File("somefile")).hashCode(), new PlayableObject(new File("somefile"))
				.hashCode());
		TestInputStream tis = new TestInputStream();
		assertEquals(new PlayableObject(tis).hashCode(), new PlayableObject(tis).hashCode());
	}

	public void testPlayableObjectURL() throws Exception {
		PlayableObject po = new PlayableObject(new URL("http://zekr.org"));
		assertNotNull(po);

		try {
			po = new PlayableObject((URL) null);
			fail("Non-reachable code.");
		} catch (NullPointerException npe) {
			// this is ok
		}

	}

	public void testPlayableObjectFile() throws Exception {
		PlayableObject po = new PlayableObject(new File("somefile"));
		assertNotNull(po);

		try {
			po = new PlayableObject((File) null);
			fail("Non-reachable code.");
		} catch (NullPointerException npe) {
		}
	}

	public void testPlayableObjectInputStream() throws Exception {
		PlayableObject po = new PlayableObject(new TestInputStream());
		assertNotNull(po);

		try {
			po = new PlayableObject((InputStream) null);
			fail("Non-reachable code.");
		} catch (NullPointerException npe) {
		}
	}

	public void testToString() throws Exception {
		URL url = new URL("http://zekr.org");
		assertEquals(new URL("http://zekr.org"), url);
	}

	public void testEqualsObject() throws Exception {
		URL url = new URL("http://zekr.org");
		PlayableObject po = new PlayableObject(url);
		assertEquals(po, new PlayableObject(new URL("http://zekr.org")));
		assertNotSame(po, new PlayableObject(new URL("https://zekr.org")));

		po = new PlayableObject(new File("somefile"));
		assertEquals(po, new PlayableObject(new File("somefile")));
		assertNotSame(po, new PlayableObject(new File("some/other/file")));
	}

}
