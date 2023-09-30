import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Init_Setup
{
    public static final int NUM = 10000;
    public static final int LEN = 1000000; // (bytes) maximum length of files
    public static final int CNUM = 100;
    public static final byte[] IV_1 = generateRandomString(16);
    public static final byte[] IV_2 = generateRandomString(16);
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String key = "THIS_IS_AES_PRIVATE_KEY_FOR_TEST";
    private static final String iv = key.substring(0, 16);

    public static byte[] H(byte[] plainText)
    {
        try
        {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(plainText);

            LoggerManager.logInfo("[*] hash operation done");
            return hash;
        } catch (Exception e) {
            e.printStackTrace();
            LoggerManager.logError("[-] hash operation failed", e);
        }
        return null;
    }

    public char[] Enc(byte[] plainBytes)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(key.substring(0,16).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(plainBytes);
            char[] encrypted = new char[encryptedBytes.length];
            for(int i = 0; i < encryptedBytes.length; i++)
                encrypted[i] = (char) (encryptedBytes[i] & 0XFF);

            LoggerManager.logInfo("[*] AES256 encryption done");

            return encrypted;
        } catch (Exception e)
        {
            e.printStackTrace();
            LoggerManager.logError("[*] AES256 encryption failed", e);
        }
        return null;
    }

    public byte[] Dec(char[] encrypted)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

//            byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
            byte[] encryptedBytes = new byte[encrypted.length];
            for (int i = 0; i < encrypted.length; i++)
                encryptedBytes[i] = (byte) (encrypted[i] & 0XFF);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            LoggerManager.logInfo("[*] AES256 decryption done");

            return decryptedBytes;
        } catch (Exception e)
        {
            e.printStackTrace();
            LoggerManager.logError("[*] AES256 decryption failed", e);
        }
        return null;
    }

    private static byte[] generateRandomString(int len)
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[len];
        secureRandom.nextBytes(randomBytes);

        return randomBytes;
    }
}
