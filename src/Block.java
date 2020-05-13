import java.util.Date;

public class Block {

    private String data; //our data will be a simple message.
    private String previousHash;
    private long timeStamp; //as number of milliseconds since 1/1/1970.
    private String hash;
    private int nonce;


    //Block Constructor.
    public Block(String data,String previousHash ) {

        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); //Making sure we do this after we set the other values.
        nonce = 0;

    }

    public Block(String data, String previousHash,String hash, long timeStamp,int nonce) {

        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = hash;
        this.nonce = nonce;
    }

    //Genysis Block Constructor
    public Block(String data,String previousHash, long timeStamp ) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    //Calculate new hash based on blocks contents
    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
        return calculatedhash;
    }

    //Increases nonce value until hash target is reached.
    public void mineBlock(int difficulty) {
        String target = StringUtil.getDificultyString(difficulty); //Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce = nonce + 1;
            hash = calculateHash();
        }
        System.out.println("Block Mined !");
    }

@Override

    public String toString()
    {
        return data + ","+ previousHash + "," + hash + "," + timeStamp + "," + nonce ;
    }


    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

}