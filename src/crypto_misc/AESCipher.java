package crypto_misc;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 7:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class AESCipher implements IEncoder {
    private static final String CRYPTO_ALG = "AES";
    private static final String CRYPTO_MODE = "CTR";
    private static final String PADDING = "NoPadding";
    private Cipher crypter;
    private Cipher decrypter;

    public AESCipher(SecretKey key) throws Exception {
        String initStr = CRYPTO_ALG + "/" + CRYPTO_MODE + "/" + PADDING;
        crypter = Cipher.getInstance(initStr);
        decrypter = Cipher.getInstance(initStr);
        crypter.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));
        decrypter.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));

    }

    public byte[] encrypt(byte[] plain) {
        try {
            return crypter.doFinal(plain);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(byte[] cipher) {
        try {
            return decrypter.doFinal(cipher);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] enc(byte[] plain) {
        return encrypt(plain);
    }
}
