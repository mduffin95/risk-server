package net.mjduffin.risk.cli.adapters.console;

import net.mjduffin.risk.lib.usecase.GameState;
import net.mjduffin.risk.lib.usecase.PlayerOutput;
import net.mjduffin.risk.lib.usecase.request.Request;
import net.mjduffin.risk.lib.usecase.request.RequestAcceptor;

public class ConsoleManager implements PlayerOutput, ConsoleController {
    RequestAcceptor useCases;
    ConsoleView view;

    public ConsoleManager(RequestAcceptor useCases) {
        this.useCases = useCases;
        useCases.registerPlayerOutput(this);
    }

    private ConsoleViewModel convertToViewModel(GameState gameState) {
        //Take game state from usecases and convert to view model specific to console output medium
        ConsoleViewModel vm = new ConsoleViewModel();
        vm.setCurrentPlayer(gameState.getCurrentPlayer());
        vm.setPhase(gameState.getPhase());
        vm.unitsToPlace = gameState.getUnitsToPlace();
        vm.territories = gameState.getTerritories().toArray(new String[0]);
        vm.occupyingPlayers = gameState.getOccupyingPlayers().toArray(new String[0]);
        vm.units = gameState.getUnits().toArray(new Integer[0]);

        return vm;
    }

    public void turn(GameState gameState) {
        ConsoleViewModel vm = convertToViewModel(gameState);
        view.display(vm);

        //TODO: Wait for a response from the console
    }

    @Override
    public void request(ConsoleRequest request) {
        GameState gameState = useCases.getGameState();
        //Get current player and phase and use to convert to a request
        try {
            Request r = ConsoleRequestConverter.convertRequest(request, gameState.getCurrentPlayer(), gameState.getPhase());
            assert r != null;
            useCases.receiveRequest(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerView(ConsoleView view) {
        this.view = view;
    }


}
