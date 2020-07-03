package net.mjduffin.risk.usecase.request;

public class MoveRequest extends Request {
    public MoveRequest(String player, int num) {
        this.requestType = Type.MOVE;
    }
}
