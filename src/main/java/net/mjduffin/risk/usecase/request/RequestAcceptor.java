package net.mjduffin.risk.usecase.request;

import net.mjduffin.risk.usecase.GameState;
import net.mjduffin.risk.usecase.PlayerOutput;

public interface RequestAcceptor {
    void receiveRequest(Request request);
    void registerPlayerOutput(PlayerOutput output);
    GameState getGameState();
}
