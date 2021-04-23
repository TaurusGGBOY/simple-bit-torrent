package cfg;

import peer.Peer;

import java.io.*;
import java.util.Map;

public class PeerInfoCfg {

    // TODO 这个路径是否正确还未知
    private final static String filePath = "/PeerInfo.cfg";

    public static Map<Integer, Peer> peers;

    public static void read() throws IOException {
        File file = new File(filePath);
        BufferedReader fileReader = new BufferedReader (new FileReader(file));
        String line;
        while ((line = fileReader.readLine()) != null) {
            String[] splits = line.split(" ");
            Peer peer = new Peer();
            peer.setID(Integer.valueOf(splits[0]));

        }
    }
}