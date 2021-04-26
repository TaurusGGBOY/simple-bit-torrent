package messageHandler;

import message.ActualMessage;
import peer.LocalPeer;
import log.Logger;

public class NotInterestedMessageHandler {
    public void handle(ActualMessage msg) {
        LocalPeer.peers.get(msg.getSendTo()).setInterstedInLocal(false);

        // log NotInterested
        Logger.receiveNotInterested(LocalPeer.id, msg.getSendTo());
    }
}