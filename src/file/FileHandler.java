package file;

import java.io.File;

public class FileHandler {
    public void createSubdir(String peer_id) {
        String dirName = "peer_" + peer_id;
        File dir = new File(dirName);
        if(!dir.exists()) {
            dir.mkdir();
        }
    }
}
