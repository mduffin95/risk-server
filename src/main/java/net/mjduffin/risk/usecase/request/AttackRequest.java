package net.mjduffin.risk.usecase.request;

public class AttackRequest extends Request {
    private String player;
    private String attacker;
    private String defender;

    public AttackRequest(String player, String attacker, String defender) {
        this.requestType = Type.ATTACK;
        this.player = player;
        this.attacker = attacker;
        this.defender = defender;
    }

    public String getPlayer() {
        return player;
    }

    public String getAttacker() {
        return attacker;
    }

    public String getDefender() {
        return defender;
    }

}
