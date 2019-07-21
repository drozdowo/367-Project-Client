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
                if (this.in.available() > 0){ //data availabl
                    byte[] buffer = this.in.readNBytes(this.in.available());
                    int len = buffer.length;
                    //Try to create a string from it, if we get an exception its probabl
                    //an object.
                    try{
                        String msg = new String(buffer, 0, len);
                        this.myClient.onRecieveServerMessage(new String(buffer, 0, len));
                    } catch (Exception e1){
                        //If we get an exception, lets try to create an object
                        try {
                            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                            bais.read();
                            ObjectInputStream ois = new ObjectInputStream(bais);
                            Object temp = ois.readObject();
                            if (temp instanceof ArrayList<?>){
                                System.out.println("is arraylist??");
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
                            }
                        } catch (Exception e2){
                            System.out.println("Exception in read");
                            e2.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}