package messages;

public class MessageHandler {
    public void handleHandShakeMessage(byte[] bytes) {
        try {
            HandshakeMessage hsm = new HandshakeMessage(bytes);
            int PeerId = hsm.getPeerId();
        } catch (Exception e) {
            System.out.println("handshake message error: " + e);
        }
    }

    public void handleActualMessage(byte[] bytes) {
        try {
            ActualMessage am = new ActualMessage(bytes);

        } catch (Exception e) {
            System.out.println("actual message error: " + e);
        }
    }
}
