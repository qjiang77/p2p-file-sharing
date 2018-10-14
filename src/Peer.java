import configs.PeerInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

public class Peer {
    private PeerInfo peerInfo;
    private Vector<PeerInfo> neighborPeerInfo;

    Peer() {
        BufferedReader input = new BufferedReader(new FileReader("configs.PeerInfo.cfg"));
    }

    public PeerInfo getPeerInfo() {
        return PeerInfo;
    }

    public Vector<PeerInfo> getNeighborPeerInfo() {
        return neighborPeerInfo;
    }
}
