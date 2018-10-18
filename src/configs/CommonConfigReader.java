package configs;

import java.io.BufferedReader;
import java.io.FileReader;

public class CommonConfigReader {
    public static CommonInfo readCommonConfig(String commonConfigDir) {
        String st;
        CommonInfo commonInfo = new CommonInfo();
        try {
            BufferedReader in = new BufferedReader(new FileReader(commonConfigDir));
            while ((st = in.readLine()) != null) {
                String[] tokens = st.split("\\s+");
                switch (tokens[0]) {
                    case "NumberOfPreferredNeighbors":
                        commonInfo.setNumberOfPreferredNeighbors(Integer.parseInt(tokens[1]));
                        break;
                    case "UnchokingInterval":
                        commonInfo.setUnchokingInterval(Integer.parseInt(tokens[1]));
                        break;
                    case "OptimisticUnchokingInterval":
                        commonInfo.setOptimisticUnchokingInterval(Integer.parseInt(tokens[1]));
                        break;
                    case "FileName":
                        commonInfo.setFileName(tokens[1]);
                        break;
                    case "FileSize":
                        commonInfo.setFileSize(Integer.parseInt(tokens[1]));
                        break;
                    case "PieceSize":
                        commonInfo.setPieceSize(Integer.parseInt(tokens[1]));
                        break;
                    default:
                        throw (new Exception("wrong common config entry: " + tokens[1]));
                }
            }
            in.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return commonInfo;
    }
}
