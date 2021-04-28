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

        // 等待队列中去掉
        LocalPeer.pieceWaitingMap.remove(index);

        // 保存piece
        PieceFile.savePiece(index, LocalPeer.id, Arrays.copyOfRange(payload, 4, msg.getLen() - 1));

        LocalPeer.localUser.pieces.add(index);


        //log
        Logger.finishPiece(LocalPeer.id, msg.getFrom(), index, LocalPeer.localUser.pieces.size());
        if (LocalPeer.localUser.pieces.size() >= CommonCfg.maxPieceNum) {
            Logger.finishFile(LocalPeer.id);
        }

        // 给所有老哥发送have消息 包括接收方
        LocalPeer.peers.entrySet().stream()
                .forEach((entry -> Client.getInstance().sendHaveMessage(entry.getKey(), index)));

        // 检查是否结束
        LocalPeer.checkFinish();

        // 给不感兴趣的人发不感兴趣
        LocalPeer.peers.entrySet().stream().filter((entry) -> {
            // 如果有我感兴趣的就不发，如果没有就发不感兴趣
            for (int piece : entry.getValue().pieces) {
                if (!LocalPeer.localUser.pieces.contains(piece)) {
                    return false;
                }
            }
            return true;
        }).forEach((entry) -> Client.getInstance().sendNotInterestedMessage(entry.getKey()));

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

        // 不感兴趣 就停止发request
        if (list.size() <= 0) {
            return;
        }


        int random = new Random().nextInt(list.size());
        Client.getInstance().sendRequestMessage(msg.getFrom(), list.get(random));
    }
}