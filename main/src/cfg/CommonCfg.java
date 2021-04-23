package cfg;

import java.io.IOException;

public class CommonCfg {

    private final static String filePath = "/Common.cfg";

    // TODO 这个文件名字和文件大小是自己指定的？
    public static int NumberOfPreferredNeighbors;
    public static int UnchokingInterval;
    public static int OptimisticUnchokingInterval;
    public static String FileName;
    public static int FileSize;
    public static int PieceSize;

    // TODO 这个时候构造函数写不写？怎么写？有什么模式？有单例吗

    // TODO 读配置
    public static void read() throws IOException {

    }

}