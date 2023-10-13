package com.dedup;

import java.net.Socket;
import java.util.Random;
import java.io.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static int C_id = 0;
    private static char[] C;

    public static void main(String[] args)
    {
        LoggerManager.logInfo("client run");
    }

    public byte[] clientReq(char[] M, byte[] IV_1, byte[] IV_2)
    {
        // Choose random C_id from [1, CNUM]
        Random random = new Random();
        C_id = random.nextInt(Init.getCNUM()) + 1;

        // K = H(IV_1 || M)
        byte[] concatArr_1 = concatByteChar(IV_1, M);
        byte[] K = Init.h(concatArr_1);

        // t = H(IV_2 || M)
        byte[] concatArr_2 = concatByteChar(IV_2, M);
        byte[] t = Init.h(concatArr_2);

        // C = Enc(K, M)
        C = Init.enc(K, M);

        ///// send 't' to server /////
        return t;
    }

    public void clientResponse(char Server_R)
    {
        if(Server_R == 'c')
        {
            LoggerManager.logInfo("[+] received 'Check T'");

            // T = H(IV_1 || C)
            byte[] concatArr = concatByteChar(Init.IV_1, C);
            byte[] T = Init.h(concatArr);

            ///// send 'T' to server /////
        }
        else if (Server_R == 'u')
        {
            LoggerManager.logInfo("[+] received 'Upload'");

            sendC(C);

            ///// send 'C' && 'C_id" to server /////
        }
        // duplicate
        else if (Server_R == 'd')
        {
            LoggerManager.logInfo("[+] received 'Duplicate'");

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
                LoggerManager.logInfo("[+] Sent 'C' to the server: " + userInputMessage);
            }

            String message;
            while ((message = in.readLine()) != null)
            {
                LoggerManager.logInfo("[+] Received from server: " + message);
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