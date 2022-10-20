package thrones.game;

import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public class RuleChecker {

    private ArrayList<Rule> rules = new ArrayList<>();

    public RuleChecker() {
        rules.add(new DiamondOnHeartRule());
        rules.add(new HeartStartRule());
    }


    public boolean checkMove(GoTCards.Suit suit, Hand pile, boolean throwException) throws BrokeRuleException {
        for (Rule rule: rules) {
            if (!rule.checkValidMove(suit, pile)) {
                if (throwException) {
                    throw new BrokeRuleException("Illegal move has been played.");
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}