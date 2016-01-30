/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmail;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Tobi
 */
public class RSA {
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException
    {
    		KeyPair keyPair = null;
    		try {
    			keyPair = KeyPairGenerator.getInstance("RSA",new org.bouncycastle.jce.provider.BouncyCastleProvider()).generateKeyPair();
    		} catch (NullPointerException ignore) {} // thrown by getInstance()...
    		return keyPair;
    }
}
