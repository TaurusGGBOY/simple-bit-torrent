package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class PieceFile {
    //splitNum:要分几片，currentDir：分片后存放的位置，subSize：按多大分片

    public static void nioSpilt(File file, int splitNum, String currentDir, double subSize) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        FileChannel inputChannel = fis.getChannel();
        FileOutputStream fos;
        FileChannel outputChannel;
        long splitSize = (long) subSize;
        long startPoint = 0;
        long endPoint = splitSize;
        File pieceDir = new File(currentDir);
        if (!pieceDir.exists()) {
            pieceDir.mkdir();
        }
        for (int i = 1; i <= splitNum; i++) {
            fos = new FileOutputStream(currentDir + File.separator + "piece_" + i);
            outputChannel = fos.getChannel();
            inputChannel.transferTo(startPoint, splitSize, outputChannel);
            startPoint += splitSize;
            endPoint += splitSize;
            outputChannel.close();
            fos.close();
        }
        inputChannel.close();
        fis.close();
    }
}