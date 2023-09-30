import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDR = "localhost";
    private static final int SERVER_PORT = 1234;
    public static int C_id = 0;
    public static char[] C;

    public static void main(String[] args)
    {
//        new Thread(Client::startClient).start();
    }

    public byte[] Client_Req(char[] M, byte[] IV_1, byte[] IV_2)
    {
        // Choose random C_id from [1, CNUM]
        Random random = new Random();
        this.C_id = random.nextInt(Init_Setup.CNUM) + 1;

        // K = H(IV_1 || M)
        byte[] concatArr_1 = concatByteChar(IV_1, M);
        byte[] K = Init_Setup.H(concatArr_1);

        // t = H(IV_2 || M)
        byte[] concatArr_2 = concatByteChar(IV_2, M);
        byte[] t = Init_Setup.H(concatArr_2);

        // C = Enc(K, M)
        char[] C = Init_Setup.Enc(K, M);
        this.C = C;

        ///// send 't' to server /////
        return t;
    }

    public void Client_Response(char Server_R)
    {
        if(Server_R == 'c')
        {
            System.out.println("[*] received 'Check T'");

            // T = H(IV_1 || C)
            byte[] concatArr = concatByteChar(Init_Setup.IV_1, this.C);
            byte[] T = Init_Setup.H(concatArr);

            System.out.print("Target T (" + T.length +"bytes): ");
            for (byte b : T)
                System.out.print(String.format("%02X ", b));
            System.out.println();

            ///// send 'T' to server /////
        }
        else if (Server_R == 'u')
        {
            System.out.println("[*] received 'Upload'");

            ///// send 'C' && 'C_id" to server ///// -> TODO
        }
        // duplicate
        else if (Server_R == 'd')
        {
            System.out.println("[*] received 'Duplicate'");

            // end of process
            int status = 0;
            System.exit(status);
        }
    }

    private static void startClient()
    {
        try(
                Socket socket = new Socket(SERVER_ADDR, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))

        ) {
            out.println("Hello, server!");

            String response = in.readLine();
            System.out.println("Server Response: " + response);

        } catch (Exception e) {
            e.printStackTrace();
            LoggerManager.logError("[-] client error", e);
        }
    }

    private byte[] concatByteChar(byte[] byteArr, char[] charArr)
    {
        byte[] concatArr = new byte[byteArr.length + charArr.length];

        int idx = 0;
        for(byte b : byteArr)
            concatArr[idx++] = b;
        for(char c : charArr)
        {
            concatArr[idx++] = (byte) c;
        }

        return concatArr;
    }
}
