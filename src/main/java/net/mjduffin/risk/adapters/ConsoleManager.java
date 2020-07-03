package net.mjduffin.risk.adapters;

import net.mjduffin.risk.usecase.*;
import net.mjduffin.risk.usecase.request.AttackRequest;
import net.mjduffin.risk.usecase.request.EndAttackRequest;
import net.mjduffin.risk.usecase.request.Request;
import net.mjduffin.risk.usecase.request.RequestAcceptor;
import net.mjduffin.risk.view.RawInput;

public class ConsoleManager implements PlayerOutput, ConsoleController {
    RequestAcceptor useCases;
    String name;
    ConsoleView view;

    public ConsoleManager(String name, RequestAcceptor useCases) {
        this.name = name;
        this.useCases = useCases;
        useCases.registerPlayerOutput(this);
    }

    @Override
    public void notifyTurn() {
        System.out.println("Turn: " + name);
    }

    @Override
    public String getPlayerName() {
        return name;
    }

    ConsoleViewModel convertToViewModel(GameState gameState) {
        return null;
    }

    public void turn(GameState gameState) {
        ConsoleViewModel vm = convertToViewModel(gameState);
        view.display(vm);
    }

    @Override
    public void request(ConsoleRequest request) {
        GameState gameState = useCases.getGameState();
        Request r = ConsoleRequestConverter.convertRequest(request, name, gameState.getPhase());
        useCases.receiveRequest(r);
    }


}
