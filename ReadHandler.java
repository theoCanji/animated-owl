package projectTwo;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ReadHandler implements  Runnable {

    private final int PORT;
    private BCNode connNode;

    public ReadHandler(int port, BCNode connNode) {
        PORT = port;
        this.connNode = connNode;
    }
    
    @Override
    public void run() {
        
        
    }

    
}
