package messageHandler;

import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import util.ByteUtil;

import java.util.Arrays;
import java.util.Set;

public class PieceMessageHandler {
    public void handle(ActualMessage msg) {
        byte[] payload = msg.getPayload();
        int index = ByteUtil.byteArrayToInt(Arrays.copyOfRange(payload, 0, 4));

        LocalPeer.localUser.pieces.add(index);
        LocalPeer.pieceWaitingMap.remove(index);
        // TODO 保存piece

        // TODO 遍历看还对老哥感兴趣否
        int flag = 0;
        for (int piece : LocalPeer.peers.get(msg.getPeerID()).pieces) {
            if (!LocalPeer.localUser.pieces.contains(piece)) {
                flag = 1;
                // TODO 随机选一个块发送
                break;
            }
        }
    }
}