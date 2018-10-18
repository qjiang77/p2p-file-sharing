import configs.CommonInfo;
import configs.PeerInfo;

import java.util.BitSet;
import java.util.List;

public class Peer {
    private PeerInfo peerInfo;
    private CommonInfo commonInfo;
    private List<PeerInfo> peerList;



    Peer(int peerId, CommonInfo commonInfo) {
        this.peerInfo = new PeerInfo(peerId);
        this.commonInfo = commonInfo;
    }

    public void start(List<PeerInfo> peerList) {

    }
}
