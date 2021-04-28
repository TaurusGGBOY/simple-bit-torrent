package cfg;

import peer.Peer;

import java.io.*;
import java.util.LinkedHashMap;

public class PeerInfoCfg {
    private final static String filePath = "./main/PeerInfo.cfg";

    public static LinkedHashMap<String, Peer> peers = new LinkedHashMap<>();

    /**
     * 默认从filePath读取PeerInfo
     * @throws IOException
     */
    public static void read() throws IOException {
        read(filePath);
    }

    /**
     * 从Path读取PeerInfo
     * @param Path
     * @throws IOException
     */
    public static void read(String Path) throws IOException {
        File file = new File(Path);
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = fileReader.readLine()) != null) {
            String[] strs = line.split("\\s+");
            Peer peer = new Peer();
            peer.setID(strs[0]);
            peer.setHostName(strs[1]);
            peer.setPort(Integer.parseInt(strs[2]));
            peer.setHasFileOrNot(strs[3].equals("1"));
            peers.put(strs[0], peer);
        }
    }

}