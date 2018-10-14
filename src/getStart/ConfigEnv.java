package getStart;

import com.jcraft.jsch.*;

public class ConfigEnv {
    public static void configSSH(PeerInfo pInfo) {
        String userName = "jiangchu";
        JSch jsch = new JSch();
        Session session;
        String privateKeyPath = "~/.ssh/id_rsa";
        String knownHostPath = "~/.ssh/known_hosts";
        try {
            jsch.addIdentity(privateKeyPath);
            jsch.setKnownHosts(knownHostPath);
            session = jsch.getSession(userName, pInfo.peerAddress, 22);
            session.setConfig("PreferredAuthentications", "publickey");
            session.setConfig("StrictHostKeyChecking", "no");
        } catch (JSchException e) {
            throw new RuntimeException("Failed to create Jsch Session object.", e);
        }

        try {
            session.connect();
            Channel channel = session.openChannel("shell");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();
            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }
}
