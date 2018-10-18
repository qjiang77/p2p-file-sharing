import configs.CommonInfo;
import configs.PeerInfo;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class Peer {
    private PeerInfo peerInfo;
    private CommonInfo commonInfo;
    private Map<Integer, PeerInfo> peerMap;

    Peer(int peerId, CommonInfo commonInfo) {
        this.peerInfo = new PeerInfo(peerId);
        this.commonInfo = commonInfo;

        // init bit field

    }

    public void start(List<PeerInfo> peerList) {
        // load neighbor info
        for(PeerInfo info : peerList) {
            peerMap.put(info.getPeerId(), info);
        }

        // load this peer config
        

    }
}
