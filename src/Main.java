public class Main
{
    public static void main(String[] args)
    {
        Init_Setup init = new Init_Setup();

//        String ret = init.h("Hello World");
//        System.out.println("SHA256: " + ret);
//        LoggerManager.logInfo("[*] Init_Setup::h done...");

        /*
        String encryptedText = init.enc("Hello World");
        String decryptedText = init.dec(encryptedText);

        System.out.println("Encrypted: " + encryptedText);
        System.out.println("Decrypted: " + decryptedText);

        System.out.println(init.iv_1);
        System.out.println(init.iv_2);

        System.out.println(init.len);
        System.out.println(init.cNum);
        System.out.println(init.num);
         */

        Client c = new Client();

    }
}