package net.mjduffin.risk.cli;

import net.mjduffin.risk.cli.adapters.ConsoleManager;
import net.mjduffin.risk.cli.view.ConsoleInput;
import net.mjduffin.risk.cli.view.ConsoleViewImpl;

public class ConsoleGame {

    public static void main(String[] args) {
        String[] players = {"Joe", "Sam"};

        GameManager manager = GameFactory.basicGame(players);
        ConsoleInput input = new ConsoleInput();
        ConsoleManager consoleManager = new ConsoleManager(manager);

        //View registers itself with controller
        new ConsoleViewImpl(input, consoleManager);

        try {
            manager.start();
        } catch (InterruptedException | GameplayException | PlayerNotFoundException | TerritoryNotFoundException e) {
            e.printStackTrace();
        }
    }


}
