package projectTwo;
import java.io.ObjectInputStream;
import java.io.EOFException;
import java.net.SocketException;
import java.io.IOException;

// ReadHandler will listen for incoming blocks from connected nodes. Once a block is recieved, the block is validated and added to the blockchain and then broadcasted to all connected nodes.
public class ReadHandler implements  Runnable {

    // input stream to listen for incoming blocks from connected nodes
    private final ObjectInputStream OIS;

    // reference to the node that this readhandler is associated with so that it can edit the blockchain
    private BCNode thisNode;

    public ReadHandler(ObjectInputStream ois, BCNode thisNode) {
        this.thisNode = thisNode;
        OIS = ois;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                Block b = (Block) OIS.readObject();
    
                // Validate and add the block to the blockchain
                if (thisNode.blockValidate(b)) {
                    thisNode.addBlock(b);
                }
            }
        } 
        catch (EOFException | SocketException e) {
            // The other node disconnected or was killed
            System.out.println("Connection lost. Stopping ReadHandler for this connection.");
        }
        catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            // Clean up resources if necessary
            try {
                OIS.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    


    
}
