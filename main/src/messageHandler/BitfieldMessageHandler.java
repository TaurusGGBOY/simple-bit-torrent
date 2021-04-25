package messageHandler;

import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import peer.Peer;

import java.util.Set;

public class BitfieldMessageHandler {
    public void handle(ActualMessage msg) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < msg.getPayload().length; i++) {
            for (int j = 7; j >= 0; j--) {
                sb.append((msg.getPayload()[i] >> j) & 0x1);
            }
        }

        // 跟新该peer的pieces set
        char[] chars = sb.toString().toCharArray();
        Set<Integer> pieces = LocalPeer.peers.get(msg.getPeerID()).pieces;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '1') {
                pieces.add(i);
            }
        }

        // 遍历收到的bitfield 如果有感兴趣的就发送感兴趣
        for (int piece : pieces) {
            if (!LocalPeer.localUser.pieces.contains(piece)) {
                Client.getInstance().sendInterestedMessage(msg.getPeerID());
                break;
            }
        }
    }
}