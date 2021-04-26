package messageHandler;

import cfg.CommonCfg;
import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import util.ByteUtil;
import log.Logger;

import java.util.Set;

public class HaveMessageHandler {
    public void handle(ActualMessage msg) {
        int index = ByteUtil.byteArrayToInt(msg.getPayload());

        // log
        Logger.receiveHave(LocalPeer.id, msg.getSendTo(),index);


        // 更新该人的set
        Set<Integer> pieces = LocalPeer.peers.get(msg.getSendTo()).pieces;
        pieces.add(index);

        // 如果该peer都有了 log
        if (pieces.size() >= CommonCfg.maxPieceNum) {
            Logger.finishFile(msg.getSendTo());
        }

        // 如果有感兴趣的就发送感兴趣
        if (!LocalPeer.localUser.pieces.contains(index)) {
            Client.getInstance().sendInterestedMessage(msg.getSendTo());
        }

        // 检查是否结束
        LocalPeer.checkFinish();
    }
}