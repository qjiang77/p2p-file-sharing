import configs.CommonInfo;
import configs.PeerInfo;

import java.util.ArrayList;
import java.util.List;

public class PeerProcess {
    public static void main(String[] args) {
        if(args == null || args.length == 0) {
            System.out.println("expect argument peer ID");
        }
        CommonInfo commonInfo = new CommonInfo("Common.cfg");
        List<PeerInfo> peerList = new ArrayList<>();
        int peerId = Integer.parseInt(args[0]);
        Peer peer = new Peer(peerId, commonInfo);
        peer.start(peerList);
    }
}
