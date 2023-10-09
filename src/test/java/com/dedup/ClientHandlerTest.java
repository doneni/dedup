package com.dedup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandlerTest implements Runnable {
    private final Socket socket;

    public ClientHandlerTest(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String clientMessage = in.readLine();
            System.out.println("[+] Received from client: " + clientMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
