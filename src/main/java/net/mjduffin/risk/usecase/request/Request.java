package net.mjduffin.risk.usecase.request;

public class Request {
    public enum Type {
        DRAFT,
        ATTACK,
        ENDATTACK,
        MOVE,
        FORTIFY,
        SKIPFORTIFY
    }

    protected Type requestType;

    public Type getRequestType() {
        return requestType;
    }
}
