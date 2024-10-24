package projectTwo; 
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class BCNode{


    private final int PORT;
    private final int N = 5;
    private ArrayList<Block> blockchain = new ArrayList<>();

    // arraylist of output streams to each connected nodes so that we can broadcast blocks to connected nodes when we need to
    private ArrayList<ObjectOutputStream> oos;

    public BCNode(int port, ArrayList<BCNode> connNodes) {
        PORT = port;
        
        try {
            for (int i = 0; i < connNodes.size(); i++) {
                Socket s = new Socket("localhost", connNodes.get(i).getPort());
                oos.add(new ObjectOutputStream(s.getOutputStream()));
                ReadHandler readHandler = new ReadHandler(connNodes.get(i).getPort(), new ObjectInputStream(s.getInputStream()), this);
                Thread rh = new Thread(readHandler);
                rh.start();
                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if(blockchain.size() == 0) {
            blockchain.add(new Block("Genesis Block", "0"));
        }
        
        
        ConnectionHandler handler = new ConnectionHandler(port, this);
        Thread ch = new Thread(handler);
        ch.start();
    }

    public boolean addBlock(Block b) {
        mineBlock(b);
        
        b.setPreviousHash(blockchain.get(blockchain.size() - 1).getHash());
        if(blockValidate(b)) {
            System.out.println("Block added to the blockchain");
            blockchain.add(b);
            broadcastBlock(b);
            return true;
        }
        return false;
    }

    // broadcast block to all connected nodes
    private void broadcastBlock(Block b) {
        for (int i = 0; i < oos.size(); i++) {
            try {
                oos.get(i).writeObject(b);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public int getPort() {
        return PORT;
    }

    public ArrayList<Block> getBlockchain() {
        return blockchain;
    }

    public void addOOStream(ObjectOutputStream oos) {
        this.oos.add(oos);
    }

    public static void main(String[] args) {
        
    }
}
