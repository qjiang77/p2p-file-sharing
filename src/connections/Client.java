package connections;

import configs.PeerInfo;
import messages.HandshakeMessage;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
	Socket requestSocket;           //socket connect to the server
	DataOutputStream out;         //stream write to the socket
 	DataInputStream in;          //stream read from the socket
	byte[] send;                //message send to the server
	byte[] receive;                //capitalized message read from the server
	int peerId;

	private ConcurrentLinkedQueue<byte[]> messageQueue;

	public Client(int peerId) {
		this.peerId = peerId;
		this.messageQueue = new ConcurrentLinkedQueue<>();
	}

	void run()
	{
		try{
			//create a socket to connect to the server
			requestSocket = new Socket("localhost", 8000);
			System.out.println("Connected to localhost in port 8000");
			//initialize inputStream and outputStream
			out = new DataOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new DataInputStream(requestSocket.getInputStream());
			
			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

			// send handshake message
			HandshakeMessage hsMsg = new HandshakeMessage();
			send = HandshakeMessage.

			while(true)
			{
				System.out.print("Hello, please input a sentence: ");
				//read a sentence from the standard input
				send = bufferedReader.readLine();
				//Send the sentence to the server
				sendMessage(message);
				//Receive the upperCase sentence from the server
				receive = (String)in.readObject();

				//show the message to the user
				System.out.println("Receive message: " + MESSAGE);
			}
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch ( ClassNotFoundException e ) {
            		System.err.println("Class not found");
        	} 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	//send a message to the output stream
	void sendMessage(String msg)
	{
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

}
