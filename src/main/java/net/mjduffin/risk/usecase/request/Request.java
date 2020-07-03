package net.mjduffin.risk.usecase.request;

import net.mjduffin.risk.entities.Game;

public class Request {
    public enum Type {
        DRAFT,
        ATTACK,
        ENDATTACK,
        MOVE,
        FORTIFY
    }

    protected Type requestType;

    public Type getRequestType() {
        return requestType;
    }
}
