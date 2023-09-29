import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays;
public class Server
{
    private static final int PORT = 1234;
    private static final int MAX_CLIENTS = Init_Setup.NUM;
    private int file_counter = 0;
    private byte[][] SearchList;

    public static void main(String[] args)
    {
        Server s = new Server();
        s.Server_Init();

        byte[] t = new byte[32];
        for (int i = 0; i < 32; i++) {
            t[i] = (byte) (i + 1);
        }

        System.out.print("t: ");
        for (byte b : t)
            System.out.print(b);
        System.out.println();

        byte[] T = s.Server_Search(t);

        System.out.print("T: ");
        for (byte b : T)
            System.out.print(b);
        System.out.println();

        /*
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
         */
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

    public void Server_Init()
    {
        // Prepare SearchList : NUM * [256bit || 256 bit || int || int]
        int numRows = 32 * 2 + 4 * 2;
        int numCols = Init_Setup.NUM;

        this.SearchList = new byte[numRows][numCols];

        // set file_counter 0
        this.file_counter = 0;

        //////////////////TEST///////////////////
        byte[] t = new byte[32];
        byte[] T = new byte[32];
        int file_counter = 123;
        int C_id = 456;
        for (int i = 0; i < 32; i++)
        {
            t[i] = (byte) i;
            T[i] = (byte) (i + 32);
        }
        byte[] fileCounterBytes = ByteBuffer.allocate(4).putInt(file_counter).array();
        byte[] CidBytes = ByteBuffer.allocate(4).putInt(C_id).array();

        byte[] data0 = new byte[72];

        System.arraycopy(t, 0, data0, 0, 32);
        System.arraycopy(T,0, data0, 32, 32);
        System.arraycopy(fileCounterBytes, 0, data0, 64, 4);
        System.arraycopy(CidBytes, 0, data0, 68, 4);

        SearchList[0] = data0;

        for (int i = 0; i < 32; i++)
        {
            t[i] = (byte) (i + 1);
            T[i] = (byte) (i + 33);
        }

        byte[] data1 = new byte[72];

        System.arraycopy(t, 0, data1, 0, 32);
        System.arraycopy(T,0, data1, 32, 32);
        System.arraycopy(fileCounterBytes, 0, data1, 64, 4);
        System.arraycopy(CidBytes, 0, data1, 68, 4);

        SearchList[1] = data1;

        for (int i = 0; i < 32; i++)
        {
            t[i] = (byte) (i + 2);
            T[i] = (byte) (i + 34);
        }

        byte[] data2 = new byte[72];

        System.arraycopy(t, 0, data2, 0, 32);
        System.arraycopy(T,0, data2, 32, 32);
        System.arraycopy(fileCounterBytes, 0, data2, 64, 4);
        System.arraycopy(CidBytes, 0, data2, 68, 4);

        SearchList[2] = data2;

        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < SearchList[j].length; i++)
                System.out.print(SearchList[j][i] + " ");
            System.out.println();
        }
    }

    public byte[] Server_Search(byte[] t)
    {
        // Binary search with t in SearchList

        int left = 0;
//        int right = SearchList.length - 1;
        int right = 2;

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
        System.out.println("not found");
        return null;
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

    public void Server_Upload(byte[] t, )
}

