package net.mjduffin.risk.cli;

import net.mjduffin.risk.cli.adapters.ConsoleManager;
import net.mjduffin.risk.cli.view.ConsoleInput;
import net.mjduffin.risk.cli.view.ConsoleViewImpl;
import net.mjduffin.risk.lib.entities.Game;
import net.mjduffin.risk.lib.usecase.*;
import net.mjduffin.risk.lib.usecase.request.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsoleGame {

    private static final BlockingQueue<Request> REQUEST_QUEUE = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        GameManager manager = GameFactory.basicGame();
        ConsoleInput input = new ConsoleInput();
        RequestProxy proxy = new RequestProxy(manager);
        ConsoleManager consoleManager = new ConsoleManager(proxy);

        //View registers itself with controller
        new ConsoleViewImpl(input, consoleManager);

        try {
            start(manager, consoleManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start(GameManager game, ConsoleManager consoleManager) {
        while (!game.getGameState().getPhase().equals(Game.State.END.name())) {
            GameState gameState = game.getGameState();

            consoleManager.turn(gameState);

            try {
                Request r = REQUEST_QUEUE.take();
                processRequest(game, r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Game has ended, %s has won!", game.getGameState().getOccupyingPlayers().get(0));
    }

    private static void processRequest(GameManager gameManager, Request request) {
        if (request instanceof DraftRequest) {
            DraftRequest draftRequest = (DraftRequest) request;
            gameManager.draftSingle(draftRequest.getPlayer(), draftRequest.getTerritory(), draftRequest.getUnits());
        }
        if (request instanceof AttackRequest) {
            AttackRequest attackRequest = (AttackRequest) request;
            gameManager.attack(attackRequest.getPlayer(), attackRequest.getAttacker(), attackRequest.getDefender());
        }
        if (request instanceof MoveRequest) {
            MoveRequest moveRequest = (MoveRequest) request;
            gameManager.move(moveRequest.getPlayerName(), moveRequest.getUnits());
        }
        if (request instanceof EndAttackRequest) {
            EndAttackRequest endAttackRequest = (EndAttackRequest) request;
            gameManager.endAttack(endAttackRequest.getPlayerName());
        }
        if (request instanceof FortifyRequest) {
            FortifyRequest fortifyRequest = (FortifyRequest) request;
            gameManager.fortify(
                fortifyRequest.getPlayerName(),
                fortifyRequest.getFromTerritory(),
                fortifyRequest.getToTerritory(),
                fortifyRequest.getUnits());
        }
        if (request instanceof SkipFortifyRequest) {
            gameManager.endTurn();
        }
    }

    static class RequestProxy implements RequestAcceptor {

        private final GameManager gameManager;

        public RequestProxy(GameManager gameManager) {
            this.gameManager = gameManager;
        }

        @Override
        public void receiveRequest(@NotNull Request request) {
            REQUEST_QUEUE.add(request);
        }

        @NotNull
        @Override
        public GameState getGameState() {
            return gameManager.getGameState();
        }
    }
}
