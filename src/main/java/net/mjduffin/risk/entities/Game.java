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
                return MOVE;
            }
        },
        MOVE {
            @Override
            public State nextState() {
                return ATTACK;
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

    //Return true if we have reached the end of a cycle
    public void nextPlayer() {
        Player oldPlayer = getCurrentPlayer();
        playerIndex = (playerIndex + 1) % players.size();
        Player newPlayer = getCurrentPlayer();
        playerChangeObservers.forEach(x -> x.notify(oldPlayer, newPlayer));
    }

    public Player getCurrentPlayer() {
        return players.get(playerIndex);
    }

    public boolean isFirstPlayer() {
        return playerIndex == 0;
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
        State oldState = getState();
        this.state = this.state.nextState();
        State newState = getState();
        stateChangeObservers.forEach(x -> x.notify(oldState, newState));
    }

    public State getState() {
        return state;
    }

    public void setState(Game.State state) {
        this.state = state;
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
