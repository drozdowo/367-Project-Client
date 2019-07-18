import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;

public class Client {

    private String playerName;
    private ServerConnection serverConnection;
    private ReadThread readThread;
    private Thread readingThread;
    private static int MAX_CONNECTION_ATTEMPTS = 10;
    private static int THREAD_DELAY = 500; //in msec

    public Client(String playerName){
        this.playerName = playerName;
    }

    public boolean doConnection(int port){
        try {
            this.serverConnection = new ServerConnection(port);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        if (this.serverConnection != null){
            return true;
        }
        return false;
    }

    public void setUpThreads() {
        try {
            System.out.println("Starting reading thread...");
            this.readThread = new ReadThread(this.serverConnection.getInputStream());
            this.readingThread = new Thread(this.readThread);
            this.readingThread.start();
            System.out.println("Reading thread started...");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private class ReadThread implements Runnable {
        private InputStream in;
        public ReadThread(InputStream in){
            this.in = in;
        }

        public void run() {
            try {
                while (true){
                    System.out.println("Read Thread Active...");
                    if (this.in.available() > 0){ //data available
                        System.out.println("Bytes Available!");
                        byte[] buffer = this.in.readNBytes(this.in.available());
                        int len = buffer.length;
                        System.out.println("Read data: " + new String(buffer, 0, len));
                    }
                    Thread.sleep(THREAD_DELAY);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class ServerConnection {
        Socket serverSocket;
        private int connectionAttempts = 0;
        public ServerConnection(int port) throws IOException, InterruptedException {
            System.out.println("Attempting to connect to port: " + port);
            while(!attemptServerConnection(port)){
                Thread.sleep(1000);
                System.out.println("Waiting to connect to server...");
                this.connectionAttempts++;
                if (this.connectionAttempts >= MAX_CONNECTION_ATTEMPTS){
                    System.out.println("Exceeded maximum connection attempts! Exiting now!");
                    System.exit(2);
                }
            }
        }

        public boolean attemptServerConnection(int port) throws IOException{
            try {
                this.serverSocket = new Socket("localhost", port);
            } catch (ConnectException e){
                return false;
            }
            return true;
        }

        public InputStream getInputStream() throws IOException{
            return this.serverSocket.getInputStream();
        }

        public OutputStream getOutputStream() throws IOException{
            return this.serverSocket.getOutputStream();
        }

        public boolean isConnected(){
            return this.serverSocket.isConnected();
        }
    }

    public static void main(String[] args) {
        Scanner inScan = new Scanner(System.in);
        System.out.println("Please enter your player name: ");
        String playerName = inScan.nextLine();
        System.out.println("Welcome " + playerName + "!. Connecting to server...");
        Client me = new Client(playerName);
        me.doConnection(25565);
        System.out.println("Successfully Connected!");
        me.setUpThreads();
    }
}