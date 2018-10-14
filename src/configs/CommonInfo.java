package configs;

import java.io.BufferedReader;
import java.io.FileReader;

public class CommonInfo {

    private int numberOfPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private String fileName;
    private int fileSize;
    private int pieceSize;

    public CommonInfo(String commonConfigDir) {
        String st;
        try {
            BufferedReader in = new BufferedReader(new FileReader(commonConfigDir));
            while ((st = in.readLine()) != null) {
                String[] tokens = st.split("\\s+");
                switch (tokens[0]) {
                    case "NumberOfPreferredNeighbors":
                        numberOfPreferredNeighbors = Integer.parseInt(tokens[1]);
                        break;
                    case "UnchokingInterval":
                        unchokingInterval = Integer.parseInt(tokens[1]);
                        break;
                    case "OptimisticUnchokingInterval":
                        optimisticUnchokingInterval = Integer.parseInt(tokens[1]);
                        break;
                    case "FileName":
                        fileName = tokens[1];
                        break;
                    case "FileSize":
                        fileSize = Integer.parseInt(tokens[1]);
                        break;
                    case "PieceSize":
                        pieceSize = Integer.parseInt(tokens[1]);
                        break;
                    default:
                        throw (new Exception("wrong common config entry: " + tokens[1]));
                }
            }
            in.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public int getNumberOfPreferredNeighbors() {
        return numberOfPreferredNeighbors;
    }

    public int getUnchokingInterval() {
        return unchokingInterval;
    }

    public int getOptimisticUnchokingInterval() {
        return optimisticUnchokingInterval;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getPieceSize() {
        return pieceSize;
    }
}
