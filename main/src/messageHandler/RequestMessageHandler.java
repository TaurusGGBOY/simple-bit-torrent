package messageHandler;

import file.PieceFile;
import io.Client;
import log.Logger;
import message.ActualMessage;
import peer.LocalPeer;
import util.ByteUtil;

public class RequestMessageHandler {
    /**
     *
     * @param msg
     */
    public void handle(ActualMessage msg) {
        int index = ByteUtil.byteArrayToInt(msg.getPayload());

        Logger.receiveRequest(LocalPeer.id, msg.getFrom(), index);

        // 如果选举后远方peer成为choke 那就不发送了
        if (LocalPeer.peers.get(msg.getFrom()).isChoke()) {
            return;
        }

        // 读取piece
        byte[] piece = PieceFile.readPiece(index, LocalPeer.id);

        // 发送给远方
        Client.getInstance().sendPieceMessage(msg.getFrom(), index, piece);

    }
}