package messageHandler;

import message.ActualMessage;

public class ActualMessageHandler {
    /**
     *
     * @param msg
     */
    public void handle(ActualMessage msg) {
        switch (msg.getType()) {
            case ActualMessage.CHOKE: {
                new ChokeMessageHandler().handle(msg);
                break;
            }
            case ActualMessage.UNCHOKE: {
                new UnChokeMessageHandler().handle(msg);
                break;
            }
            case ActualMessage.INTERESTED: {
                new InterestedMessageHandler().handle(msg);
                break;
            }
            case ActualMessage.NOTINTERESTED: {
                new NotInterestedMessageHandler().handle(msg);
                break;
            }
            case ActualMessage.HAVE: {
                new HaveMessageHandler().handle(msg);
                break;
            }
            case ActualMessage.BITFIELD: {
                new BitfieldMessageHandler().handle(msg);
                break;
            }
            case ActualMessage.REQUEST: {
                new RequestMessageHandler().handle(msg);
                break;
            }
            case ActualMessage.PIECE: {
                new PieceMessageHandler().handle(msg);
                break;
            }
        }
    }
}
