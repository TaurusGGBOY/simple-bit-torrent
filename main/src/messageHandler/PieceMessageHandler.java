package messageHandler;

import cfg.CommonCfg;
import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import util.ByteUtil;
import util.Logger;

import java.util.*;

public class PieceMessageHandler {
    public void handle(ActualMessage msg) {
        byte[] payload = msg.getPayload();
        int index = ByteUtil.byteArrayToInt(Arrays.copyOfRange(payload, 0, 4));

        LocalPeer.localUser.pieces.add(index);

        // 检查是否结束
        LocalPeer.checkFinish();

        LocalPeer.pieceWaitingMap.remove(index);
        // TODO 保存piece

        //log
        Logger.finishPiece(LocalPeer.id, msg.getPeerID(), index, LocalPeer.localUser.pieces.size());
        if (LocalPeer.localUser.pieces.size() >= CommonCfg.maxPieceNum) {
            Logger.finishFile(LocalPeer.id);
        }

        // 如果choke了 就停止发request
        if (LocalPeer.peers.get(msg.getPeerID()).isChoke()) {
            return;
        }

        // 遍历看还对远方感兴趣否
        List<Integer> list = new ArrayList<>();

        for (int piece : LocalPeer.peers.get(msg.getPeerID()).pieces) {
            if (!LocalPeer.localUser.pieces.contains(piece)) {
                list.add(piece);
            }
        }

        if (list.size() <= 0) {
            return;
        }

        Client.getInstance().sendRequestMessage(msg.getPeerID(), list.get(new Random().nextInt(list.size())));
    }
}