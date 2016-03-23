package crypto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAKeys {
	private static final String KEYPAIR_FILE = "rsa_keypair";
	
	private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPair keyPair = null;
		try {
			keyPair = KeyPairGenerator.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider())
					.generateKeyPair();
		} catch (NullPointerException ignore) {
		} // thrown by getInstance()...
		
		
		return keyPair;
	}
	
	
	private static KeyPair getKeyPair() throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
		File file = new File(KEYPAIR_FILE);
		KeyPair keyPair;
		if(file.exists()) {
			ObjectInputStream oin = new ObjectInputStream(
		            new BufferedInputStream(new FileInputStream(KEYPAIR_FILE)));
			keyPair = (KeyPair) oin.readObject();
			oin.close();
		}
		else {
			keyPair = generateKeyPair();
			ObjectOutputStream oout = new ObjectOutputStream(
		            new BufferedOutputStream(new FileOutputStream(KEYPAIR_FILE)));
			oout.writeObject(keyPair);
			oout.close();
		}
		return keyPair;
	}
	
	public static PublicKey getPublicKey() {
		KeyPair keyPair = null;
		try {
			keyPair = getKeyPair();
		} catch (ClassNotFoundException | NoSuchAlgorithmException | IOException e) {
		}
		return (keyPair != null) ? keyPair.getPublic() : null;
	}
	
	public static PrivateKey getPrivateKey() {
		KeyPair keyPair = null;
		try {
			keyPair = getKeyPair();
		} catch (ClassNotFoundException | NoSuchAlgorithmException | IOException e) {
		}
		return (keyPair != null) ? keyPair.getPrivate() : null;
	}
}
