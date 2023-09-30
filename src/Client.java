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

    public static void main(String[] args)
    {
//        new Thread(Client::startClient).start();
        Client c = new Client();

        char[] datafile = {'H', 'e', 'l', 'l', 'o'};

        byte[] t = c.Client_Req(datafile, Init_Setup.IV_1, Init_Setup.IV_2);
        System.out.print("Target t (" + t.length +"bytes): ");
        for (byte b : t)
            System.out.print(String.format("%02X ", b));
        System.out.println();

        // send t to server ////////////////////////////////





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

        return t;
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

    public void Client_Response(char[] t, char[] T)
    {

    }
}
