package net.mjduffin.risk.usecase;

import net.mjduffin.risk.entities.Territory;

public class Engagement {

    public final Territory attacker;
    public final Territory defender;

    public Engagement(Territory attacker, Territory defender)  {
        this.attacker = attacker;
        this.defender = defender;
    }
}

