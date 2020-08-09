package net.mjduffin.risk.lib.usecase.request;

import net.mjduffin.risk.lib.usecase.GameState;
import net.mjduffin.risk.lib.usecase.PlayerOutput;

public interface RequestAcceptor {
    void receiveRequest(Request request);
    void registerPlayerOutput(PlayerOutput output);
    GameState getGameState();
}
