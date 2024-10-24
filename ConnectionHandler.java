package projectTwo;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable {

    private final int PORT;
    private  ArrayList<BCNode> connNodes;

    public ConnectionHandler(int port) {
        PORT = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            while(true) {
                System.out.println("Waiting for a call");
                Socket s = ss.accept(); // blocking
                System.out.println("Accepted");
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                BCNode bcn = (BCNode)ois.readObject();
                int connPort = bcn.getPort();
                connNodes.add(bcn);
                System.out.println("Connected to " + bcn + " on port " + connPort);
                ReadHandler readHandler = new ReadHandler(connPort, ois);
                Thread rh = new Thread(readHandler);
                rh.start();

                s.close();
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
