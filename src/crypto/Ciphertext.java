package crypto;

/**
 *
 * @author Tobi
 */
public class Ciphertext {

    public final byte[] encText;	// encrypted string
    public final byte[] iv;	// initialization vector

    public Ciphertext(byte[] encText, byte[] iv) {
        this.encText = encText;
        this.iv = iv;
    }

    public Ciphertext(byte[] encText) {
        this.encText = encText;
        this.iv = null;
    }

}
