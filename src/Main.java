import java.nio.ByteBuffer;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Client c = new Client();

        LoggerManager.logInfo("[*] Client_Req Test...");
        char[] datafile = {'H', 'e', 'l', 'l', 'o'};
        byte[] tt = c.Client_Req(datafile, Init_Setup.IV_1, Init_Setup.IV_2);
        System.out.print("Target t (" + tt.length +"bytes): ");
        for (byte b : tt)
            System.out.print(String.format("%02X ", b));
        System.out.println();

        LoggerManager.logInfo("[*] Client_Response Test...");
        LoggerManager.logInfo("c");
        c.Client_Response('c');
//        LoggerManager.logInfo("u");
//        c.Client_Response('u');
//        LoggerManager.logInfo("d");
//        c.Client_Response('d');



        Server s = new Server();

        LoggerManager.logInfo("[*] Server_Init Test...");
        s.Server_Init();

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

        s.SearchList[0] = data0;

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

        s.SearchList[1] = data1;

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

        s.SearchList[2] = data2;

        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < s.SearchList[j].length; i++)
                System.out.print(s.SearchList[j][i] + " ");
            System.out.println();
        }

        s.file_counter = 3;

        LoggerManager.logInfo("[*] Server_Search Test...");

        byte[] ttt = new byte[32];
        for (int i = 0; i < 32; i++) {
            ttt[i] = (byte) (i + 3);
        }

        System.out.print("ttt: ");
        for (byte b : ttt)
            System.out.print(b + " ");
        System.out.println();

        byte[] TT = s.Server_Search(ttt);

//        System.out.print("TT: ");
//        for (byte b : TT)
//            System.out.print(b + " ");
//        System.out.println();

        LoggerManager.logInfo("[*] Server_Upload Test...");

        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < s.SearchList[j].length; i++)
                System.out.print(s.SearchList[j][i] + " ");
            System.out.println();
        }

        s.Server_Upload(ttt, c.C, c.C_id);
        LoggerManager.logInfo("Upload");

        for (int j = 0; j < 4; j++)
        {
            for (int i = 0; i < s.SearchList[j].length; i++)
                System.out.print(s.SearchList[j][i] + " ");
            System.out.println();
        }



    }
}