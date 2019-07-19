import java.io.InputStream;

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
                    byte[] buffer = this.in.readNBytes(this.in.available());
                    int len = buffer.length;
                    this.myClient.onRecieveServerMessage(new String(buffer, 0, len));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}