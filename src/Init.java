import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

public class Init
{
    private static final int NUM = 10000;
    private static final int LEN = 1000000; // (bytes) maximum length of files
    private static final int CNUM = 100;
    public static final byte[] IV_1 = generateRandomBytes(16);
    public static final byte[] IV_2 = generateRandomBytes(16);
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static byte[] h(byte[] plainText)
    {
        try
        {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(plainText);

            LoggerManager.logInfo("[+] hash operation done");

            return hash;
        } catch (Exception e) {
            e.printStackTrace();
            LoggerManager.logError("[-] hash operation failed", e);
        }
        return null;
    }

    public static char[] enc(byte[] key, char[] plainText)
    {
        byte[] iv = new byte[16];
        System.arraycopy(key, 0, iv, 0, 16);

        try {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.toString().getBytes(StandardCharsets.UTF_8));
            char[] encrypted = new char[encryptedBytes.length];
            for (int i = 0; i < encryptedBytes.length; i++)
                encrypted[i] = (char) (encryptedBytes[i] & 0XFF);

            LoggerManager.logInfo("[+] AES256 encryption done");

            return encrypted;
        } catch (ArrayIndexOutOfBoundsException oobe) {
            oobe.printStackTrace();
            LoggerManager.logError("[-] check key size (32 bytes)", oobe);
        } catch (Exception e)
        {
            e.printStackTrace();
            LoggerManager.logError("[-] AES256 encryption failed", e);
        }
        return null;
    }

    public static byte[] dec(char[] encrypted, char[] charKey)
    {
        byte[] key = new byte[32];
        for (int i = 0; i < 32; i++)
            key[i] = (byte) (charKey[i] & 0xFF);

        byte[] iv = new byte[16];
        System.arraycopy(key, 0, iv, 0, 16);

        try
        {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = new byte[encrypted.length];
            for (int i = 0; i < encrypted.length; i++)
                encryptedBytes[i] = (byte) (encrypted[i] & 0XFF);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            LoggerManager.logInfo("[+] AES256 decryption done");

            return decryptedBytes;
        } catch (Exception e)
        {
            e.printStackTrace();
            LoggerManager.logError("[-] AES256 decryption failed", e);
        }
        return null;
    }

    private static byte[] generateRandomBytes(int len)
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[len];
        secureRandom.nextBytes(randomBytes);

        LoggerManager.logInfo("[+] generate random bytes done");

        return randomBytes;
    }

    public static int getNUM()
    {
        return NUM;
    }

    public static int getLEN()
    {
        return LEN;
    }

    public static int getCNUM()
    {
        return CNUM;
    }
}