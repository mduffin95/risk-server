package net.mjduffin.risk.adapters;

import net.mjduffin.risk.usecase.*;
import net.mjduffin.risk.usecase.request.Request;
import net.mjduffin.risk.usecase.request.RequestAcceptor;

import java.util.Arrays;

public class ConsoleManager implements PlayerOutput, ConsoleController {
    RequestAcceptor useCases;
    ConsoleView view;

    public ConsoleManager(RequestAcceptor useCases) {
        this.useCases = useCases;
        useCases.registerPlayerOutput(this);
    }

    ConsoleViewModel convertToViewModel(GameState gameState) {
        //Take game state from usecases and convert to view model specific to console output medium
        ConsoleViewModel vm = new ConsoleViewModel();
        vm.setCurrentPlayer(gameState.getCurrentPlayer());
        vm.setPhase(gameState.getPhase());
        vm.unitsToPlace = gameState.unitsToPlace;
        vm.territories = Arrays.copyOf(gameState.territories, gameState.territories.length);
        vm.occupyingPlayers = Arrays.copyOf(gameState.occupyingPlayers, gameState.occupyingPlayers.length);
        vm.units = Arrays.copyOf(gameState.units, gameState.units.length);

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
        Request r = ConsoleRequestConverter.convertRequest(request, gameState.getCurrentPlayer(), gameState.getPhase());
        useCases.receiveRequest(r);
    }

    @Override
    public void registerView(ConsoleView view) {
        this.view = view;
    }


}
