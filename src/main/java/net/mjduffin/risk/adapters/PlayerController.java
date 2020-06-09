package net.mjduffin.risk.adapters;

import net.mjduffin.risk.usecase.GameState;
import net.mjduffin.risk.usecase.GameplayException;
import net.mjduffin.risk.usecase.PlayerInput;
import net.mjduffin.risk.usecase.PlayerOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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
        String cmd;
        switch (gameState.getPhase()) {
            case "DRAFT":
            case "ALLDRAFT":
                Map<String, Integer> draft = new HashMap<>();
                while (!(cmd = console.get()).equals("DONE")) {
                    String[] args = cmd.split(" ");
                    String territory = args[0];
                    int num = Integer.parseInt(args[1]);
                    draft.put(territory, num);
                }
                try {
                    input.draft(name, draft);
                } catch (GameplayException e) {
                    e.printStackTrace();
                }
                break;
            case "ATTACK":
                break;
        }

        System.out.println(input);
    }
}
