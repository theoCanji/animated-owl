package projectTwo;
import java.util.ArrayList;


public class BCNode{

    private final int N = 5;
    private ArrayList<Block> blockchain = new ArrayList<>();

    public BCNode(int port, ArrayList<BCNode> connNodes) {
        blockchain.add(new Block("Genesis Block", "0"));
        
        ConnectionHandler handler = new ConnectionHandler(port, connNodes);
        Thread h = new Thread(handler);
        h.start();

        for (int i = 0; i < connNodes.size(); i++) {
            BCNodeThread nodeThread = new BCNodeThread(connNodes.get(i));
            Thread t = new Thread(nodeThread);
            t.start();
        }
    }

    public boolean addBlock(Block b) {
        mineBlock(b);
        
        b.setPreviousHash(blockchain.get(blockchain.size() - 1).getHash());
        if(blockValidate(b)) {
            System.out.println("Block added to the blockchain");
            blockchain.add(b);
            return true;
        }
        return false;
    }


    private void mineBlock(Block b) {
        String prefixZeros = new String(new char[N]).replace('\0', '0');
        while (!b.getHash().substring(0, N).equals(prefixZeros)) {
            b.setNonce(b.getNonce() + 1);
            b.setHash(b.calculateHash());
        }
    }

    private boolean blockValidate(Block b) {
        if(!b.getHash().equals(b.calculateHash())) {
            System.out.println("Block hash is invalid");
            return false;
        }
        if(!b.getPreviousHash().equals(blockchain.get(blockchain.size() - 1).getHash())) {
            System.out.println("Block previous hash is invalid");
            return false;
        }
        String prefixZeros = new String(new char[N]).replace('\0', '0');
        if(!b.getHash().substring(0, N).equals(prefixZeros)) {
            System.out.println("Block hash does not meet the difficulty requirement");
            return false;
        }
        return true;
    }

    public String toString() {
        return "BCNode{" +
        "blockchain=" + blockchain +
        '}';
    }

    public static void main(String[] args) {
        
    }
}
