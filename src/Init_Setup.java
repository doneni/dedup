import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Init_Setup
{
    public static final int num = 10000;
    public static final int len = 1000000; // (bytes) maximum length of files
    public static final int cNum = 100;
    public static final String iv_1 = generateRandomString(16);
    public static final String iv_2 = generateRandomString(16);
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String key = "THIS_IS_AES_PRIVATE_KEY_FOR_TEST";
    private static final String iv = key.substring(0, 16);

    public String h(String plainText)
    {
        try
        {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(plainText.getBytes());
//            System.out.println("hash" + hash.toString());

//            return hash;
            /* hex to hexString */
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash)
            {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            LoggerManager.logInfo("[*] hash operation done");
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerManager.logError("[-] hash operation failed", e);
        }
        return null;
    }

    public String enc(String plainText)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(key.substring(0,16).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

            LoggerManager.logInfo("[*] AES256 encryption done");

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e)
        {
            e.printStackTrace();
            LoggerManager.logError("[*] AES256 encryption failed", e);
        }
        return null;
    }

    public String dec(String encryptedText)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            LoggerManager.logInfo("[*] AES256 decryption done");

            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e)
        {
            e.printStackTrace();
            LoggerManager.logError("[*] AES256 decryption failed", e);
        }
        return null;
    }

    private static String generateRandomString(int len)
    {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[len];
        secureRandom.nextBytes(randomBytes);

        return Base64.getEncoder().encodeToString(randomBytes);
    }
}
