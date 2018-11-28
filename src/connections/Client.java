package connections;

import configs.PeerInfo;
import messages.ActualMessage;
import messages.HandshakeMessage;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends Thread{
    Socket socket;
    DataOutputStream out;
    PeerInfo peerInfo;

    private ConcurrentLinkedQueue<ActualMessage> messageQueue;

    public Client(PeerInfo info) {
        this.peerInfo = info;
        this.messageQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        try {
            //create a socket to connect to the server
            socket = new Socket(peerInfo.getPeerAddress(), peerInfo.getPeerPort());
            out = new DataOutputStream(socket.getOutputStream());

            // send handshake message
            HandshakeMessage hsMsg = new HandshakeMessage(peerInfo.getPeerId());
            out.write(hsMsg.toByteArray());
            out.flush();

            while (true) {
                while(!messageQueue.isEmpty()) {
                    out.write(messageQueue.poll().toByteArray());
                    out.flush();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void closeClient() {
        try {
            out.close();
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    //send a message to the output stream
    // TODO send
    public void sendMessage(ActualMessage msg) {
        messageQueue.add(msg);
    }

}
