package messageHandler;

import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import peer.Peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnChokeMessageHandler {
    public void handle(ActualMessage msg) {
        Peer remotePeer = LocalPeer.peers.get(msg.getPeerID());
        remotePeer.setChoke(false);

        List<Integer> list = new ArrayList<>();
        for (int piece : remotePeer.pieces) {
            if (!LocalPeer.localUser.pieces.contains(piece)) {
                list.add(piece);
            }
        }

        // 如果没有想要的就直接返回就好
        if (list.size() <= 0) {
            return;
        }

        Client.getInstance().sendRequestMessage(msg.getPeerID(), list.get(new Random().nextInt(list.size())));

    }
}