import java.io.*;
import java.net.*;
import java.util.Random;




public class Player {
	private int pid; //i know, stop.
	private int otherpid;

	private int[] values;
	private int TURN;
	private int points; //testing
	private int opponentPoints; //testing

	private ClientSideConnection csc;

	public Player() {
		points = 0;
		opponentPoints = 0;
	}


	public void connectToServer() {
		csc = new ClientSideConnection();
	}


	//Client acceptConnection inner class

	private class ClientSideConnection {
		private Socket socket;
		private DataInputStream input;
		private DataOutputStream output;



		public ClientSideConnection() {
			int HP = 100;
			int dmg;
			Random rand = new Random();


			System.out.println("~~~Client~~~");
			try {
				socket = new Socket("localhost", 51467);
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
				pid = input.readInt();
				System.out.println("Player # " + pid + " connected to the server.");

				do {

					dmg = rand.nextInt(10) + 1;
					HP -= dmg;
					output.writeInt(dmg);
					System.out.println("Player #" + pid + " did " + dmg +"dmg!");
				} while (HP != 0 && TURN == pid);
			} catch(IOException ex) { System.out.println("IO Exception from csc constructor."); }
		}
	}


	public static void main(String[] args) {
		Player player = new Player();
		player.connectToServer(); //connect to server
	}
}












// public class client {
//
// 	private Socket socket = null;
//
// 	public client() {
// 		String address = "137.207.82.52";
// 		int port = 25565;
// 		try {
//
// 			socket = new Socket(address, port); //attached socket to transport section
//
// 			System.out.println("Connected! - test");
//
// 			String line; // holds input stream for testing
//
// 			DataInputStream input = new DataInputStream(socket.getInputStream());
//
//
// 			while(true) {
// 					line = input.readUTF();
// 					if (line.equals("")) {
// 						continue;
// 					}
// 					else {
// 						System.out.println(line);
// 					}
// 				}
//
//
// 		} catch(Exception e) {
//
// 		}
//
//
//
//
// 	}
// 	public static void main(String args[]) {
// 		client c = new client();
// 	}
// }
