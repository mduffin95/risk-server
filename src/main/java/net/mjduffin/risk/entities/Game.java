package net.mjduffin.risk.entities;

import java.util.*;

public class Game {
    public enum State {
        ALLDRAFT {
            @Override
            public State nextState() {
                return ATTACK;
            }
        },
        DRAFT {
            @Override
            public State nextState() {
                return ATTACK;
            }
        },
        ATTACK {
            @Override
            public State nextState() {
                return FORTIFY;
            }
        },
        FORTIFY {
            @Override
            public State nextState() {
                return DRAFT;
            }
        },
        END {
            @Override
            public State nextState() {
                return END;
            }
        };

        public abstract State nextState();
    }

    Random random;
    List<Player> players;
    Board board;

    int playerIndex = 0;
    State state = State.ALLDRAFT;

    Set<PlayerChangeObserver> playerChangeObservers = new HashSet<>();
    Set<StateChangeObserver> stateChangeObservers = new HashSet<>();

    //Assume game is in draft mode as soon as it is created
    public Game(Board board, List<Player> players, Random random) {
        this.players = players;
        this.board = board;
        this.random = random;
    }

    public void nextPlayer() {
        playerIndex = (playerIndex + 1) % players.size();
        playerChangeObservers.forEach(x -> x.notify(getCurrentPlayer()));
    }

    public Player getCurrentPlayer() {
        return players.get(playerIndex);
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer(String name) {
        for (Player p: players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void nextState() {
        this.state = this.state.nextState();
        stateChangeObservers.forEach(x -> x.notify(getState()));
    }

    public State getState() {
        return state;
    }

    public int getNumPlayers() {
        return players.size();
    }

    public void registerPlayerChangeObserver(PlayerChangeObserver observer) {
        playerChangeObservers.add(observer);
    }

    public void registerStateChangeObserver(StateChangeObserver observer) {
        stateChangeObservers.add(observer);

    }

}
