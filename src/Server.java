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
    public int file_counter = 0;
    public static byte[][] SearchList;

    public static void main(String[] args)
    {
        Server s = new Server();
        s.Server_Init();

        byte[] t = new byte[32];
        for (int i = 0; i < 32; i++) {
            t[i] = (byte) (i + 3);
        }

        System.out.print("t: ");
        for (byte b : t)
            System.out.print(b);
        System.out.println();

//        byte[] T = s.Server_Search(t);
//
//        System.out.print("T: ");
//        for (byte b : T)
//            System.out.print(b);
//        System.out.println();

        char[] C = {'h', 'e', 'l', 'l', 'o'};
        s.Server_Upload(t, C, Client.C_id);

        for (int j = 0; j < 4; j++)
        {
            for (int i = 0; i < SearchList[j].length; i++)
                System.out.print(SearchList[j][i] + " ");
            System.out.println();
        }

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
    }

    public byte[] Server_Search(byte[] t)
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
        System.out.println("[*] not found");
        return null;
    }

    public void Server_Upload(byte[] t, char[] C, int C_id) {
        // T = H(IV_1 || C)
        byte[] concatArr = concatByteChar(Init_Setup.IV_1, C);
        byte[] T = Init_Setup.H(concatArr);

        byte[] data = new byte[72];

        System.arraycopy(t, 0, data, 0, 32);
        System.arraycopy(T, 0, data, 32, 32);
        byte[] fileCounterBytes = ByteBuffer.allocate(4).putInt(this.file_counter).array();
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

    public void Server_Check(byte[] t)
    {
        byte[] T_ = Server_Search(t);
        byte[] T = {};
        char[] C = {};
        int C_id = 0;
        char Server_R = '\0';

        if(T_ == null)
        {
            Server_R = 'u';

            ///// send 'Server_R' to client /////

            ///// receive 'C' && 'C_id' from client /////

            Server_Upload(t, C, C_id);
        }
        else
        {
            Server_R = 'c';

            ///// send 'Server_R' to client /////

            ///// receive 'T' from client /////

            if(T != T_)
            {
                Server_R = 'u';

                ///// send 'Server_R' to client /////

                ///// receive 't' && 'C' && 'C_id' from client /////

                Server_Upload(t, C, C_id);
            }
            else
            {
                Server_R = 'd';

                ///// send 'Server_R' to client /////
            }
        }
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
}

