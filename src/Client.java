import java.net.Socket;
import java.util.Random;
import java.io.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static int C_id = 0;
    private static char[] C;

    public byte[] Client_Req(char[] M, byte[] IV_1, byte[] IV_2)
    {
        // Choose random C_id from [1, CNUM]
        Random random = new Random();
        C_id = random.nextInt(Init_Setup.getCNUM()) + 1;

        // K = H(IV_1 || M)
        byte[] concatArr_1 = concatByteChar(IV_1, M);
        byte[] K = Init_Setup.H(concatArr_1);

        // t = H(IV_2 || M)
        byte[] concatArr_2 = concatByteChar(IV_2, M);
        byte[] t = Init_Setup.H(concatArr_2);

        // C = Enc(K, M)
        C = Init_Setup.Enc(K, M);

        ///// send 't' to server /////
        return t;
    }

    public void Client_Response(char Server_R)
    {
        if(Server_R == 'c')
        {
            System.out.println("[*] received 'Check T'");

            // T = H(IV_1 || C)
            byte[] concatArr = concatByteChar(Init_Setup.IV_1, C);
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

            sendC(C);

            ///// send 'C' && 'C_id" to server /////
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

    private static void sendC(char[] C)
    {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send 'C' to the server
            String userInputMessage = String.valueOf(C);
            if (userInputMessage != null)
            {
                out.println(userInputMessage);
                System.out.println("Sent 'C' to the server: " + userInputMessage);
            }

            String message;
            while ((message = in.readLine()) != null)
            {
                System.out.println("Received from server: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int getC_id()
    {
        return C_id;
    }
}