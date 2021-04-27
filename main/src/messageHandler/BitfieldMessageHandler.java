package messageHandler;

import cfg.CommonCfg;
import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import log.Logger;
import util.ByteUtil;

import java.util.Set;

public class BitfieldMessageHandler {
    public void handle(ActualMessage msg) {
        String from = msg.getFrom();
        Logger.receiveBitfield(LocalPeer.id, from);

        // TODO 收到bitfiled之后 怎么转换成index
        int bitfieldLen = msg.getLen() - 1;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bitfieldLen; i++) {
            sb.append(ByteUtil.getBit(msg.getPayload()[i]));
        }

        // 更新该peer的pieces set
        char[] chars = sb.toString().toCharArray();
        Set<Integer> pieces = LocalPeer.peers.get(from).pieces;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '1') {
                pieces.add(i);
            }
        }

        // 如果该peer都有了 log
        if (pieces.size() >= CommonCfg.maxPieceNum) {
            Logger.finishFile(msg.getFrom());
        }

        // 遍历收到的bitfield 如果有感兴趣的就发送感兴趣
        boolean interested = false;
        for (int piece : pieces) {
            if (!LocalPeer.localUser.pieces.contains(piece)) {
                Client.getInstance().sendInterestedMessage(from);
                interested = true;
                break;
            }
        }

        // 如果不感兴趣就发送不感兴趣
        if (!interested) {
            Client.getInstance().sendNotInterestedMessage(from);
        }

        // 检查是否结束
        LocalPeer.checkFinish();
    }
}