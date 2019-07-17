import java.io.*;
import java.net.*;
import java.*;
import java.util.Scanner;
import java.util.Random;

public class client {
    private ClientSideConnection csc;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 25565);
            SendThread sendThread = new SendThread(socket);
            Thread thread = new Thread(sendThread);
            thread.start();
            ReceiveThread receiveThread = new ReceiveThread(socket);
            Thread threadTwo = new Thread(receiveThread);
            threadTwo.start();

        } catch (Excpetion e) {
            System.out.println(e.getMessage());
        }
    }





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
