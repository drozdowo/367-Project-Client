import java.io.*;
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
                    //lets try to create an object
                    BufferedInputStream myIn = new BufferedInputStream(this.in);
                    myIn.mark(this.in.available());
                    try {
                        ObjectInputStream ois = new ObjectInputStream(myIn);
                        Object temp = ois.readObject();
                        if (temp instanceof ArrayList<?>){
//                            System.out.println("is arraylist??");
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
                        //Exception means its not an object. We'll reset the input
                        //stream and read a string like normal.
                        myIn.reset();
                        byte[] buffer = new byte[120120];
                        myIn.read(buffer, 0, myIn.available());
                        int len = buffer.length;
//                        System.out.print("not an object  " + len +" |" + new String(buffer, 0, len));
                        this.myClient.onRecieveServerMessage(new String(buffer, 0, len));
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}