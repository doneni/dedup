import java.util.Arrays;
public class Main
{
    public static void main(String[] args) throws Exception
    {
        Init_Setup init = new Init_Setup();

//        String ret = init.h("Hello World");
//        System.out.println("SHA256: " + ret);
//        LoggerManager.logInfo("[*] Init_Setup::h done...");

        char[] plain = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};

        char[] encrypted = init.Enc(new String("Hello World").getBytes("UTF-8"));
        byte[] decryptedText = init.Dec(encrypted);

        System.out.println("Plain: " + Arrays.toString(plain));
        System.out.println("Encrypted: " + Arrays.toString(encrypted));
        System.out.println("Decrypted: " + Arrays.toString(decryptedText));

        Client c = new Client();

    }
}