package test;

import cfg.CommonCfg;
import file.PieceFile;

import java.io.File;

public class FileTest {
    private static void test1() {
        System.out.println(System.getProperty("user.dir"));
        File file = new File("./main/cpabe.rar");
        System.out.println(file.getParent());
        try {
            CommonCfg.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PieceFile.nioSpilt(file, CommonCfg.maxPieceNum, file.getParent() + File.separator + "piece", 32768);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test1();
    }
}