import javax.json.Json;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Dushyant Sharma & Rishabh Shukla
 */

public class Peer {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static ArrayList<Boolean> flag_arr = new ArrayList<Boolean>();
    static ServerThread serverThread;
    public static int difficulty = 4;
    static String[] inputValues;
    static int counter = 0;
    static String username;
    static Block block1;
    static Block obj;

    public static void setObj(Block object, Boolean flag_by_sender)
    {
        System.out.println("Arrived at receiver setObj function");
        block1 = object;
        blockchain.add(block1);
        boolean validity = isChainValid();
        blockchain.remove(block1);
        boolean need_cycle = flagCounter(flag_by_sender);

        if(need_cycle == false) {
            broadcast(validity);
            boolean x = flagCounter(validity);
        }
    }

    /**
     * this method is used to count the number of valid flag values of all the users
     * and implements consensus theorem
     * @param flag
     * @return flag value which informs if we need to send it further back to user which had mined the block
     */
    public static boolean flagCounter(boolean flag)
    {
        int true_count = 0;
        int false_count = 0;

        int size = inputValues.length + 1;

        boolean need_cycle = false;

        flag_arr.add(flag);
        if(flag_arr.size() == size)
        {
            for (Boolean i : flag_arr)
            {
                if(i == true)
                {
                    true_count++;
                }
                else
                {
                    false_count++;
                }
            }
            if(true_count>false_count)
            {
                if(counter == 1) {
                    blockchain.add(obj);
                } else if(counter == 0) {
                    blockchain.add(block1);
                }
                System.out.println("The Mined Block has been added in the chain according to consensus protocol...");
                    counter = 0;
                flag_arr.clear();
            }
            else
            {
                System.out.println("Mined block is not added... As it does not satisfy consensus protocol...");
            }

            print_complete_chain();
            System.out.println("Blockchain size is -> " + blockchain.size());
            need_cycle = true;
        }
        return need_cycle;
    }

    /**
     * this method is used to retrieve the details of the user i.e. name localhost/ip and port_number
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter Name and (localhost or ip address ) and port number for this peer " +
                "(all separated by space)");
        String[] setup_values = bufferedReader.readLine().split(" ");
        serverThread = new ServerThread(setup_values[2]);
        serverThread.start();
        username = setup_values[0] + " : " +setup_values[1];
        new Peer().updateListenToPeers(bufferedReader , username, serverThread);
        //instead of username you can also pass setup_values[0]
    }

    /**
     * this method is used to collect the list of all other peer who are on the network
     * the input is provided in the format ( localhost:port_no or ip_address:port_no)
     * @param bufferedReader
     * @param username
     * @param serverThread
     * @throws Exception
     */
    public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread) throws Exception {
        System.out.println("Enter localhost/IP_Address:port_number (separated by spaces)");
        System.out.println("of peers to receive message from (s to skip)");
        String input = bufferedReader.readLine();
        inputValues = input.split(" ");

        if (!input.equals("s")) {
            for (String inputValue : inputValues) {
                String[] address = inputValue.split(":");
                Socket socket = null;
                try {
                    socket = new Socket(address[0], Integer.parseInt(address[1]));
                    new PeerThread(socket).start();
                } catch (Exception e) {
                    if (socket != null) {
                        socket.close();
                    } else {
                        System.out.println("Invalid input.. skipping to next step");
                        System.out.println("Exception is :" + e);
                        e.printStackTrace();
                    }
                }
            }
        }

        //creating genesis block before initiating any transaction
        if (blockchain.isEmpty()){
            blockchain.add(addBlock(new Block("Genesis Block", "0", 1588685584073l)));
        }
        communicate(bufferedReader, username, serverThread);

    }

    /**
     * this method is used for communication.
     * It is following operations: update peers list , exit,
     * get_chain , check_validity , length and add (for mining new block)
     * the add option first reads the data for the block to be mined
     * and then creates and mines the block and send it over the network
     * @param bufferedReader
     * @param username
     * @param serverThread
     */
    public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread){

        try{
//            Car obj= new Car();

            System.out.println("you can communicate.. (press e to exit or c to add more peers or add to mine a new block)");
            boolean flag = true;

            while(flag){
                String message = bufferedReader.readLine();

                if(message.equals("exit")) {
                    flag = false;
                }
                else if(message.equals("update")) {
                    updateListenToPeers(bufferedReader, username, serverThread);
                }
                else if(message.equals("get_chain")){
                    System.out.println("The complete chain is:");
                    print_complete_chain();
                }
                else if(message.equals("check_validity")){
                    System.out.println("The chain for the given user is : " + isChainValid());
                }
                else if(message.equals("length")){
                    System.out.println("The chain size for the given user is : " + blockchain.size());
                }
                else if(message.equals("add")){

                    String data = "This is a new block";
                    obj = addBlock(new Block(data,blockchain.get(blockchain.size()-1).getHash()));
                    counter = 1;                    //setting counter value 1 -> signifies sender

                    blockchain.add(obj);
                    boolean validity1 = isChainValid();
                    blockchain.remove(obj);

                    flag_arr.add(validity1);
                    String validity = Boolean.toString(validity1);

                    String mess = obj.toString();

                    StringWriter stringWriter = new StringWriter();
                    Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                                .add("username",username)
                                .add("message", mess)
                                .add("validity", validity)
                                .build());
                    serverThread.sendMessage(stringWriter.toString());
                }
                else{
                    System.out.println("Unrecognised input... Please try again !!");
                }

            }
            System.exit(0);
        }catch(Exception e){
            System.out.println("Exception raised while communicating: " + e);
            e.printStackTrace();

        }
    }

    /**
     * this method is used to broadcast back the value of validity back to the sender who had initially mined
     * the block to complete the consensus protocol
     * @param validity
     */
    public static void broadcast(Boolean validity) {
        System.out.println("Broadcasting message back to Sender (who mined the block)");
        String mess = validity.toString();
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("username", username)
                .add("validity", mess)
                .build());
        serverThread.sendMessage(stringWriter.toString());
        System.out.println("Message has been broadcast with value : " + validity);

    }

    /**
     * this method is used to set the nonce value of the mined block..
     * as it is not done during mining
     * @return the object which was taken as parameter;
     */
    public static Block addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        //blockchain.add(newBlock);
        return newBlock;
    }

    /**
     * this method is used to check the validity of chain by iterating through the
     * complete chain..
     * @return boolean value (True/False)
     */
    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.getHash().equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getHash().substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

        }
        return true;
    }

    public static void print_complete_chain()
    {
        String blockchainJson = StringUtil.getJson(blockchain);
        System.out.println("The complete chain is :");
        System.out.println(blockchainJson);
    }
}