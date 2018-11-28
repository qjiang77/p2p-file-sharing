package messages;

import java.io.ByteArrayOutputStream;

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

}
