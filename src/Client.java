import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDR = "localhost";
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args)
    {
        new Thread(Client::startClient).start();
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
}
