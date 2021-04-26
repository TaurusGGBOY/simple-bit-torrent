package messageHandler;

import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import util.ByteUtil;

public class RequestMessageHandler {
    public void handle(ActualMessage msg) {
        if (LocalPeer.peers.get(msg.getFrom()).isChoke()) {
            return;
        }
        int index = ByteUtil.byteArrayToInt(msg.getPayload());

        // TODO 读取piece
        byte[] piece = null;

        // 发送给远方
        Client.getInstance().sendPieceMessage(msg.getFrom(), index, piece);
    }
}