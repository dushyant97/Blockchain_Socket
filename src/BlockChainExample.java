import java.util.ArrayList;
import java.util.Date;


public class BlockChainExample {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 3;

    public static void main(String[] args) {

        //add our blocks to the blockchain ArrayList:

        if (blockchain.isEmpty()){
            System.out.println("Trying to mine the genesis Block... ");
            addBlock(new Block("genesis Block", "0", 1588685584073l));
        }

        System.out.println("Trying to Mine block 2... ");
        addBlock(new Block("Yo im the second block",blockchain.get(blockchain.size()-1).getHash()));

        System.out.println("Trying to Mine block 3... ");
        addBlock(new Block("Yo im the second block",blockchain.get(blockchain.size()-1).getHash()));

        System.out.println("\nBlockchain is Valid: " + isChainValid());

        print_complete_chain();

    }

    public static Boolean isChainValid() {
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

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public static void print_complete_chain()
    {
        String blockchainJson = StringUtil.getJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }

}
