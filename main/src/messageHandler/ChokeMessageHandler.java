package messageHandler;

import message.ActualMessage;
import peer.LocalPeer;
import log.Logger;

public class ChokeMessageHandler {
    public void handle(ActualMessage msg) {
        // TODO 是否还需要其他操作？
        // TODO reqeust 的时候是否需要检查choke标志位？
        Logger.receiveChoke(LocalPeer.id, msg.getFrom());
    }
}