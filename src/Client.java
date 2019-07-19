import java.awt.desktop.SystemSleepEvent;
import java.io.IOException;
import java.util.Scanner;

public class Client {

    private String playerName;
    private ServerConnection serverConnection;
    private ReadThread readThread;
    private Thread readingThread;
    private static int MAX_CONNECTION_ATTEMPTS = 10;
    private static int THREAD_DELAY = 50; //in msec
    private enum PLAYER_STATE { NOT_CONNECTED, SUBMITTED_NAME, PLAYER_TURN, WAITING}
    private PLAYER_STATE myState;

    public Client(String playerName){
        this.playerName = playerName;
        this.myState = PLAYER_STATE.NOT_CONNECTED;
    }

    public boolean doConnection(int port){
        try {
            this.serverConnection = new ServerConnection(port);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        if (this.serverConnection != null){
            //We have a connection here, so we'll broadcast to the server our name:
            this.myState = PLAYER_STATE.SUBMITTED_NAME;
            sendMessageToServer("NAME_REG " + this.playerName);
            return true;
        }
        return false;
    }

    public void setUpThreads() {
        try {
            System.out.println("Starting reading thread...");
            this.readThread = new ReadThread(this.serverConnection.getInputStream(), this);
            this.readingThread = new Thread(this.readThread);
            this.readingThread.start();
            System.out.println("Reading thread started...");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void onRecieveServerMessage(String serverMessage){
        //don't want to implement serializable classes, so we'll decode it based on the text
        //every message from the server will be in two parts:
        // MESSAGE OPTION
        //ie:   DAMAGE <damage> -> decrease our life
        //      READY <pid> -> server is ready for our move
        //      READY_ALL 0 -> game is ready
        String message = serverMessage.split(" ")[0];
        String option = serverMessage.split(" ")[1];
        this.stateHandler(message, option);
    }

    private void stateHandler(String message, String options){
        System.out.println("|||StateHandler|||");
        System.out.println("curState: " + this.myState + " | Input: " + message + " " + options);
        //Here we'll read in server messages based on two things:
        //what STATE we're in, and the INPUT
        if (this.myState.equals(PLAYER_STATE.NOT_CONNECTED)){
            //Waiting to connect
            if (message.equals("ALL_READY")){
                this.myState = PLAYER_STATE.WAITING; //Waiting on server
                return;
            } else if (message.equals("YOUR_TURN")){
                this.myState = PLAYER_STATE.PLAYER_TURN;
                System.out.println("=== Round " + options + "===");
                System.out.println("=== Your Turn! ===");
            }
        }
        else if (this.myState.equals(PLAYER_STATE.PLAYER_TURN)){
            //it's our turn. Block thread here, take input, submit data, etc
            if (message.equals("SEND_TURN")){
                this.myState = PLAYER_STATE.WAITING;
                sendMessageToServer(message + " " + options);
            }
        }
        else if (this.myState.equals(PLAYER_STATE.SUBMITTED_NAME)){
            //Submitted our name, waiting for READY_ALL
            if (message.equals("NAME_ACCEPT")){
                this.myState = PLAYER_STATE.WAITING;
            }
        }
        else if (this.myState.equals(PLAYER_STATE.WAITING)){
            //Waiting for our turn, can give player input here? will need to
            //interrupt this thread when we get reply from server...
            if (message.equals("YOUR_TURN")){
                this.myState = PLAYER_STATE.PLAYER_TURN;
                System.out.println("=== Round " + options + " ===");
                System.out.println("=== Your Turn! ===");
                handleMyTurn();
            }
        }
    }

    public void handleMyTurn(){
        System.out.println("================");
        System.out.println("1: ATTACK ");
        System.out.println("2: POKEMON");
        System.out.println("3: ITEMS");
        System.out.println("----------------");
        System.out.println("Active Pokemon: PIKACHU");
        System.out.println("HP: 100");
        System.out.println("================");
        Scanner input = new Scanner(System.in);
        System.out.println("What will you do?: ");
        String myMove = input.nextLine();
        if (myMove.equals("1")){
            System.out.println("================");
            System.out.println("1. QUICK ATTACK ");
            System.out.println("2. THUNDERBOLT ");
            System.out.println("3. TAIL WHIP ");
            System.out.println("0. BACK");
            System.out.println("================");
            System.out.println("What will you do?: ");
            String myChoice = input.nextLine();
            this.stateHandler("SEND_TURN",myMove + "_" + myChoice);
            return;
        }

    }

    public void sendMessageToServer(String msg){
        try {
            System.out.println("Sending msg to server: " + msg);
            this.serverConnection.getOutputStream().write(msg.getBytes());
            this.serverConnection.getOutputStream().flush();
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
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