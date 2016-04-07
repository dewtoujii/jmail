package crypto;

import java.io.Serializable;

import de.unifreiburg.cs.proglang.jgs.support.Constraints;
import de.unifreiburg.cs.proglang.jgs.support.Effects;
import de.unifreiburg.cs.proglang.jgs.support.Sec;

/**
 *
 * @author Tobi
 */
public class Ciphertext implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8859764714808008859L;
	@Sec("pub")
	public final byte[] encText;	// encrypted string
	@Sec("pub")
    public final byte[] iv;	// initialization vector

	@Constraints({"@0 <= pub", "@1 <= pub"})
	@Effects({"pub"})
    public Ciphertext(byte[] encText, byte[] iv) {
        this.encText = encText;
        this.iv = iv;
    }

	@Constraints({"@0 <= pub"})
	@Effects({"pub"})
    public Ciphertext(byte[] encText) {
        this.encText = encText;
        this.iv = null;
    }

}
