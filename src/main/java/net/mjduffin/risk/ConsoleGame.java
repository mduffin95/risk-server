package net.mjduffin.risk;

import net.mjduffin.risk.adapters.ConsoleController;
import net.mjduffin.risk.adapters.ConsoleManager;
import net.mjduffin.risk.adapters.ConsoleView;
import net.mjduffin.risk.usecase.*;
import net.mjduffin.risk.usecase.request.RequestAcceptor;
import net.mjduffin.risk.view.ConsoleInput;
import net.mjduffin.risk.view.ConsoleViewImpl;
import net.mjduffin.risk.view.RawInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsoleGame {
    PlayerOutput playerOutput;

    public static void main(String[] args) {
        String[] players = {"Joe", "Sam"};

        GameManager manager = GameFactory.basicGame(players);
        ConsoleInput input = new ConsoleInput();
        ConsoleManager consoleManager = new ConsoleManager(manager);

        //View registers itself with controller
        new ConsoleViewImpl(input, consoleManager);

        manager.start();
    }


}
