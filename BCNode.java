package projectTwo; 
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class BCNode{

    private final int PORT;
    private final int N = 5;
    private ArrayList<Block> blockchain = new ArrayList<>();

    // arraylist of output streams to each connected nodes so that we can broadcast blocks to connected nodes when we need to
    private ArrayList<ObjectOutputStream> oos;

    @SuppressWarnings("unchecked")
    public BCNode(int port, List<Integer> remotePorts) {
        PORT = port;
        
        try {
            // connect to all nodes in the input list of nodes
            for (int i = 0; i < remotePorts.size(); i++) {
                Socket s = new Socket("localhost", remotePorts.get(i));

                // if we don't have the blockchain yet get it from the first block we connect to
                if (blockchain.isEmpty()) {
                    ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                    blockchain = (ArrayList<Block>)ois.readObject();
                }

                // adding output stream to the list of output streams so that we can later broadcast blocks to connected nodes
                oos.add(new ObjectOutputStream(s.getOutputStream()));

                // creates a readhandler for each node we connect to
                ReadHandler readHandler = new ReadHandler(new ObjectInputStream(s.getInputStream()), this);

                // create thread for readhandler to run on
                Thread rh = new Thread(readHandler);

                // start the readhandler thread
                rh.start();
                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if(blockchain.isEmpty()) {
            blockchain.add(new Block("Genesis Block"));
        }
        
        // create one connectionhandler thread for each node
        ConnectionHandler connHandler = new ConnectionHandler(port, this);
        Thread ch = new Thread(connHandler);
        ch.start();
    }

    // synchronized so that only one block can be added at a time and we don't have any race conditions
    public synchronized boolean addBlock(Block b) {
        b.setPreviousHash(blockchain.get(blockchain.size() - 1).getHash());
        mineBlock(b);
        
        System.out.println("Block added to the blockchain");
        blockchain.add(b);
        broadcastBlock(b);
        return true;
    }

    // broadcast block to all connected nodes
    private void broadcastBlock(Block b) {
        for (int i = 0; i < oos.size(); i++) {
            try {
                oos.get(i).reset();
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

    public boolean blockValidate(Block b) {
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
        else {
            return true;
        }
    }

    public String toString() {
        return "Port: " + PORT +
        " blockchain: " + blockchain +
        '}';
    }

    public ArrayList<Block> getBlockchain() {
        return blockchain;
    }

    public synchronized void addObjectOutputStream(ObjectOutputStream oos) {
        this.oos.add(oos);
    }

    public static void main(String[] args) {
        Scanner keyScan = new Scanner(System.in);
        
        // Grab my port number on which to start this node
        System.out.print("Enter port to start (on current IP): ");
        int myPort = keyScan.nextInt();
        
        // Need to get what other Nodes to connect to
        System.out.print("Enter remote ports (current IP is assumed): ");
        keyScan.nextLine(); // skip the NL at the end of the previous scan int
        String line = keyScan.nextLine();
        List<Integer> remotePorts = new ArrayList<>();
        if (line != "") {
            String[] splitLine = line.split(" ");
            for (int i=0; i<splitLine.length; i++) {
                remotePorts.add(Integer.parseInt(splitLine[i]));
            }
        }
        // Create the Node
        BCNode n = new BCNode(myPort, remotePorts);
        
        String ip = "";
        try {
             ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        System.out.println("Node started on " + ip + ": " + myPort);
        
        // Node command line interface
        while(true) {
            System.out.println("\nNODE on port: " + myPort);
            System.out.println("1. Display Node's blockchain");
            System.out.println("2. Create/mine new Block");
            System.out.println("3. Kill Node");
            System.out.print("Enter option: ");
            int in = keyScan.nextInt();
            
            if (in == 1) {
                System.out.println(n);
                
            } else if (in == 2) {
                // Grab the information to put in the block
                System.out.print("Enter information for new Block: ");
                String blockInfo = keyScan.next();
                Block b = new Block(blockInfo);
                n.addBlock(b);
                
            } else if (in == 3) {
                // Take down the whole virtual machine (and all the threads)
                //   for this Node.  If we just let main end, it would leave
                //   up the Threads the node created.
                keyScan.close();
                System.exit(0);
            }
        }
    }
}
