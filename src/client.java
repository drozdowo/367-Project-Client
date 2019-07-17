import java.io.*;
import java.net.*;
import java.*;
import java.nio.Buffer;
import java.util.Scanner;
import java.util.Random;

public class client {


    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 25565);
            SendThread sendThread = new SendThread(socket);
            Thread thread = new Thread(sendThread);
            thread.start();
            ReceiveThread receiveThread = new ReceiveThread(socket);
            Thread threadTwo = new Thread(receiveThread);
            threadTwo.start();

        } catch (Exception e) {System.out.println(e.getMessage());}
    }

    static class ReceiveThread implements Runnable {
        Socket socket = null;
        BufferedReader receive = null;

        public ReceiveThread(Socket socket) {
            this.socket = socket;

        }

        public void run() {
            try {
                byte[] buffer = new byte[4096];
                int length = -1;
                System.out.println(this.socket.getInputStream() == null);
                while ((length = this.socket.getInputStream().read(buffer)) > -1){
                    String read = new String(buffer, 0 ,length);
                    System.out.println("read: " + read);
                }
            } catch(IOException e) { e.printStackTrace();}
        }
    }

    static class SendThread implements Runnable {
        Socket socket = null;
        PrintWriter print = null;
        BufferedReader input = null;

        public SendThread(Socket socket)  {
            this.socket = socket;
        }

        public void run() {
            try {
                if(socket.isConnected())
                {
                    System.out.println("Client connected to" + socket.getInetAddress() + " on port " + socket.getPort());
                    this.print = new PrintWriter(socket.getOutputStream(), true);
                    while(true) {
                        System.out.print(">> ");
                        input = new BufferedReader(new InputStreamReader(System.in));
                        String ToServerMessage = null;
                        ToServerMessage = input.readLine();
                        this.print.println(ToServerMessage);
                        this.print.flush();

                        if (ToServerMessage.equals("EXIT")) break;
                    }
                    socket.close();
                }
            } catch(Exception e) { System.out.println(e.getMessage()); }
        }
    }
}


/*
 try {
                receive = new BufferedInputStream(InputStreamReader(this.socket.getInputStream());

                String messageReceived = null;

                while ((messageReceived = receive.readLine()) != null) {
                    System.out.println("From server: " + messageReceived);
                }
            } catch(IOException e) { e.printStackTrace();}
*/





//   public client() {
//
//   }
//
//   public void serverConnection() {
//     csc = new ClientSideConnection();
//   }
//
//   private class ClientSideConnection implements Runnable{
//     private Socket socket;
//     private DataInputStream dis;
//     private DataOutputStream dos;
//     private BufferedReader stdIn;
//
//     private int hp;
//     private int dmg;
//
//     public ClientSideConnection() {
//       hp = 100;
//       System.out.println("~~Client~~");
//       try {
//         socket = new Socket("localhost", 25565);
//         System.out.println("You are connected to the host.");
//         dis = new DataInputStream(socket.getInputStream());
//         dos = new DataOutputStream(socket.getOutputStream());
//         stdIn = new BufferedReader(new InputStreamReader(System.in));
//         /*Setup pokemon choices, set up hp too. for testing, hp set to 100.*/
//
//       } catch (IOException ex) {System.out.println("IO Exception in ClientSideConnection."); }
//     }
//
//     public void run() {
//       String fromServer;
//       String fromClient;
//       while (true) {
//         try {
//           if ((fromServer = stdIn.readLine()) != null) System.out.println(fromServer + "(from server).");
//         } catch (IOException e) {System.out.println("IO Exception from run().");}
//       }
//     }
//   }
//   public static void main(String[] args) {
//     client client = new client();
//     client.serverConnection();
//   }
// }
