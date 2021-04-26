package cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class CommonCfg {

    private final static String filePath = "./main/Common.cfg";

    // TODO 这个文件名字和文件大小是自己指定的？
    public static int numberOfPreferredNeighbors;
    public static int unchokingInterval;
    public static int optimisticUnchokingInterval;
    public static String fileName;
    public static int fileSize;
    public static int pieceSize;

    public static int maxPieceNum;

    public static void read() throws IOException {
        read(filePath);
    }

    public static void read(String Path) throws IOException {
        File file = new File(Path);
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String line;

        // 先读取属性
        Properties properties = new Properties();
        while ((line = fileReader.readLine()) != null) {
            String[] strs = line.split(" ");
            properties.setProperty(strs[0], strs[1]);
        }

        // 然后将属性写入
        try {
            numberOfPreferredNeighbors = Integer.parseInt(properties.getProperty("NumberOfPreferredNeighbors"));
            unchokingInterval = Integer.parseInt(properties.getProperty("UnchokingInterval"));
            optimisticUnchokingInterval = Integer.parseInt(properties.getProperty("OptimisticUnchokingInterval"));
            fileName = properties.getProperty("NumberOfPreferredNeighbors");
            fileSize = Integer.parseInt(properties.getProperty("FileSize"));
            pieceSize = Integer.parseInt(properties.getProperty("PieceSize"));
            maxPieceNum = (int) Math.ceil(fileSize * 1.0f / pieceSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}