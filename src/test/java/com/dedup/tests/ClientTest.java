package com.dedup.tests;

import com.dedup.LoggerManager;
import com.dedup.Client;
import com.dedup.Init;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ClientTest {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static int C_id = 0;
    private static char[] C;

    public static void main(String[] args)
    {
        LoggerManager.logInfo("client run");

        Client c = new Client();

        // load sample file
        char[] file = getFileChars(new String("resource/jabberwocky.txt"));

        // upload file (clientReq)
        LoggerManager.logInfo("clientReq() test");
        byte[] t = c.clientReq(file, Init.IV_1, Init.IV_2);
        System.out.print("t: ");
        for (byte b: t)
            System.out.print(b + " ");
        System.out.println("");

        ///// Send t to Server /////



        LoggerManager.logInfo("clientResponse() test");

        ///// Receive Server_R(option: 'c', 'u', 'd') from Server /////
        char Server_R = 'c';
        c.clientResponse(Server_R);
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

    public void clientResponse(char Server_R) throws Exception
    {
        if(Server_R == 'c')
        {
            LoggerManager.logInfo("[+] received 'Check T'");

            // T = H(IV_1 || C)
            byte[] concatArr = concatByteChar(Init.IV_1, C);
            byte[] T = Init.h(concatArr);

            System.out.print("[+] Target T (" + T.length +"bytes): ");
            for (byte b : T)
                System.out.print(String.format("%02X ", b));
            System.out.println();

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

    static char[] getFileChars(String filePath)
    {
        try {
            Path path = Paths.get(filePath);

            byte[] fileBytes = Files.readAllBytes(path);
            char[] fileChars = new String(fileBytes, StandardCharsets.UTF_8).toCharArray();

            return fileChars;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}