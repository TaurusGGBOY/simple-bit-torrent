package file;

import cfg.CommonCfg;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class PieceFile {

    // 保存piece
    public static byte[] readPiece(int index, String id) {
        String currentDir = "./main/piece/id_" + id + File.separator + "piece_" + index;
        try {
            // 创建一个bytebuffer内存块
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 33);
            // 创建文件输出流
            FileInputStream fileInputStream = new FileInputStream(currentDir);
            // 创建文件通道
            FileChannel fileChannel0 = fileInputStream.getChannel();

            // 将bytebuffer中的内容写入到通道中
            fileChannel0.read(byteBuffer);
            // 关闭资源
            fileChannel0.close();
            fileInputStream.close();
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    // 保存piece
    public static void savePiece(int index, String id, byte[] bytes) {
        // 创建文件夹
        String dir = "./main/piece/id_" + id;
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        String filePath = "./main/piece/id_" + id + File.separator + "piece_" + index;
        File file = new File(filePath);
        try {
            file.createNewFile();
            // 创建一个bytebuffer内存块
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 32 * 2);
            // 将字符串内容存入bytebuffer
            byteBuffer.put(bytes);
            // 创建文件输出流
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            // 创建文件通道
            FileChannel fileChannel0 = fileOutputStream.getChannel();
            // 切换到读模式
            byteBuffer.flip();
            // 将bytebuffer中的内容写入到通道中
            fileChannel0.write(byteBuffer);
            // 关闭资源
            fileChannel0.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 文件分片
    public static void spilt(String filePath, int splitNum, double subSize, String id) {
        String currentDir = "./main/piece/id_" + id;
        File dir = new File(currentDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            nioSpilt(new File(filePath), splitNum, currentDir, CommonCfg.pieceSize);
        } catch (Exception e) {
            e.printStackTrace();
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
        for (int i = 0; i < splitNum; i++) {
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
            raf = new RandomAccessFile(new File(piecePath + File.separator + afterName), "rw");
            //开始合并文件，对应切片的二进制文件
            for (int i = 0; i < pieceCount; i++) {
                //读取切片文件
                RandomAccessFile reader = new RandomAccessFile(new File(piecePath + File.separator + "piece_" + i), "r");
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

    public static void removeDir(String path) {

        File f = new File(path);
        if (f.isDirectory()) {//如果是目录，先递归删除
            String[] list = f.list();
            for (int i = 0; i < list.length; i++) {
                removeDir(path + "//" + list[i]);//先删除目录下的文件
            }
        }
        f.delete();
    }
}