import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

class ReadThread implements Runnable {
    private InputStream in;
    private Client myClient;
    private static int THREAD_DELAY = 50; //msec

    public ReadThread(InputStream in, Client client){
        this.in = in;
        myClient = client;
    }

    public void run() {
        try {
            while (true){
                if (this.in.available() > 0){ //data available
                    System.out.println("got data");
                    byte[] buffer = this.in.readNBytes(this.in.available());
                    int len = buffer.length;
                    //Before we do anything, we'll try to change this into an object to
                    //see if it is an object before we pass it on
                    try {
                        ObjectInputStream ois = new ObjectInputStream(this.in);
                        Object temp = ois.readObject();
                        if (temp instanceof ArrayList<?>){
                            ArrayList temp2 = (ArrayList<?>) temp;
                            if (temp2.get(0) instanceof Pokemon){
                                //Deserialize it here into an actual new arraylist of pokemon...
                                ArrayList<Pokemon> readList = new ArrayList<Pokemon>();
                                for (Object a: temp2) {
                                    Pokemon tempPokemon = (Pokemon) a;
                                    readList.add(tempPokemon);
                                }
                                this.myClient.onReceivePokemonList(readList);
                            }
                        } else {
                            this.myClient.onRecieveServerMessage(new String(buffer, 0, len));
                        }
                    } catch (Exception e){
                        System.out.println("Exception in read");
                        //Not an object? treat it like normal...
                        this.myClient.onRecieveServerMessage(new String(buffer, 0, len));
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}