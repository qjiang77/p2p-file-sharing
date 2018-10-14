package configs;

public class PeerInfo {
	private int peerId;
	private String peerAddress;
	private int peerPort;
	private boolean hasFile;
	
	public PeerInfo(int pId, String pAddress, int pPort, boolean hasFile) {
		this.peerId = pId;
		this.peerAddress = pAddress;
		this.peerPort = pPort;
		this.hasFile = hasFile;
	}

	public int getPeerId() {
		return peerId;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public int getPeerPort() {
		return peerPort;
	}

	public boolean isHasFile() {
		return hasFile;
	}
}
