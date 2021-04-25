package messageHandler;

import cfg.PeerInfoCfg;
import io.Client;
import message.ShakeHandMessage;
import peer.LocalPeer;
import peer.Peer;

import java.net.Socket;

public class ShakeHandMessageHandler {
    public void handle(ShakeHandMessage msg) {
        // 检测是否给他发过了 如果发过了 就说明是feedback 不用管
        if (LocalPeer.peers.containsKey(msg.getPeerID())) {
            Client.getInstance().sendBitFieldMessage(msg.getPeerID());
            return;
        }

        //如果没有发过 说明是来握手的 加入到本地的peers里面
        Peer peer = new Peer();
        peer.setID(msg.getPeerID());
        peer.setHostName(PeerInfoCfg.peers.get(msg.getPeerID()).getHostName());
        peer.setPort(PeerInfoCfg.peers.get(msg.getPeerID()).getPort());
        peer.setHasFileOrNot(PeerInfoCfg.peers.get(msg.getPeerID()).isHasFileOrNot());
        LocalPeer.peers.put(msg.getPeerID(), peer);

        // 注册这个套接字
        Client.getInstance().register(peer.getID(), peer.getHostName(), peer.getPort());

        // 并握手
        Client.getInstance().shakeHands(peer.getID());

        // 并发送bitfield
        Client.getInstance().sendBitFieldMessage(peer.getID());
    }
}