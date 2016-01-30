/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmail;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author Tobi
 */
public class AES {

    public static Key getNewKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance("AES").generateKey();
    }

    public static Ciphertext encrypt(Key k, byte[] bytes) {
        Ciphertext ciphertext = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted = cipher.doFinal(bytes);
            ciphertext = new Ciphertext(encrypted, cipher.getIV());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }	
        return ciphertext;
    }

    public static byte[] decrypt(Key k, Ciphertext ciphertext) {
        byte[] decrypted = null;
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(ciphertext.iv));
            decrypted = cipher.doFinal(ciphertext.encText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return decrypted;
    }
}
