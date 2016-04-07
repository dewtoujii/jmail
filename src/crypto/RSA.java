package crypto;

import java.security.Key;

import de.unifreiburg.cs.proglang.jgs.support.Constraints;
import exceptionhandling.CryptoHelper;
import exceptionhandling.Result;

/**
 *
 * @author Tobi
 */
public class RSA {

	@Constraints({ "@0 <= Owner", "@1 <= pub" })
	public static byte[] encrypt(Key key, byte[] input) {
		Result<Ciphertext> encryptResult = CryptoHelper.doRSAEncryption(key, input);
		if (encryptResult.isSuccess())
			return encryptResult.getObject().encText;
		else
			throw new RuntimeException("Fehler beim Verschlüsseln!", encryptResult.getException());
	}

	@Constraints({ "@0 <= Owner", "@1 <= pub", "Owner <= @ret" })
	public static byte[] decrypt(Key key, byte[] encryptedBytes) {
		Result<Ciphertext> decryptResult = CryptoHelper.doRSADecryption(key, encryptedBytes);
		if (decryptResult.isSuccess())
			return decryptResult.getObject().encText;
		else
			throw new RuntimeException("Fehler beim Entschlüsseln!", decryptResult.getException());
	}
}
