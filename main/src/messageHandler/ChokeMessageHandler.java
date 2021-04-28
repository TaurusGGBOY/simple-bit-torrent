package messageHandler;

import message.ActualMessage;
import peer.LocalPeer;
import log.Logger;

public class ChokeMessageHandler {
    /**
     *
     * @param msg
     */
    public void handle(ActualMessage msg) {
        // 不需要其他操作 就算发送了request过去 对方也会拒绝 通信就不会继续了
        Logger.receiveChoke(LocalPeer.id, msg.getFrom());
    }
}