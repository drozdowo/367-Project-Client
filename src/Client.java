
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private String playerName;
    private ServerConnection serverConnection;
    private ReadThread readThread;
    private Thread readingThread;
    private static int MAX_CONNECTION_ATTEMPTS = 10;
    private static int THREAD_DELAY = 50; //in msec
    private ArrayList<Pokemon> pokemonList;
    private Pokemon myPokemon;
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
//            System.out.println("Starting reading thread...");
            this.readThread = new ReadThread(this.serverConnection.getInputStream(), this);
            this.readingThread = new Thread(this.readThread);
            this.readingThread.start();
//            System.out.println("Reading thread started...");
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

    public void onReceivePokemonList(ArrayList<Pokemon> list){
//        System.out.println("Got Pokemon: " + list.size());
        this.pokemonList = list;
        this.stateHandler("RECEIVED_POKEMON", "0");
    }

    private void stateHandler(String message, String options){
//        System.out.println("|||StateHandler|||");
//        System.out.println("curState: " + this.myState + " | Input: " + message + " " + options);
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
            return;
        }
        else if (this.myState.equals(PLAYER_STATE.PLAYER_TURN)){
            //it's our turn. Block thread here, take input, submit data, etc
            if (message.equals("SEND_TURN")){
                this.myState = PLAYER_STATE.WAITING;
                sendMessageToServer(message + " " + options);
            }
            return;
        }
        else if (this.myState.equals(PLAYER_STATE.SUBMITTED_NAME)){
            //Submitted our name, waiting for READY_ALL
            if (message.equals("NAME_ACCEPT")){
                this.myState = PLAYER_STATE.WAITING;
            }
            return;
        }
        else if (this.myState.equals(PLAYER_STATE.WAITING)){
            //Waiting for our turn, can give player input here? will need to
            //interrupt this thread when we get reply from server...
            if (message.equals("RECEIVED_POKEMON")){
                selectPokemon();
            } else if (message.equals("YOUR_TURN")){
                this.myState = PLAYER_STATE.PLAYER_TURN;
                System.out.println("=== Round " + (int)Math.ceil(Double.valueOf(options)*0.5) + " ===");
                System.out.println("=== Your Turn! ===");
                handleMyTurn();
            } else if (message.equals("POKEMON_READY")){
                this.sendMessageToServer("POKEMON_READY "+options);
            } else if(message.equals("DEAL_DAMAGE")){
                damageNotificationDealt(options);
            } else if (message.equals("RECEIVE_DAMAGE")){
                damageNotificationReceived(options);
            } else if(message.equals("YOU_WIN")){
                youWin(options);
            } else if (message.equals("YOU_LOSE")){
                youLose(options);
            }
            return;
        }
    }

    public void youWin(String options){
        damageNotificationDealt(options);
        System.out.println("**================**");
        System.out.println("**    You Won!!   **");
        System.out.println("**================**");
        System.exit(0);
    }

    public void youLose(String options){
        damageNotificationReceived(options);
        System.out.println("**================**");
        System.out.println("**    You Lost!   **");
        System.out.println("**================**");
        System.exit(0);
    }

    public void damageNotificationDealt(String options){
        //Need to decode the option string here:
        //<TheirPokemon int>_<move int>_<dmg int>_<didCrit 1/0>
        String[] msgs = options.split("_");
        String theirPokemon = this.pokemonList.get(Integer.parseInt(msgs[0])).getName();
        String move = this.myPokemon.getMoves().get(Integer.parseInt(msgs[1])).getName();
        int damage = Integer.parseInt(msgs[2]);
        boolean didCrit = msgs[3].equals("1")?true:false;
        System.out.println("*** Your " + this.myPokemon.getName() + " used " + move + " and did " + damage + " to their " + theirPokemon + "." + (didCrit?" It was super effective!***":"***"));
        System.out.println("*** Your " + this.myPokemon.getName() +" has " + this.myPokemon.getHp() +" HP Left ***");
    }

    public void damageNotificationReceived(String options){
        //Need to decode the option string here:
        //<TheirPokemon int>_<move int>_<dmg int>_<didCrit 1/0>
        String[] msgs = options.split("_");
        String theirPokemon = this.pokemonList.get(Integer.parseInt(msgs[0])).getName();
        String move = this.pokemonList.get(Integer.parseInt(msgs[0])).getMoves().get(Integer.parseInt(msgs[1])).getName();
        int damage = Integer.parseInt(msgs[2]);
        this.myPokemon.setHp(this.myPokemon.getHp() - damage);
        boolean didCrit = msgs[3].equals("1")?true:false;
        System.out.println("*** Your " + this.myPokemon.getName() + " was hit by enemy "+theirPokemon+"'s "+ move + " and received " + damage + " damage!! " + (didCrit?" It was super effective!***":"***"));
        System.out.println("*** Your " + this.myPokemon.getName() +" has " + this.myPokemon.getHp() +" HP Left ***");
    }

    public void handleMyTurn(){
        System.out.println("============");
        for (PokemonMove move:this.myPokemon.getMoves()) {
            System.out.println("["+(move.getId()+1)+"] - " + move.getName());
        }
        System.out.println("============");
        Scanner input = new Scanner(System.in);
        System.out.println("What will you do?: ");
        String myMove = input.nextLine();
        this.stateHandler("SEND_TURN",1 + "_" + (Integer.parseInt(myMove)-1));
        this.myState = PLAYER_STATE.WAITING;
        return;
    }

    public void selectPokemon(){
        System.out.println("==========");
        for (int i = 0; i < this.pokemonList.size();i++){
            Pokemon me = this.pokemonList.get(i);
            System.out.println("["+i+"]: " + me.getName() + " Type: " + me.getType());
        }
        System.out.println("---------");
        System.out.println("Select a Pokemon:");
        Scanner input = new Scanner(System.in);
        int myChoice = input.nextInt();
        System.out.println("You selected " + this.pokemonList.get(myChoice).getName());
        this.myPokemon = this.pokemonList.get(myChoice);
        stateHandler("POKEMON_READY", this.myPokemon.getId()+"");
    }

    public void sendMessageToServer(String msg){
        try {
//            System.out.println("Sending msg to server: " + msg);
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