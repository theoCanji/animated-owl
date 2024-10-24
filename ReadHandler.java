package projectTwo;
import java.io.ObjectInputStream;

// ReadHandler will listen for incoming blocks from connected nodes. Once a block is recieved, the block is validated and added to the blockchain and then broadcasted to all connected nodes.
public class ReadHandler implements  Runnable {

    private final int PORT;
    private ObjectInputStream ois;
    private BCNode thisNode;
    private BCNode connNode;

    public ReadHandler(int port, ObjectInputStream ois, BCNode thisNode) {
        PORT = port;
        this.ois = ois;
    }
    
    @Override
    public void run() {
        try {
            while(true) {
                Block b = (Block)ois.readObject();
                if(thisNode.addBlock(b)) {
                    // broadcast block to all connected nodes
                    for (int i = 0; i < connNode.getConnNodes().size(); i++) {
                        connNode.getOos()[i].writeObject(b);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }

    
}
