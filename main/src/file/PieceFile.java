package file;

import cfg.CommonCfg;

import java.io.*;
import java.nio.channels.FileChannel;

public class PieceFile {
    public static String currentDir;

    // 文件分片
    public static void spilt(String filePath, int splitNum, double subSize, String id) {
        currentDir = "./main/piece/id_" + id;
        File dir = new File(currentDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            nioSpilt(new File(filePath), splitNum, currentDir, CommonCfg.pieceSize);
        } catch (Exception e) {
        }
    }

    // 合并
    public static void merge(String afterName, int pieceCount, String id) {
        String piecePath = "./main/piece/id_" + id;
        mergeFile(afterName, piecePath, pieceCount);
    }


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

    /**
     * 文件合并
     *
     * @param afterName  指定合并文件
     * @param piecePath  分割前的文件名
     * @param pieceCount 文件个数
     */
    public static void mergeFile(String afterName, String piecePath, int pieceCount) {
        RandomAccessFile raf = null;
        try {
            //申明随机读取文件RandomAccessFile
            raf = new RandomAccessFile(new File(afterName), "rw");
            //开始合并文件，对应切片的二进制文件
            for (int i = 0; i < pieceCount; i++) {
                //读取切片文件
                RandomAccessFile reader = new RandomAccessFile(new File(piecePath + "_" + i), "r");
                byte[] b = new byte[1024];
                int n = 0;
                //先读后写
                while ((n = reader.read(b)) != -1) {//读
                    raf.write(b, 0, n);//写
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}