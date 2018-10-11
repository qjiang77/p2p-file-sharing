package messages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MessageFactory {
    public Message makeMessage(byte[] bytes, int type) throws Exception {
        if(type == 0) {
            if(bytes.length != 32) {
                throw new Exception("invalid handshake message length");
            }
            String header = new String(Arrays.copyOfRange(bytes, 0, 18), "UTF-8");
            if(header.equals("P2PFILESHARINGPROJ")) {
                throw new Exception("wrong handshake header: " + header);
            }
            int peerId = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 28, 32)).getInt();
            return new HandshakeMessage(peerId);
        }
        else {
            return new ActualMessage();
        }
    }
}
