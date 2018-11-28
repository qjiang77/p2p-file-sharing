package messages;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ActualMessage {
    int type;
    byte[] payload;

    public int getType() {
        return type;
    }
    public byte[] getPayload() {
        return payload;
    }

    public ActualMessage(byte[] bytes) {
        type = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 0, 1)).getInt();
        payload = Arrays.copyOfRange(bytes, 0, 1);
    }

    ActualMessage (int type, byte[] payload) {
        this.type = type;
        this.payload = payload;
    }

    // message 加工处理还有问题
    public byte[] toByteArray() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        return bytes.toByteArray();
    }
}
