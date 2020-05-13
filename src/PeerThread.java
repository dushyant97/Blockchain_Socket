import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PeerThread extends Thread {
    private BufferedReader bufferedReader;
    private Block obj;

    public PeerThread(Socket socket) throws IOException{
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Block getObj()
    {
        return obj;
    }

    @Override
    public void run() {
        boolean flag = true;

        while (flag){
            try {

                JsonObject  jsonObject = Json.createReader(bufferedReader).readObject();
                if(jsonObject.containsKey("username"))
                {
                    if(jsonObject.containsKey("message"))
                    {
                        String message = jsonObject.getString("message");
                        Boolean flag1 = Boolean.parseBoolean(jsonObject.getString("validity"));
                        String[] values = message.split(",");
                        obj = new Block(values[0], values[1], values[2], Long.parseLong(values[3]), Integer.parseInt(values[4]));
                        Peer.setObj(obj, flag1);

/*
                    //Used for Car class -- for unit testing purpose

                    System.out.println("[" + jsonObject.getString("username") + "]: " + jsonObject.getString("message") );
                    Car a = new Car(values[0], Long.parseLong(values[1]));
                    System.out.println("Values array is " + values[0]);
                    System.out.println(obj.getData());
                    System.out.println(a.getName());
                    System.out.println(a.getTimestamp());
*/
                    }
                    else {

                        boolean flag1 = Boolean.parseBoolean(jsonObject.getString("validity"));
                        Peer.flagCounter(flag1);
                    }

                }

            }catch (Exception e){
                flag = false;
                interrupt();
            }
        }
    }
}