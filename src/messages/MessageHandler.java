package messages;

public interface MessageHandler {
    // return the peer id
    int handleHandShakeMessage(byte[] bytes);
    // return the response message
    void handleActualMessage(byte[] bytes, int peerId);

}
