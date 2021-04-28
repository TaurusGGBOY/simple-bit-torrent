package messageHandler;

import cfg.PeerInfoCfg;
import io.Client;
import message.ShakeHandMessage;
import peer.LocalPeer;
import peer.Peer;
import log.Logger;

import javax.sound.midi.Soundbank;
import java.io.IOException;

public class ShakeHandMessageHandler {
    public void handle(ShakeHandMessage msg) {
        // 情况1: 发送方接收到了返回的握手 应该发送bitfiled
        if (LocalPeer.shakingHands.contains(msg.getFrom())) {
            Logger.isConnection(LocalPeer.id, msg.getFrom());

            //这个时候才将这个人加入到peers列表中
            LocalPeer.peers.put(msg.getFrom(), PeerInfoCfg.peers.get(msg.getFrom()));

            //给他发送bitfield
            LocalPeer.bitfielding.add(msg.getFrom());
            Client.getInstance().sendBitFieldMessage(msg.getFrom());

            // 在队列中删除这个用户
            LocalPeer.shakingHands.remove(msg.getFrom());
            return;
        }

        // 情况2: 接收方第一次接收到握手信息 要做的应该是发送发送握手信息回去？然后发送bitfield
        Peer peer = PeerInfoCfg.peers.get(msg.getFrom());
        LocalPeer.peers.put(msg.getFrom(), peer);

        // 注册这个套接字
        try {
            Client.getInstance().register(peer.getID(), peer.getHostName(), peer.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 并发送握手
        Client.getInstance().sendShakeHandMessage(peer.getID());

        // 并握手
        Logger.makeConnection(LocalPeer.id, msg.getFrom());
        Logger.isConnection(LocalPeer.id, msg.getFrom());

    }
}