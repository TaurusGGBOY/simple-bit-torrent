package test;

import log.Logger;

import java.util.ArrayList;
import java.util.List;

public class LoggerTest {

    public static void logTest() {
        String str = "testString";
        String id = "1001";
        Logger.id = id;
        Logger.createLogFileByID(id);
        Logger.log(str);
        Logger.log(str);
        Logger.log(str);
        Logger.log(str);
    }

    public static void logTest2() {
        String str = "testString";
        String id = "1002";
        Logger.id = id;
        Logger.createLogFileByID(id);
        Logger.log(str);
    }

    public static void logTest3() {
        String str = "testString";
        String localID = "1001";
        String remoteID = "1002";
        int index = 4;
        int number = 5;
        List<String> list = new ArrayList<>();
        list.add("1001");
        list.add("1002");
        list.add("1003");

        Logger.id = localID;
        Logger.createLogFileByID(localID);

        Logger.changeOpt(localID, remoteID);
        Logger.finishFile(remoteID);
        Logger.finishPiece(localID, remoteID, index, number);
        Logger.changeOpt(localID, remoteID);
        Logger.changePrefered(localID, list);
        Logger.isConnection(localID, remoteID);
        Logger.makeConnection(localID, remoteID);
        Logger.receiveChoke(localID, remoteID);
        Logger.receiveUnchoke(localID, remoteID);
        Logger.receiveHave(localID, remoteID, index);
        Logger.receiveInterested(localID, remoteID);
        Logger.receiveNotInterested(localID, remoteID);
    }

    public static void main(String[] args) {
//        System.out.println(LocalDateTime.now());
        logTest3();
    }
}