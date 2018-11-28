import configs.CommonInfo;
import configs.PeerInfo;
import connections.Client;
import connections.Server;
import log.LogWriter;
import messages.ActualMessage;
import messages.HandshakeMessage;
import messages.MessageFactory;
import messages.MessageHandler;

import java.nio.ByteBuffer;
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
    private Set<Integer> interested;
    private Map<Integer, boolean[]> bitMap;
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
        this.interested = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
        this.bitMap = new HashMap<>();
        this.peerInfoMap = new HashMap<>();
        this.downloadRateMap = new ConcurrentHashMap<>();

        this.myBitField = new boolean[commonInfo.getFileSize() / commonInfo.getPieceSize() + 1];
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
                        Vector<Integer> tempOfInterest = new Vector<>();
                        for(int interest : interested) {
                            tempOfInterest.add(interest);
                        }
                        while (nextPreferredNeighbors.size() < commonInfo.getNumberOfPreferredNeighbors()) {
                            if (tempOfInterest.isEmpty()) {break;}
                            int randomIndex = new Random().nextInt(interested.size());
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
                Integer newOptimisticallyUnchokedNeighbor = -1;
                if (interested.size() > 0) {
                    while (true) {
                        if (interested.isEmpty()) {
                            break;
                        }
                        int randomIndex = new Random().nextInt(interested.size());
                        Iterator<Integer> iterator = interested.iterator();
                        for(int i = 0; i <= randomIndex; ++i) {
                            newOptimisticallyUnchokedNeighbor = iterator.next();
                        }
                        if (newOptimisticallyUnchokedNeighbor != optimisticallyUnchockedNeighbor &&
                                !preferredNeighbors.contains(newOptimisticallyUnchokedNeighbor)) {
                            break;
                        }
                    }
                }
                try {
                    clients.get(newOptimisticallyUnchokedNeighbor);
                    // TODO send unchoke
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!preferredNeighbors.contains(optimisticallyUnchockedNeighbor)) {
                    try {
                        clients.get(optimisticallyUnchockedNeighbor);
                        // TODO send choke
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                optimisticallyUnchockedNeighbor = newOptimisticallyUnchokedNeighbor;
                // TODO log writer
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
        if(!allFalse(myBitField)) {
            MessageFactory.bitfieldMessage(myBitField);
        }
        return peerId;
    }

    private boolean allFalse(boolean[] bitfield) {
        for(boolean b : bitfield) {
            if(b) return false;
        }
        return true;
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
                    respMessage = handleInterestedMessage(am.getPayload(), peerId);
                    break;
                case 3:
                    respMessage = handleUnInterestedMessage(am.getPayload(), peerId);
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
        if(respMessage != null) {
            clients.get(peerId).sendMessage(respMessage);
        }
    }

    private ActualMessage handleChockMessage(byte[] payload, int peerId) {
        //logger
        // TODO check request map
        return null;
    }

    private ActualMessage handleUnchockMessage(byte[] payload, int peerId) {
        // logger
        return requestPiece(peerId);
    }

    private ActualMessage handleInterestedMessage(byte[] payload, int peerId) {
//        logWriter.receiveInterestedMessage(myId, peerId);
        interested.add(peerId);
        return null;
    }

    private ActualMessage handleUnInterestedMessage(byte[] payload, int peerId) {
        if(interested.contains(peerId)) {
            interested.remove(peerId);
        }
        return null;
    }

    private ActualMessage handleHaveMessage(byte[] payload, int peerId) {
        int index = ByteBuffer.wrap(payload).getInt();
        bitMap.get(peerId)[index] = true;
        if(!myBitField[index]) {
            return MessageFactory.interestedMessage();
        }
        return null;
    }

    private ActualMessage handleBitfieldMessage(byte[] payload, int peerId) {
        bitMap.put(peerId, new boolean[commonInfo.getFileSize()/commonInfo.getPieceSize() + 1]);
        boolean interestFlag = false;
        for (int i = 0; i < payload.length; i++) {
            byte[] array = new byte[8];
            for (int k = 7; k >= 0; k--) {
                array[k] = (byte)(payload[i] & 1);
                payload[i] = (byte) (payload[i] >> 1);
            }

            for (int j = 0; j < 8; j++) {
                if (array[j] == 1) {
                    bitMap.get(peerId)[i * 8 + j] = true;
                    if (!myBitField[i * 8 + j]) interestFlag = true;
                }
            }
        }
        if (interestFlag) return MessageFactory.interestedMessage();
        return null;
    }

    private ActualMessage handleRequestMessage(byte[] payload, int peerId) {
        return MessageFactory.pieceMessage(payload);
    }

    private ActualMessage handlePieceMessage(byte[] payload, int peerId) {
        // update bitfield
        updateBitField(payload);

        // finally should send not interested to some nodes
        int len = preferredNeighbors.size();
        for(int i = 0; i < len; i++) {
            int preferredNeighborID = preferredNeighbors.get(i);
            if(hasCompleteFiles() || containAllNeighborFiles(preferredNeighborID)) {
                ActualMessage respMessage = MessageFactory.uninterestedMessage();
                clients.get(peerId).sendMessage(respMessage);
            }
        }

        if(!containAllNeighborFiles(peerId)) {
            if(preferredNeighbors.contains(peerId)) {
                return requestPiece(peerId);
            }
        }

    }

    private ActualMessage requestPiece(int peerId) {
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
        int i = new Random().nextInt(missingPieceSize);
        return MessageFactory.requestMessage(index.get(i));
    }

    public void updateBitField(byte[] payload) {
        int pieceIndex = ByteBuffer.wrap(payload).getInt();
        myBitField[pieceIndex] = true;
    }

    public boolean hasCompleteFiles() {
        for(boolean hasFile : myBitField) {
            if(hasFile == false) {
                return false;
            }
        }
        return true;
    }

    public boolean containAllNeighborFiles(int neighborId) {
        boolean[] neighborBitField = bitMap.get(neighborId);
        int len = neighborBitField.length;
        for(int i = 0; i < len; i++) {
            if(neighborBitField[i] == true) {
                if(myBitField[i] == false) {
                    return false;
                }
            }
        }
        return true;
    }

}
