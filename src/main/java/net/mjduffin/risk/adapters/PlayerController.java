package net.mjduffin.risk.adapters;

import net.mjduffin.risk.usecase.PlayerInput;
import net.mjduffin.risk.usecase.PlayerOutput;

public class PlayerController implements PlayerOutput {
    PlayerInput input;
    String name;

    public PlayerController(String name, PlayerInput input) {
        this.input = input;
    }

    @Override
    public void notifyTurn() {
    }
}
