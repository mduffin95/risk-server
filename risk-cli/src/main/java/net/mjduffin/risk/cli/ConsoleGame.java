package net.mjduffin.risk.cli;

import net.mjduffin.risk.cli.adapters.console.ConsoleManager;
import net.mjduffin.risk.cli.view.ConsoleInput;
import net.mjduffin.risk.cli.view.ConsoleViewImpl;
import net.mjduffin.risk.lib.usecase.*;

import java.util.Arrays;
import java.util.List;

public class ConsoleGame {

    public static void main(String[] args) {
        GameManager manager = GameFactory.basicGame();
        ConsoleInput input = new ConsoleInput();
        ConsoleManager consoleManager = new ConsoleManager(manager);

        //View registers itself with controller
        new ConsoleViewImpl(input, consoleManager);

        try {
            manager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
