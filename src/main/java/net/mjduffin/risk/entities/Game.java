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

    //Assume game is in draft mode as soon as it is created
    public Game(Board board, List<Player> players, Random random) {
        this.players = players;
        this.board = board;
        this.random = random;
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
