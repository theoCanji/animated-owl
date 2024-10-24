package projectTwo;
import java.io.ObjectInputStream;

public class ReadHandler implements  Runnable {

    private final int PORT;
    private BCNode connNode;

    public ReadHandler(int port, ObjectInputStream ois) {
        PORT = port;
        this.connNode = connNode;
    }
    
    @Override
    public void run() {
        
        
    }

    
}
