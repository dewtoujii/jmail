package crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author Tobi
 */
public class RSA {
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPair keyPair = null;
		try {
			keyPair = KeyPairGenerator.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider())
					.generateKeyPair();
		} catch (NullPointerException ignore) {
		} // thrown by getInstance()...
		return keyPair;
	}

	/**
	 * reads a public key from an InputStream in X.509 encoding and returns it
	 * 
	 * @param InputStream
	 *            with public key
	 * @return X.509 public key read in from @in
	 */
	public static PublicKey getPublicKey(InputStream in) {
		// read keys from file
		final ByteArrayOutputStream pubKeyBaos = new ByteArrayOutputStream();

		X509EncodedKeySpec pubKeySpec = null;
		int curByte = 0;
		if (pubKeyBaos != null && in != null) {
			try {
				while ((curByte = in.read()) != -1) {
					pubKeyBaos.write(curByte);
				}
			} catch (IOException e) {
			}

			pubKeySpec = new X509EncodedKeySpec(pubKeyBaos.toByteArray());
			try {
				pubKeyBaos.close();
			} catch (IOException e) {
			}
		}

		PublicKey pubKey = null;
		try {
			pubKey = KeyFactory.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider())
					.generatePublic(pubKeySpec);
		} catch (InvalidKeySpecException e) {
		} catch (NoSuchAlgorithmException e) {
		} catch (NullPointerException ignore) { // thrown by getInstance(...).
		}

		return pubKey;
	}
}
