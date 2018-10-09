import java.io.*;
import java.util.*;

public class StartRemotePeers {

	public Vector<RemotePeerInfo> peerInfoVector;
	
	public void getConfiguration()
	{
		String st;
		int i1;
		peerInfoVector = new Vector<>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while((st = in.readLine()) != null) {
				
				 String[] tokens = st.split("\\s+");
			    
			     peerInfoVector.addElement(new RemotePeerInfo(tokens[0], tokens[1], tokens[2]));
			
			}
			
			in.close();
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			StartRemotePeers myStart = new StartRemotePeers();
			myStart.getConfiguration();

			// get user name
			String userName = "jiangchu";
			// get current path
			String path = System.getProperty("user.dir");

			// start clients at remote hosts
			for (int i = 0; i < myStart.peerInfoVector.size(); i++) {
				RemotePeerInfo pInfo = myStart.peerInfoVector.elementAt(i);

				// comment this line if already copied SSH keys
				ConfigEnv.configSSH(pInfo);
				
				System.out.println("Start remote peer " + pInfo.peerId +  " at " + pInfo.peerAddress );
				Runtime.getRuntime().exec("ssh " + userName + "@" + pInfo.peerAddress + " mkdir " + pInfo.peerAddress);
//				Runtime.getRuntime().exec("ssh " + userName + pInfo.peerAddress + " cd " + path + "; java peerProcess " + pInfo.peerId);
			}
			System.out.println("Starting all remote peers has done." );

		}
		catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
