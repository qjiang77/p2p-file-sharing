package connections;

import messages.ActualMessage;
import messages.MessageHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {

    ServerSocket serverSocket;
    ConcurrentLinkedQueue<ActualMessage> messageQueue;

    public void openServer(int port) throws Exception{
        serverSocket = new ServerSocket(port);
        while(true) {
            ServerConnection sc = new ServerConnection(serverSocket.accept(), port);
            sc.start();
        }
    }

    public void closeServer() throws Exception {
        serverSocket.close();
    }

    private class ServerConnection extends Thread {
        private Socket connection;
        private DataInputStream in;
        private DataOutputStream out;
        private int pid;

        ServerConnection(Socket connection, int pid) {
            this.connection = connection;
            this.pid = pid;
        }
        @Override
        public void run() {
            try {
                MessageHandler handler = new MessageHandler();
                out = new DataOutputStream(connection.getOutputStream());
                out.flush();
                in = new DataInputStream(connection.getInputStream());
                try {
                    byte[] bytes = new byte[32];
                    in.readFully(bytes);
                    handler.handleHandShakeMessage(bytes);
                } catch (Exception e) {
                    System.out.println("illegal handshake message. " + e);
                }
                try {
                    while(true) {
                        int len = in.readInt();
                        byte[] bytes = new byte[len];
                        in.readFully(bytes);
                        handler.handleActualMessage(bytes);
                    }
                } catch (Exception e) {
                    System.out.println("read actual message error: " + e);
                }

            } catch (IOException ioException) {
                System.out.println("Disconnect with connections.Client " + pid);
            }
        }
    }

}
