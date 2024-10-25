package projectTwo;
import java.io.ObjectInputStream;

// ReadHandler will listen for incoming blocks from connected nodes. Once a block is recieved, the block is validated and added to the blockchain and then broadcasted to all connected nodes.
public class ReadHandler implements  Runnable {

    // input stream to listen for incoming blocks from connected nodes
    private final ObjectInputStream OIS;

    // reference to the node that this readhandler is associated with so that it can edit the blockchain
    private BCNode thisNode;

    public ReadHandler(ObjectInputStream ois, BCNode thisNode) {
        OIS = ois;
    }
    
    @Override
    public void run() {
        
        while (true) {
            try {
                Block b = (Block)OIS.readObject();

                // if the block is valid it will be added to the blockchain and broadcast to neighboring nodes
                if (thisNode.blockValidate(b)) {
                    
                    thisNode.addBlock(b);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        
    }

    
}
