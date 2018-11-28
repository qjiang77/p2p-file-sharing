import configs.CommonInfo;
import configs.PeerInfo;
import connections.Client;
import connections.Server;
import log.LogWriter;
import messages.ActualMessage;
import messages.HandshakeMessage;
import messages.MessageHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Peer implements MessageHandler{
    private int myId;
    private CommonInfo commonInfo;
    private PeerInfo myPeerInfo;
    private boolean[] myBitField;
    private Server server;

    private int optimisticallyUnchockedNeighbor;
    private Map<Integer, Client> clients;
    private Vector<Integer> interested;
    private Map<Integer, boolean[]> bitSetMap;
    private Map<Integer, PeerInfo> peerInfoMap;
    private ConcurrentHashMap<Integer, Integer> downloadRateMap;

    private Vector<Integer> preferredNeighbors;

    private LogWriter logWriter;

    Peer(int peerId, CommonInfo commonInfo) {
        this.myId = peerId;
        this.commonInfo = commonInfo;
        this.logWriter = new LogWriter();
        // init bit field

        this.clients = new HashMap<>();
        this.interested = new Vector<>();
        this.bitSetMap = new HashMap<>();
        this.peerInfoMap = new HashMap<>();
        this.downloadRateMap = new ConcurrentHashMap<>();
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

    private void getPreferredPeers() {
        Timer getPereferPeersTimer = new Timer();
        getPereferPeersTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Vector<Integer> nextPreferredNeighbors = new Vector<>();
                if (myPeerInfo.hasFile()) {
                    preferredNeighbors.clear();
                    if (interested.size() > 0) {
                        Vector<Integer> tempOfInterest = interested;
                        while (nextPreferredNeighbors.size() < commonInfo.getNumberOfPreferredNeighbors()) {
                            if (tempOfInterest.isEmpty()) {break;}
                            int randomIndex = new Random().nextInt(tempOfInterest.size());
                            nextPreferredNeighbors.add(tempOfInterest.get(randomIndex));
                            tempOfInterest.remove(randomIndex);
                        }
                    }
                } else {
                    if (!downloadRateMap.isEmpty()) {
                        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(downloadRateMap.entrySet());
                        list.sort(Map.Entry.comparingByValue());
                        for (Map.Entry<Integer, Integer> entry : list) {
                            nextPreferredNeighbors.add(entry.getKey());
                            if (nextPreferredNeighbors.size() >= commonInfo.getNumberOfPreferredNeighbors()) break;;
                        }
                        downloadRateMap.clear();
                    }
                }
                for (Integer newPreferredNeighbor : nextPreferredNeighbors) {
                    if (!preferredNeighbors.contains(newPreferredNeighbor)) {
                        // new comer
                        try {
                            clients.get(newPreferredNeighbor).start();
                            // TODO send unchoke message to that neighbor
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                for (Integer oldPreferredNeighbor : preferredNeighbors) {
                    if (!nextPreferredNeighbors.contains(oldPreferredNeighbor)) {
                        try {
                            clients.get(oldPreferredNeighbor).start();
                            // TODO send choke message to that neighbor
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                preferredNeighbors = nextPreferredNeighbors;
//                logWriter.changePreferredNeighbors(myPeerInfo.getPeerId(), preferredNeighbors);
            }
        }, 0, 1000 * commonInfo.getUnchokingInterval());
    }
    private void getOptiUnchokedPeers() {
        Timer getOptiUnchokedPeersTimer = new Timer();
        getOptiUnchokedPeersTimer.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        }, 0, 1000 * commonInfo.getOptimisticUnchokingInterval());
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
        List<Integer> index = new ArrayList<>();
        for(int i = 0; i < myBitField.length; ++i) {
            if(!myBitField[i]) {
                index.add(i);
            }
        }
        int missingPieceSize = index.size();
        if(missingPieceSize == 0) {
            return null;
        }
        int pieceIndex = new Random().nextInt(missingPieceSize);
        return new ActualMessage.makeMessage(ActualMessage.TYPE.REQUEST, pieceIndex);
    }

}
