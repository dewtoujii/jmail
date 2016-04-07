package crypto;

import java.security.Key;

import de.unifreiburg.cs.proglang.jgs.support.Constraints;
import exceptionhandling.CryptoHelper;
import exceptionhandling.Result;

/**
 *
 * @author Tobi
 */
public class AES {

	@Constraints({"Owner <= @ret"})
    public static Key getNewKey() {
		Result<Key> keyResult = CryptoHelper.generateAESKey();
		if(keyResult.isSuccess())
			return keyResult.getObject();
		else
			throw new RuntimeException("Fehler beim Erzeugen eines neuen AES-Keys!", keyResult.getException());
    }

	@Constraints({ "@0 <= Owner", "@1 <= pub" })
    public static Ciphertext encrypt(Key k, byte[] bytes) {
    	Result<Ciphertext> encryptResult = CryptoHelper.doAESEncryption(k, bytes);
    	if(encryptResult.isSuccess())
    		return encryptResult.getObject();
    	else
    		throw new RuntimeException("Fehler beim Verschlüsseln!", encryptResult.getException());
    }

	@Constraints({ "@0 <= Owner", "@1 <= pub", "Owner <= @ret" })
    public static byte[] decrypt(Key k, Ciphertext ciphertext) {
    	Result<Ciphertext> decryptResult = CryptoHelper.doAESDecryption(k, ciphertext);
    	if(decryptResult.isSuccess())
    		return decryptResult.getObject().encText;
    	else
    		throw new RuntimeException("Fehler beim Entschlüsseln!", decryptResult.getException());
    }
}
