package exceptionhandling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import crypto.Ciphertext;
import de.unifreiburg.cs.proglang.jgs.support.Constraints;

public class CryptoHelper {
	@Constraints({"Owner <= @ret"})
	public static Result<Key> generateAESKey() {
		Key key;
		try {
			key = KeyGenerator.getInstance("AES").generateKey();
		} catch (NoSuchAlgorithmException e) {
			return new Result<Key>(false, null, e);
		}
		return new Result<Key>(true, key, null);
	}

	public static Result<KeyPair> generateRSAKeyPair() {
		KeyPair keyPair;
		try {
			keyPair = KeyPairGenerator.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider())
					.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			return new Result<KeyPair>(false, null, e);
		}
		return new Result<KeyPair>(true, keyPair, null);
	}

	public static Result<Ciphertext> doAESEncryption(Key k, byte[] bytes) {
		Ciphertext ciphertext;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, k);
			byte[] encrypted = cipher.doFinal(bytes);
			ciphertext = new Ciphertext(encrypted, cipher.getIV());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			return new Result<Ciphertext>(false, null, e);
		}
		return new Result<Ciphertext>(true, ciphertext, null);
	}

	public static Result<Ciphertext> doAESDecryption(Key k, Ciphertext ciphertext) {
		byte[] decrypted;
		try {

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(ciphertext.iv));
			decrypted = cipher.doFinal(ciphertext.encText);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			return new Result<Ciphertext>(false, null, e);
		}
		return new Result<Ciphertext>(true, new Ciphertext(decrypted), null);
	}
	
	public static Result<Ciphertext> doRSAEncryption(Key key, byte[] input) {
		byte[] encryptedBytes = null;
		try {
			final Cipher rsaCipher_ = Cipher.getInstance("RSA/ECB/PKCS1Padding",
					new org.bouncycastle.jce.provider.BouncyCastleProvider());
			if (rsaCipher_ != null) {
				rsaCipher_.init(Cipher.ENCRYPT_MODE, key);

				// final byte{L}[]{L} input = s.getBytes();
				encryptedBytes = rsaCipher_.doFinal(input);
			}
		} catch(BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			return new Result<Ciphertext>(false, null, e);
		}
		return new Result<Ciphertext>(true, new Ciphertext(encryptedBytes), null);
	}

	public static Result<Ciphertext> doRSADecryption(Key key, byte[] encryptedBytes) {
		byte[] decrypted;
		Cipher rsaCipher_;
		try {
			rsaCipher_ = Cipher.getInstance("RSA/ECB/PKCS1Padding",
					new org.bouncycastle.jce.provider.BouncyCastleProvider());
			rsaCipher_.init(Cipher.DECRYPT_MODE, key);
			decrypted = rsaCipher_.doFinal(encryptedBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			return new Result<Ciphertext>(false, null, e);
		}
		return new Result<Ciphertext>(true, new Ciphertext(decrypted), null);
	}

	public static Result<Object> readObject(ObjectInputStream ois) {
		Object object;
		try {
			object = ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			return new Result<Object>(false, null, e);
		}
		return new Result<Object>(true, object, null);
	}

	public static Result<ObjectInputStream> createObjectInputStream(File file) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
		} catch (IOException e) {
			return new Result<ObjectInputStream>(false, null, e);
		}
		return new Result<ObjectInputStream>(true, ois, null);
	}

	public static Result<ObjectOutputStream> createObjectOutputStream(File file) {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		} catch (IOException e) {
			return new Result<ObjectOutputStream>(false, null, e);
		}
		return new Result<ObjectOutputStream>(true, oos, null);
	}
	
	public static Result<Void> closeObjectInputStream(ObjectInputStream ois) {
		try {
			ois.close();
		} catch (IOException e) {
			return new Result<Void>(false, null, e);
		}
		return new Result<Void>(true, null, null);
	}
	
	public static Result<Void> writeObjectAndcloseObjectOutputStream(KeyPair keyPair, ObjectOutputStream oos) {
		try {
			oos.writeObject(keyPair);
			oos.close();
		} catch (IOException e) {
			return new Result<Void>(false, null, e);
		}
		return new Result<Void>(true, null, null);
	}
}
