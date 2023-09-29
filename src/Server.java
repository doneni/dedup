import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Server
{
    private static final int PORT = 1234;
    private static final int MAX_CLIENTS = Init_Setup.num;

    public static void main(String[] args)
    {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_CLIENTS);

        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Server started. Listening on port " + PORT);

            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoggerManager.logError("[-] server error", e);
        } finally {
            executor.shutdown();
        }
    }

    private static class ClientHandler implements Runnable
    {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket)
        {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run()
        {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                String inputLine;
                while((inputLine = in.readLine()) != null)
                {
                    System.out.println("Received from Client: " + inputLine);
                    out.println("Server received: " + inputLine);
                }
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
                LoggerManager.logError("[-] clientHandler error", e);
            }
        }
    }
}

