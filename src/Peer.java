import configs.CommonInfo;
import configs.PeerInfo;
import connections.Client;
import connections.Server;
import log.LogWriter;
import messages.ActualMessage;
import messages.HandshakeMessage;
import messages.MessageHandler;

import java.util.*;

public class Peer implements MessageHandler{
    private int myId;
    private CommonInfo commonInfo;
    private PeerInfo myPeerInfo;
    private BitField myBitField;
    private Server server;

    private int optimisticallyUnchockedNeighbor;
    private Map<Integer, Client> clients;
    private Set<Integer> interested;
    private Map<Integer, BitField> bitSetMap;
    private Map<Integer, PeerInfo> peerInfoMap;
    private Map<Integer, Integer> downloadRate;


    private LogWriter logWriter;

    class BitField {
        boolean[] bits;
        Set<Integer> index;

        BitField(int size) {
            bits = new boolean[size];
            index = new HashSet<>();
        }
    }

    Peer(int peerId, CommonInfo commonInfo) {
        this.myId = peerId;
        this.commonInfo = commonInfo;
        this.logWriter = new LogWriter();
        // init bit field

        this.clients = new HashMap<>();
        this.interested = new HashSet<>();
        this.bitSetMap = new HashMap<>();
        this.peerInfoMap = new HashMap<>();
        this.downloadRate = new HashMap<>();
    }

    public void start(List<PeerInfo> peerList) throws Exception {
        // load neighbor info
        for(PeerInfo info : peerList) {
            if(info.getPeerId() != myId) {
                peerInfoMap.put(info.getPeerId(), info);
            }
        }

        // load this peer config
        if(myPeerInfo.hasFile()) {

        }

        // start the server
        server.startServer(myPeerInfo.getPeerPort(), this, logWriter);

        // init clients, start the client whose id before it
        for(PeerInfo info : peerList) {
            if(info.getPeerId() < myId) {
                clients.get(info.getPeerId()).start();
            }
        }

    }

    public int handleHandShakeMessage(byte[] bytes) {
        int peerId = 0;
        try {
            HandshakeMessage hsm = new HandshakeMessage(bytes);
            peerId = hsm.getPeerId();
        } catch (Exception e) {
            System.out.println("handshake message error: " + e);
        }
        return peerId;
    }

    public void handleActualMessage(byte[] bytes, int peerId) {
        ActualMessage respMessage = null;
        try {
            ActualMessage am = new ActualMessage(bytes);
            switch(am.getType()) {
                case 0:
                    respMessage = handleChockMessage(am.getPayload(), peerId);
                    break;
                case 1:
                    respMessage = handleUnchockMessage(am.getPayload(), peerId);
                    break;
                case 2:
                    respMessage = handleInterestMessage(am.getPayload(), peerId);
                    break;
                case 3:
                    respMessage = handleUnInterestMessage(am.getPayload(), peerId);
                    break;
                case 4:
                    respMessage = handleHaveMessage(am.getPayload(), peerId);
                    break;
                case 5:
                    respMessage = handleBitfieldMessage(am.getPayload(), peerId);
                    break;
                case 6:
                    respMessage = handleRequestMessage(am.getPayload(), peerId);
                    break;
                case 7:
                    respMessage = handlePieceMessage(am.getPayload(), peerId);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(respMessage == null) {

        }
    }

    public ActualMessage handleChockMessage(byte[] payload, int peerId) {
        //logger
        return null;
    }

    public ActualMessage handleUnchockMessage(byte[] payload, int peerId) {
        // logger
        return requestPiece(peerId);
    }

    public ActualMessage handleInterestMessage(byte[] payload, int peerId) {
//        logWriter.receiveInterestedMessage(myId, peerId);
        interested.add(peerId);
        return null;
    }

    public ActualMessage handleUnInterestMessage(byte[] payload, int peerId) {
        if(interested.contains(peerId)) {
            interested.remove(peerId);
        }
        return null;
    }

    public ActualMessage handleHaveMessage(byte[] payload, int peerId) {

    }

    public ActualMessage handleBitfieldMessage(byte[] payload, int peerId) {

    }

    public ActualMessage handleRequestMessage(byte[] payload, int peerId) {

    }

    public ActualMessage handlePieceMessage(byte[] payload, int peerId) {

    }

    public ActualMessage requestPiece(int peerId) {
        int missingPieceSize = myBitField.index.size();
        if(missingPieceSize == 0) {
            return null;
        }
        int pieceIndex = new Random().nextInt();
        ActualMessage requestMessage = new ActualMessage();
    }

}
