package projectTwo;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


// One connection handler thread is created for each node. The connection handler listens for incoming connections from other nodes. Once a connection is established, a new readhandler thread is created to listen for added blocks from the connected node
public class ConnectionHandler implements Runnable {

    // port of the "server" socket for the node
    private final int PORT;

    // reference to the node that this connection handler is associated with so that it can be passed to the readhandler
    private final BCNode NODE;

    public ConnectionHandler(int port, BCNode thisNode) {
        PORT = port;
        this.NODE = thisNode;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            // infinite loop of waiting for connections
            while(true) {
                System.out.println("Waiting for a call");
                Socket s = ss.accept(); // blocking
                System.out.println("Accepted");

                // ois is used by the readhandler to listen for incoming blocks from the connected node
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

                // send the blockchain to the connected node since that node is new and does not have the blockchain
                // outside of this oos is only used by the BCNode to send blocks to connected nodes
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(NODE.getBlockchain());
                NODE.addObjectOutputStream(oos);

                // start new readhandler thread to listen for added blocks from the connected node. Once a block is recieved, the block is validated and added to the blockchain and then broadcasted to all connected nodes.
                ReadHandler readHandler = new ReadHandler(ois, NODE);
                Thread rh = new Thread(readHandler);
                rh.start();

                // threads[i] = new Thread(new BCNodeThread(ss, connNodes.get(i)));
                // System.out.println("Waiting for a call");
                // nodes[i] = ss.accept(); // blocking
                // System.out.println("Accepted");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
