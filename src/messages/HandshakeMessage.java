package messages;

import java.io.ByteArrayOutputStream;

public class HandshakeMessage implements Message{
    private int peerId;
    HandshakeMessage(int peerId){
        this.peerId = peerId;
    }

    public byte[] toByteArray(int peerId) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String header = "P2PFILESHARINGPROJ";
        try {
            bytes.write(header.getBytes("UTF-8"));
            bytes.write(new byte[10]);
            bytes.write(this.peerId);
        } catch(Exception e) {
            System.out.println("writing handshake message error");
        }
        return bytes.toByteArray();
    }
}
