import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Init_Setup {
    public void H(String plainText)
    {
        try
        {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(plainText.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash)
            {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            LoggerManager.logInfo("SHA-256 hash: " + hexString.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
