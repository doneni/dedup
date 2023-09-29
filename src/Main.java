public class Main
{
    public static void main(String[] args)
    {
        LoggerManager.logInfo("Hello");
        Init_Setup init = new Init_Setup();
        init.H("Hello World");
        LoggerManager.logInfo("[*] Init_Setup::H done...");


    }
}