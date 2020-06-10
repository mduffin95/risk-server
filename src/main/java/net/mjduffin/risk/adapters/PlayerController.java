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
                int total = gameState.unitsToPlace;
                Map<String, Integer> draft = new HashMap<>();
                while (!(cmd = console.get()).equals("DONE")) {
                    String[] args = cmd.split(" ");
                    String territory = args[0];
                    int num = Integer.parseInt(args[1]);
                    if (num <= total) {
                        total -= num;
                    } else {
                        System.out.println("Not enough units");
                        continue;
                    }
                    draft.merge(territory, num, Integer::sum);
                    System.out.println("Units remaining: " + total);
                    if (total == 0) {
                        System.out.println("Finished drafting");
                        break;
                    }
                }
                try {
                    input.draft(name, draft);
                } catch (GameplayException e) {
                    e.printStackTrace();
                }
                break;
            case "ATTACK":
                while (!(cmd = console.get()).equals("DONE")) {
                    System.out.println(cmd);
                }
                break;
            case "FORTIFY":
                while (!(cmd = console.get()).equals("DONE")) {
                    System.out.println(cmd);
                }
                break;
        }
    }
}
