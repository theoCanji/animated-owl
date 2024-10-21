package projectTwo;

import java.security.MessageDigest;

public class Block {
    private String data;   
    private long timeStamp;
    private int nonce;
    private String previousHash;
    private String hash;

    public static void main (String[] args) {
        Block b = new Block("Hello", "0");
        System.out.println(b.toString());
        System.out.println(b.calculateHash());
    }

    // Constructor
    public Block(String data, String previousHash) {
        this.data = data;
        this.timeStamp = System.currentTimeMillis();
        this.nonce = 0;
        this.previousHash = previousHash;
        this.hash = this.calculateHash();
    }

    // hash function (SHA-256)
    public String calculateHash() {
        //first combine all instance variables into a single string
        String combindedVariables = this.data + this.timeStamp + this.nonce + this.previousHash;
        try{ 
            //create a message digest object
            MessageDigest myDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = myDigest.digest(combindedVariables.getBytes("UTF-8"));
            

            //convert the byte array to a string
            StringBuffer buffer = new StringBuffer();
            for (byte b: hash) {
                buffer.append(String.format("%02x", b));
            }
            String hashStr = buffer.toString();
            return hashStr;   
            }
        catch (Exception e) {
           String error = ("Error: " + e);
           return error;
        }
    }
     
    //to string helper function
    public String toString() {
        return "Block{" +
        "data='" + data + '\'' +
        ", timeStamp=" + timeStamp +
        ", nonce=" + nonce +
        ", previousHash='" + previousHash + '\'' +
        ", hash='" + hash + '\'' +
        '}';    
    }

    //getter and setter methods
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public long getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public int getNonce() {
        return nonce;
    }
    public void setNonce(int nonce) {
        this.nonce = nonce;
    }
    public String getPreviousHash() {
        return previousHash;
    }
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }
}

