import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

class ServerConnection {
    Socket serverSocket;
    private int connectionAttempts = 0;
    private static int MAX_CONNECTION_ATTEMPTS = 10;

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