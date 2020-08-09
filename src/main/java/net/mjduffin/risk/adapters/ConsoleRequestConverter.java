package net.mjduffin.risk.adapters;

import net.mjduffin.risk.usecase.request.*;

public class ConsoleRequestConverter {
    public static Request convertRequest(ConsoleRequest consoleRequest, String player, String phase) {

        String cmd = consoleRequest.getRequest();
        String[] args = cmd.split(" ");
        switch (phase) {
            case "DRAFT":
            case "ALLDRAFT":
                String territory = args[0];
                int num = Integer.parseInt(args[1]);
//                    if (num <= total) {
//                        total -= num;
//                    } else {
//                        throw new GameplayException("Not enough units");
//                    }
                return new DraftRequest(player, territory, num);

//                    useCases.draftSingle(name, territory, num);
//                    System.out.println("Units remaining: " + total);
            case "ATTACK":
                if ("DONE".equals(args[0])) {
                    return new EndAttackRequest(player);
                }
                String attackTerritory = args[0];
                String defendTerritory = args[1];
                return new AttackRequest(player, attackTerritory, defendTerritory);
            case "MOVE":
                int toMove = Integer.parseInt(args[0]);
                System.out.println("MOVE PHASE");
                return new MoveRequest(player, toMove);
            case "FORTIFY":
                if ("SKIP".equals(args[0])) {
                    return new SkipFortifyRequest();
                }
                String from = args[0];
                String to = args[1];
                int toFortify = Integer.parseInt(args[2]);
                return new FortifyRequest(player, from, to, toFortify);
        }
        return null;
    }
}
