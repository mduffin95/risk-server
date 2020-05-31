package net.mjduffin.risk.entities;

import java.util.*;

public class Game {
    public enum State {
        SETUP {
            @Override
            public State nextState() {
                return ALLDRAFT;
            }
        },
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
        };

        public abstract State nextState();
    }

    Random random;
    List<Player> players;
    Board board;

    int playerIndex = 0;
    State state = State.SETUP;

    public Game(Board board, Random random) {
        this.players = new ArrayList<>();
        this.board = board;
        this.random = random;
    }

    public void start() {
        if (getState() == State.SETUP) {
            Collections.shuffle(players, random);
            nextState();
        } else {
            //TODO: Throw exception
        }
    }

    public void nextPlayer() {
        playerIndex = (playerIndex + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(playerIndex);
    }

    public Board getBoard() {
        return board;
    }

    public Player addPlayer(String playerName) {
        if (getState() == State.SETUP) {
            Player player = new Player(playerName);
            players.add(player);
            return player;
        } else {
            //TODO: Throw exception
            return null;
        }
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
    }

    public State getState() {
        return state;
    }

    public int getNumPlayers() {
        return players.size();
    }

}
