package messageHandler;

import message.ActualMessage;
import peer.LocalPeer;
import util.Logger;

public class NotInterestedMessageHandler {
    public void handle(ActualMessage msg) {
        LocalPeer.peers.get(msg.getPeerID()).setInterstedInLocal(false);

        // log NotInterested
        Logger.receiveNotInterested(LocalPeer.id, msg.getPeerID());
    }
}