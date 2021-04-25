package messageHandler;

import message.ActualMessage;
import peer.LocalPeer;

public class NotInterestedMessageHandler {
    public void handle(ActualMessage msg) {
        LocalPeer.peers.get(msg.getPeerID()).setInterstedInLocal(false);
    }
}