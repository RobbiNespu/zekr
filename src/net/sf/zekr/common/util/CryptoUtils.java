/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 7, 2008
 */
package net.sf.zekr.common.util;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import net.sf.zekr.common.ZekrBaseRuntimeException;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Mohsen Saboorian
 */
public class CryptoUtils {
	public static final byte[] PUBLIC_KEY;
	static {
		try {
			PUBLIC_KEY = Base64
					.decodeBase64(("MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZp"
							+ "RV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fn"
							+ "xqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuE"
							+ "C/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJ"
							+ "FnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImo"
							+ "g9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAW+7HDcQiEiNnPsHa/wx5f53CltL2iTDCqSCQ"
							+ "d6AoNQe/OfBUtFUnohFh3CD0iFfCKEDbsP1Q/4tI62Y1hKFJv/S5Ju4CzBCwt5/SRDgVwJ0pP808"
							+ "OZQ38Yx6ZOqVdgaHaYt5Yo3P/shkVZvlVu9VO66dcnnS7A+NP37IbwxSJb8=").getBytes("US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			throw new ZekrBaseRuntimeException(e);
		}
	}

	public static byte[] sign(String datafile, PrivateKey prvKey, String sigAlg) throws Exception {
		Signature sig = Signature.getInstance(sigAlg);
		sig.initSign(prvKey);
		FileInputStream fis = new FileInputStream(datafile);
		byte[] dataBytes = new byte[1024];
		int nread = fis.read(dataBytes);
		while (nread > 0) {
			sig.update(dataBytes, 0, nread);
			nread = fis.read(dataBytes);
		}
		return sig.sign();
	}

	public static boolean verify(byte[] text, PublicKey pubKey, byte[] sigBytes) throws NoSuchAlgorithmException,
			InvalidKeyException, SignatureException {
		Signature sig = Signature.getInstance("SHA1withDSA");
		sig.initVerify(pubKey);
		sig.update(text);
		return sig.verify(sigBytes);
	}

	public static boolean verify(byte[] text, byte[] sigBytes) throws GeneralSecurityException {
		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(PUBLIC_KEY);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		PublicKey pubKey = keyFactory.generatePublic(pubSpec);
		return verify(text, pubKey, sigBytes);
	}

	public static byte[] sign(byte[] text, byte[] prvKeyBytes) throws GeneralSecurityException {
		PKCS8EncodedKeySpec prvSpec = new PKCS8EncodedKeySpec(prvKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		PrivateKey prvKey = keyFactory.generatePrivate(prvSpec);
		Signature sig = Signature.getInstance("SHA1withDSA");
		sig.initSign(prvKey);
		sig.update(text);
		return sig.sign();
	}

	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		// Generate a 1024-bit Digital Signature Algorithm (DSA) key pair
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		keyGen.initialize(1024);
		KeyPair keypair = keyGen.genKeyPair();
		return keypair;
	}
}
