package net.mjduffin.risk.view;

import net.mjduffin.risk.adapters.ConsoleController;
import net.mjduffin.risk.adapters.ConsoleRequest;
import net.mjduffin.risk.adapters.ConsoleView;
import net.mjduffin.risk.adapters.ConsoleViewModel;
import net.mjduffin.risk.usecase.GameState;

public class ConsoleViewImpl implements ConsoleView {
    RawInput rawInput;
    ConsoleController consoleController;

    public ConsoleViewImpl(RawInput input, ConsoleController controller) {
        this.rawInput = input;
        this.consoleController = controller;
    }

    @Override
    public void display(ConsoleViewModel vm) {
        System.out.println("Current player: " + vm.getCurrentPlayer());
        System.out.println("Phase: " + vm.getPhase());
        if (vm.getPhase().equals("DRAFT") || vm.getPhase().equals("ALLDRAFT")) {
            System.out.println("Units to place: " + vm.unitsToPlace);
        }
        System.out.println("***********");
        for (int i = 0; i < vm.territories.length; i++) {
            System.out.println(vm.territories[i] + " -> " + vm.occupyingPlayers[i] + " (" + vm.units[i] +")");
        }
    }
    //Prints to and reads from command line

    private void readInput() {
        while (true) {
            String input = rawInput.get();
            ConsoleRequest request = new ConsoleRequest(input);
            consoleController.request(request);
        }
    }
}
