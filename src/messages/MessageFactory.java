package messages;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

public class MessageFactory {

    public static ActualMessage requestMessage(int pieceIndex) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bytes.write(pieceIndex);
        byte[] payload = bytes.toByteArray();
        return new ActualMessage(7, payload);
    }

    public static ActualMessage interestedMessage() {
        return new ActualMessage(2, null);
    }

    public static ActualMessage uninterestedMessage() {
        return new ActualMessage(3, null);
    }

    public static ActualMessage pieceMessage(byte[] payload) {return new ActualMessage(7, payload);}

    public static ActualMessage bitfieldMessage(boolean[] bitfield) {
        byte[] bytes = new byte[(bitfield.length + 7) / 8];
        for(int i = 0; i < bitfield.length; ++i) {
            bytes[i / 8] *= 2;
            bytes[i / 8] += bitfield[i] ? 1 : 0;
        }
        return new ActualMessage(5, bytes);
    }
}
