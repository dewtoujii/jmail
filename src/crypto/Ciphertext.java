package crypto;

import java.io.Serializable;

/**
 *
 * @author Tobi
 */
public class Ciphertext implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8859764714808008859L;
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
