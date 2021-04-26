package messageHandler;

import cfg.PeerInfoCfg;
import io.Client;
import message.ShakeHandMessage;
import peer.LocalPeer;
import peer.Peer;
import log.Logger;

import java.util.Collections;

public class ShakeHandMessageHandler {
    public void handle(ShakeHandMessage msg) {
        // 检测是否给他发过了 如果发过了 就说明是feedback
        if (LocalPeer.peers.containsKey(msg.getPeerID())) {
            Logger.isConnection(LocalPeer.id,msg.getPeerID());

            //这个时候才将这个人加入到peers列表中
            LocalPeer.peers.put(msg.getPeerID(), PeerInfoCfg.peers.get(msg.getPeerID()));

            //给他发送bitfield
            Client.getInstance().sendBitFieldMessage(msg.getPeerID());
            return;
        }

        //如果没有发过 说明是来握手的 加入到本地的peers里面
        Peer peer = PeerInfoCfg.peers.get(msg.getPeerID());
        LocalPeer.peers.put(msg.getPeerID(), peer);

        // 注册这个套接字
        Client.getInstance().register(peer.getID(), peer.getHostName(), peer.getPort());

        // 并握手
        Logger.makeConnection(LocalPeer.id,msg.getPeerID());
        Client.getInstance().shakeHands(peer.getID());

        // 并发送bitfield
        Client.getInstance().sendBitFieldMessage(peer.getID());
    }
}