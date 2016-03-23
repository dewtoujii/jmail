package crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Tobi
 */
public class RSA {

	public static byte[] encrypt(Key key, byte[] input)
	// throws (InvalidKeyException, IllegalBlockSizeException,
	// NullPointerException,
	// BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException)
	// where oneway(DESprin,P)
	{
		byte[] encryptedBytes = null;

		try {
			final Cipher rsaCipher_ = Cipher.getInstance("RSA/ECB/PKCS1Padding",
					new org.bouncycastle.jce.provider.BouncyCastleProvider());
			if (rsaCipher_ != null) {
				rsaCipher_.init(Cipher.ENCRYPT_MODE, key);

				// final byte{L}[]{L} input = s.getBytes();
				encryptedBytes = rsaCipher_.doFinal(input);
			}
		} catch (Exception e) {
		}

		return encryptedBytes;
	}
	
	public static byte[] decrypt(Key key, byte[] encryptedBytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher rsaCipher_ = Cipher.getInstance("RSA/ECB/PKCS1Padding",
				new org.bouncycastle.jce.provider.BouncyCastleProvider());

		rsaCipher_.init(Cipher.DECRYPT_MODE, key);// .getBytes()));

		// byte{P:}[]{P:} encrypted = ciph.encText.getBytes();
		return rsaCipher_.doFinal(encryptedBytes);
	}
}
