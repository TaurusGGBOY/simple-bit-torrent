package messageHandler;

import message.ActualMessage;
import peer.LocalPeer;
import log.Logger;

public class InterestedMessageHandler {
    public void handle(ActualMessage msg) {
        LocalPeer.peers.get(msg.getSendTo()).setInterstedInLocal(true);

        // log interested
        Logger.receiveInterested(LocalPeer.id, msg.getSendTo());
    }
}