package file;

import configs.CommonInfo;

import java.io.*;

public class FileHandler {
    CommonInfo commonInfo;

    public FileHandler(CommonInfo commonInfo){
        this.commonInfo = commonInfo;
    }
    public void createSubdir(int peerID) {
        String dirName = "peer_" + peerID;
        File dir = new File(dirName);
        if(!dir.exists()) {
            dir.mkdir();
        }
    }

    public void createFile(int peerID) {
        createSubdir(peerID);
        String filename = "peer_" + peerID + "/TheFile";
        File file = new File(filename);

        try {
            if(!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public byte[] readPiece(int pieceIndex, int readFromID) throws IOException {
        String filename = "peer_" + readFromID + "/TheFile";
        File myFile = new File(filename);
        if(!myFile.exists()) {
            createFile(readFromID);
        }

        int pieceSize = commonInfo.getPieceSize();
        int fileSize = commonInfo.getFileSize();
        RandomAccessFile read = new RandomAccessFile(myFile, "r");
        int offset = pieceIndex * pieceSize;

        byte[] buffer;
        int remainingBytes = fileSize - pieceIndex * pieceSize;
        if (remainingBytes < pieceSize) {
            buffer = new byte[remainingBytes];
        } else {
            buffer = new byte[pieceSize];
        }
        int length = buffer.length;
        read.read(buffer, offset, length);
        read.close();
        return buffer;
    }


    public void writePiece(int pieceIndex, int writeToID, byte[] pieceData) throws IOException {
        String filename = "peer_" + writeToID + "/TheFile";
        File myFile = new File(filename);
        if(!myFile.exists()) {
            createFile(writeToID);
        }

        int pieceSize = commonInfo.getPieceSize();
        RandomAccessFile read = new RandomAccessFile(myFile, "rws");
        int offset = pieceIndex * pieceSize;
        int length = pieceData.length;
        read.write(pieceData, offset, length);
        read.close();
    }
}
