package com.dedup.tests;

import com.dedup.Server;
import com.dedup.Client;
import com.dedup.Init;
import com.dedup.LoggerManager;
import com.dedup.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class ServerTest
{
    private static final int PORT = 1234;
    private static final int MAX_CLIENTS = Init.getNUM();
    private static int file_counter = 0;
    private static byte[][] SearchList;

    public static void main(String[] args)
    {
        LoggerManager.logInfo("[+] server run");

        Client c = new Client();
        Server s = new Server();
        s.serverInit();

        // load sample file
        char[] file = getFileChars(new String("resource/jabberwocky.txt"));

        // upload file
        byte[] t = c.clientReq(file, Init.IV_1, Init.IV_2);

        ///// receive to from Client /////

        // server check file
        s.serverCheck(t);
    }

    public void serverInit()
    {
        // Prepare SearchList : NUM * [256bit || 256 bit || int || int]
        int numRows = 32 * 2 + 4 * 2;
        int numCols = Init.getNUM();

        SearchList = new byte[numRows][numCols];

        // set file_counter 0
        file_counter = 0;
    }

    public void serverCheck(byte[] t)
    {
        byte[] T_ = serverSearch(t);
        byte[] T = {};
        char[] C = {};
        int C_id = 0;
        char Server_R = '\0';

        if(T_ == null)
        {
            LoggerManager.logInfo("Server_R: u");
            Server_R = 'u';

            ///// send 'Server_R' to client /////

            recvC(Server_R);
            ///// receive 'C' && 'C_id' from client /////

            serverUpload(t, C, C_id);
        }
        else
        {
            LoggerManager.logInfo("Server_R: c");
            Server_R = 'c';

            ///// send 'Server_R' to client /////

            ///// receive 'T' from client /////

            if(T != T_)
            {
                LoggerManager.logInfo("Server_R: u");
                Server_R = 'u';

                ///// send 'Server_R' to client /////

                recvC(Server_R);
                ///// receive 't' && 'C' && 'C_id' from client /////

                serverUpload(t, C, C_id);
            }
            else
            {
                LoggerManager.logInfo("Server_R: d");
                Server_R = 'd';

                ///// send 'Server_R' to client /////
            }
        }
    }

    public byte[] serverSearch(byte[] t)
    {
        // Binary search with t in SearchList
        int left = 0;
        int right = file_counter - 1;

        while(left <= right)
        {
            int mid = left + (right - left) / 2;
            int cmp = compare(SearchList[mid], t);

            if (cmp == 0){
                byte[] T = new byte[32];
                System.arraycopy(SearchList[mid], 32, T, 0, 32);
                return T;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        LoggerManager.logInfo("[+] not found");
        return null;
    }

    public void serverUpload(byte[] t, char[] C, int C_id) {
        // T = H(IV_1 || C)
        byte[] concatArr = concatByteChar(Init.IV_1, C);
        byte[] T = Init.h(concatArr);

        byte[] data = new byte[72];

        System.arraycopy(t, 0, data, 0, 32);
        System.arraycopy(T, 0, data, 32, 32);
        byte[] fileCounterBytes = ByteBuffer.allocate(4).putInt(file_counter).array();
        byte[] CidBytes = ByteBuffer.allocate(4).putInt(C_id).array();
        System.arraycopy(fileCounterBytes, 0, data, 64, 4);
        System.arraycopy(CidBytes, 0, data, 68, 4);

        int idx = 0;

        for (int i = 0; i < file_counter; i++)
        {
            if (compare(SearchList[i], t) <= 0)
                idx++;
            else
                break;
        }

        for (int i = file_counter - 1; i > idx; i--)
            SearchList[i] = SearchList[i - 1];
        SearchList[idx] = data;

        file_counter++;
    }

    private static int compare(byte[] a, byte[] b)
    {
        for (int i = 0; i < b.length; i++)
        {
            int diff = a[i] - b[i];
            if (diff != 0)
                return diff;
        }
        return 0;
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

    private void recvC(char Server_R)
    {
        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            LoggerManager.logInfo("[+] Server started. Waiting for a client...");

            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                LoggerManager.logInfo("[*] Client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread handlerThread = new Thread(clientHandler);
                handlerThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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