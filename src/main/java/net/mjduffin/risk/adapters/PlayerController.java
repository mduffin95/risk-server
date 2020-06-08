package net.mjduffin.risk.adapters;

import net.mjduffin.risk.usecase.GameState;
import net.mjduffin.risk.usecase.PlayerInput;
import net.mjduffin.risk.usecase.PlayerOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PlayerController implements PlayerOutput {
    PlayerInput input;
    String name;
    RawInput console;

    public PlayerController(String name, PlayerInput input, RawInput console) {
        this.name = name;
        this.input = input;
        this.console = console;
        input.registerPlayerOutput(this);
    }

    @Override
    public void notifyTurn() {
        System.out.println("Turn: " + name);
    }

    @Override
    public String getPlayerName() {
        return name;
    }

    public void turn(GameState gameState) {
        String input = console.get();
        System.out.println(input);
    }
}
