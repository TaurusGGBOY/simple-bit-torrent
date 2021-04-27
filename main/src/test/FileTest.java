package test;

import cfg.CommonCfg;
import file.PieceFile;

import java.io.File;
import java.io.IOException;

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

    private static void test2() {
        try {
            CommonCfg.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 1001; i <= 1005; i++) {
            PieceFile.merge("cpabe.rar", CommonCfg.maxPieceNum, String.valueOf(i));
        }

    }

    public static void main(String[] args) {

//        test1();
        test2();
    }
}