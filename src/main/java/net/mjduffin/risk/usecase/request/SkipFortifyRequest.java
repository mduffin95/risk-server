package net.mjduffin.risk.usecase.request;

import static net.mjduffin.risk.usecase.request.Request.Type.SKIPFORTIFY;

public class SkipFortifyRequest extends Request {
    public SkipFortifyRequest() {
        this.requestType = SKIPFORTIFY;
    }
}
