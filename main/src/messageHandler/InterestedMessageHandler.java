package messageHandler;

import message.ActualMessage;
import peer.LocalPeer;
import log.Logger;

public class InterestedMessageHandler {
    /**
     *
     * @param msg
     */
    public void handle(ActualMessage msg) {
        LocalPeer.peers.get(msg.getFrom()).setInterstedInLocal(true);

        // log interested
        Logger.receiveInterested(LocalPeer.id, msg.getFrom());
    }
}