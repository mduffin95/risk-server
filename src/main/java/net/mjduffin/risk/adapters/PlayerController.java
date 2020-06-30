package net.mjduffin.risk.adapters;

import net.mjduffin.risk.usecase.*;

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
        String cmd = console.get();
        String[] args = cmd.split(" ");
        try {
            switch (gameState.getPhase()) {
                case "DRAFT":
                case "ALLDRAFT":
                    int total = gameState.unitsToPlace;
                    String territory = args[0];
                    int num = Integer.parseInt(args[1]);
                    if (num <= total) {
                        total -= num;
                    } else {
                        throw new GameplayException("Not enough units");
                    }
                    input.draftSingle(name, territory, num);
                    System.out.println("Units remaining: " + total);
                    break;
                case "ATTACK":
                    if ("DONE".equals(args[0])) {
                        input.endAttack(name);
                        break;
                    }
                    String attackTerritory = args[0];
                    String defendTerritory = args[1];
                    input.attack(name, attackTerritory, defendTerritory);
                    break;
                case "MOVE":
                    int toMove = Integer.parseInt(args[0]);
                    System.out.println("MOVE PHASE");
                    input.move(name, toMove);
                    break;
                case "FORTIFY":
                    String from = args[0];
                    String to = args[1];
                    int toFortify = Integer.parseInt(args[2]);
                    input.fortify(name, from, to, toFortify);

                    break;
            }
        } catch (GameplayException | TerritoryNotFoundException | PlayerNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
