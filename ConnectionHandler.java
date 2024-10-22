package projectTwo;
import java.net.ServerSocket;

public class ConnectionHandler implements Runnable{
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            for (int i = 0; i < connNodes.size(); i++) {
                // threads[i] = new Thread(new BCNodeThread(ss, connNodes.get(i)));
                    System.out.println("Waiting for a call");
                    // nodes[i] = ss.accept(); // blocking
                    System.out.println("Accepted");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
