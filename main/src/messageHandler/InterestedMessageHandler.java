package messageHandler;

import message.ActualMessage;
import peer.LocalPeer;
import util.Logger;

public class InterestedMessageHandler {
    public void handle(ActualMessage msg) {
        LocalPeer.peers.get(msg.getPeerID()).setInterstedInLocal(true);

        // log interested
        Logger.receiveInterested(LocalPeer.id, msg.getPeerID());
    }
}