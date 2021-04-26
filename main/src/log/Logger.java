package log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Logger {

    public static String id;
    private static String logPath;
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void createLogFile() {
        File dir = new File("./main/log");
        if (!dir.exists()) {
            dir.mkdir();
        }
        logPath = "./main/log/peer_" + id + ".log";
        File file = new File(logPath);
        try {
            if (file.exists() && file.isFile())
                file.delete();
            file.createNewFile();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String str) {
        File file = new File(logPath);
        FileWriter fileWriter = null;

        LocalDateTime time = LocalDateTime.now();
        String localTime = df.format(time);

        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.write(localTime);
            fileWriter.write(": ");
            fileWriter.write(str);
            fileWriter.write(System.getProperty("line.separator"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void makeConnection(String localID, String remoteID) {
        log("Peer " + localID + " makes a connection to Peer " + remoteID);
    }

    public static void isConnection(String localID, String remoteID) {
        log("Peer " + localID + " is connected from Peer " + remoteID);
    }

    public static void changePrefered(String localID, List<String> neighbors) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Peer " + localID + " has the preferred neighbors ");
        for (int i = 0; i < neighbors.size() - 1; i++) {
            stringBuilder.append(neighbors.get(i));
            stringBuilder.append(",");
        }
        stringBuilder.append(neighbors.get(neighbors.size() - 1));
        log(stringBuilder.toString());
    }

    public static void changeOpt(String localID, String remoteID) {
        log("Peer " + localID + " has the optimistically unchoked neighbor " + remoteID);
    }

    public static void receiveBitfield(String localID, String remoteID) {
        log("Peer " + localID + " receive bitfield from " + remoteID);
    }

    public static void receiveUnchoke(String localID, String remoteID) {
        log("Peer " + localID + " is unchoked by " + remoteID);
    }

    public static void receiveChoke(String localID, String remoteID) {
        log("Peer " + localID + " is choked by " + remoteID);
    }

    public static void receiveHave(String localID, String remoteID, int index) {
        log("Peer " + localID + " received the 'have' message from " + remoteID + " for the piece " + index);
    }

    public static void receiveInterested(String localID, String remoteID) {
        log("Peer " + localID + " received the 'interested' message from " + remoteID);
    }

    public static void receiveNotInterested(String localID, String remoteID) {
        log("Peer " + localID + " received the 'not interested' message from " + remoteID);
    }

    public static void finishPiece(String localID, String remoteID, int index, int numberOfPieces) {
        log("Peer " + localID + " has downloaded the piece " + index + "  from " + remoteID
                + ". Now the number of pieces it has is " + numberOfPieces);
    }

    public static void finishFile(String remoteID) {
        log("Peer " + remoteID + " has downloaded the complete file");
    }
}