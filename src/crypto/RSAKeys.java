package crypto;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import exceptionhandling.CryptoHelper;
import exceptionhandling.Result;

public class RSAKeys {
	private static final String KEYPAIR_FILE = "rsa_keypair";

	private static KeyPair generateKeyPair() {
		Result<KeyPair> keyPairResult = CryptoHelper.generateRSAKeyPair();
		if (keyPairResult.isSuccess())
			return keyPairResult.getObject();
		else
			throw new RuntimeException("Fehler beim Generieren des KeyPairs!", keyPairResult.getException());
	}

	/*
	 * private static KeyPair generateKeyPair() throws NoSuchAlgorithmException
	 * { KeyPair keyPair = null; keyPair = KeyPairGenerator.getInstance("RSA",
	 * new org.bouncycastle.jce.provider.BouncyCastleProvider())
	 * .generateKeyPair(); return keyPair; }
	 */

	private static KeyPair getKeyPair() {
		File file = new File(KEYPAIR_FILE);
		KeyPair keyPair;
		if (file.exists()) {
			ObjectInputStream ois;
			Result<ObjectInputStream> oisResult = CryptoHelper.createObjectInputStream(file);
			if (oisResult.isSuccess())
				ois = oisResult.getObject();
			else
				throw new RuntimeException("Fehler beim Erzeugen des ObjectInputStreams!", oisResult.getException());

			Result<Object> readObjectResult = CryptoHelper.readObject(ois);
			if (readObjectResult.isSuccess())
				keyPair = (KeyPair) readObjectResult.getObject();
			else
				throw new RuntimeException("Fehler beim Abrufen des KeyPairs!", readObjectResult.getException());

			Result<Void> closeResult = CryptoHelper.closeObjectInputStream(ois);
			if (!closeResult.isSuccess())
				throw new RuntimeException("Fehler beim Schlieﬂen des Streams!", closeResult.getException());
		} else {
			keyPair = generateKeyPair();
			ObjectOutputStream oos;
			Result<ObjectOutputStream> oosResult = CryptoHelper.createObjectOutputStream(file);
			if (oosResult.isSuccess())
				oos = oosResult.getObject();
			else
				throw new RuntimeException("Fehler beim Erzeugen des ObjectOutputStreams!", oosResult.getException());

			Result<Void> writeAndCloseResult = CryptoHelper.writeObjectAndcloseObjectOutputStream(keyPair, oos);
			if (!writeAndCloseResult.isSuccess())
				throw new RuntimeException("IOException!", writeAndCloseResult.getException());
		}
		return keyPair;
	}

	/*
	 * private static KeyPair getKeyPair() throws FileNotFoundException,
	 * IOException, ClassNotFoundException { File file = new File(KEYPAIR_FILE);
	 * KeyPair keyPair; if(file.exists()) { ObjectInputStream oin = new
	 * ObjectInputStream( new BufferedInputStream(new
	 * FileInputStream(KEYPAIR_FILE))); keyPair = (KeyPair) oin.readObject();
	 * oin.close(); } else { keyPair = generateKeyPair(); ObjectOutputStream
	 * oout = new ObjectOutputStream( new BufferedOutputStream(new
	 * FileOutputStream(KEYPAIR_FILE))); oout.writeObject(keyPair);
	 * oout.close(); } return keyPair; }
	 */

	public static PublicKey getPublicKey() {
		KeyPair keyPair = null;
		// try {
		keyPair = getKeyPair();
		// } catch (ClassNotFoundException | NoSuchAlgorithmException |
		// IOException e) {
		// }
		return (keyPair != null) ? keyPair.getPublic() : null;
	}

	public static PrivateKey getPrivateKey() {
		KeyPair keyPair = null;
		// try {
		keyPair = getKeyPair();
		// } catch (ClassNotFoundException | NoSuchAlgorithmException |
		// IOException e) {
		// }
		return (keyPair != null) ? keyPair.getPrivate() : null;
	}
}
