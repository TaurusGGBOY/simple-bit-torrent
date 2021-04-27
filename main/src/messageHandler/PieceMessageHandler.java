package messageHandler;

import cfg.CommonCfg;
import file.PieceFile;
import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import sun.security.util.Length;
import util.ByteUtil;
import log.Logger;

import java.util.*;

public class PieceMessageHandler {
    public void handle(ActualMessage msg) {
        byte[] payload = msg.getPayload();
        int index = ByteUtil.byteArrayToInt(Arrays.copyOfRange(payload, 0, 4));

        LocalPeer.localUser.pieces.add(index);

        // 检查是否结束
        LocalPeer.checkFinish();

        // TODO 添加定时任务清除
        LocalPeer.pieceWaitingMap.remove(index);

        // 保存piece
        PieceFile.savePiece(index, LocalPeer.id, Arrays.copyOfRange(payload, 4, 4 + msg.getLen() - 1));

        //log
        Logger.finishPiece(LocalPeer.id, msg.getFrom(), index, LocalPeer.localUser.pieces.size());
        if (LocalPeer.localUser.pieces.size() >= CommonCfg.maxPieceNum) {
            Logger.finishFile(LocalPeer.id);
        }

        // 给除了接收方的其他所有老哥询问have消息
        LocalPeer.peers.entrySet().stream()
                .filter((entry) -> !msg.getFrom().equals(entry.getKey()) && !entry.getValue().pieces.contains(index))
                .forEach((entry -> Client.getInstance().sendHaveMessage(entry.getKey(), index)));

        // 如果choke了 就停止发request
        if (LocalPeer.peers.get(msg.getFrom()).isChoke()) {
            return;
        }

        // 遍历看还对远方感兴趣否
        List<Integer> list = new ArrayList<>();

        for (int piece : LocalPeer.peers.get(msg.getFrom()).pieces) {
            if (!LocalPeer.localUser.pieces.contains(piece) && !LocalPeer.pieceWaitingMap.containsKey(piece)) {
                list.add(piece);
            }
        }

        if (list.size() <= 0) {
            return;
        }

        int random = new Random().nextInt(list.size());
        Client.getInstance().sendRequestMessage(msg.getFrom(), list.get(random));
    }
}